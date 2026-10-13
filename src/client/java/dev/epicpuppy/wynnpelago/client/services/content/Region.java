package dev.epicpuppy.wynnpelago.client.services.content;

import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.services.LevelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final List<Region> visibleConnections = new ArrayList<>();
    private final List<Location> locations = new ArrayList<>();

    @Setter
    private AccessRule accessRule = null;

    @Setter
    private boolean enabled = false;

    @Setter
    private boolean accessible = false;

    @Setter
    private boolean unlocked = false;

    @Setter
    private boolean containsGoal = false;

    public void addConnection(Region conn) {
        connections.add(conn);
        if (Objects.equals(name, "Selchar") || Objects.equals(conn.getName(), "Selchar")) {
            return;
        }
        visibleConnections.add(conn);
    }

    public RegionState getState() {
        if (!enabled) {
            return RegionState.DISABLED;
        }
        if (!unlocked) {
            return RegionState.LOCKED;
        }
        if (!accessible) {
            return RegionState.INACCESSIBLE;
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
                    if (level >= location.getLevel()
                            || (location.getType() == DataType.TERRITORY
                                    && level >= location.getLevel() - ArchipelagoOptions.getEarlyTerritoryLevels())) {
                        availableCheck = true;
                    }
                }
            }
        }
        if (availableCheck) {
            return RegionState.HAS_AVAILABLE;
        }
        if (inLogicCheck) {
            return RegionState.HAS_IN_LOGIC;
        }
        if (allChecks) {
            return RegionState.COMPLETE;
        }
        return RegionState.ACCESSIBLE;
    }

    public record AccessRule(int level, List<Location> prereqs) {}
}
