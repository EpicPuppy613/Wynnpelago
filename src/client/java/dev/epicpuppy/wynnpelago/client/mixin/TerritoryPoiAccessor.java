package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.services.map.pois.TerritoryPoi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TerritoryPoi.class)
public interface TerritoryPoiAccessor {
    @Accessor("territoryProfileCache")
    TerritoryProfile wynnpelago$getTerritoryProfile();
}
