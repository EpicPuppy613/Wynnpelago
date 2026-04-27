package dev.epicpuppy.wynnpelago.client.providers;

import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelProvider {
    private static final String LEVEL_UP_PATTERN = "§6\\s+§lLevel Up!";
    private static final Pattern LEVEL_LEVEL_PATTERN = Pattern.compile("§e\\s+You are now combat level ([0-9]+)");

    public static final Event<LevelUp> LEVEL_UP_EVENT =
            EventFactory.createArrayBacked(LevelUp.class, callbacks -> (level) -> {
                for (LevelUp callback : callbacks) {
                    callback.onLevel(level);
                }
            });
    private boolean levelUp = false;

    public static int getLevel() {
        return Models.CombatXp.getCombatLevel().current();
    }

    public LevelProvider() {
        ClientReceiveMessageEvents.GAME.register(this::onChatMessage);
    }

    private void onChatMessage(Component message, boolean overlay) {
        if (overlay) return;

        if (levelUp) {
            Matcher result = LEVEL_LEVEL_PATTERN.matcher(message.getString());
            if (result.find()) {
                Wynnpelago.LOGGER.info("Level: {}", result.group(1));
                LEVEL_UP_EVENT.invoker().onLevel(Integer.parseInt(result.group(1)));
                levelUp = false;
            }
        } else if (message.getString().matches(LEVEL_UP_PATTERN)) {
            levelUp = true;
        }
    }

    @FunctionalInterface
    public interface LevelUp {
        void onLevel(int newLevel);
    }
}
