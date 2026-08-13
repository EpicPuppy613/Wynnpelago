package dev.epicpuppy.wynnpelago.client.archipelago;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ArchipelagoOptions {
    @Getter
    private static GoalType goalType = GoalType.LEVEL;

    @Getter
    private static int goalLevel = 40;

    @Getter
    private static String goalDungeon = "";

    @Getter
    private static String goalQuest = "";

    @Getter
    private static RegionEnforcement lockedRegionEnforcement = RegionEnforcement.COUNTDOWN;

    @Getter
    private static int lockedRegionCountdown = 3;

    @Getter
    private static int levelIncrement = 1;

    @Getter
    private static GearLockMode gearLockMode = GearLockMode.OFF;

    @Getter
    private static boolean singleGearTier = false;

    @Getter
    private static int gearLevelIncrement = 5;

    @Getter
    private static boolean questChecks = true;

    @Getter
    private static boolean miniQuestChecks = true;

    @Getter
    private static boolean caveChecks = true;

    @Getter
    private static boolean dungeonChecks = true;

    @Getter
    private static boolean levelChecks = true;

    @Getter
    private static boolean logicalLevels = true;

    @Getter
    private static boolean territoryChecks = true;

    @Getter
    private static int earlyTerritoryLevels = 5;

    @Getter
    private static int trapSeconds = 15;

    public static void loadSlotOptions(SlotData data) {
        goalType = GoalType.fromId(data.goalType());
        goalLevel = data.goalLevel();
        goalDungeon = data.goalDungeon();
        goalQuest = data.goalQuest();

        lockedRegionEnforcement = RegionEnforcement.fromId(data.lockedRegionEnforcement());
        lockedRegionCountdown = data.lockedRegionCountdown();

        levelIncrement = data.levelIncrement();
        gearLockMode = GearLockMode.fromId(data.gearLockMode());
        singleGearTier = data.singleGearRarity() == 1;
        gearLevelIncrement = data.gearLevelIncrement();

        questChecks = data.questChecks() == 1;
        miniQuestChecks = data.miniQuestChecks() == 1;
        caveChecks = data.caveChecks() == 1;
        dungeonChecks = data.dungeonChecks() == 1;
        levelChecks = data.levelChecks() == 1;
        logicalLevels = data.logicalLevels() == 1;
        territoryChecks = data.territoryChecks() == 1;
        earlyTerritoryLevels = data.earlyTerritoryLevels();

        trapSeconds = data.trapSeconds();
    }

    @RequiredArgsConstructor
    public enum GoalType {
        LEVEL(0),
        DUNGEON(1),
        QUEST(2);

        private final int id;

        public static GoalType fromId(int id) {
            for (GoalType value : values()) {
                if (value.id == id) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unknown goal type id: " + id);
        }
    }

    @RequiredArgsConstructor
    public enum RegionEnforcement {
        KILL(0),
        COUNTDOWN(1),
        LENIENT(2);

        private final int id;

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

        private final int id;

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
