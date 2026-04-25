package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.services.map.pois.TerritoryPoi;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TerritoryPoi.class)
public class TerritoryPoiMixin {
    @ModifyVariable(method = "renderAt", at = @At(value = "LOAD", ordinal = 0), name = "colors")
    private List<CustomColor> changeColor(List<CustomColor> colors) {
        if (!WynnpelagoClient.enabled) return colors;
        TerritoryProfile territory = ((TerritoryPoiAccessor) this).wynnpelago$getTerritoryProfile();
        if (TerritoryUnlock.RESPAWN_TERRITORIES.contains(territory.getName())) {
            return List.of(CommonColors.AQUA);
        } else if (TerritoryUnlock.unlockedTerritories.contains(territory.getName())) {
            return List.of(CommonColors.GREEN);
        } else {
            return List.of(CommonColors.RED);
        }
    }
}
