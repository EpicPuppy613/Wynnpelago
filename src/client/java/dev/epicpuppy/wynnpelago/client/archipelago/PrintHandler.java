package dev.epicpuppy.wynnpelago.client.archipelago;

import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import io.github.archipelagomw.Print.APPrintJsonType;
import io.github.archipelagomw.Print.APPrintPart;
import io.github.archipelagomw.Print.APPrintType;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.PrintJSONEvent;
import io.github.archipelagomw.flags.NetworkItem;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PrintHandler {
    public static boolean itemSendFilter = false;
    public static boolean connectionFilter = false;

    @ArchipelagoEventListener
    public static void onPrint(PrintJSONEvent event) {
        if (event.type == APPrintJsonType.ItemSend && itemSendFilter) {
            if (event.item.playerID != WynnpelagoClient.client.getSlot()
                    && event.player != WynnpelagoClient.client.getSlot()) {
                return;
            }
        } else if ((event.type == APPrintJsonType.Join
                        || event.type == APPrintJsonType.Part
                        || event.type == APPrintJsonType.TagsChanged)
                && connectionFilter) {
            if (event.player != WynnpelagoClient.client.getSlot()) {
                return;
            }
        }
        MutableComponent component = Component.empty();
        for (APPrintPart part : event.apPrint.parts) {
            if (part == null) continue;
            if (part.type == APPrintType.color) {
                component.append(Component.literal(part.text)).withColor(part.color.color.getRGB());
            } else {
                ChatFormatting format =
                        switch (part.type) {
                            case playerName, playerID -> {
                                if (Objects.equals(part.text, WynnpelagoClient.client.getAlias())) {
                                    yield ChatFormatting.LIGHT_PURPLE;
                                }
                                yield ChatFormatting.YELLOW;
                            }
                            case itemName, itemID -> {
                                if (event.item.flags == 0) {
                                    yield ChatFormatting.AQUA;
                                } else if ((event.item.flags & NetworkItem.ADVANCEMENT) != 0) {
                                    yield ChatFormatting.BLUE;
                                } else if ((event.item.flags & NetworkItem.USEFUL) != 0) {
                                    yield ChatFormatting.DARK_AQUA;
                                } else if ((event.item.flags & NetworkItem.TRAP) != 0) {
                                    yield ChatFormatting.RED;
                                } else {
                                    yield ChatFormatting.AQUA;
                                }
                            }
                            case locationName, locationID -> ChatFormatting.GREEN;
                            case null, default -> ChatFormatting.WHITE;
                        };
                component.append(Component.literal(part.text).withStyle(format));
            }
        }
        WynnpelagoClient.sendClientMessage(WynnpelagoClient.getAPPrefix().append(component));
    }
}
