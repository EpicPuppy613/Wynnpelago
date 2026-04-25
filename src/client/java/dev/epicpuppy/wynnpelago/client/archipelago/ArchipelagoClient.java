package dev.epicpuppy.wynnpelago.client.archipelago;

import io.github.archipelagomw.Client;
import io.github.archipelagomw.EventManager;

public class ArchipelagoClient extends Client {
    public ArchipelagoClient() {
        setGame("Wynncraft");
        setItemsHandlingFlags(0b111);

        // Archipelago Events
        EventManager eventManager = getEventManager();
        eventManager.registerListener(ConnectionHandler.class);
        eventManager.registerListener(PrintHandler.class);
        eventManager.registerListener(ReceiveItemHandler.class);
    }

    @Override
    public void onError(Exception ex) {}

    @Override
    public void onClose(String Reason, int attemptingReconnect) {}
}
