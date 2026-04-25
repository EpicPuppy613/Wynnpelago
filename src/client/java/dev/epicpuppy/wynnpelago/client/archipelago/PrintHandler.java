package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import io.github.archipelagomw.Print.APPrintPart;
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
            ChatFormatting format =
                    switch (part.type) {
                        default -> ChatFormatting.RESET;
                    };
            component.append(Component.literal(part.text).withStyle(format));
        }
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getAPPrefix().append(component));
    }
}
