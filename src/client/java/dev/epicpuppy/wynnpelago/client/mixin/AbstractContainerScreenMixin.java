package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.mc.extension.ItemStackExtension;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Unique
    private static final List<Integer> GEAR_LOCK_SLOTS = List.of(5, 6, 7, 8, 9, 10, 11, 12);

    @Unique
    private ItemStack heldItem = ItemStack.EMPTY;

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (!WynnpelagoClient.enabled) {
            return;
        }

        if (!((Object) this instanceof InventoryScreen) && !((Object) this instanceof ContainerScreen)) {
            return;
        }

        if (type == ClickType.QUICK_MOVE && (Object) this instanceof InventoryScreen) {
            if (slotId < 0) {
                return;
            }

            if (!slot.hasItem()) {
                return;
            }

            ItemStack itemStack = slot.getItem();
            if (!GearUnlock.canUseItem(itemStack)) {
                ci.cancel();
            }
        }

        if (type == ClickType.PICKUP) {
            if (GEAR_LOCK_SLOTS.contains(slotId)) {
                if (!GearUnlock.canUseItem(heldItem)) {
                    ci.cancel();
                    return;
                }
            }

            heldItem = slot.getItem().copy();

            ((ItemStackExtension) (Object) heldItem)
                    .setAnnotation(((ItemStackExtension) (Object) slot.getItem()).getAnnotation());
            ((ItemStackExtension) (Object) heldItem)
                    .setOriginalName(((ItemStackExtension) (Object) slot.getItem()).getOriginalName());
        }
    }
}
