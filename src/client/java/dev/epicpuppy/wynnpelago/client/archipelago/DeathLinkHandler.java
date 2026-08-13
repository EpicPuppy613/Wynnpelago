package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.services.DeathLinkService;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.DeathLinkEvent;
import net.minecraft.network.chat.Component;

public class DeathLinkHandler {
    @ArchipelagoEventListener
    public static void onDeathLink(DeathLinkEvent event) {
        if (!ArchipelagoOptions.isDeathLink()) {
            return;
        }
        DeathLinkService.triggerDeath();
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getAPPrefix().append(Component.literal(event.cause)));
    }
}
