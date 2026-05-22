package dev.epicpuppy.wynnpelago.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wynntils.models.territories.TerritoryInfo;
import com.wynntils.screens.maps.GuildMapScreen;
import com.wynntils.services.map.pois.TerritoryPoi;
import dev.epicpuppy.wynnpelago.client.services.ConnectionOverrideService;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuildMapScreen.class)
public class GuildMapScreenMixin {
    @WrapOperation(
            method =
                    "renderPois(Ljava/util/List;Lnet/minecraft/client/gui/GuiGraphics;Lcom/wynntils/utils/type/BoundingBox;FII)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/wynntils/models/territories/TerritoryInfo;getTradingRoutes()Ljava/util/List;"))
    private List<String> onGetRoutes(
            TerritoryInfo instance,
            Operation<List<String>> original,
            @Local(name = "territoryPoi") TerritoryPoi territoryPoi) {
        String name = territoryPoi.getName();
        if (!ConnectionOverrideService.connectionAdditions.containsKey(name)
                && !ConnectionOverrideService.connectionRemovals.containsKey(name)) {
            return original.call(instance);
        }
        List<String> newList = new ArrayList<>(original.call(instance));
        newList.addAll(ConnectionOverrideService.connectionAdditions.getOrDefault(name, List.of()));
        newList.removeAll(ConnectionOverrideService.connectionRemovals.getOrDefault(name, List.of()));
        return newList;
    }
}
