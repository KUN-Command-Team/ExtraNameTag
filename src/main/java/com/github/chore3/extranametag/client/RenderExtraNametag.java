package com.github.chore3.extranametag.client;

import com.github.chore3.extranametag.Extranametag;
import com.github.chore3.extranametag.registry.NametagClientState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Extranametag.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class RenderExtraNametag {
    private RenderExtraNametag() {}

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        double distanceSqr = Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(entity);
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

        for (int i = 0; i < lines.size(); i++) {
            int yPixels = -(i + 1 + baseLineOffset) * (Minecraft.getInstance().font.lineHeight + 1);
            drawLineLikeVanilla(event, Component.literal(lines.get(i)), yPixels);
        }
    }

    private static void drawLineLikeVanilla(RenderNameTagEvent event, Component text, int yPixels) {
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

