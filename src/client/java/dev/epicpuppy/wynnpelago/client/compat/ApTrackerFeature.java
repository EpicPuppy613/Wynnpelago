package dev.epicpuppy.wynnpelago.client.compat;

import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.features.ProfileDefault;
import com.wynntils.core.persisted.Persisted;
import com.wynntils.core.persisted.config.Config;
import com.wynntils.utils.colors.CustomColor;
import dev.epicpuppy.wynnpelago.client.services.content.RegionState;

public class ApTrackerFeature extends Feature {
    @Persisted
    private final Config<CustomColor> disabledColor = new Config<>(CustomColor.fromInt(0x333333));

    @Persisted
    private final Config<CustomColor> lockedColor = new Config<>(CustomColor.fromInt(0x888888));

    @Persisted
    private final Config<CustomColor> inaccessibleColor = new Config<>(CustomColor.fromInt(0xff4444));

    @Persisted
    private final Config<CustomColor> accessibleColor = new Config<>(CustomColor.fromInt(0xff44ff));

    @Persisted
    private final Config<CustomColor> inLogicColor = new Config<>(CustomColor.fromInt(0xffff44));

    @Persisted
    private final Config<CustomColor> availableColor = new Config<>(CustomColor.fromInt(0x44ff44));

    @Persisted
    private final Config<CustomColor> completeColor = new Config<>(CustomColor.fromInt(0x44ffff));

    @Persisted
    private final Config<CustomColor> goalColor = new Config<>(CustomColor.fromInt(0xffbb44));

    public ApTrackerFeature() {
        super(ProfileDefault.onlyDefault());
        RegionState.populateFeature(this);
    }

    public CustomColor getDisabledColor() {
        return disabledColor.get();
    }

    public CustomColor getLockedColor() {
        return lockedColor.get();
    }

    public CustomColor getInaccessibleColor() {
        return inaccessibleColor.get();
    }

    public CustomColor getAccessibleColor() {
        return accessibleColor.get();
    }

    public CustomColor getInLogicColor() {
        return inLogicColor.get();
    }

    public CustomColor getAvailableColor() {
        return availableColor.get();
    }

    public CustomColor getCompleteColor() {
        return completeColor.get();
    }

    public CustomColor getGoalColor() {
        return goalColor.get();
    }
}
