package dev.epicpuppy.wynnpelago.client.unlock;

import com.wynntils.core.components.Handlers;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.services.LevelService;
import io.github.archipelagomw.ClientStatus;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class LevelUnlock {
    public static int maxLevel = 1;

    private static String lastSend = null;

    public static synchronized void resetMaxLevel() {
        maxLevel = 1;
        enforceMaxLevel();
    }

    public static synchronized void increaseMaxLevel() {
        maxLevel += ArchipelagoOptions.getLevelIncrement();
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                .append(Component.literal("Your max level is now "))
                .append(Component.literal(String.valueOf(maxLevel)).withStyle(ChatFormatting.GREEN)));
        enforceMaxLevel();
    }

    public LevelUnlock() {
        LevelService.LEVEL_UP_EVENT.register(this::onLevelUp);
    }

    private void onLevelUp(int level) {
        if (level >= ArchipelagoOptions.getGoalLevel()) {
            if (WynnpelagoClient.client != null) {
                WynnpelagoClient.client.setGameState(ClientStatus.CLIENT_GOAL);
            }
        }
        enforceMaxLevel();
    }

    private static void enforceMaxLevel() {
        if (LevelService.getLevel() >= maxLevel) {
            setXPContribution("100");
        } else {
            setXPContribution("0");
        }
    }

    private static void setXPContribution(String contribution) {
        if (Objects.equals(contribution, lastSend)) {
            return;
        }
        lastSend = contribution;
        Handlers.Chat.queueChatCommand("gu xp " + contribution);
        Wynnpelago.LOGGER.info("Set XP Contribution: {}", contribution);
    }
}
