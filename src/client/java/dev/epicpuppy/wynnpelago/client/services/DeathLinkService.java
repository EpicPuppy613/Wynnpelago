package dev.epicpuppy.wynnpelago.client.services;

import com.wynntils.core.components.Handlers;
import com.wynntils.models.character.event.CharacterDeathEvent;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;

public class DeathLinkService {
    private static int deathCooldown = 0;

    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public static void triggerDeath() {
        if (deathCooldown > 0) {
            return;
        }
        Handlers.Chat.queueChatCommand("kill");
        deathCooldown = 200;
    }

    public static void sendDeath() {
        if (deathCooldown > 0) {
            return;
        }
        WynnpelagoClient.client.sendDeathlink(
                WynnpelagoClient.client.getMyName(), WynnpelagoClient.client.getMyName() + " died");
        deathCooldown = 200;
    }

    @SubscribeEvent
    public void onDeath(CharacterDeathEvent event) {
        if (WynnpelagoClient.enabled && ArchipelagoOptions.isDeathLink()) {
            sendDeath();
        }
    }

    private void onTick(Minecraft client) {
        if (deathCooldown > 0) {
            deathCooldown--;
        }
    }
}
