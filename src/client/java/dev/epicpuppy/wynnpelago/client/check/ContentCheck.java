package dev.epicpuppy.wynnpelago.client.check;

import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.labels.event.TextDisplayChangedEvent;
import com.wynntils.models.activities.type.ActivityInfo;
import com.wynntils.models.activities.type.ActivityStatus;
import com.wynntils.models.activities.type.ActivityType;
import com.wynntils.models.lootrun.event.LootrunFinishedEvent;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

public class ContentCheck {
    private static final double LOOTRUN_CAMP_DETECTION_RADIUS = 20;

    private static final Pattern CAVE_PATTERN = Pattern.compile("§e§l([A-Za-z '&0-9]+) Rewards\\n§7");
    private static final Pattern DUNGEON_PATTERN =
            Pattern.compile("§6Great job! You've completed the ([A-Za-z '&0-9À\\-]+) Dungeon!");
    private static final Pattern QUEST_PATTERN =
            Pattern.compile("^\\s*(?<!Started: )(§e|§a|§#b38fadff)\\s*(?:§l)?([A-Za-z '&0-9]+)");
    private static final Pattern QUEST_PART_PATTERN = Pattern.compile("^(.+?)(\\d+)$");
    private static final Pattern SECRET_PATTERN = Pattern.compile("§3Secret Discovery: §b([A-Za-z '&0-9,]+)");

    private static final Map<String, String> PART_STRINGS = new HashMap<>();
    private static final Map<String, Vec3> LOOTRUN_CAMPS = new HashMap<>();

    static {
        PART_STRINGS.put("1", "I");
        PART_STRINGS.put("2", "II");
        PART_STRINGS.put("3", "III");
        PART_STRINGS.put("4", "IV");
        PART_STRINGS.put("5", "V");

        LOOTRUN_CAMPS.put("Canyon of the Lost Excursion", new Vec3(575, 78, -5025));
        LOOTRUN_CAMPS.put("The Corkus Traversal", new Vec3(-1560, 97, -2675));
        LOOTRUN_CAMPS.put("Molten Heights Hike", new Vec3(1270, 10, -5130));
        LOOTRUN_CAMPS.put("Sky Islands Exploration", new Vec3(1035, 135, -4420));
        LOOTRUN_CAMPS.put("Silent Expanse Expedition", new Vec3(990, 77, -785));
        LOOTRUN_CAMPS.put("The Fruma Foray (West)", new Vec3(-2002, 4, -775));
        LOOTRUN_CAMPS.put("The Fruma Foray (East)", new Vec3(-1294, 75, -1146));
    }

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
        Models.Activity.scanContentBook(ActivityType.SECRET_DISCOVERY, ((activities, texts) -> {
            for (ActivityInfo info : activities) {
                if (info.status() == ActivityStatus.COMPLETED) {
                    WynnpelagoClient.sendCheck("Discover: " + info.name());
                }
            }
        }));
    }

    public void init() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
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
            // If quest ends in a number, send both roman numerals and the number
            Matcher questPart = QUEST_PART_PATTERN.matcher(quest.group(2).trim());
            if (questPart.find()) {
                String questName = (quest.group(1) + PART_STRINGS.getOrDefault(quest.group(2), "")).trim();
                Wynnpelago.LOGGER.info("*Quest: {}", questName);
                WynnpelagoClient.sendCheck("Complete: " + questName);
            }
            Wynnpelago.LOGGER.info("Quest: {}", quest.group(2).trim());
            WynnpelagoClient.sendCheck("Complete: " + quest.group(2).trim());
        }

        // Secret Discovery
        Matcher secret = SECRET_PATTERN.matcher(text);
        if (secret.find()) {
            Wynnpelago.LOGGER.info("Secret: {}", secret.group(1).trim());
            WynnpelagoClient.sendCheck("Discover: " + secret.group(1).trim());
        }
    }

    @SubscribeEvent
    public void onTextDisplayUpdate(TextDisplayChangedEvent.Text event) {
        // Cave
        Matcher cave = CAVE_PATTERN.matcher(event.getText().getString());
        if (cave.find()) {
            Wynnpelago.LOGGER.info("Cave: {}", cave.group(1).trim());
            WynnpelagoClient.sendCheck("Explore: " + cave.group(1).trim());
        }
    }

    @SubscribeEvent
    public void onLootrunCompleted(LootrunFinishedEvent.Completed event) {
        Vec3 pos = Minecraft.getInstance().player.getPosition(0);
        for (Map.Entry<String, Vec3> camp : LOOTRUN_CAMPS.entrySet()) {
            if (camp.getValue().distanceTo(pos) < LOOTRUN_CAMP_DETECTION_RADIUS) {
                Wynnpelago.LOGGER.info("Lootrun: {}", camp.getKey());
                WynnpelagoClient.sendCheck("Run: " + camp.getKey());
                break;
            }
        }
    }
}
