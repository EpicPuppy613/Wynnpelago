package dev.epicpuppy.wynnpelago.client.services;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;

public class TextDisplayService {
    // Uses mixin: TextDisplayMixin

    public static final Event<TextDisplayUpdate> TEXT_DISPLAY_UPDATE_EVENT =
            EventFactory.createArrayBacked(TextDisplayUpdate.class, callbacks -> (component) -> {
                for (TextDisplayUpdate callback : callbacks) {
                    callback.onUpdate(component);
                }
            });

    public static void onTextDisplayUpdate(Component component) {
        TEXT_DISPLAY_UPDATE_EVENT.invoker().onUpdate(component);
    }

    @FunctionalInterface
    public interface TextDisplayUpdate {
        void onUpdate(Component component);
    }
}
