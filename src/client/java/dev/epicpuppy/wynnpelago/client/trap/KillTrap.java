package dev.epicpuppy.wynnpelago.client.trap;

import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class KillTrap {
    private boolean trigger = false;

    public KillTrap() {
        TrapProvider.TRAP_EVENT.register(this::onTrap);
        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    protected void onTrap(TrapProvider.TrapType type) {
        if (type == TrapProvider.TrapType.KILL) {
            trigger = true;
        }
    }

    protected void onTick(Minecraft client) {
        if (trigger) {
            McUtils.sendChat("/kill");
            trigger = false;
        }
    }
}
