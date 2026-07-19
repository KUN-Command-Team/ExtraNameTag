package com.github.chore3.extranametag.client;

import com.github.chore3.extranametag.Extranametag;
import com.github.chore3.extranametag.registry.NametagClientState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Extranametag.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class RenderExtraNametag {
    private static final float VANILLA_LINE_STEP = 9.0F * 1.15F;

    private RenderExtraNametag() {}

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        double distanceSqr = Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(entity);
        if (!shouldRenderExtraNameTag(event, distanceSqr)) {
            return;
        }
        if (!ForgeHooksClient.isNameplateInRenderDistance(entity, distanceSqr)) {
            return;
        }

        List<String> lines = new ArrayList<>(NametagClientState.getLines(entity.getUUID()));

        lines.clear();
        lines.add("apple");
        lines.add("banana");

        int baseLineOffset = 0;
        if (entity instanceof Player player && distanceSqr < 100.0D) {
            Scoreboard scoreboard = player.getScoreboard();
            Objective objective = scoreboard.getDisplayObjective(2);
            if (objective != null) {
                baseLineOffset = 1;
            }
        }

        int nameLineOffset = 1;

        for (int i = 0; i < lines.size(); i++) {
            float yPixels = -(i + nameLineOffset + baseLineOffset) * VANILLA_LINE_STEP;
            drawLineLikeVanilla(event, Component.literal(lines.get(i)), yPixels);
        }

    }

    private static boolean shouldRenderExtraNameTag(RenderNameTagEvent event, double distanceSqr) {
        Event.Result result = event.getResult();
        if (result == Event.Result.DENY) {
            return false;
        }
        if (result == Event.Result.ALLOW) {
            return true;
        }

        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            return shouldShowName(livingEntity, distanceSqr);
        }
        return entity.shouldShowName() && entity.hasCustomName();
    }

    private static boolean shouldShowName(LivingEntity entity, double distanceSqr) {
        float max = entity.isDiscrete() ? 32.0F : 64.0F;
        if (distanceSqr >= (double) (max * max)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer == null) {
            return false;
        }

        boolean visible = !entity.isInvisibleTo(localPlayer);
        if (entity != localPlayer) {
            Team team = entity.getTeam();
            Team localTeam = localPlayer.getTeam();
            if (team != null) {
                Team.Visibility visibility = team.getNameTagVisibility();
                switch (visibility) {
                    case ALWAYS:
                        return visible;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return localTeam == null ? visible : team.isAlliedTo(localTeam) && (team.canSeeFriendlyInvisibles() || visible);
                    case HIDE_FOR_OWN_TEAM:
                        return localTeam == null ? visible : !team.isAlliedTo(localTeam) && visible;
                    default:
                        return true;
                }
            }
        }

        return Minecraft.renderNames() && entity != minecraft.getCameraEntity() && visible && !entity.isVehicle();
    }

    private static void drawLineLikeVanilla(RenderNameTagEvent event, Component text, float yPixels) {
        Entity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        PoseStack poseStack = event.getPoseStack();
        boolean seeThroughPass = !entity.isDiscrete();
        float nameTagOffsetY = entity.getNameTagOffsetY();
        float opacity = minecraft.options.getBackgroundOpacity(0.25F);
        int background = (int) (opacity * 255.0F) << 24;

        poseStack.pushPose();
        poseStack.translate(0.0F, nameTagOffsetY, 0.0F);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix = poseStack.last().pose();
        float x = -font.width(text) / 2.0F;

        font.drawInBatch(text, x, yPixels, 553648127, false, matrix, event.getMultiBufferSource(), seeThroughPass ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, background, event.getPackedLight());
        if (seeThroughPass) {
            font.drawInBatch(text, x, yPixels, -1, false, matrix, event.getMultiBufferSource(), Font.DisplayMode.NORMAL, 0, event.getPackedLight());
        }

        poseStack.popPose();
    }
}

