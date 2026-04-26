package dev.epicpuppy.wynnpelago.client.trap;

import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import net.minecraft.client.Minecraft;

public class FreezeTrap extends EffectTrap {
    private static boolean trapActive = false;

    public FreezeTrap() {
        super(TrapProvider.TrapType.FREEZE);
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
