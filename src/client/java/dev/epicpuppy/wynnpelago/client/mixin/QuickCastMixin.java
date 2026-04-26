package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.features.combat.QuickCastFeature;
import com.wynntils.models.spells.type.CombatClickType;
import dev.epicpuppy.wynnpelago.client.trap.SilenceTrap;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(QuickCastFeature.class)
public class QuickCastMixin {
    @Inject(method = "tryCastSpell", at = @At("HEAD"), cancellable = true)
    private void silenceSpellCast(
            List<CombatClickType> clicks, boolean notifyInvalidWeapon, CallbackInfoReturnable<Boolean> cir) {
        if (SilenceTrap.isActive()) {
            cir.setReturnValue(false);
        }
    }
}
