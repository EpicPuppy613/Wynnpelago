package dev.epicpuppy.wynnpelago.client.trap;

import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import net.minecraft.client.Minecraft;

public class BlindTrap extends EffectTrap {
    private static boolean trapActive = false;

    public BlindTrap() {
        super(TrapProvider.TrapType.BLIND);
    }

    @Override
    protected void onTick(Minecraft client) {
        super.onTick(client);
        trapActive = activeTicks > 0;
    }

    public static boolean isActive() {
        return trapActive;
    }
}
