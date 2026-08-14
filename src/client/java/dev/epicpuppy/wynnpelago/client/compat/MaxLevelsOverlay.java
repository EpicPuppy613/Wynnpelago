package dev.epicpuppy.wynnpelago.client.compat;

import com.mojang.blaze3d.platform.Window;
import com.wynntils.core.consumers.overlays.OverlayPosition;
import com.wynntils.core.consumers.overlays.OverlaySize;
import com.wynntils.core.consumers.overlays.TextOverlay;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.VerticalAlignment;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class MaxLevelsOverlay extends TextOverlay {
    public MaxLevelsOverlay() {
        super(
                new OverlayPosition(
                        0,
                        6,
                        VerticalAlignment.MIDDLE,
                        HorizontalAlignment.LEFT,
                        OverlayPosition.AnchorSection.MIDDLE_LEFT),
                new OverlaySize(120, 45),
                HorizontalAlignment.LEFT,
                VerticalAlignment.MIDDLE);
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Window window) {
        if (!WynnpelagoClient.enabled) {
            return;
        }
        super.render(guiGraphics, deltaTracker, window);
    }

    @Override
    protected String getTemplate() {
        return """
                {to_background_text("Max Levels";from_hex("#FFFFFF");from_hex("#00AA00");"PILL";"PILL")}
                {styled_text(concat("&2";to_fancy_text("Combat ");"&a";to_fancy_text(str(curr(wp_max_level)))))}
                {styled_text(if_str(wp_is_gear_lock_enabled;
                    if_str(wp_is_gear_lock_type_mode;
                    concat("&a";
                        to_fancy_text("Armor");if_str(wp_is_gear_lock_rarity_mode;
                            concat(" &e";to_fancy_text(str(wp_gear_level("armor";"unique")));" &d";to_fancy_text(str(wp_gear_level("armor";"rare")));" &b";to_fancy_text(str(wp_gear_level("armor";"legendary"))));
                            concat(" &6";to_fancy_text(str(wp_gear_level("armor";"all")))))
                    ;"\\n&a";
                        to_fancy_text("Accessories");if_str(wp_is_gear_lock_rarity_mode;
                            concat(" &e";to_fancy_text(str(wp_gear_level("accessories";"unique")));" &d";to_fancy_text(str(wp_gear_level("accessories";"rare")));" &b";to_fancy_text(str(wp_gear_level("accessories";"legendary"))));
                            concat(" &6";to_fancy_text(str(wp_gear_level("accessories";"all")))))
                    ;"\\n&a";
                        to_fancy_text("Weapons");if_str(wp_is_gear_lock_rarity_mode;
                            concat(" &e";to_fancy_text(str(wp_gear_level("weapons";"unique")));" &d";to_fancy_text(str(wp_gear_level("weapons";"rare")));" &b";to_fancy_text(str(wp_gear_level("weapons";"legendary"))));
                            concat(" &6";to_fancy_text(str(wp_gear_level("weapons";"all"))))));
                    concat("&a";
                        to_fancy_text("Gear");if_str(wp_is_gear_lock_rarity_mode;
                            concat(" &e";to_fancy_text(str(wp_gear_level("gear";"unique")));" &d";to_fancy_text(str(wp_gear_level("gear";"rare")));" &b";to_fancy_text(str(wp_gear_level("gear";"legendary"))));
                            concat(" &6";to_fancy_text(str(wp_gear_level("gear";"all")))))))
                ;""))}
                """;
    }

    @Override
    protected String getPreviewTemplate() {
        return """
                {to_background_text("Max Levels";from_hex("#FFFFFF");from_hex("#00AA00");"PILL";"PILL")}
                {styled_text(concat("&2";to_fancy_text("Combat ");"&a";to_fancy_text("42")))}
                {styled_text(concat("&a";to_fancy_text("Gear");" &e";to_fancy_text("10");" &d";to_fancy_text("20");" &b";to_fancy_text("30")))}
                """;
    }
}
