package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.features.commands.FilterAdminCommandsFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(FilterAdminCommandsFeature.class)
public class FilterCommandsMixin {
    @Final
    @Shadow
    private static Set<String> FILTERED_COMMANDS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClassInit(CallbackInfo ci) {
        FILTERED_COMMANDS.remove("connect");
    }
}
