package dev.epicpuppy.wynnpelago.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.models.activities.ContentBookQueries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContentBookQueries.class)
public class ContentBookQueryMixin {
    @WrapOperation(
            method = "queryContentBook",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/wynntils/handlers/container/scriptedquery/QueryStep;clickOnSlot(I)Lcom/wynntils/handlers/container/scriptedquery/QueryStep;"))
    public QueryStep contentBookQueryVerification(int slotNum, Operation<QueryStep> original) {
        return original.call(slotNum).verifyContentChange((content, stacks, type) -> stacks.containsKey(53));
    }
}
