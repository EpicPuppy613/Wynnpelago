package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.check.TerritoryCheck;
import dev.epicpuppy.wynnpelago.client.compat.BackwardsFlags;
import dev.epicpuppy.wynnpelago.client.services.TrapService;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ConnectionResultEvent;
import io.github.archipelagomw.network.ConnectionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ConnectionHandler {
    @ArchipelagoEventListener
    public static void onConnected(ConnectionResultEvent event) {
        if (event.getResult() == ConnectionResult.Success) {
            WynnpelagoClient.connect();
            LevelUnlock.resetMaxLevel();
            GearUnlock.resetMaxLevels();
            TerritoryUnlock.resetUnlocked();
            TerritoryCheck.resetVisited();
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("Connected to " + WynnpelagoClient.client.getConnectedAddress())
                            .withStyle(ChatFormatting.GREEN)));
            ArchipelagoOptions.loadSlotOptions(event.getSlotData(SlotData.class));
            TrapService.resetInitialCooldown();

            if (ArchipelagoOptions.isDeathLink()) {
                WynnpelagoClient.client.setDeathLinkEnabled(true);
            }
        } else {
            String message =
                    switch (event.getResult()) {
                        case InvalidGame -> "Invalid game for slot";
                        case InvalidSlot -> "Invalid slot";
                        case InvalidPassword -> "Invalid password";
                        case SlotAlreadyTaken -> "Slot already taken";
                        case IncompatibleVersion -> "Incompatible archipelago version";
                        case null, default -> "Unknown error";
                    };
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("Connection failed: " + message).withStyle(ChatFormatting.RED)));
            WynnpelagoClient.client.disconnect();
        }
    }
}
