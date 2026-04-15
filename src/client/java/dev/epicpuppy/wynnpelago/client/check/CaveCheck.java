package dev.epicpuppy.wynnpelago.client.check;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaveCheck {
    private static final Pattern NAME_PATTERN = Pattern.compile("\\[Cave Completed]\\n§e\\s+§l([A-Za-z '0-9]+)\\n");

    public CaveCheck() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay || !WynnpelagoClient.enabled) return;
        String text = message.getString();
        Matcher result = NAME_PATTERN.matcher(text);
        if (!result.find()) return;
        Wynnpelago.LOGGER.info("Cave: {}", result.group(1));
        WynnpelagoClient.sendCheck(result.group(1));
    }
}
