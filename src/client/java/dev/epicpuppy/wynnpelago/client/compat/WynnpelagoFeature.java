package dev.epicpuppy.wynnpelago.client.compat;

import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.features.ProfileDefault;
import com.wynntils.core.consumers.features.properties.RegisterSubFeature;
import com.wynntils.core.consumers.overlays.Overlay;
import com.wynntils.core.consumers.overlays.annotations.RegisterOverlay;
import com.wynntils.core.persisted.Persisted;
import com.wynntils.core.persisted.config.Config;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;

public class WynnpelagoFeature extends Feature {
    @Persisted
    private final Config<Boolean> showLockedTerritoryBorders = new Config<>(true);

    @Persisted
    private final Config<Boolean> filterItemSendMessages = new Config<>(false);

    @Persisted
    private final Config<Boolean> filterConnectionMessages = new Config<>(false);

    @RegisterOverlay
    private final Overlay maxLevelsOverlay = new MaxLevelsOverlay();

    @RegisterSubFeature
    private final ApTrackerFeature apTrackerFeature = new ApTrackerFeature();

    public WynnpelagoFeature() {
        super(ProfileDefault.onlyDefault());
        WynnpelagoClient.wynntilsInit();
    }

    public boolean getShowLockedTerritoryBorders() {
        return showLockedTerritoryBorders.get();
    }

    public boolean getFilterItemSendMessages() {
        return filterItemSendMessages.get();
    }

    public boolean getFilterConnectionMessages() {
        return filterConnectionMessages.get();
    }
}
