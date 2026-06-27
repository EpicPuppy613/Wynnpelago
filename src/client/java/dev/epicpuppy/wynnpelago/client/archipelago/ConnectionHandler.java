package dev.epicpuppy.wynnpelago.client.archipelago;

import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.check.ContentCheck;
import dev.epicpuppy.wynnpelago.client.check.TerritoryCheck;
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
            LevelUnlock.resetMaxLevel();
            GearUnlock.resetMaxLevels();
            TerritoryUnlock.resetUnlocked();
            TerritoryCheck.resetVisited();
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("Connected to " + WynnpelagoClient.client.getConnectedAddress())
                            .withStyle(ChatFormatting.GREEN)));
            WynnpelagoClient.enabled = true;
            WynnpelagoClient.sendQueuedChecks();
            ArchipelagoOptions.loadSlotOptions(event.getSlotData(SlotData.class));
            TrapService.resetInitialCooldown();
        } else {
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("Connection failed").withStyle(ChatFormatting.RED)));
        }
    }
}
