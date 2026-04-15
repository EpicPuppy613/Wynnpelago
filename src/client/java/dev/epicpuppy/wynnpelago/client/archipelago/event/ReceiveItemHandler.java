package dev.epicpuppy.wynnpelago.client.archipelago.event;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ReceiveItemEvent;
import net.minecraft.network.chat.Component;

public class ReceiveItemHandler {
    @ArchipelagoEventListener
    public static void onReceiveItem(ReceiveItemEvent event) {
        String name = event.getItemName();
        WynnpelagoClient.sendClientMessage(Component.literal("You received " + name));
        if (name.startsWith("Region: ")) {
            TerritoryUnlock.unlockedTerritories.add(name.substring(8));
        }
    }
}
