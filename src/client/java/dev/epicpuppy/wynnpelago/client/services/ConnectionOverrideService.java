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
        addConnection("Nemract", "Selchar");
        addConnection("Selchar", "Zhight Island");
        addConnection("Selchar", "Pirate Town");
        addConnection("Selchar", "Lost Atoll");
        addConnection("Selchar", "Nodguj Nation");
        addConnection("Durum Isles Barn", "Mage Island");
        addConnection("Overrun Docks", "Ava's Workshop");

        removeConnection("Troms", "Sulphuric Hollow");
        removeConnection("Rooster Island", "Durum Malt Islet");
        removeConnection("Selchar", "Durum Malt Islet");
        removeConnection("Selchar", "Durum Barley Islet");
        removeConnection("Cathedral Harbour", "Durum Malt Islet");
        removeConnection("Cathedral Harbour", "Durum Oat Islet");
        removeConnection("Durum Oat Islet", "Mage Island");
        removeConnection("Durum Barley Islet", "Mage Island");
        removeConnection("Monte's Village", "Entamis Village");
    }
}
