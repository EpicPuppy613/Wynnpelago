package dev.epicpuppy.wynnpelago.client;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.Wynnpelago;
import dev.epicpuppy.wynnpelago.client.check.CaveCheck;
import dev.epicpuppy.wynnpelago.client.check.DiscoveryCheck;
import dev.epicpuppy.wynnpelago.client.check.LevelCheck;
import dev.epicpuppy.wynnpelago.client.check.QuestCheck;
import dev.epicpuppy.wynnpelago.client.command.WynnpelagoCommand;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class WynnpelagoClient implements ClientModInitializer {
	private static CaveCheck caveCheck;
	private static DiscoveryCheck discoveryCheck;
	private static LevelCheck levelCheck;
	private static QuestCheck questCheck;

	private static TerritoryUnlock territoryUnlock;

	// Enable all Wynnpelago features (only when connected to an Archipelago server)
	public static boolean enabled = false;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		caveCheck = new CaveCheck();
		discoveryCheck = new DiscoveryCheck();
		levelCheck = new LevelCheck();
		questCheck = new QuestCheck();

		territoryUnlock = new TerritoryUnlock();

		WynnpelagoCommand.register();
	}
}