package dev.epicpuppy.wynnpelago.client.check;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.services.LevelService;

public class LevelCheck {
    public static void syncLevel() {
        for (int i = 1; i <= LevelService.getLevel(); i++) {
            WynnpelagoClient.sendCheck(String.format("Level Up: %d", i));
        }
    }

    public LevelCheck() {
        LevelService.LEVEL_UP_EVENT.register(this::onLevelUp);
    }

    private void onLevelUp(int level) {
        WynnpelagoClient.sendCheck(String.format("Level Up: %d", level));
    }
}
