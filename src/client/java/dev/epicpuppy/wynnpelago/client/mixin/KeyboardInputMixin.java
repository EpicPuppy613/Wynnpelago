package dev.epicpuppy.wynnpelago.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.epicpuppy.wynnpelago.client.trap.FreezeTrap;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @WrapOperation(
            method = "tick",
            at =
                    @At(
                            value = "FIELD",
                            opcode = Opcodes.PUTFIELD,
                            target =
                                    "Lnet/minecraft/client/player/KeyboardInput;keyPresses:Lnet/minecraft/world/entity/player/Input;"))
    private void freezePlayerMovement(KeyboardInput instance, Input newValue, Operation<Void> original) {
        if (FreezeTrap.isActive()) {
            instance.keyPresses = new Input(false, false, false, false, false, false, false);
        } else {
            instance.keyPresses = newValue;
        }
    }
}
