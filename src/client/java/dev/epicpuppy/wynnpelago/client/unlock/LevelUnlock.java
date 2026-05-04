package dev.epicpuppy.wynnpelago.client.unlock;

import com.wynntils.core.components.Handlers;
import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.Wynnpelago;
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
    private static final Pattern XP_PATTERN = Pattern.compile("(?s)contribute (§b)?(\\d+)%.+XP");

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
            currentXpContribution = contribution.group(2);
            Wynnpelago.LOGGER.info("XP Contribution: {}", currentXpContribution);
        }
    }

    private void onTick(Minecraft client) {
        if (!WynnpelagoClient.enabled) return;
        if (!Models.Guild.isInGuild()) return;
        boolean atLevelCap = LevelProvider.getLevel() >= maxLevel;
        if (--commandCooldown > 0) return;
        if (atLevelCap && !currentXpContribution.equals("100")) {
            setXPContribution("100");
            commandCooldown = 20;
        } else if (!atLevelCap && !currentXpContribution.equals("0")) {
            setXPContribution("0");
            commandCooldown = 20;
        }
    }

    private void onLevelUp(int level) {
        if (level >= ArchipelagoOptions.getGoalLevel()) {
            if (WynnpelagoClient.client != null) {
                WynnpelagoClient.client.setGameState(ClientStatus.CLIENT_GOAL);
            }
        }
        if (level >= maxLevel) {
            setXPContribution("100");
        } else {
            setXPContribution("0");
        }
    }

    private void setXPContribution(String contribution) {
        Handlers.Chat.queueChatCommand("gu xp " + contribution);
        Wynnpelago.LOGGER.info("Set XP Contribution: {}", contribution);
    }
}
