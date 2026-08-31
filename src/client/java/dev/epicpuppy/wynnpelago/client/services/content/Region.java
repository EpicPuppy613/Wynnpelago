package dev.epicpuppy.wynnpelago.client.services.content;

import com.wynntils.utils.colors.CustomColor;
import java.util.ArrayList;
import java.util.List;

import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.services.LevelService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class Region {
    private final String name;
    private final int level;
    private final boolean defaultUnlock;
    private final List<Region> connections = new ArrayList<>();
    private final List<Location> locations = new ArrayList<>();

    @Setter
    private boolean enabled = false;

    @Setter
    private boolean accessible = false;

    @Setter
    private boolean unlocked = false;

    @Setter
    private boolean containsGoal = false;

    public State getState() {
        if (!enabled) {
            return State.DISABLED;
        }
        if (!unlocked) {
            return State.LOCKED;
        }
        if (!accessible) {
            return State.INACCESSIBLE;
        }
        boolean inLogicCheck = false;
        boolean availableCheck = false;
        boolean allChecks = true;
        int level = LevelService.getLevel();
        for (Location location : locations) {
            if (!location.isCollected()) {
                allChecks = false;
                if (location.isAccessible()) {
                    inLogicCheck = true;
                    if (level >= location.getLevel() || (location.getType() == DataType.TERRITORY && level >= location.getLevel() - ArchipelagoOptions.getEarlyTerritoryLevels())) {
                        availableCheck = true;
                    }
                }
            }
        }
        if (availableCheck) {
            return State.HAS_AVAILABLE;
        }
        if (inLogicCheck) {
            return State.HAS_IN_LOGIC;
        }
        if (allChecks) {
            return State.COMPLETE;
        }
        return State.ACCESSIBLE;
    }

    @RequiredArgsConstructor
    @Getter
    public enum State {
        DISABLED(CustomColor.fromInt(0x333333)),
        LOCKED(CustomColor.fromInt(0x888888)),
        INACCESSIBLE(CustomColor.fromInt(0xff3333)),
        ACCESSIBLE(CustomColor.fromInt(0xff33ff)),
        HAS_IN_LOGIC(CustomColor.fromInt(0xffff33)),
        HAS_AVAILABLE(CustomColor.fromInt(0x33ff33)),
        COMPLETE(CustomColor.fromInt(0x33ffff));

        private final CustomColor color;
    }
}
