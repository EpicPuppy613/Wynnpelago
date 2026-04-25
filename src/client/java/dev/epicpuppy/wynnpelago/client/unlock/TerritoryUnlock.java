package dev.epicpuppy.wynnpelago.client.unlock;

import com.wynntils.core.components.Models;
import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TerritoryUnlock {
    public static final List<String> RESPAWN_TERRITORIES = List.of(
            "Ragni",
            "Alekin",
            "Maltic",
            "Detlas",
            "Ternaves",
            "Nemract",
            "Tempo Town",
            "Elkurn",
            "Nesaak",
            "Selchar",
            "Almuj",
            "Rymek",
            "Lusuco",
            "Bremminglar",
            "Nodguj Nation",
            "Troms",
            "Pirate Town",
            "Dujgon Nation",
            "Cinfras",
            "Olux",
            "Gelibord",
            "Lexdale",
            "Thanos",
            "Thesead",
            "Eltom",
            "Ahmsord",
            "Corkus City",
            "Legendary Island",
            "Rodoroc",
            "Llevigar",
            "Lutho",
            "Iboju Village",
            "Entrance to Bucie",
            "Efilim",
            "Relos",
            "Espren",
            "Timasca",
            "Aldwell",
            "Hyloch",
            "Contested District",
            "Dogun Ritual Site",
            "Kandon-Beda");

    public static Set<String> unlockedTerritories;
    private int countdownTicks = 0;
    private int cooldownTicks;

    public static void resetUnlocked() {
        unlockedTerritories = new HashSet<>();
        unlockedTerritories.addAll(RESPAWN_TERRITORIES);
    }

    public static void unlockTerritory(String territory) {
        TerritoryUnlock.unlockedTerritories.add(territory);
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                .append(Component.literal("You unlocked "))
                .append(Component.literal(territory).withStyle(ChatFormatting.AQUA)));
    }

    public TerritoryUnlock() {
        unlockedTerritories = new HashSet<>();
        unlockedTerritories.addAll(RESPAWN_TERRITORIES);

        cooldownTicks = 200;

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(Minecraft client) {
        if (client.player == null || --cooldownTicks > 0 || !WynnpelagoClient.enabled) return;
        TerritoryProfile territory = Models.Territory.getTerritoryProfileForPosition(client.player.position());
        if (territory == null) {
            countdownTicks = 0;
            return;
        }
        if (!unlockedTerritories.contains(territory.getName())) {
            ArchipelagoOptions.RegionEnforcement enforcement = ArchipelagoOptions.getLockedRegionEnforcement();
            if (enforcement == ArchipelagoOptions.RegionEnforcement.KILL) {
                McUtils.sendChat("/kill");
                cooldownTicks = 200;
            } else if (enforcement == ArchipelagoOptions.RegionEnforcement.COUNTDOWN) {
                if (countdownTicks > ArchipelagoOptions.getLockedRegionCountdown() * 20) {
                    McUtils.sendChat("/kill");
                    cooldownTicks = 100;
                } else if (countdownTicks++ % 20 == 0) {
                    int secondsLeft = ArchipelagoOptions.getLockedRegionCountdown() - countdownTicks / 20;
                    WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                            .append(Component.literal(String.format(
                                            "You are in a locked territory. %d second%s until death.",
                                            secondsLeft, secondsLeft == 1 ? "" : "s"))
                                    .withStyle(ChatFormatting.RED)));
                }
            }
        }
    }
}
