package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.features.FeatureManager;
import dev.epicpuppy.wynnpelago.client.compat.WynnpelagoFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureManager.class)
public abstract class FeatureManagerMixin {
    @Shadow
    protected abstract void registerFeature(Feature feature);

    @Inject(method = "init", at = @At("HEAD"))
    public void initFeatures(CallbackInfo ci) {
        registerFeature(new WynnpelagoFeature());
    }
}
