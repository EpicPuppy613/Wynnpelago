package dev.epicpuppy.wynnpelago.client;

import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Managers;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.check.CaveCheck;
import dev.epicpuppy.wynnpelago.client.check.DiscoveryCheck;
import dev.epicpuppy.wynnpelago.client.check.LevelCheck;
import dev.epicpuppy.wynnpelago.client.check.QuestCheck;
import dev.epicpuppy.wynnpelago.client.unlock.RegionUnlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class WynnpelagoClient implements ClientModInitializer {
	private static CaveCheck caveCheck;
	private static DiscoveryCheck discoveryCheck;
	private static LevelCheck levelCheck;
	private static QuestCheck questCheck;

	private static RegionUnlock regionUnlock;

	// Enable all Wynnpelago features (only when connected to an Archipelago server)
	public static boolean enabled = false;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		caveCheck = new CaveCheck();
		discoveryCheck = new DiscoveryCheck();
		levelCheck = new LevelCheck();
		questCheck = new QuestCheck();

		regionUnlock = new RegionUnlock();

		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("wynnpelago")
					.then(ClientCommandManager.literal("version").executes(WynnpelagoClient::executeVersionCommand))
					.then(ClientCommandManager.literal("enable").executes(WynnpelagoClient::executeEnableCommand)));
		}));
	}

	private static int executeVersionCommand(CommandContext<FabricClientCommandSource> context) {
		context.getSource().sendFeedback(Component.literal("Wynnpelago v" + Wynnpelago.VERSION));
		return 1;
	}

	private static int executeEnableCommand(CommandContext<FabricClientCommandSource> context) {
		enabled = !enabled;
		context.getSource().sendFeedback(Component.literal(enabled ? "Wynnpelago enabled" : "Wynnpelago disabled").withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
		return 1;
	}
}