package dev.epicpuppy.wynnpelago.client.check;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.providers.LevelProvider;

public class LevelCheck {
    public LevelCheck() {
        LevelProvider.LEVEL_UP_EVENT.register(this::onLevelUp);
    }

    private void onLevelUp(int prevLevel, int newLevel) {
        for (int i = prevLevel + 1; i <= newLevel; i++) {
            Wynnpelago.LOGGER.info("Level Up: {}", i);
            WynnpelagoClient.sendCheck(String.format("Level Up: %d", i));
        }
    }
}