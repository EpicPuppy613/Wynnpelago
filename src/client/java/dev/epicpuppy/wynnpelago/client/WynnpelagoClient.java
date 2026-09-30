package dev.epicpuppy.wynnpelago.client;

import com.wynntils.core.WynntilsMod;
import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.check.ContentCheck;
import dev.epicpuppy.wynnpelago.client.check.LevelCheck;
import dev.epicpuppy.wynnpelago.client.check.TerritoryCheck;
import dev.epicpuppy.wynnpelago.client.command.ArchipelagoCommand;
import dev.epicpuppy.wynnpelago.client.command.WynnpelagoCommand;
import dev.epicpuppy.wynnpelago.client.render.TerritoryBorderRenderer;
import dev.epicpuppy.wynnpelago.client.services.ContentService;
import dev.epicpuppy.wynnpelago.client.services.DeathLinkService;
import dev.epicpuppy.wynnpelago.client.services.LevelService;
import dev.epicpuppy.wynnpelago.client.services.TrapService;
import dev.epicpuppy.wynnpelago.client.trap.BlindTrap;
import dev.epicpuppy.wynnpelago.client.trap.DazeTrap;
import dev.epicpuppy.wynnpelago.client.trap.FreezeTrap;
import dev.epicpuppy.wynnpelago.client.trap.KillTrap;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import io.github.archipelagomw.ClientStatus;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class WynnpelagoClient implements ClientModInitializer {
    public static ArchipelagoClient client;

    private static final LevelService LEVEL_SERVICE = new LevelService();
    private static final TrapService TRAP_SERVICE = new TrapService();
    private static final DeathLinkService DEATH_LINK_SERVICE = new DeathLinkService();

    private static final ContentCheck CONTENT_CHECK = new ContentCheck();
    private static final LevelCheck LEVEL_CHECK = new LevelCheck();
    private static final TerritoryCheck TERRITORY_CHECK = new TerritoryCheck();

    private static final GearUnlock GEAR_UNLOCK = new GearUnlock();
    private static final LevelUnlock LEVEL_UNLOCK = new LevelUnlock();
    private static final TerritoryUnlock TERRITORY_UNLOCK = new TerritoryUnlock();

    private static final FreezeTrap FREEZE_TRAP = new FreezeTrap();
    private static final DazeTrap DAZE_TRAP = new DazeTrap();
    private static final BlindTrap BLIND_TRAP = new BlindTrap();
    private static final KillTrap KILL_TRAP = new KillTrap();

    private static final TerritoryBorderRenderer TERRITORY_BORDER_RENDERER = new TerritoryBorderRenderer();

    private static final Queue<Component> MESSAGE_QUEUE = new ArrayDeque<>();
    private static final Queue<String> CHECK_QUEUE = new ArrayDeque<>();

    private static int connectionCooldown = 0;

    // Enable all Wynnpelago features (only when connected to an Archipelago server)
    public static boolean enabled = false;

    public static void sendClientMessage(Component message) {
        MESSAGE_QUEUE.add(message);
    }

    public static void sendClientFeedback(Component message) {
        if (connectionCooldown > 0) return;
        sendClientMessage(getWPPrefix().append(message));
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
        if (client != null && client.isConnected()) {
            ContentService.checkLocation(location);
            if (ArchipelagoOptions.getGoalType() == ArchipelagoOptions.GoalType.DUNGEON) {
                if (Objects.equals(location, ArchipelagoOptions.getGoalDungeon())) {
                    client.setGameState(ClientStatus.CLIENT_GOAL);
                    return;
                }
            }
            if (ArchipelagoOptions.getGoalType() == ArchipelagoOptions.GoalType.QUEST) {
                if (Objects.equals(location, ArchipelagoOptions.getGoalQuest())) {
                    client.setGameState(ClientStatus.CLIENT_GOAL);
                    return;
                }
            }
            long itemId = client.getDataPackage()
                    .getGame("Wynncraft")
                    .locationNameToId
                    .getOrDefault(location, -1L);
            if (itemId == -1) {
                return;
            }
            client.getLocationManager().checkLocation(itemId);
        } else {
            CHECK_QUEUE.add(location);
        }
    }

    public static void connect() {
        connectionCooldown = 40;
        enabled = true;
        sendQueuedChecks();
    }

    public static void unlockTerritory(String territory) {
        TerritoryUnlock.unlockTerritory(territory);
        if (connectionCooldown <= 0) {
            ContentService.unlockRegion(territory);
        }
    }

    private static void postConnect() {
        ContentService.populateGameState();
    }

    public static void wynntilsInit() {
        WynntilsMod.registerEventListener(DEATH_LINK_SERVICE);
        WynntilsMod.registerEventListener(CONTENT_CHECK);
    }

    public static ArchipelagoClient resetArchipelago() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        client = new ArchipelagoClient();
        return client;
    }

    public static void sendQueuedChecks() {
        while (!CHECK_QUEUE.isEmpty()) {
            sendCheck(CHECK_QUEUE.remove());
        }
    }

    @Override
    public void onInitializeClient() {
        LEVEL_SERVICE.init();
        TRAP_SERVICE.init();
        DEATH_LINK_SERVICE.init();

        CONTENT_CHECK.init();
        LEVEL_CHECK.init();
        TERRITORY_CHECK.init();

        GEAR_UNLOCK.init();
        LEVEL_UNLOCK.init();
        TERRITORY_UNLOCK.init();

        FREEZE_TRAP.init();
        DAZE_TRAP.init();
        BLIND_TRAP.init();
        KILL_TRAP.init();

        TERRITORY_BORDER_RENDERER.init();

        WynnpelagoCommand.register();
        ArchipelagoCommand.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (!MESSAGE_QUEUE.isEmpty()) {
                McUtils.sendMessageToClient(MESSAGE_QUEUE.remove());
            }
            if (connectionCooldown > 0) {
                connectionCooldown--;
                if (connectionCooldown == 0) {
                    postConnect();
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (WynnpelagoClient.client == null || !WynnpelagoClient.client.isConnected()) {
                return;
            }
            WynnpelagoClient.client.disconnect();
            WynnpelagoClient.enabled = false;
        });
    }
}
