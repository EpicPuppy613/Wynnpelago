package dev.epicpuppy.wynnpelago.client.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionOverrideService {
    public static final Map<String, List<String>> connectionAdditions = new HashMap<>();
    public static final Map<String, List<String>> connectionRemovals = new HashMap<>();

    private static void addConnection(String terr1, String terr2) {
        add(terr1, terr2, connectionAdditions);
    }

    private static void removeConnection(String terr1, String terr2) {
        add(terr1, terr2, connectionRemovals);
    }

    private static void add(String terr1, String terr2, Map<String, List<String>> map) {
        if (map.containsKey(terr1)) {
            map.get(terr1).add(terr2);
        } else {
            List<String> list = new ArrayList<>();
            list.add(terr2);
            map.put(terr1, list);
        }
        if (map.containsKey(terr2)) {
            map.get(terr2).add(terr1);
        } else {
            List<String> list = new ArrayList<>();
            list.add(terr1);
            map.put(terr2, list);
        }
    }

    static {
        addConnection("Ternaves", "Owl Tribe");
        addConnection("Detlas", "Plains Lake");
        addConnection("Corrupted Tower", "Scorched Trail");
        addConnection("Scorched Trail", "Lava Springs");
        addConnection("Scorched Trail", "Savannah Plains");
        addConnection("Overrun Docks", "Ava's Workshop");
        addConnection("Thanos Underpass", "Thanos Exit");
        addConnection("Upper Thanos", "Thanos Exit");
        addConnection("Troll's Challenge", "Thanos");
        addConnection("Road to Time Valley", "Abandoned Farm");
        addConnection("Guild Hall", "Celestial Impact");
        addConnection("Cliffside Passage South", "Kandon Farm");
        addConnection("Elephelk Trail", "Kandon Farm");
        addConnection("Entrance to Molten Heights", "Lava Lakes");
        addConnection("Kandon-Beda", "Path to Ahmsord");

        removeConnection("Troms", "Sulphuric Hollow");
        removeConnection("Monte's Village", "Entamis Village");
        removeConnection("Thanos", "Upper Thanos");
        removeConnection("Troll's Challenge", "Upper Thanos");
        removeConnection("Kandon Farm", "Kandon Ridge");
        removeConnection("Lighthouse Lookout", "Royal Gate");
        removeConnection("Entrance to Molten Heights", "Rodoroc");

        // Ocean Removals
        removeConnection("Nemract", "Rooster Island");
        removeConnection("Rooster Island", "Durum Malt Islet");
        removeConnection("Rooster Island", "Bear Zoo");
        removeConnection("Selchar", "Durum Malt Islet");
        removeConnection("Selchar", "Durum Barley Islet");
        removeConnection("Selchar", "Durum Isles Barn");
        removeConnection("Selchar", "Skien's Island");
        removeConnection("Selchar", "Rooster Island");
        removeConnection("Cathedral Harbour", "Durum Malt Islet");
        removeConnection("Cathedral Harbour", "Durum Oat Islet");
        removeConnection("Durum Oat Islet", "Mage Island");
        removeConnection("Durum Barley Islet", "Mage Island");
        removeConnection("Durum Barley Islet", "Nodguj Nation");
        removeConnection("Mage Island", "Half Moon Island");
        removeConnection("Mage Island", "Nodguj Nation");
        removeConnection("Mage Island", "Santa's Hideout");
        removeConnection("Pirate Town", "Volcanic Isles");
        removeConnection("Tree Island", "Light Peninsula");
        removeConnection("Volcanic Isles", "Light Peninsula");
        removeConnection("Volcanic Isles", "Tree Island");
        removeConnection("Volcanic Excavation", "Light Peninsula");
        removeConnection("Entrance to Gavel", "Volcanic Excavation");
        removeConnection("Disturbed Crypt", "Dujgon Nation");
        removeConnection("Lifeless Forest", "Jofash Docks");
        removeConnection("Disturbed Crypt", "Regular Island");
        removeConnection("Dreary Docks", "Dujgon Nation");
        removeConnection("Skien's Island", "Nodguj Nation");
        removeConnection("Skien's Island", "Dreary Docks");
        removeConnection("Dujgon Nation", "Regular Island");
        removeConnection("Icy Island", "Regular Island");
        removeConnection("Maro Peaks", "Skien's Island");
        removeConnection("Maro Peaks", "Tree Island");
        removeConnection("Lost Atoll", "Volcanic Isles");
        removeConnection("Lost Atoll", "Tree Island");
        removeConnection("Lost Atoll", "Pirate Town");
        removeConnection("Zhight Island", "Pirate Town");
        removeConnection("Zhight Island", "Legendary Island");
        removeConnection("Zhight Island", "Bear Zoo");
        removeConnection("Bloody Beach", "Volcanic Excavation");
        removeConnection("Bloody Beach", "Pirate Town");
    }
}
