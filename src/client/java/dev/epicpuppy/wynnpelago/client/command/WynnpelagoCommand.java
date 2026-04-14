package dev.epicpuppy.wynnpelago.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static dev.epicpuppy.wynnpelago.client.WynnpelagoClient.enabled;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WynnpelagoCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            dispatcher.register(literal("wynnpelago")
                    .then(literal("version").executes(WynnpelagoCommand::executeVersionCommand))
                    .then(literal("enable").executes(WynnpelagoCommand::executeEnableCommand))
                    .then(literal("territory")
                            .then(literal("unlock").then(argument("territory", StringArgumentType.greedyString())
                                    .suggests(new TerritorySuggestionProvider(true)).executes(WynnpelagoCommand::executeTerritoryUnlockCommand)))
                            .then(literal("lock").then(argument("territory", StringArgumentType.greedyString())
                                    .suggests(new TerritorySuggestionProvider(false)).executes(WynnpelagoCommand::executeTerritoryLockCommand)))));
        }));
    }

    private static int executeVersionCommand(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Component.literal("Wynnpelago v" + Wynnpelago.VERSION));
        return 1;
    }

    private static int executeEnableCommand(CommandContext<FabricClientCommandSource> context) {
        enabled = !enabled;
        context.getSource().sendFeedback(Component.literal(enabled ? "Wynnpelago enabled" : "Wynnpelago disabled")
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
        return 1;
    }

    private static int executeTerritoryUnlockCommand(CommandContext<FabricClientCommandSource> context) {
        String territory = StringArgumentType.getString(context, "territory");
        if (TerritoryUnlock.unlockedTerritories.contains(territory)) {
            context.getSource().sendError(Component.literal("Territory already unlocked").withStyle(ChatFormatting.RED));
            return 0;
        } else if (Models.Territory.getTerritoryProfile(territory) == null) {
            context.getSource().sendError(Component.literal("Unknown territory").withStyle(ChatFormatting.RED));
            return 0;
        } else {
            TerritoryUnlock.unlockedTerritories.add(territory);
            context.getSource().sendFeedback(Component.literal("Unlocked territory: " + territory).withStyle(ChatFormatting.GREEN));
            return 1;
        }
    }

    private static int executeTerritoryLockCommand(CommandContext<FabricClientCommandSource> context) {
        String territory = StringArgumentType.getString(context, "territory");
        if (!TerritoryUnlock.unlockedTerritories.contains(territory)) {
            context.getSource().sendError(Component.literal("Territory not unlocked").withStyle(ChatFormatting.RED));
            return 0;
        } else if (TerritoryUnlock.RESPAWN_TERRITORIES.contains(territory)) {
            context.getSource().sendError(Component.literal("That territory is always unlocked").withStyle(ChatFormatting.RED));
            return 0;
        } else {
            TerritoryUnlock.unlockedTerritories.remove(territory);
            context.getSource().sendFeedback(Component.literal("Locked territory: " + territory).withStyle(ChatFormatting.GREEN));
            return 1;
        }
    }
}
