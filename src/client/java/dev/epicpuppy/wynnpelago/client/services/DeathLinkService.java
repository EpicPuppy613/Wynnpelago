package dev.epicpuppy.wynnpelago.client.services;

import com.wynntils.core.components.Handlers;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class DeathLinkService {
    private static int deathCooldown = 0;

    public DeathLinkService() {
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

    private void onTick(Minecraft client) {
        if (deathCooldown > 0) {
            deathCooldown--;
        }
    }
}
