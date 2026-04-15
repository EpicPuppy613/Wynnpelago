package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ReceiveItemEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ReceiveItemHandler {
    @ArchipelagoEventListener
    public static void onReceiveItem(ReceiveItemEvent event) {
        String name = event.getItemName();
        if (name.startsWith("Region: ")) {
            TerritoryUnlock.unlockedTerritories.add(name.substring(8));
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("You unlocked "))
                    .append(Component.literal(name.substring(8)).withStyle(ChatFormatting.AQUA)));
        }
    }
}
