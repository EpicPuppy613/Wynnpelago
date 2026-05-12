package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.core.consumers.functions.Function;
import com.wynntils.core.consumers.functions.FunctionManager;
import dev.epicpuppy.wynnpelago.client.compat.WynnpelagoFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FunctionManager.class)
public abstract class FunctionManagerMixin {
    @Shadow
    protected abstract void registerFunction(Function<?> function);

    @Inject(method = "registerAllFunctions", at = @At("TAIL"))
    private void registerWynnpelagoFunctions(CallbackInfo ci) {
        registerFunction(new WynnpelagoFunctions.WpEnabledFunction());
        registerFunction(new WynnpelagoFunctions.WpMaxLevelFunction());
    }
}
