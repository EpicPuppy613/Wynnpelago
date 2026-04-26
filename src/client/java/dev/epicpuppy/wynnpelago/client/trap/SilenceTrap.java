package dev.epicpuppy.wynnpelago.client.trap;

import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SilenceTrap extends EffectTrap {
    private static boolean trapActive = false;

    public SilenceTrap() {
        super(TrapProvider.TrapType.SILENCE);

        UseItemCallback.EVENT.register(this::onUse);
        ClientPreAttackCallback.EVENT.register(this::onAttack);
    }

    private InteractionResult onUse(Player player, Level world, InteractionHand hand) {
        return isActive() ? InteractionResult.FAIL : InteractionResult.PASS;
    }

    private boolean onAttack(Minecraft client, LocalPlayer player, int clickCount) {
        return isActive();
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
