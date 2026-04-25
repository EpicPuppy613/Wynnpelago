package dev.epicpuppy.wynnpelago.client;

import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoClient;
import dev.epicpuppy.wynnpelago.client.check.ContentCheck;
import dev.epicpuppy.wynnpelago.client.check.LevelCheck;
import dev.epicpuppy.wynnpelago.client.command.ArchipelagoCommand;
import dev.epicpuppy.wynnpelago.client.command.WynnpelagoCommand;
import dev.epicpuppy.wynnpelago.client.providers.LevelProvider;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.util.ArrayDeque;
import java.util.Queue;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class WynnpelagoClient implements ClientModInitializer {
    public static ArchipelagoClient client;

    private static LevelProvider levelProvider;

    private static ContentCheck contentCheck;
    private static LevelCheck levelCheck;

    private static LevelUnlock levelUnlock;
    private static TerritoryUnlock territoryUnlock;

    private static Queue<Component> messageQueue;
    private static Queue<String> checkQueue;

    // Enable all Wynnpelago features (only when connected to an Archipelago server)
    public static boolean enabled = false;

    public static void sendClientMessage(Component message) {
        messageQueue.add(message);
    }

    public static MutableComponent getAPPrefix() {
        return Component.empty()
                .append(Component.literal("AP").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                .append(Component.literal(" >> ").withStyle(ChatFormatting.GRAY));
    }

    public static MutableComponent getWPPrefix() {
        return Component.empty()
                .append(Component.literal("WP").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD))
                .append(Component.literal(" >> ").withStyle(ChatFormatting.GRAY));
    }

    public static void sendCheck(String location) {
        if (client == null) {
            return;
        }
        if (client.isConnected()) {
            long itemId = client.getDataPackage()
                    .getGame("Wynncraft")
                    .locationNameToId
                    .getOrDefault(location, -1L);
            if (itemId == -1) {
                return;
            }
            client.getLocationManager().checkLocation(itemId);
        } else {
            checkQueue.add(location);
        }
    }

    @Override
    public void onInitializeClient() {
        levelProvider = new LevelProvider();

        contentCheck = new ContentCheck();
        levelCheck = new LevelCheck();

        levelUnlock = new LevelUnlock();
        territoryUnlock = new TerritoryUnlock();

        messageQueue = new ArrayDeque<>();

        WynnpelagoCommand.register();
        ArchipelagoCommand.register();

        ClientTickEvents.END_CLIENT_TICK.register((Minecraft client) -> {
            while (!messageQueue.isEmpty()) {
                McUtils.sendMessageToClient(messageQueue.remove());
            }
        });
    }

    public static ArchipelagoClient resetArchipelago() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        client = new ArchipelagoClient();
        return client;
    }

    public static void sendQueuedChecks() {
        while (!checkQueue.isEmpty()) {
            sendCheck(checkQueue.remove());
        }
    }
}
