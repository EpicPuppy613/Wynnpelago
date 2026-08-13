package dev.epicpuppy.wynnpelago.client.services;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.services.content.APType;
import dev.epicpuppy.wynnpelago.client.services.content.DataEntry;
import dev.epicpuppy.wynnpelago.client.services.content.DataType;
import dev.epicpuppy.wynnpelago.client.services.content.Location;
import dev.epicpuppy.wynnpelago.client.services.content.Region;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jspecify.annotations.NonNull;

public class ContentService implements ResourceManagerReloadListener {
    private final List<DataEntry> entries = new ArrayList<>();
    private final Map<String, Region> regions = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();

    public Region getRegion(String name) {
        return regions.getOrDefault(name, null);
    }

    public void unlockRegion(String name) {
        Region region = regions.getOrDefault(name, null);
        if (region == null) {
            Wynnpelago.LOGGER.warn("Could not unlock {}: region not found", name);
            return;
        }
        region.setUnlocked(true);
        updateAccessibility();
    }

    public void checkLocation(String name) {
        Location location = locations.getOrDefault(name, null);
        if (location == null) {
            Wynnpelago.LOGGER.warn("Could not check {}: location not found", name);
            return;
        }

        if (location.isCollected()) {
            return;
        }

        location.setCollected(true);
    }

    public void updateAccessibility() {
        updateRegionAccessibility();
        updateLocationAccessibility();
    }

    public void updateRegionAccessibility() {
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
                    queue.add(conn.getName());
                    break;
                }
            }
        }
        // Update region accessibility
        for (Region region : regions.values()) {
            region.setAccessible(accessible.contains(region.getName()));
        }
    }

    public void updateLocationAccessibility() {
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
        for (Location location : locations.values()) {
            // Validate non-location based requirements
            if (!accessible.contains(location.getName())) {
                location.setAccessible(false);
            } else {
                boolean gearreq = true;
                for (Location.GearRequirement req : location.getGearreqs()) {
                    if (!req.fufilled()) {
                        gearreq = false;
                    }
                }
                location.setAccessible(LevelUnlock.maxLevel >= location.getLevel() && gearreq);
            }
        }
    }

    public void populateGameState() {
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
        // Step 2: Iterate through all regions and update state
        for (Region region : regions.values()) {
            region.setEnabled(region.getLevel() <= maxLevel);
            region.setUnlocked(TerritoryUnlock.unlockedTerritories.contains(region.getName()));
        }
        // Step 3: Iterate through all locations and update state
        Set<Long> uncheckedIds = ArchipelagoClient.client.getLocationManager().getMissingLocations();
        for (Location location : locations.values()) {
            location.setCollected(!uncheckedIds.contains(location.getId())
                    || (ArchipelagoOptions.getGoalType() == ArchipelagoOptions.GoalType.DUNGEON
                            && Objects.equals(location.getName(), ArchipelagoOptions.getGoalDungeon()))
                    || (ArchipelagoOptions.getGoalType() == ArchipelagoOptions.GoalType.QUEST
                            && Objects.equals(location.getName(), ArchipelagoOptions.getGoalQuest())));
        }

        updateAccessibility();
    }

    private void loadData(ResourceManager manager) throws IOException {
        Identifier id = Identifier.fromNamespaceAndPath(Wynnpelago.MOD_ID, "wynncraft-data.csv");
        Optional<Resource> resource = manager.getResource(id);
        if (resource.isEmpty()) {
            throw new RuntimeException("Could not find data file");
        }
        entries.clear();
        entries.addAll(new CsvToBeanBuilder<DataEntry>(
                        new CSVReader(new InputStreamReader(resource.get().open())))
                .withType(DataEntry.class).build().parse().stream()
                        .filter(DataEntry::isReady)
                        .toList());
    }

    private void prepareContentModel() {
        regions.forEach((k, v) -> v.getConnections().clear());
        locations.forEach((k, v) -> {
            v.getRegions().clear();
            v.getPrereqs().clear();
            v.getDependents().clear();
        });
        regions.clear();
        locations.clear();
        // Register all regions
        entries.stream().filter(e -> e.getType() == DataType.REGION).forEach(entry -> {
            Region region = new Region(entry.getName(), entry.getLevel(), entry.getApType() == APType.DEFAULT);
            regions.put(region.getName(), region);
        });
        // Register all region connections
        entries.stream().filter(e -> e.getType() == DataType.REGION).forEach(entry -> {
            Region region = regions.get(entry.getName());
            for (String conn : entry.getRegions()) {
                Region other = regions.getOrDefault(conn, null);
                if (other != null) {
                    region.getConnections().add(other);
                } else {
                    Wynnpelago.LOGGER.warn("Could not connect {} to {}", conn, region.getName());
                }
            }
        });
        // Register all locations
        entries.stream().filter(e -> e.getApType() == APType.LOCATION).forEach(entry -> {
            List<Region> reqRegions = new ArrayList<>();
            for (String reqName : entry.getRegions()) {
                if (reqName.isBlank()) {
                    continue;
                }
                Region region = regions.getOrDefault(reqName, null);
                if (region == null) {
                    Wynnpelago.LOGGER.warn("Could not find region {} for {}", reqName, entry.getName());
                    continue;
                }
                reqRegions.add(region);
            }
            Location location =
                    new Location(entry.getName(), entry.getId(), entry.getLevel(), entry.getType(), reqRegions);
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
        });
    }

    @Override
    public void onResourceManagerReload(@NonNull ResourceManager resourceManager) {
        try {
            loadData(resourceManager);
            prepareContentModel();
            if (WynnpelagoClient.enabled) {
                populateGameState();
            }
        } catch (Exception e) {
            Wynnpelago.LOGGER.warn("Failed to load data file: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
