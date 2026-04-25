package dev.epicpuppy.wynnpelago.client.archipelago;

import com.google.gson.annotations.SerializedName;

public record SlotData(
        @SerializedName("goal_level") int goalLevel,
        @SerializedName("level_increment") int levelIncrement,
        @SerializedName("trap_duration") int trapSeconds,
        @SerializedName("locked_region_enforcement") int lockedRegionEnforcement,
        @SerializedName("locked_region_countdown") int lockedRegionCountdown) {}
