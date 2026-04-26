package dev.epicpuppy.wynnpelago.client.trap;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DazeTrap extends EffectTrap {
    private static boolean trapActive = false;

    public DazeTrap() {
        super(TrapProvider.TrapType.DAZE);

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
        if (trapActive && activeTicks <= 0) {
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("You're no longer dazed").withStyle(ChatFormatting.AQUA)));
        }
        trapActive = activeTicks > 0;
    }

    @Override
    protected void onTrap(TrapProvider.TrapType type) {
        super.onTrap(type);
        if (this.type == type) {
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("You've been dazed").withStyle(ChatFormatting.LIGHT_PURPLE)));
        }
    }

    public static boolean isActive() {
        return trapActive;
    }
}
