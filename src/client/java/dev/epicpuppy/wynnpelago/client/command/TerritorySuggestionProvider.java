package dev.epicpuppy.wynnpelago.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.wynntils.core.components.Models;
import dev.epicpuppy.wynnpelago.client.unlock.TerritoryUnlock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class TerritorySuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    private final boolean isUnlock;

    TerritorySuggestionProvider(boolean isUnlock) {
        this.isUnlock = isUnlock;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder)
            throws CommandSyntaxException {
        Collection<String> territories;
        if (isUnlock) {
            territories = new ArrayList<>(Models.Territory.getTerritoryNames().toList());
            territories.removeAll(TerritoryUnlock.unlockedTerritories);
        } else {
            territories = new ArrayList<>(TerritoryUnlock.unlockedTerritories);
            territories.removeAll(TerritoryUnlock.RESPAWN_TERRITORIES);
        }

        String partial = "";
        try {
            partial = StringArgumentType.getString(context, "territory");
        } catch (IllegalArgumentException ignored) {
        }

        for (String territory : territories) {
            if (territory.startsWith(partial)) builder.suggest(territory);
        }

        return builder.buildFuture();
    }
}
