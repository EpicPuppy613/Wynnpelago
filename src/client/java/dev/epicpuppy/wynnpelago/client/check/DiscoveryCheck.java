package dev.epicpuppy.wynnpelago.client.check;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscoveryCheck {
    private static final Pattern DISCOVERY_PATTERN = Pattern.compile("\\s+§6Area Discovered: §e([A-Za-z '0-9]+)§d");

    public DiscoveryCheck() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay || !WynnpelagoClient.enabled) return;
        Matcher result = DISCOVERY_PATTERN.matcher(message.getString());
        if (!result.find()) return;
        Wynnpelago.LOGGER.info("Discovery: {}", result.group(1));
    }
}
