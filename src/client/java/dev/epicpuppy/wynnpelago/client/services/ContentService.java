package dev.epicpuppy.wynnpelago.client.services;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.compat.BackwardsFlags;
import dev.epicpuppy.wynnpelago.client.services.content.APType;
import dev.epicpuppy.wynnpelago.client.services.content.DataEntry;
import dev.epicpuppy.wynnpelago.client.services.content.DataType;
import dev.epicpuppy.wynnpelago.client.services.content.LevelRuleEntry;
import dev.epicpuppy.wynnpelago.client.services.content.LevelRuleType;
import dev.epicpuppy.wynnpelago.client.services.content.Location;
import dev.epicpuppy.wynnpelago.client.services.content.Region;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class ContentService {
    private static final Identifier FALLBACK_DATA_FILE =
            Identifier.fromNamespaceAndPath(Wynnpelago.MOD_ID, "data/0.4.5.csv");

    private static final List<LevelRuleEntry> levelRules = new ArrayList<>();
    private static final Set<Integer> levelRuleLevels = new HashSet<>();

    private static final List<DataEntry> entries = new ArrayList<>();
    private static final Map<String, Region> regions = new HashMap<>();
    private static final Map<String, Location> locations = new HashMap<>();

    @Getter
    private static final List<Location> regionless = new ArrayList<>();

    @Getter
    private static String goalObjective = "";

    /**
     * Maximum level based on region access alone
     */
    @Getter
    private static int maxLogicalLevel = 1;

    /**
     * Max level, capped by logical level
     */
    @Getter
    private static int effectiveMaxLevel = 1;

    /**
     * Max level regions that can be accessed
     */
    @Getter
    private static int regionAccessLevel = 1;

    @Getter
    private static int availableChecks = 0;

    @Getter
    private static int inLogicChecks = 0;

    @Getter
    private static int remainingChecks = 0;

    public static Region getRegion(String name) {
        return regions.getOrDefault(name, null);
    }

    public static void unlockRegion(String name) {
        Region region = regions.getOrDefault(name, null);
        if (region == null) {
            Wynnpelago.LOGGER.warn("Could not unlock {}: region not found", name);
            return;
        }
        region.setUnlocked(true);
        updateAccessibility();
    }

    public static void checkLocation(String name) {
        Location location = locations.getOrDefault(name, null);
        if (location == null) {
            Wynnpelago.LOGGER.warn("Could not check {}: location not found", name);
            return;
        }

        if (location.isCollected()) {
            return;
        }

        location.setCollected(true);
        updateLocationAccessibility();
    }

    public static void updateAccessibility() {
        updateLevelAccessibility();
        updateRegionAccessibility();
        updateLocationAccessibility();
    }

    public static void updateLocationAccessibility() {
        updateLevelAccessibility();

        Set<String> accessible = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        for (Location location : locations.values()) {
            if (location.getPrereqs().isEmpty()) {
                queue.add(location.getName());
            }
        }
        while (!queue.isEmpty()) {
            String name = queue.remove();
            Location location = locations.getOrDefault(name, null);
            if (location == null) {
                Wynnpelago.LOGGER.warn("Could not find location {}, skipping", name);
                continue;
            }
            boolean canAccess = true;
            for (Region region : location.getRegions()) {
                if (!region.isAccessible()) {
                    canAccess = false;
                    break;
                }
            }
            for (Region altRegion : location.getAltRegions()) {
                if (!altRegion.isUnlocked()) {
                    canAccess = false;
                    break;
                }
            }
            for (Location prereq : location.getPrereqs()) {
                if (!accessible.contains(prereq.getName())) {
                    canAccess = false;
                    break;
                }
            }
            if (canAccess) {
                accessible.add(name);
                for (Location dependent : location.getDependents()) {
                    if (!accessible.contains(dependent.getName())) {
                        queue.add(dependent.getName());
                    }
                }
            }
        }

        availableChecks = 0;
        inLogicChecks = 0;
        remainingChecks = 0;
        int level = LevelService.getLevel();
        for (Location location : locations.values()) {
            // Validate non-location based requirements
            if (!accessible.contains(location.getName())) {
                location.setAccessible(false);
                location.setAvailable(false);
            } else {
                boolean gearreq = true;
                for (Location.GearRequirement req : location.getGearreqs()) {
                    if (!req.fulfilled()) {
                        gearreq = false;
                    }
                }
                if (location.getType() == DataType.TERRITORY) {
                    location.setAccessible(regionAccessLevel >= location.getLevel());
                    location.setAvailable(regionAccessLevel >= location.getLevel());
                } else if (location.getType() == DataType.LEVEL) {
                    location.setAccessible(effectiveMaxLevel >= location.getLevel());
                    location.setAvailable(effectiveMaxLevel >= location.getLevel());
                } else {
                    location.setAccessible(effectiveMaxLevel >= location.getLevel() && gearreq);
                    location.setAvailable(level >= location.getLevel() && location.isAccessible());
                }
            }
            if (!location.isCollected()) {
                remainingChecks++;
                if (location.isAccessible()) {
                    inLogicChecks++;
                    if (location.isAvailable()) {
                        availableChecks++;
                    }
                }
            }
        }
    }

    public static void populateGameState() {
        // Step 1: Determine max level for the slot
        int maxLevel =
                switch (ArchipelagoOptions.getGoalType()) {
                    case LEVEL -> ArchipelagoOptions.getGoalLevel() - 1;
                    case DUNGEON -> {
                        Location location = locations.getOrDefault(ArchipelagoOptions.getGoalDungeon(), null);
                        if (location == null) {
                            throw new RuntimeException("Could not get dungeon info");
                        }
                        yield location.getLevel();
                    }
                    case QUEST -> {
                        Location location = locations.getOrDefault(ArchipelagoOptions.getGoalQuest(), null);
                        if (location == null) {
                            throw new RuntimeException("Could not get quest info");
                        }
                        yield location.getLevel();
                    }
                };
        // Step 2: Set goal objective
        goalObjective = switch (ArchipelagoOptions.getGoalType()) {
            case LEVEL -> "";
            case DUNGEON -> ArchipelagoOptions.getGoalDungeon();
            case QUEST -> ArchipelagoOptions.getGoalQuest();
        };
        // Step 3: Iterate through all regions and update state
        for (Region region : regions.values()) {
            region.setEnabled(region.getLevel() <= maxLevel);
            region.setUnlocked(TerritoryUnlock.unlockedTerritories.contains(region.getName()));
            region.setContainsGoal(false);
        }
        // Step 4: Iterate through all locations and update state
        Set<Long> uncheckedIds = ArchipelagoClient.client.getLocationManager().getMissingLocations();
        for (Location location : locations.values()) {
            location.setCollected(!uncheckedIds.contains(location.getId())
                    && !(ArchipelagoOptions.getGoalType() == ArchipelagoOptions.GoalType.DUNGEON
                            && Objects.equals(location.getName(), ArchipelagoOptions.getGoalDungeon()))
                    && !(ArchipelagoOptions.getGoalType() == ArchipelagoOptions.GoalType.QUEST
                            && Objects.equals(location.getName(), ArchipelagoOptions.getGoalQuest())));
            if (Objects.equals(location.getName(), goalObjective)) {
                for (Region region : location.getRegions()) {
                    region.setContainsGoal(true);
                }
            }
        }

        updateAccessibility();
    }

    public static void fullReloadData(ResourceManager manager) {
        try {
            loadLevelData(manager);
            String path = "data/" + ArchipelagoOptions.getWorldVersion() + ".csv";
            Wynnpelago.LOGGER.info("Loading content model with file: {}", path);
            loadData(manager, path);
            prepareContentModel();
        } catch (Exception e) {
            Wynnpelago.LOGGER.warn("Failed to load data file: {}", e.getMessage());
        }
    }

    private static void updateLevelAccessibility() {
        maxLogicalLevel = 121;

        List<GearUnlock.Type> gearTypes = new ArrayList<>();
        if (ArchipelagoOptions.getGearLockMode() == ArchipelagoOptions.GearLockMode.UNIFIED) {
            gearTypes.add(GearUnlock.Type.GEAR);
        } else if (ArchipelagoOptions.getGearLockMode() == ArchipelagoOptions.GearLockMode.FULL) {
            gearTypes.add(GearUnlock.Type.ARMOR);
            gearTypes.add(GearUnlock.Type.WEAPON);
        }

        for (int i = 2; i <= LevelUnlock.getMaxLevel(); i++) {
            if ((i - 1) % 5 == 0 && ArchipelagoOptions.isLogicalGearLevels()) {
                boolean hasAccess = true;

                for (GearUnlock.Type type : gearTypes) {
                    if (ArchipelagoOptions.isSingleGearTier()) {
                        hasAccess &= GearUnlock.getMaxLevel(type, GearUnlock.Rarity.ALL) >= i;
                    } else {
                        hasAccess &= GearUnlock.getMaxLevel(type, GearUnlock.Rarity.UNIQUE) >= i
                                || GearUnlock.getMaxLevel(type, GearUnlock.Rarity.RARE) >= i
                                || GearUnlock.getMaxLevel(type, GearUnlock.Rarity.LEGENDARY) >= i;
                    }
                }

                if (!hasAccess) {
                    maxLogicalLevel = i - 1;
                    break;
                }
            }

            if (!levelRuleLevels.contains(i)) {
                continue;
            }

            boolean failed = false;
            for (LevelRuleEntry rule : levelRules) {
                if (!levelRuleEnabled(rule.getType()) || rule.getLevel() != i) {
                    continue;
                }

                boolean valid = false;
                for (String region : rule.getRegions()) {
                    if (region.isBlank()) {
                        continue;
                    }

                    valid |= ContentService.getRegion(region).isAccessible();
                }

                for (String prereq : rule.getPrereqs()) {
                    if (prereq.isBlank()) {
                        continue;
                    }

                    Location location = locations.get(prereq);
                    valid |= location.isAccessible();
                }

                if (!valid) {
                    maxLogicalLevel = i - 1;
                    failed = true;
                    break;
                }
            }
            if (failed) {
                break;
            }
        }

        effectiveMaxLevel = Math.min(LevelUnlock.getMaxLevel(), maxLogicalLevel);
        regionAccessLevel = effectiveMaxLevel + ArchipelagoOptions.getEarlyTerritoryLevels();
    }

    private static boolean levelRuleEnabled(LevelRuleType type) {
        return switch (type) {
            case REGION -> ArchipelagoOptions.isLogicalLevels();
            case GRIND_SPOT -> ArchipelagoOptions.isLogicalGrindSpots();
            case MOUNT -> ArchipelagoOptions.isLogicalMounts();
            case null -> false;
        };
    }

    private static void updateRegionAccessibility() {
        if (!regions.containsKey("Ragni")) {
            // Region model can be assumed to be broken if Ragni does not exist
            Wynnpelago.LOGGER.error("Region model is incomplete");
            return;
        }
        Set<String> accessible = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add("Ragni");
        while (!queue.isEmpty()) {
            String name = queue.remove();
            Region region = regions.getOrDefault(name, null);
            if (region == null) {
                Wynnpelago.LOGGER.warn("Could not find region {}, skipping", name);
                continue;
            }
            accessible.add(name);
            for (Region conn : region.getConnections()) {
                if ((conn.isUnlocked() || conn.isDefaultUnlock())
                        && region.isEnabled()
                        && !accessible.contains(conn.getName())) {
                    if (BackwardsFlags.isRegionEntryLevel() && regionAccessLevel < region.getLevel()) {
                        continue;
                    }
                    queue.add(conn.getName());
                }
            }
        }
        // Update region accessibility
        for (Region region : regions.values()) {
            region.setAccessible(accessible.contains(region.getName()));
        }
    }

    private static void loadLevelData(ResourceManager manager) throws IOException {
        Identifier id = Identifier.fromNamespaceAndPath(Wynnpelago.MOD_ID, "data/levels.csv");
        Optional<Resource> resource = manager.getResource(id);
        if (resource.isEmpty()) {
            Wynnpelago.LOGGER.error("Level logic file missing");
            return;
        }

        levelRules.clear();
        levelRules.addAll(new CsvToBeanBuilder<LevelRuleEntry>(
                        new CSVReader(new InputStreamReader(resource.get().open())))
                .withType(LevelRuleEntry.class).build().parse().stream()
                        .sorted(Comparator.comparing(LevelRuleEntry::getLevel))
                        .toList());

        for (LevelRuleEntry entry : levelRules) {
            levelRuleLevels.add(entry.getLevel());
        }
    }

    private static void loadData(ResourceManager manager, String path) throws IOException {
        Identifier id = Identifier.fromNamespaceAndPath(Wynnpelago.MOD_ID, path);
        Optional<Resource> resource = manager.getResource(id);
        if (resource.isEmpty()) {
            Wynnpelago.LOGGER.warn("Failed to load versioned data file");
            resource = manager.getResource(FALLBACK_DATA_FILE);
            if (resource.isEmpty()) {
                throw new RuntimeException("Could not find load fallback data file");
            }
        }

        entries.clear();
        entries.addAll(new CsvToBeanBuilder<DataEntry>(
                        new CSVReader(new InputStreamReader(resource.get().open())))
                .withType(DataEntry.class).build().parse().stream()
                        .filter(DataEntry::isReady)
                        .toList());
    }

    private static void prepareContentModel() {
        regions.forEach((k, v) -> {
            v.getConnections().clear();
            v.getVisibleConnections().clear();
            v.getLocations().clear();
        });
        locations.forEach((k, v) -> {
            v.getRegions().clear();
            v.getAltRegions().clear();
            v.getPrereqs().clear();
            v.getDependents().clear();
            v.getGearreqs().clear();
        });
        regions.clear();
        locations.clear();
        regionless.clear();
        // Register all regions
        entries.stream()
                .filter(e -> e.getType() == DataType.REGION)
                .filter(e -> !e.getName().startsWith("*"))
                .forEach(entry -> {
                    Region region = new Region(entry.getName(), entry.getLevel(), entry.getApType() == APType.DEFAULT);
                    regions.put(region.getName(), region);
                });
        // Register all region connections
        entries.stream()
                .filter(e -> e.getType() == DataType.REGION)
                .filter(e -> !e.getName().startsWith("*"))
                .forEach(entry -> {
                    Region region = regions.get(entry.getName());
                    for (String conn : entry.getRegions()) {
                        Region other = regions.getOrDefault(conn, null);
                        if (other != null) {
                            region.addConnection(other);
                        } else {
                            Wynnpelago.LOGGER.warn("Could not connect {} to {}", conn, region.getName());
                        }
                    }
                });
        // Register all locations
        entries.stream().filter(e -> e.getApType() == APType.LOCATION).forEach(entry -> {
            List<Region> reqRegions = new ArrayList<>();
            List<Region> altRegions = new ArrayList<>();
            boolean hasRegion = true;
            for (String reqName : entry.getRegions()) {
                if (reqName.isBlank()) {
                    hasRegion = false;
                    continue;
                }
                if (reqName.startsWith("*")) {
                    Region altRegion = regions.getOrDefault(reqName.substring(1), null);
                    if (altRegion == null) {
                        Wynnpelago.LOGGER.warn("Could not find alt region {} for {}", reqName, entry.getName());
                        continue;
                    }
                    altRegions.add(altRegion);
                    continue;
                }
                Region region = regions.getOrDefault(reqName, null);
                if (region == null) {
                    Wynnpelago.LOGGER.warn("Could not find region {} for {}", reqName, entry.getName());
                    continue;
                }
                reqRegions.add(region);
            }
            Location location = new Location(
                    entry.getName(), entry.getId(), entry.getLevel(), entry.getType(), reqRegions, altRegions);
            for (String gearReq : entry.getGearreqs()) {
                if (gearReq.isBlank()) {
                    continue;
                }
                String[] parts = gearReq.split(" ");
                Location.GearRequirement req = new Location.GearRequirement(
                        Integer.parseInt(parts[2]),
                        GearUnlock.Rarity.fromDataName(parts[0]),
                        GearUnlock.Type.fromDataName(parts[1]));
                location.getGearreqs().add(req);
            }
            locations.put(location.getName(), location);
            if (!hasRegion) {
                regionless.add(location);
            }
        });
        // Register all location prerequisites and dependents
        entries.stream().filter(e -> e.getApType() == APType.LOCATION).forEach(entry -> {
            Location location = locations.get(entry.getName());
            for (String prereq : entry.getPrereqs()) {
                if (!prereq.isBlank()) {
                    Location req = locations.getOrDefault(prereq, null);
                    if (req == null) {
                        Wynnpelago.LOGGER.warn("Could not find prereq {} for {}", prereq, entry.getName());
                        continue;
                    }
                    location.getPrereqs().add(req);
                    req.getDependents().add(location);
                }
            }
            for (Region region : location.getRegions()) {
                region.getLocations().add(location);
            }
            for (Region altRegion : location.getAltRegions()) {
                altRegion.getLocations().add(location);
            }
        });
    }
}
