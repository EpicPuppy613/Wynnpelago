package dev.epicpuppy.wynnpelago.client.archipelago;

import com.google.gson.annotations.SerializedName;

public record SlotData(
        @SerializedName("goal_type") int goalType,
        @SerializedName("goal_level") int goalLevel,
        @SerializedName("goal_dungeon") String goalDungeon,
        @SerializedName("goal_quest") String goalQuest,

        @SerializedName("locked_region_enforcement") int lockedRegionEnforcement,
        @SerializedName("locked_region_countdown") int lockedRegionCountdown,

        @SerializedName("level_increment") int levelIncrement,
        @SerializedName("gear_lock_mode") int gearLockMode,
        @SerializedName("single_gear_rarity") int singleGearRarity,
        @SerializedName("gear_level_increment") int gearLevelIncrement,

        @SerializedName("quest_checks") int questChecks,
        @SerializedName("mini_quest_checks") int miniQuestChecks,
        @SerializedName("cave_checks") int caveChecks,
        @SerializedName("dungeon_checks") int dungeonChecks,
        @SerializedName("level_checks") int levelChecks,
        @SerializedName("logical_levels") int logicalLevels,
        @SerializedName("territory_checks") int territoryChecks,
        @SerializedName("early_territory_levels") int earlyTerritoryLevels,

        @SerializedName("trap_duration") int trapSeconds,

        @SerializedName("death_link") int deathLink) {}
