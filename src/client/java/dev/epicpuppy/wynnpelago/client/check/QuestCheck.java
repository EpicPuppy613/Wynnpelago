package dev.epicpuppy.wynnpelago.client.check;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestCheck {
    private static final String QUEST_PATTERN = "§6\\s+\\[Quest Completed]";
    private static final Pattern NAME_PATTERN = Pattern.compile("§e\\s+§l([A-Za-z '0-9]+)");

    private boolean questCompleted = false;

    public QuestCheck() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay || !WynnpelagoClient.enabled) return;
        String text = message.getString();
        if (questCompleted) {
            Matcher result = NAME_PATTERN.matcher(text);
            if (!result.find()) return;
            Wynnpelago.LOGGER.info("Quest: {}", result.group(1));
            WynnpelagoClient.sendCheck(result.group(1));
            questCompleted = false;
        }
        else if (text.matches(QUEST_PATTERN)) {
            questCompleted = true;
        }
    }
}
