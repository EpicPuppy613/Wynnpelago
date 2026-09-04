package dev.epicpuppy.wynnpelago.client.compat;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class BackwardsFlags {
    @Getter
    private static boolean regionEntryLevel = false;

    private static final Map<String, Integer> versionMap = new HashMap<>();

    static {
        versionMap.put("0.4.0", 1);
        versionMap.put("0.4.1", 2);
        versionMap.put("0.4.2", 3);
    }

    public static void loadFlags(String versionString) {
        Integer version = versionMap.getOrDefault(versionString, 0);
        regionEntryLevel = version >= versionMap.get("0.4.2");
    }
}
