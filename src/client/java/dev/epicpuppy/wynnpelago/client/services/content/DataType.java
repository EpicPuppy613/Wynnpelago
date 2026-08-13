package dev.epicpuppy.wynnpelago.client.services.content;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DataType {
    QUEST("Quest"),
    CAVE("Cave"),
    LEVEL("Level"),
    MINI_QUEST("Mini-Quest"),
    DUNGEON("Dungeon"),
    BOSS("Boss"),
    TERRITORY("Territory"),
    SPECIAL("Special"),
    REGION("Region");

    private final String serializedName;

    public static DataType fromSerializedName(String name) {
        for (DataType type : values()) {
            if (type.serializedName.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
