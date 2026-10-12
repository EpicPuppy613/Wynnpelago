package dev.epicpuppy.wynnpelago.client.archipelago;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ArchipelagoOptions {
    @Getter
    private static String worldVersion = "";

    // -- Goal --

    @Getter
    private static GoalType goalType = GoalType.LEVEL;

    @Getter
    private static int goalLevel = 40;

    @Getter
    private static String goalDungeon = "";

    @Getter
    private static String goalQuest = "";

    @Getter
    private static int extraContentLevels = 0;

    // -- Region Lock --

    @Getter
    private static RegionLockMode regionLockMode = RegionLockMode.ZONES;

    @Getter
    private static RegionEnforcement lockedRegionEnforcement = RegionEnforcement.COUNTDOWN;

    @Getter
    private static int lockedRegionCountdown = 3;

    // -- Levels & Gear Lock --

    @Getter
    private static int levelIncrement = 1;

    @Getter
    private static GearLockMode gearLockMode = GearLockMode.OFF;

    @Getter
    private static boolean singleGearTier = false;

    @Getter
    private static int gearLevelIncrement = 5;

    // -- Checks --

    @Getter
    private static boolean questChecks = true;

    @Getter
    private static boolean miniQuestChecks = true;

    @Getter
    private static boolean caveChecks = true;

    @Getter
    private static boolean dungeonChecks = true;

    @Getter
    private static boolean corruptedDungeonChecks = false;

    @Getter
    private static boolean levelChecks = true;

    @Getter
    private static boolean territoryChecks = true;

    @Getter
    private static boolean secretChecks = true;

    @Getter
    private static boolean bossChecks = true;

    @Getter
    private static boolean raidChecks = false;

    @Getter
    private static boolean worldEventChecks = false;

    @Getter
    private static boolean lootrunChecks = false;

    // -- QOL --

    @Getter
    private static int earlyTerritoryLevels = 5;

    @Getter
    private static boolean logicalLevels = true;

    @Getter
    private static boolean logicalGearLevels = false;

    @Getter
    private static boolean logicalGrindSpots = false;

    @Getter
    private static boolean logicalMounts = false;

    @Getter
    private static boolean dungeonQuests = true;

    // -- Traps --

    @Getter
    private static int trapSeconds = 15;

    @Getter
    private static boolean deathLink = false;

    public static void loadSlotOptions(SlotData data) {
        worldVersion = data.worldVersion();

        // Goal
        goalType = GoalType.fromId(data.goalType());
        goalLevel = data.goalLevel();
        goalDungeon = data.goalDungeon();
        goalQuest = data.goalQuest();
        extraContentLevels = data.extraContentLevels();

        // Region Lock
        regionLockMode = RegionLockMode.fromId(data.regionLockMode());
        lockedRegionEnforcement = RegionEnforcement.fromId(data.lockedRegionEnforcement());
        lockedRegionCountdown = data.lockedRegionCountdown();

        // Levels & Gear Lock
        levelIncrement = data.levelIncrement();
        gearLockMode = GearLockMode.fromId(data.gearLockMode());
        singleGearTier = data.singleGearRarity() == 1;
        gearLevelIncrement = data.gearLevelIncrement();

        // Checks
        questChecks = data.questChecks() == 1;
        miniQuestChecks = data.miniQuestChecks() == 1;
        caveChecks = data.caveChecks() == 1;
        dungeonChecks = data.dungeonChecks() == 1;
        corruptedDungeonChecks = data.corruptedDungeonChecks() == 1;
        levelChecks = data.levelChecks() == 1;
        territoryChecks = data.territoryChecks() == 1;
        secretChecks = data.secretChecks() == 1;
        bossChecks = data.bossChecks() == 1;
        raidChecks = data.raidChecks() == 1;
        worldEventChecks = data.worldEventChecks() == 1;
        lootrunChecks = data.lootrunChecks() == 1;

        // QOL
        earlyTerritoryLevels = data.earlyTerritoryLevels();
        logicalLevels = data.logicalLevels() == 1;
        logicalGearLevels = data.logicalGearLevels() == 1;
        logicalGrindSpots = data.logicalGrindSpots() == 1;
        logicalMounts = data.logicalMounts() == 1;
        dungeonQuests = data.dungeonQuests() == 1;

        // Traps
        trapSeconds = data.trapSeconds();

        deathLink = data.deathLink() == 1;
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
    public enum RegionLockMode {
        FULL(0),
        ZONES(1),
        AREAS(2),
        OFF(3);

        private final int id;

        public static RegionLockMode fromId(int id) {
            for (RegionLockMode value : values()) {
                if (value.id == id) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unknown region lock mode id: " + id);
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
