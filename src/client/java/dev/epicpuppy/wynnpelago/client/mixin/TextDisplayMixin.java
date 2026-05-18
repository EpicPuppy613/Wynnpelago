package dev.epicpuppy.wynnpelago.client.mixin;

import dev.epicpuppy.wynnpelago.client.services.TextDisplayService;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Display.TextDisplay.class)
public abstract class TextDisplayMixin {
    @Shadow
    public abstract Component getText();

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void onUpdate(CallbackInfo ci) {
        String text = getText().getString();
        if (text.isBlank()) {
            return;
        }
        TextDisplayService.onTextDisplayUpdate(getText());
    }
}
