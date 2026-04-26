package dev.epicpuppy.wynnpelago.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wynntils.models.territories.TerritoryInfo;
import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.services.map.pois.TerritoryPoi;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

    @WrapOperation(
            method = "renderAt",
            at = @At(value = "INVOKE", target = "Lcom/wynntils/models/territories/TerritoryInfo;isHeadquarters()Z"))
    private boolean disableHeadquarters(TerritoryInfo instance, Operation<Boolean> original) {
        if (!WynnpelagoClient.enabled) return original.call(instance);
        return false;
    }

    @ModifyVariable(method = "renderAt", at = @At(value = "LOAD", ordinal = 0), name = "mapText")
    private String changeMapText(String mapText) {
        if (!WynnpelagoClient.enabled) return mapText;
        TerritoryProfile territory = ((TerritoryPoiAccessor) this).wynnpelago$getTerritoryProfile();
        return Arrays.stream(territory.getName().split(" "))
                .map((s) -> s.substring(0, 1))
                .collect(Collectors.joining());
    }
}
