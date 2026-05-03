package dev.epicpuppy.wynnpelago.client.trap;

import com.wynntils.core.components.Handlers;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.providers.TrapProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class KillTrap {
    private boolean trigger = false;

    public KillTrap() {
        TrapProvider.TRAP_EVENT.register(this::onTrap);
        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    protected void onTrap(TrapProvider.TrapType type) {
        if (type == TrapProvider.TrapType.KILL) {
            trigger = true;
            WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                    .append(Component.literal("You've been killed").withStyle(ChatFormatting.LIGHT_PURPLE)));
        }
    }

    protected void onTick(Minecraft client) {
        if (trigger) {
            Handlers.Chat.queueChatCommand("kill");
            trigger = false;
        }
    }
}
