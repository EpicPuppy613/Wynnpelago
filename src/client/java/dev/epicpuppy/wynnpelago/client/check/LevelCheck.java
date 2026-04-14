package dev.epicpuppy.wynnpelago.client.check;

import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class LevelCheck {
    public static int highestLevel = 1;

    public LevelCheck() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(Minecraft client) {
        if (!WynnpelagoClient.enabled) return;
        int level = Models.CombatXp.getCombatLevel().current();
        if (level > highestLevel) {
            for (int i = highestLevel; i < level; i++) {
                Wynnpelago.LOGGER.info("Level Up: {}", i + 1);
            }
            highestLevel = level;
        }
    }
}
