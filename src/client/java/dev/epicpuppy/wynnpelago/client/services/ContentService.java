package dev.epicpuppy.wynnpelago.client.services;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.services.content.APType;
import dev.epicpuppy.wynnpelago.client.services.content.DataEntry;
import dev.epicpuppy.wynnpelago.client.services.content.DataType;
import dev.epicpuppy.wynnpelago.client.services.content.Location;
import dev.epicpuppy.wynnpelago.client.services.content.Region;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public void unlockRegion(String name, boolean suppressUpdate) {
        Region region = regions.getOrDefault(name, null);
        if (region == null) {
            Wynnpelago.LOGGER.warn("Could not unlock {}: region not found", name);
            return;
        }
        region.setUnlocked(true);
        if (!suppressUpdate) {
            updateRegionAccessibility();
        }
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
        entries.stream()
                .filter(e -> e.getType() == DataType.REGION)
                .peek(entry -> {
                    // Register all regions
                    Region region = new Region(entry.getName(), entry.getLevel(), entry.getApType() == APType.DEFAULT);
                    regions.put(region.getName(), region);
                })
                .forEach(entry -> {
                    // Register all region connections
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
        entries.stream()
                .filter(e -> e.getApType() == APType.LOCATION)
                .peek(entry -> {
                    // Register all locations
                    List<Region> reqRegions = new ArrayList<>();
                    for (String reqName : entry.getRegions()) {
                        Region region = regions.getOrDefault(reqName, null);
                        if (region == null) {
                            Wynnpelago.LOGGER.warn("Could not find region {} for {}", reqName, entry.getName());
                        }
                        reqRegions.add(region);
                    }
                    Location location = new Location(entry.getName(), entry.getLevel(), entry.getType(), reqRegions);
                    locations.put(location.getName(), location);
                })
                .forEach(entry -> {
                    // Register all location prerequisites and dependents
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
                });
    }

    @Override
    public void onResourceManagerReload(@NonNull ResourceManager resourceManager) {
        try {
            loadData(resourceManager);
            prepareContentModel();
        } catch (Exception e) {
            Wynnpelago.LOGGER.warn("Failed to load data file: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
