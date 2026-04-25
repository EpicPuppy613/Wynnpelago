package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.features.commands.FilterAdminCommandsFeature;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilterAdminCommandsFeature.class)
public class FilterCommandsMixin {
    @Final
    @Mutable
    @Shadow
    private static Set<String> FILTERED_COMMANDS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClassInit(CallbackInfo ci) {
        Set<String> mutableCommands = new HashSet<>(FILTERED_COMMANDS);
        mutableCommands.remove("connect");
        FILTERED_COMMANDS = Set.copyOf(mutableCommands);
    }
}
