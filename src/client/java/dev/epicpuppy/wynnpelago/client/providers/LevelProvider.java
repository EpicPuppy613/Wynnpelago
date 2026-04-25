package dev.epicpuppy.wynnpelago.client.providers;

import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

public class LevelProvider {
    public static final Event<LevelUp> LEVEL_UP_EVENT =
            EventFactory.createArrayBacked(LevelUp.class, callbacks -> (prevLevel, newLevel) -> {
                for (LevelUp callback : callbacks) {
                    callback.onLevel(prevLevel, newLevel);
                }
            });
    public static int highestLevel = 1;
    public static boolean resetLevel = false;

    public LevelProvider() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public static void resetLevel() {
        resetLevel = true;
    }

    public static int getLevel() {
        return Models.CombatXp.getCombatLevel().current();
    }

    public void onTick(Minecraft client) {
        if (!WynnpelagoClient.enabled) return;
        if (resetLevel) {
            highestLevel = 1;
            resetLevel = false;
            return;
        }
        int level = Models.CombatXp.getCombatLevel().current();
        if (level > highestLevel) {
            LEVEL_UP_EVENT.invoker().onLevel(highestLevel, level);
            highestLevel = level;
        }
    }

    @FunctionalInterface
    public interface LevelUp {
        void onLevel(int prevLevel, int newLevel);
    }
}
