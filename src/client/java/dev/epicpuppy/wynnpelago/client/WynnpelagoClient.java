package dev.epicpuppy.wynnpelago.client;

import dev.epicpuppy.wynnpelago.client.check.CaveCheck;
import dev.epicpuppy.wynnpelago.client.check.DiscoveryCheck;
import dev.epicpuppy.wynnpelago.client.check.LevelCheck;
import dev.epicpuppy.wynnpelago.client.check.QuestCheck;
import dev.epicpuppy.wynnpelago.client.unlock.RegionUnlock;
import net.fabricmc.api.ClientModInitializer;

public class WynnpelagoClient implements ClientModInitializer {
	private static CaveCheck caveCheck;
	private static DiscoveryCheck discoveryCheck;
	private static LevelCheck levelCheck;
	private static QuestCheck questCheck;

	private static RegionUnlock regionUnlock;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		caveCheck = new CaveCheck();
		discoveryCheck = new DiscoveryCheck();
		levelCheck = new LevelCheck();
		questCheck = new QuestCheck();

		regionUnlock = new RegionUnlock();
	}
}