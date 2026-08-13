package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.handlers.bossbar.event.BossBarAddedEvent;
import com.wynntils.models.character.CharacterPhysicalModel;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.services.DeathLinkService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CharacterPhysicalModel.class)
public class CharacterPhysicalModelMixin {
    @Inject(
            method = "onBossBarAdd",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lcom/wynntils/core/WynntilsMod;postEvent(Lnet/neoforged/bus/api/Event;)Z"))
    public void onDeath(BossBarAddedEvent event, CallbackInfo ci) {
        if (WynnpelagoClient.enabled && ArchipelagoOptions.isDeathLink()) {
            DeathLinkService.sendDeath();
        }
    }
}
