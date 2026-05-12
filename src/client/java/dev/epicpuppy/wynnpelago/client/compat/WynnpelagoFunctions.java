package dev.epicpuppy.wynnpelago.client.compat;

import com.wynntils.core.consumers.functions.Function;
import com.wynntils.core.consumers.functions.arguments.FunctionArguments;
import com.wynntils.utils.type.CappedValue;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.unlock.LevelUnlock;

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
            return new CappedValue(LevelUnlock.maxLevel, 121);
        }
    }
}
