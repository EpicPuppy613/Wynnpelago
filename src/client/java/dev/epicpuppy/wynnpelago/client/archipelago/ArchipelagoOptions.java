package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import lombok.Getter;

public class ArchipelagoOptions {
    @Getter
    private static int goalLevel = 40;

    @Getter
    private static int levelIncrement = 1;

    @Getter
    private static int trapSeconds = 5;

    @Getter
    private static RegionEnforcement lockedRegionEnforcement = RegionEnforcement.COUNTDOWN;

    @Getter
    private static int lockedRegionCountdown = 3;

    public static void loadSlotOptions(SlotData data) {
        goalLevel = data.goalLevel();
        levelIncrement = data.levelIncrement();
        trapSeconds = data.trapSeconds();
        lockedRegionEnforcement =
                RegionEnforcement.valueOf(data.lockedRegionEnforcement().toUpperCase());
        lockedRegionCountdown = data.lockedRegionCountdown();
        Wynnpelago.LOGGER.info(
                "Goal: {} (+{}), Trap: {}, LRE: {}, LRC: {}",
                goalLevel,
                levelIncrement,
                trapSeconds,
                lockedRegionEnforcement.name(),
                lockedRegionCountdown);
    }

    public enum RegionEnforcement {
        KILL,
        COUNTDOWN,
        LENIENT
    }
}
