package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import io.github.archipelagomw.Print.APPrintPart;
import io.github.archipelagomw.Print.APPrintType;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.PrintJSONEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PrintHandler {
    @ArchipelagoEventListener
    public static void onPrint(PrintJSONEvent event) {
        MutableComponent component = Component.empty();
        for (APPrintPart part : event.apPrint.parts) {
            if (part == null) continue;
            if (part.type == APPrintType.color) {
                component.append(Component.literal(part.text)).withColor(part.color.color.getRGB());
            } else {
                ChatFormatting format =
                        switch (part.type) {
                            case playerName, playerID -> ChatFormatting.YELLOW;
                            case itemName, itemID -> ChatFormatting.AQUA;
                            case locationName, locationID -> ChatFormatting.GREEN;
                            default -> ChatFormatting.RESET;
                        };
                component.append(Component.literal(part.text).withStyle(format));
            }
        }
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getAPPrefix().append(component));
    }
}
