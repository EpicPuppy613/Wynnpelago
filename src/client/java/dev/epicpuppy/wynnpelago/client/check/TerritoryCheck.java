package dev.epicpuppy.wynnpelago.client.check;

import com.wynntils.core.components.Models;
import com.wynntils.models.territories.profile.TerritoryProfile;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class TerritoryCheck {
    private static String currentTerritory = "";
    private static final Set<String> visitedTerritories = new HashSet<>();

    public static synchronized void resetVisited() {
        visitedTerritories.clear();
    }

    public TerritoryCheck() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(Minecraft client) {
        if (client.player == null || !WynnpelagoClient.enabled) return;
        TerritoryProfile territory = Models.Territory.getTerritoryProfileForPosition(client.player.position());
        if (territory == null || Objects.equals(territory.getName(), currentTerritory)) return;
        currentTerritory = territory.getName();
        if (!TerritoryUnlock.unlockedTerritories.contains(currentTerritory)
                || visitedTerritories.contains(currentTerritory)) return;
        visitedTerritories.add(currentTerritory);
        WynnpelagoClient.sendCheck(currentTerritory);
        Wynnpelago.LOGGER.info("Territory: {}", currentTerritory);
    }
}
