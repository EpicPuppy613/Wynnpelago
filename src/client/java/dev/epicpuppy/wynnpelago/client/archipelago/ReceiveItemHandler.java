package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ReceiveItemEvent;

public class ReceiveItemHandler {
    @ArchipelagoEventListener
    public static void onReceiveItem(ReceiveItemEvent event) {
        String name = event.getItemName();
        if (name.startsWith("Region: ")) {
            TerritoryUnlock.unlockTerritory(name.substring(8));
            return;
        }
        if (name.equals("Progressive Max Level")) {
            LevelUnlock.increaseMaxLevel();
            return;
        }
        if (name.endsWith("Trap")) {
            TrapProvider.recieveTrap(name);
            return;
        }
        if (name.startsWith("Progressive")) {
            GearUnlock.processIncrease(name);
        }
    }
}
