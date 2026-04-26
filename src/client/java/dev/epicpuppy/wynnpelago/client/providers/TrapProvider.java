package dev.epicpuppy.wynnpelago.client.providers;

import java.util.ArrayDeque;
import java.util.Queue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

public class TrapProvider {
    public static final Event<Trap> TRAP_EVENT = EventFactory.createArrayBacked(Trap.class, callbacks -> (type) -> {
        for (Trap callback : callbacks) {
            callback.onActivate(type);
        }
    });

    private static final Queue<TrapType> trapQueue = new ArrayDeque<>();

    public TrapProvider() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public static void queueTrap(TrapType trap) {
        trapQueue.add(trap);
    }

    private void onTick(Minecraft client) {
        while (!trapQueue.isEmpty()) {
            TRAP_EVENT.invoker().onActivate(trapQueue.remove());
        }
    }

    @FunctionalInterface
    public interface Trap {
        void onActivate(TrapType type);
    }

    public enum TrapType {
        FREEZE,
        SILENCE,
        BLIND,
        KILL
    }
}
