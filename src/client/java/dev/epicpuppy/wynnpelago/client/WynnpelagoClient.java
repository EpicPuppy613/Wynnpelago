package dev.epicpuppy.wynnpelago.client;

import com.wynntils.utils.mc.McUtils;
import dev.epicpuppy.wynnpelago.client.archipelago.event.PrintHandler;
import dev.epicpuppy.wynnpelago.client.check.CaveCheck;
import dev.epicpuppy.wynnpelago.client.check.DiscoveryCheck;
import dev.epicpuppy.wynnpelago.client.check.LevelCheck;
import dev.epicpuppy.wynnpelago.client.check.QuestCheck;
import dev.epicpuppy.wynnpelago.client.command.ArchipelagoCommand;
import dev.epicpuppy.wynnpelago.client.command.WynnpelagoCommand;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import io.github.archipelagomw.Client;
import io.github.archipelagomw.EventManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayDeque;
import java.util.Queue;

public class WynnpelagoClient extends Client implements ClientModInitializer {
	public static WynnpelagoClient INSTANCE;

	private static CaveCheck caveCheck;
	private static DiscoveryCheck discoveryCheck;
	private static LevelCheck levelCheck;
	private static QuestCheck questCheck;

	private static TerritoryUnlock territoryUnlock;

	private static Queue<Component> messageQueue;

	// Enable all Wynnpelago features (only when connected to an Archipelago server)
	public static boolean enabled = false;

	public static void sendChat(Component message) {
		messageQueue.add(getPrefix().append(message));
	}

	public static MutableComponent getPrefix() {
		return Component.empty()
				.append(Component.literal("AP").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
				.append(Component.literal(" >> ").withStyle(ChatFormatting.GRAY));
	}

	public WynnpelagoClient() {
		INSTANCE = this;
		setGame("APQuest");
	}

	public void sendArchipelago(String message) {
		sendChat(message);
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		caveCheck = new CaveCheck();
		discoveryCheck = new DiscoveryCheck();
		levelCheck = new LevelCheck();
		questCheck = new QuestCheck();

		territoryUnlock = new TerritoryUnlock();

		messageQueue = new ArrayDeque<>();

		WynnpelagoCommand.register();
		ArchipelagoCommand.register();

		ClientTickEvents.END_CLIENT_TICK.register((Minecraft client) -> {
			while (!messageQueue.isEmpty()) {
				McUtils.sendMessageToClient(messageQueue.remove());
			}
		});

		// Archipelago Events
		EventManager eventManager = getEventManager();
		eventManager.registerListener(PrintHandler.class);
	}

	@Override
	public void onError(Exception ex) {

	}

	@Override
	public void onClose(String Reason, int attemptingReconnect) {

	}
}