package dev.epicpuppy.wynnpelago.client.compat;

import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.features.ProfileDefault;
import com.wynntils.core.consumers.overlays.Overlay;
import com.wynntils.core.consumers.overlays.annotations.RegisterOverlay;

public class WynnpelagoFeature extends Feature {
    public WynnpelagoFeature() {
        super(ProfileDefault.onlyDefault());
    }

    @RegisterOverlay
    private final Overlay maxLevelsOverlay = new MaxLevelsOverlay();
}
