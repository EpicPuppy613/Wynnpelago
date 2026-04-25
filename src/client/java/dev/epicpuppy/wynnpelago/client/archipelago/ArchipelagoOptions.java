package dev.epicpuppy.wynnpelago.client.archipelago;

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
        for (RegionEnforcement value : RegionEnforcement.values()) {
            if (value.id == data.lockedRegionEnforcement()) {
                lockedRegionEnforcement = value;
                break;
            }
        }
        lockedRegionCountdown = data.lockedRegionCountdown();
    }

    public enum RegionEnforcement {
        KILL(0),
        COUNTDOWN(1),
        LENIENT(2);

        public final int id;

        RegionEnforcement(int id) {
            this.id = id;
        }
    }
}
