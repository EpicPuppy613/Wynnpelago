package dev.epicpuppy.wynnpelago.client.trap;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.services.TrapService;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class FreezeTrap extends EffectTrap {
    // Uses Mixin: KeyboardInputMixin

    private static boolean trapActive = false;

    public FreezeTrap() {
        super(TrapService.TrapType.FREEZE);
    }

    @Override
    protected void onTick(Minecraft client) {
        super.onTick(client);
        if (trapActive && activeTicks <= 0) {
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("You're no longer frozen").withStyle(ChatFormatting.AQUA)));
        }
        trapActive = activeTicks > 0;
    }

    @Override
    protected void onTrap(TrapService.TrapType type) {
        super.onTrap(type);
        if (this.type == type) {
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("You've been frozen").withStyle(ChatFormatting.LIGHT_PURPLE)));
        }
    }

    public static boolean isActive() {
        return trapActive;
    }
}
