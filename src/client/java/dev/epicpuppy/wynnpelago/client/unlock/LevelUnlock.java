package dev.epicpuppy.wynnpelago.client.unlock;

import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.providers.LevelProvider;
import io.github.archipelagomw.ClientStatus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class LevelUnlock {
    public static int maxLevel = 5;

    public static void resetMaxLevel() {
        maxLevel = 5;
        enforceMaxLevel();
    }

    public static void increaseMaxLevel() {
        maxLevel += 5;
        enforceMaxLevel();
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                .append(Component.literal("Your max level is now "))
                .append(Component.literal(String.valueOf(maxLevel)).withStyle(ChatFormatting.GREEN)));
    }

    private static void enforceMaxLevel() {
        if (LevelProvider.getLevel() >= maxLevel) {
            McUtils.sendChat("/gu xp 100");
        } else {
            McUtils.sendChat("/gu xp 0");
        }
    }

    public LevelUnlock() {
        LevelProvider.LEVEL_UP_EVENT.register(this::onLevelUp);
    }

    private void onLevelUp(int prevLevel, int newLevel) {
        enforceMaxLevel();
        if (newLevel >= 20) {
            WynnpelagoClient.INSTANCE.setGameState(ClientStatus.CLIENT_GOAL);
        }
    }
}
