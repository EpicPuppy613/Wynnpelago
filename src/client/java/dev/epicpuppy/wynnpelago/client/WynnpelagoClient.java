package dev.epicpuppy.wynnpelago.client;

import dev.epicpuppy.wynnpelago.client.unlock.RegionUnlock;
import net.fabricmc.api.ClientModInitializer;

public class WynnpelagoClient implements ClientModInitializer {
	private RegionUnlock regionLockService;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		regionLockService = new RegionUnlock();
	}
}