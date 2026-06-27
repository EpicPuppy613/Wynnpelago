package dev.epicpuppy.wynnpelago.client.check;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.activities.type.ActivityInfo;
import com.wynntils.models.activities.type.ActivityStatus;
import com.wynntils.models.activities.type.ActivityType;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.services.TextDisplayService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

public class ContentCheck {
    private static final Pattern CAVE_PATTERN = Pattern.compile("§e§l([A-Za-z '&0-9]+) Rewards\\n§7");
    private static final Pattern DUNGEON_PATTERN =
            Pattern.compile("§6Great job! You've completed the ([A-Za-z '&0-9À]+) Dungeon!");
    private static final Pattern QUEST_PATTERN = Pattern.compile("(§e|§a)\\s*§l([A-Za-z '&0-9]+)");

    public static void scanContentBook() {
        Models.Activity.scanContentBook(ActivityType.QUEST, ((activities, texts) -> {
            for (ActivityInfo info : activities) {
                if (info.status() == ActivityStatus.COMPLETED) {
                    WynnpelagoClient.sendCheck("Complete: " + info.name());
                }
            }
        }));
        Models.Activity.scanContentBook(ActivityType.MINI_QUEST, ((activities, texts) -> {
            for (ActivityInfo info : activities) {
                if (info.status() == ActivityStatus.COMPLETED) {
                    WynnpelagoClient.sendCheck("Complete: " + info.name());
                }
            }
        }));
        Models.Activity.scanContentBook(ActivityType.CAVE, ((activities, texts) -> {
            for (ActivityInfo info : activities) {
                if (info.status() == ActivityStatus.COMPLETED) {
                    WynnpelagoClient.sendCheck("Explore: " + info.name());
                }
            }
        }));
    }

    public ContentCheck() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
        TextDisplayService.TEXT_DISPLAY_UPDATE_EVENT.register(this::onTextDisplayUpdate);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay) return;
        StyledText styledText = StyledText.fromComponent(message);
        String text = styledText.getString();

        // Dungeon
        Matcher dungeon = DUNGEON_PATTERN.matcher(text);
        if (dungeon.find()) {
            Wynnpelago.LOGGER.info(
                    "Dungeon: {}", dungeon.group(1).replace("ÀÀÀ", " ").trim());
            WynnpelagoClient.sendCheck(
                    "Complete: " + dungeon.group(1).replace("ÀÀÀ", " ").trim());
        }

        // Quest & Mini-Quest
        Matcher quest = QUEST_PATTERN.matcher(text);
        if (quest.find()) {
            Wynnpelago.LOGGER.info("Quest: {}", quest.group(2).trim());
            WynnpelagoClient.sendCheck("Complete: " + quest.group(2).trim());
        }
    }

    private void onTextDisplayUpdate(Component message) {
        // Cave
        Matcher cave = CAVE_PATTERN.matcher(message.getString());
        if (cave.find()) {
            Wynnpelago.LOGGER.info("Cave: {}", cave.group(1).trim());
            WynnpelagoClient.sendCheck("Explore: " + cave.group(1).trim());
        }
    }
}
