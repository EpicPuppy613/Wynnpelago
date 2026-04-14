package dev.epicpuppy.wynnpelago.client.unlock;

import com.wynntils.core.components.Models;
import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegionUnlock {
    private static final List<String> RESPAWN_TERRITORIES = List.of(
            "Ragni",
            "Alekin",
            "Detlas",
            "Tempo Town",
            "Maltic",
            "Ternaves",
            "Elkurn",
            "Nemract",
            "Bremminglar",
            "Selchar"
    );

    public static Set<String> unlockedTerritories;
    private int cooldownTicks;

    public RegionUnlock() {
        unlockedTerritories = new HashSet<>();
        unlockedTerritories.addAll(RESPAWN_TERRITORIES);

        cooldownTicks = 200;

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(Minecraft client) {
        if (client.player == null || --cooldownTicks > 0 || !WynnpelagoClient.enabled) return;
        TerritoryProfile territory = Models.Territory.getTerritoryProfileForPosition(client.player.position());
        if (territory == null) return;
        if (!unlockedTerritories.contains(territory.getName())) {
            McUtils.sendChat("/kill");
            cooldownTicks = 200;
        }
    }
}
