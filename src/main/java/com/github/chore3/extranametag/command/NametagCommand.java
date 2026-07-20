package com.github.chore3.extranametag.command;

import com.github.chore3.extranametag.data.NametagCapability;
import com.github.chore3.extranametag.data.NametagStack;
import com.github.chore3.extranametag.network.NametagNetwork;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;

public class NametagCommand {
    private static final SimpleCommandExceptionType ERROR_PUSH_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.nametag.push.failed")
    );
    private static final SimpleCommandExceptionType ERROR_POP_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.nametag.pop.failed")
    );
    private static final SimpleCommandExceptionType ERROR_CLEAR_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.nametag.clear.failed")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var nametag = Commands.literal("nametag")
                .requires(source -> source.hasPermission(2));

        var push = Commands.literal("push")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(ctx -> executePush(
                                        ctx.getSource(),
                                        EntityArgument.getEntities(ctx, "targets"),
                                        StringArgumentType.getString(ctx, "name")
                                ))));

        var pop = Commands.literal("pop")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(ctx -> executePop(
                                ctx.getSource(),
                                EntityArgument.getEntities(ctx, "targets")
                        )));

        var clear = Commands.literal("clear")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(ctx -> executeClear(
                                ctx.getSource(),
                                EntityArgument.getEntities(ctx, "targets")
                        )));

        nametag.then(push)
                .then(pop)
                .then(clear);

        dispatcher.register(nametag);
    }

    private static int executePush(CommandSourceStack source, Collection<? extends Entity> targets, String name)
            throws CommandSyntaxException {
        int count = 0;

        for (Entity entity : targets) {
            var cap = entity.getCapability(NametagCapability.NAMETAG_STACK);
            if (cap.isPresent()) {
                NametagStack stack = cap.resolve().get();
                int sizeBefore = stack.size();
                stack.push(name);
                if (stack.size() > sizeBefore) {
                    count++;
                    broadcastUpdate(entity);
                }
            }
        }

        if (count == 0) {
            throw ERROR_PUSH_FAILED.create();
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(() -> Component.translatable(
                        "commands.nametag.push.success.single",
                        name,
                        targets.iterator().next().getDisplayName()
                ), true);
            } else {
                source.sendSuccess(() -> Component.translatable(
                        "commands.nametag.push.success.multiple",
                        name,
                        targets.size()
                ), true);
            }
            return count;
        }
    }

    private static int executePop(CommandSourceStack source, Collection<? extends Entity> targets)
            throws CommandSyntaxException {
        int count = 0;

        for (Entity entity : targets) {
            var cap = entity.getCapability(NametagCapability.NAMETAG_STACK);
            if (cap.isPresent()) {
                NametagStack stack = cap.resolve().get();
                if (stack.pop()) {
                    count++;
                    broadcastUpdate(entity);
                }
            }
        }

        if (count == 0) {
            throw ERROR_POP_FAILED.create();
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(() -> Component.translatable(
                        "commands.nametag.pop.success.single",
                        targets.iterator().next().getDisplayName()
                ), true);
            } else {
                source.sendSuccess(() -> Component.translatable(
                        "commands.nametag.pop.success.multiple",
                        targets.size()
                ), true);
            }
            return count;
        }
    }

    private static int executeClear(CommandSourceStack source, Collection<? extends Entity> targets)
            throws CommandSyntaxException {
        int count = 0;

        for (Entity entity : targets) {
            var cap = entity.getCapability(NametagCapability.NAMETAG_STACK);
            if (cap.isPresent()) {
                NametagStack stack = cap.resolve().get();
                if (stack.size() > 0) {
                    stack.clear();
                    count++;
                    broadcastUpdate(entity);
                }
            }
        }

        if (count == 0) {
            throw ERROR_CLEAR_FAILED.create();
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(() -> Component.translatable(
                        "commands.nametag.clear.success.single",
                        targets.iterator().next().getDisplayName()
                ), true);
            } else {
                source.sendSuccess(() -> Component.translatable(
                        "commands.nametag.clear.success.multiple",
                        targets.size()
                ), true);
            }
            return count;
        }
    }

    private static void broadcastUpdate(Entity entity) {
        var cap = entity.getCapability(NametagCapability.NAMETAG_STACK);
        if (cap.isPresent()) {
            NametagStack stack = cap.resolve().get();
            NametagNetwork.CHANNEL.send(
                    PacketDistributor.ALL.noArg(),
                    new com.github.chore3.extranametag.network.NametagStackPacket(
                            stack.serializeNBT(),
                            entity.getUUID()
                    )
            );
        }
    }
}


