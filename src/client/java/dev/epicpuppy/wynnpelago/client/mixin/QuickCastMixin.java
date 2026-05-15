package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.features.combat.QuickCastFeature;
import com.wynntils.models.spells.type.CombatClickType;
import dev.epicpuppy.wynnpelago.client.trap.DazeTrap;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(QuickCastFeature.class)
public class QuickCastMixin {
    @Inject(method = "tryCastSpell", at = @At("HEAD"), cancellable = true)
    private void silenceSpellCast(
            List<CombatClickType> clicks, boolean notifyInvalidWeapon, CallbackInfoReturnable<Boolean> cir) {
        if (DazeTrap.isActive()) {
            cir.setReturnValue(false);
        }
        // Gear Lock
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !GearUnlock.canUseWeapon(player.getMainHandItem())) {
            cir.setReturnValue(false);
        }
    }
}
