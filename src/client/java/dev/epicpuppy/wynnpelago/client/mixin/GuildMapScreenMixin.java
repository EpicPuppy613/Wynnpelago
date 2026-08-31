package dev.epicpuppy.wynnpelago.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wynntils.models.territories.TerritoryInfo;
import com.wynntils.screens.maps.AbstractMapScreen;
import com.wynntils.screens.maps.GuildMapScreen;
import com.wynntils.services.map.pois.TerritoryPoi;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.render.GuildMapRenderer;
import dev.epicpuppy.wynnpelago.client.services.ConnectionOverrideService;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuildMapScreen.class)
public class GuildMapScreenMixin extends AbstractMapScreen {
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
        if (!WynnpelagoClient.enabled) {
            return original.call(instance);
        }
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

    @WrapOperation(
            method = "renderHoveredTerritoryInfo",
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lcom/wynntils/screens/maps/GuildMapScreen;renderTerritoryTooltip(Lnet/minecraft/client/gui/GuiGraphics;IILcom/wynntils/services/map/pois/TerritoryPoi;)V"),
                @At(
                        value = "INVOKE",
                        target =
                                "Lcom/wynntils/screens/maps/GuildMapScreen;renderTerritoryTooltipWithFakeInfo(Lnet/minecraft/client/gui/GuiGraphics;IILcom/wynntils/services/map/pois/TerritoryPoi;)V")
            })
    private void onRenderTooltip(
            GuiGraphics guiGraphics, int xOffset, int yOffset, TerritoryPoi territoryPoi, Operation<Void> original) {
        if (!WynnpelagoClient.enabled) {
            original.call(guiGraphics, xOffset, yOffset, territoryPoi);
            return;
        }
        GuildMapRenderer.renderTooltip(guiGraphics, xOffset, yOffset, territoryPoi);
    }

    @WrapOperation(
            method = "doRender",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/wynntils/screens/maps/GuildMapScreen;renderMapButtons(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void suppressButtons(
            GuildMapScreen instance, GuiGraphics guiGraphics, int x, int y, float t, Operation<Void> original) {
        if (!WynnpelagoClient.enabled) {
            original.call(instance, guiGraphics, x, y, t);
        }
    }

    @Inject(method = "doRender", at = @At("TAIL"))
    private void renderAdditional(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (WynnpelagoClient.enabled) {
            GuildMapRenderer.render(
                    guiGraphics, this.renderX, this.renderY, this.renderedBorderXOffset, this.renderedBorderYOffset);
        }
    }
}
