package dev.epicpuppy.wynnpelago.client.trap;

import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public abstract class EffectTrap {
    protected final TrapProvider.TrapType type;

    protected int activeTicks = 0;

    public EffectTrap(TrapProvider.TrapType type) {
        this.type = type;

        TrapProvider.TRAP_EVENT.register(this::onTrap);
        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    protected void onTrap(TrapProvider.TrapType type) {
        if (this.type == type) {
            activeTicks = ArchipelagoOptions.getTrapSeconds() * 20;
        }
    }

    protected void onTick(Minecraft client) {
        if (activeTicks > 0) {
            activeTicks--;
        }
    }
}
