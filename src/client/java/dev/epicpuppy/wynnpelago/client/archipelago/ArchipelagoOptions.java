package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    @Getter
    private static GearLockMode gearLockMode = GearLockMode.OFF;

    @Getter
    private static boolean singleGearTier = false;

    @Getter
    private static int gearLevelIncrement = 5;

    public static void loadSlotOptions(SlotData data) {
        goalLevel = data.goalLevel();
        levelIncrement = data.levelIncrement();
        trapSeconds = data.trapSeconds();
        lockedRegionEnforcement = RegionEnforcement.fromId(data.lockedRegionEnforcement());
        lockedRegionCountdown = data.lockedRegionCountdown();
        gearLockMode = GearLockMode.fromId(data.gearLockMode());
        singleGearTier = data.singleGearRarity() == 1;
        gearLevelIncrement = data.gearLevelIncrement();
        Wynnpelago.LOGGER.info(
                "Loaded slot options: Level {} (+{}) | Trap {}s | Region: {} ({}s) | Gear Lock: {}, {} (+{})",
                goalLevel,
                levelIncrement,
                trapSeconds,
                lockedRegionEnforcement.name(),
                lockedRegionCountdown,
                gearLockMode.name(),
                singleGearTier,
                gearLevelIncrement);
    }

    @RequiredArgsConstructor
    public enum RegionEnforcement {
        KILL(0),
        COUNTDOWN(1),
        LENIENT(2);

        public final int id;

        public static RegionEnforcement fromId(int id) {
            for (RegionEnforcement value : values()) {
                if (value.id == id) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unknown enforcement id: " + id);
        }
    }

    @RequiredArgsConstructor
    public enum GearLockMode {
        FULL(0),
        UNIFIED(1),
        OFF(2);

        public final int id;

        public static GearLockMode fromId(int id) {
            for (GearLockMode value : values()) {
                if (value.id == id) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unknown gear class id: " + id);
        }
    }
}
