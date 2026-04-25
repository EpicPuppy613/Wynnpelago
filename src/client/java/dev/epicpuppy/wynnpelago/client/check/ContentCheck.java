package dev.epicpuppy.wynnpelago.client.check;

import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

public class ContentCheck {
    private static final Pattern CAVE_PATTERN = Pattern.compile("\\[Cave Completed]\\n§e\\s+§l([A-Za-z '0-9]+)\\n");
    private static final Pattern DUNGEON_PATTERN =
            Pattern.compile("§6Great job! You've completed the ([A-Za-z '0-9À]+) Dungeon!");
    private static final String QUEST_PATTERN = "§6\\s+\\[Quest Completed]";
    private static final Pattern QUEST_NAME_PATTERN = Pattern.compile("§e\\s+§l([A-Za-z '0-9]+)");
    private static final String MINI_QUEST_PATTERN = "§2\\s+\\[Mini-Quest Completed]";
    private static final Pattern MINI_QUEST_NAME_PATTERN = Pattern.compile("§a\\s+§l([A-Za-z '0-9]+)");

    private boolean questCompleted = false;
    private boolean miniQuestCompleted = false;

    public ContentCheck() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay || !WynnpelagoClient.enabled) return;
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

        // Quest
        if (questCompleted) {
            Matcher result = QUEST_NAME_PATTERN.matcher(text);
            if (result.find()) {
                Wynnpelago.LOGGER.info("Quest: {}", result.group(1));
                WynnpelagoClient.sendCheck(result.group(1));
                questCompleted = false;
            }
        } else if (text.matches(QUEST_PATTERN)) {
            questCompleted = true;
        }

        // Mini-Quest
        if (miniQuestCompleted) {
            Matcher result = MINI_QUEST_NAME_PATTERN.matcher(text);
            if (result.find()) {
                Wynnpelago.LOGGER.info("Mini-Quest: {}", result.group(1));
                WynnpelagoClient.sendCheck(result.group(1));
                miniQuestCompleted = false;
            }
        } else if (text.matches(MINI_QUEST_PATTERN)) {
            miniQuestCompleted = true;
        }
    }
}
