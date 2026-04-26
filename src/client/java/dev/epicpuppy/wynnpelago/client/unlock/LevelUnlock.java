package dev.epicpuppy.wynnpelago.client.unlock;

import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.providers.LevelProvider;
import io.github.archipelagomw.ClientStatus;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class LevelUnlock {
    private static final Pattern XP_PATTERN = Pattern.compile("contribute §b(\\d+)%§3 of your XP");

    public static int maxLevel = 1;
    private static String currentXpContribution = "0";
    private static int commandCooldown = 0;

    public static synchronized void resetMaxLevel() {
        maxLevel = 1;
    }

    public static synchronized void increaseMaxLevel() {
        maxLevel += ArchipelagoOptions.getLevelIncrement();
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                .append(Component.literal("Your max level is now "))
                .append(Component.literal(String.valueOf(maxLevel)).withStyle(ChatFormatting.GREEN)));
    }

    public LevelUnlock() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
        LevelProvider.LEVEL_UP_EVENT.register(this::onLevelUp);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay) return;

        Matcher contribution = XP_PATTERN.matcher(message.getString());
        if (contribution.find()) {
            currentXpContribution = contribution.group(1);
        }
    }

    private void onTick(Minecraft client) {
        boolean atLevelCap = LevelProvider.getLevel() >= maxLevel;
        if (--commandCooldown > 0) return;
        if (atLevelCap && currentXpContribution.equals("0")) {
            McUtils.sendChat("/gu xp 100");
            commandCooldown = 20;
        } else if (!atLevelCap && currentXpContribution.equals("100")) {
            McUtils.sendChat("/gu xp 0");
            commandCooldown = 20;
        }
    }

    private void onLevelUp(int prevLevel, int newLevel) {
        if (newLevel >= ArchipelagoOptions.getGoalLevel()) {
            WynnpelagoClient.client.setGameState(ClientStatus.CLIENT_GOAL);
        }
    }
}
