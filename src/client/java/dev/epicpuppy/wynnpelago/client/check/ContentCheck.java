package dev.epicpuppy.wynnpelago.client.check;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

public class ContentCheck {
    private static final Pattern CAVE_PATTERN = Pattern.compile("\\[Cave Completed]\\n§e\\s+§l([A-Za-z '&0-9]+)");
    private static final Pattern DUNGEON_PATTERN =
            Pattern.compile("§6Great job! You've completed the ([A-Za-z '&0-9À]+) Dungeon!");
    private static final Pattern QUEST_PATTERN = Pattern.compile("(§e|§a)\\s+§l([A-Za-z '&0-9]+)");

    public ContentCheck() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay) return;
        String text = message.getString();

        // Cave
        Matcher cave = CAVE_PATTERN.matcher(text);
        if (cave.find()) {
            Wynnpelago.LOGGER.info("Cave: {}", cave.group(1));
            WynnpelagoClient.sendCheck(cave.group(1));
        }

        // Dungeon
        Matcher dungeon = DUNGEON_PATTERN.matcher(text);
        if (dungeon.find()) {
            Wynnpelago.LOGGER.info("Dungeon: {}", dungeon.group(1).replace("ÀÀÀ", " "));
            WynnpelagoClient.sendCheck(dungeon.group(1).replace("ÀÀÀ", " "));
        }

        // Quest & Mini-Quest
        Matcher quest = QUEST_PATTERN.matcher(text);
        if (quest.find()) {
            Wynnpelago.LOGGER.info("Quest: {}", quest.group(1));
            WynnpelagoClient.sendCheck(quest.group(1));
        }
    }
}
