package dev.epicpuppy.wynnpelago.client.services.content;

import com.wynntils.utils.colors.CustomColor;
import dev.epicpuppy.wynnpelago.client.compat.ApTrackerFeature;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RegionState {
    DISABLED(ApTrackerFeature::getDisabledColor),
    LOCKED(ApTrackerFeature::getLockedColor),
    INACCESSIBLE(ApTrackerFeature::getInaccessibleColor),
    ACCESSIBLE(ApTrackerFeature::getAccessibleColor),
    HAS_IN_LOGIC(ApTrackerFeature::getInLogicColor),
    HAS_AVAILABLE(ApTrackerFeature::getAvailableColor),
    COMPLETE(ApTrackerFeature::getCompleteColor),
    HAS_GOAL(ApTrackerFeature::getGoalColor);

    private static ApTrackerFeature feature = null;

    public static void populateFeature(ApTrackerFeature instance) {
        feature = instance;
    }

    private final Function<ApTrackerFeature, CustomColor> colorFunction;

    public CustomColor getColor() {
        if (feature == null) {
            throw new IllegalStateException("Feature instance reference is not loaded");
        }
        return colorFunction.apply(feature);
    }
}
