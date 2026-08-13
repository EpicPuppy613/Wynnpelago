package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.services.TrapService;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ReceiveItemEvent;

public class ReceiveItemHandler {
    @ArchipelagoEventListener
    public static void onReceiveItem(ReceiveItemEvent event) {
        String name = event.getItemName();
        if (name.startsWith("Region: ")) {
            WynnpelagoClient.unlockTerritory(name.substring(8));
            return;
        }
        if (name.equals("Progressive Max Level")) {
            LevelUnlock.increaseMaxLevel();
            WynnpelagoClient.getContentService().updateLocationAccessibility();
            return;
        }
        if (name.endsWith("Trap")) {
            TrapService.recieveTrap(name);
            return;
        }
        if (name.startsWith("Progressive")) {
            GearUnlock.processIncrease(name);
            WynnpelagoClient.getContentService().updateLocationAccessibility();
        }
    }
}
