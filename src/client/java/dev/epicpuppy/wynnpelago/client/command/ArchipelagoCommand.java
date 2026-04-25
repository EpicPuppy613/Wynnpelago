package dev.epicpuppy.wynnpelago.client.command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ArchipelagoCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("archipelago")
                    .then(literal("connect")
                            .then(argument("host", StringArgumentType.string())
                                    .then(argument("port", IntegerArgumentType.integer(0, 65535))
                                            .then(argument("slot", StringArgumentType.string())
                                                    .executes(ArchipelagoCommand::executeConnectCommand)
                                                    .then(argument("password", StringArgumentType.string())
                                                            .executes(ArchipelagoCommand::executeConnectCommand))))))
                    .then(literal("reconnect").executes(ArchipelagoCommand::executeReconnectCommand))
                    .then(literal("disconnect").executes(ArchipelagoCommand::executeDisconnectCommand)));
            dispatcher.register(literal("ap")
                    .then(argument("message", StringArgumentType.greedyString())
                            .executes(ArchipelagoCommand::executeAPCommand)));
        });
    }

    private static int executeConnectCommand(CommandContext<FabricClientCommandSource> context) {
        if (WynnpelagoClient.client != null && WynnpelagoClient.client.isConnected()) {
            context.getSource()
                    .sendError(WynnpelagoClient.getWPPrefix()
                            .append(Component.literal("Already connected to a server")
                                    .withStyle(ChatFormatting.RED)));
            return 0;
        }
        String host = StringArgumentType.getString(context, "host");
        int port = IntegerArgumentType.getInteger(context, "port");
        String slot = StringArgumentType.getString(context, "slot");
        String password = "";
        try {
            password = StringArgumentType.getString(context, "password");
        } catch (IllegalArgumentException ignored) {
        }

        ArchipelagoClient client = WynnpelagoClient.resetArchipelago();
        client.setName(slot);
        client.setPassword(password);
        try {
            client.connect("%s:%d".formatted(host, port));
            context.getSource()
                    .sendFeedback(WynnpelagoClient.getWPPrefix()
                            .append(Component.literal("Connecting to " + host).withStyle(ChatFormatting.YELLOW)));
        } catch (java.net.URISyntaxException e) {
            context.getSource()
                    .sendError(WynnpelagoClient.getWPPrefix()
                            .append(Component.literal("Invalid host/port").withStyle(ChatFormatting.RED)));
            return 0;
        }
        return 1;
    }

    private static int executeReconnectCommand(CommandContext<FabricClientCommandSource> context) {
        if (WynnpelagoClient.client != null && WynnpelagoClient.client.isConnected()) {
            context.getSource()
                    .sendError(WynnpelagoClient.getWPPrefix()
                            .append(Component.literal("Already connected to a server")
                                    .withStyle(ChatFormatting.RED)));
            return 0;
        }
        if (WynnpelagoClient.client == null) {
            context.getSource()
                    .sendError(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("No server to reconnect to").withStyle(ChatFormatting.RED)));
            return 0;
        }
        WynnpelagoClient.client.reconnect();
        context.getSource()
                .sendFeedback(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("Reconnecting to server").withStyle(ChatFormatting.YELLOW)));
        return 1;
    }

    private static int executeDisconnectCommand(CommandContext<FabricClientCommandSource> context) {
        if (WynnpelagoClient.client == null || !WynnpelagoClient.client.isConnected()) {
            context.getSource()
                    .sendError(WynnpelagoClient.getWPPrefix()
                            .append(Component.literal("Not connected to a server")
                                    .withStyle(ChatFormatting.RED)));
            return 0;
        }
        WynnpelagoClient.client.disconnect();
        context.getSource()
                .sendFeedback(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("Disconnected from the server")
                                .withStyle(ChatFormatting.YELLOW)));
        return 1;
    }

    private static int executeAPCommand(CommandContext<FabricClientCommandSource> context) {
        WynnpelagoClient.client.sendChat(StringArgumentType.getString(context, "message"));
        return 1;
    }
}
