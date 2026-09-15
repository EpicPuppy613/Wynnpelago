package dev.epicpuppy.wynnpelago.client.services.content;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LevelRuleType {
    REGION("Region"),
    GRIND_SPOT("Grind Spot"),
    MOUNT("Mount");

    private final String serializedName;

    public static LevelRuleType fromSerializedName(String name) {
        for (LevelRuleType type : values()) {
            if (type.serializedName.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
