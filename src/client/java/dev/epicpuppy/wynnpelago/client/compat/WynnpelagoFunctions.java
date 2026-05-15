package dev.epicpuppy.wynnpelago.client.compat;

import com.wynntils.core.consumers.functions.Function;
import com.wynntils.core.consumers.functions.arguments.Argument;
import com.wynntils.core.consumers.functions.arguments.FunctionArguments;
import com.wynntils.utils.type.CappedValue;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import dev.epicpuppy.wynnpelago.client.unlock.GearUnlock;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;
import java.util.List;

public class WynnpelagoFunctions {
    public static class WpEnabledFunction extends Function<Boolean> {
        @Override
        public Boolean getValue(FunctionArguments functionArguments) {
            return WynnpelagoClient.enabled;
        }
    }

    public static class WpMaxLevelFunction extends Function<CappedValue> {
        @Override
        public CappedValue getValue(FunctionArguments functionArguments) {
            return new CappedValue(LevelUnlock.maxLevel, ArchipelagoOptions.getGoalLevel());
        }
    }

    public static class WpIsGearLockEnabled extends Function<Boolean> {
        @Override
        public Boolean getValue(FunctionArguments functionArguments) {
            return ArchipelagoOptions.getGearLockMode() != ArchipelagoOptions.GearLockMode.OFF;
        }
    }

    public static class WpIsGearLockTypeMode extends Function<Boolean> {
        @Override
        public Boolean getValue(FunctionArguments functionArguments) {
            return ArchipelagoOptions.getGearLockMode() == ArchipelagoOptions.GearLockMode.FULL;
        }
    }

    public static class WpIsGearLockRarityMode extends Function<Boolean> {
        @Override
        public Boolean getValue(FunctionArguments functionArguments) {
            return !ArchipelagoOptions.isSingleGearTier();
        }
    }

    public static class WpGearLevel extends Function<Integer> {
        @Override
        public Integer getValue(FunctionArguments functionArguments) {
            GearUnlock.Type type = GearUnlock.Type.fromKey(
                    functionArguments.getArgument("type").getStringValue());
            GearUnlock.Rarity rarity = GearUnlock.Rarity.fromKey(
                    functionArguments.getArgument("rarity").getStringValue());
            return GearUnlock.getMaxLevel(type, rarity);
        }

        @Override
        public FunctionArguments.Builder getArgumentsBuilder() {
            return new FunctionArguments.OptionalArgumentBuilder(
                    List.of(new Argument<>("type", String.class, ""), new Argument<>("rarity", String.class, "")));
        }
    }
}
