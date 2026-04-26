package dev.epicpuppy.wynnpelago.client.providers;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
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
    private static int initialTrapCooldown = 0;

    public TrapProvider() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    public static void recieveTrap(String trap) {
        if (initialTrapCooldown > 0) return;
        switch (trap) {
            case "Freeze Trap" -> queueTrap(TrapType.FREEZE);
            case "Daze Trap" -> queueTrap(TrapType.DAZE);
            case "Blind Trap" -> queueTrap(TrapType.BLIND);
            case "Kill Trap" -> queueTrap(TrapType.KILL);
        }
    }

    public static void queueTrap(TrapType trap) {
        trapQueue.add(trap);
    }

    public static void resetInitialCooldown() {
        initialTrapCooldown = 20;
    }

    private void onTick(Minecraft client) {
        while (!trapQueue.isEmpty()) {
            TRAP_EVENT.invoker().onActivate(trapQueue.remove());
        }
        if (initialTrapCooldown > 0 && WynnpelagoClient.enabled) {
            initialTrapCooldown--;
        }
    }

    @FunctionalInterface
    public interface Trap {
        void onActivate(TrapType type);
    }

    public enum TrapType {
        FREEZE,
        DAZE,
        BLIND,
        KILL
    }
}
