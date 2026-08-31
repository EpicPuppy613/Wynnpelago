package dev.epicpuppy.wynnpelago.client.render;

import com.wynntils.core.text.StyledText;
import com.wynntils.services.map.pois.TerritoryPoi;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.render.Texture;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynntils.utils.render.type.VerticalAlignment;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.services.LevelService;
import dev.epicpuppy.wynnpelago.client.services.content.Location;
import dev.epicpuppy.wynnpelago.client.services.content.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GuildMapRenderer {
    public static void render(
            GuiGraphics guiGraphics, float renderX, float renderY, float xBorderOffset, float yBorderOffset) {
        float renderXOffset = renderX + xBorderOffset + 8;
        float renderYOffset = renderY + yBorderOffset + 8;

        Component gameStatus = Component.literal(String.format(
                        "Available: %s",
                        WynnpelagoClient.getContentService().getAvailableChecks()))
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format(
                                "In Logic: %s",
                                WynnpelagoClient.getContentService().getInLogicChecks()))
                        .withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format(
                                "Remaining: %s",
                                WynnpelagoClient.getContentService().getRemainingChecks()))
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
        FontRenderer.getInstance()
                .renderText(
                        guiGraphics,
                        StyledText.fromComponent(gameStatus),
                        renderXOffset,
                        renderYOffset,
                        CommonColors.WHITE,
                        HorizontalAlignment.LEFT,
                        VerticalAlignment.TOP,
                        TextShadow.OUTLINE);

        List<Location> additionalLocations = WynnpelagoClient.getContentService().getRegionless().stream()
                .filter(l -> !l.isCollected() && l.isAccessible())
                .toList();

        if (!additionalLocations.isEmpty()) {
            int yOffset = 20;

            FontRenderer.getInstance()
                    .renderText(
                            guiGraphics,
                            StyledText.fromComponent(Component.literal(
                                            String.format("Additional Checks: (%s)", additionalLocations.size()))
                                    .withStyle(ChatFormatting.DARK_AQUA)),
                            renderXOffset,
                            renderYOffset + yOffset,
                            CommonColors.WHITE,
                            HorizontalAlignment.LEFT,
                            VerticalAlignment.TOP,
                            TextShadow.OUTLINE);

            yOffset += 10;

            for (Location location : additionalLocations) {
                Component line = Component.literal("- ")
                        .withStyle(ChatFormatting.DARK_AQUA)
                        .append(Component.literal(location.getName()).withStyle(ChatFormatting.GRAY));

                FontRenderer.getInstance()
                        .renderText(
                                guiGraphics,
                                StyledText.fromComponent(line),
                                renderXOffset,
                                renderYOffset + yOffset,
                                CommonColors.WHITE,
                                HorizontalAlignment.LEFT,
                                VerticalAlignment.TOP,
                                TextShadow.OUTLINE);

                yOffset += 10;
            }
        }
    }

    public static void renderTooltip(GuiGraphics guiGraphics, int xOffset, int yOffset, TerritoryPoi territoryPoi) {
        Region region = WynnpelagoClient.getContentService().getRegion(territoryPoi.getName());
        List<Component> lines = new ArrayList<>();
        if (region == null) {
            lines.add(Component.literal("Not included in randomizer").withStyle(ChatFormatting.GRAY));
        } else {
            Region.State state = region.getState();
            lines.add(
                    Component.literal("Recommended Level " + region.getLevel()).withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal(""));
            if (state == Region.State.DISABLED) {
                lines.add(Component.literal("Not included in randomizer").withStyle(ChatFormatting.GRAY));
            } else if (state == Region.State.COMPLETE) {
                lines.add(Component.literal("All checks collected").withStyle(ChatFormatting.AQUA));
            } else {
                switch (state) {
                    case LOCKED -> lines.add(Component.literal("Locked").withStyle(ChatFormatting.RED));
                    case INACCESSIBLE ->
                        lines.add(Component.literal("Unlocked, Inaccessible").withStyle(ChatFormatting.YELLOW));
                    case ACCESSIBLE, HAS_IN_LOGIC, HAS_AVAILABLE ->
                        lines.add(Component.literal("Unlocked, Accessible").withStyle(ChatFormatting.GREEN));
                }

                lines.add(Component.literal(""));

                List<Location> inaccessible = new ArrayList<>();
                List<Location> accessible = new ArrayList<>();
                List<Location> available = new ArrayList<>();
                int level = LevelService.getLevel();
                for (Location location : region.getLocations()) {
                    if (!location.isCollected()) {
                        if (location.isAccessible()) {
                            if (level >= location.getLevel()) {
                                available.add(location);
                            } else {
                                accessible.add(location);
                            }
                        } else {
                            inaccessible.add(location);
                        }
                    }
                }

                String goalObjective = WynnpelagoClient.getContentService().getGoalObjective();

                if (!available.isEmpty()) {
                    lines.add(Component.literal(String.format("Available Checks: (%s)", available.size()))
                            .withStyle(ChatFormatting.GREEN));
                    for (Location location : available) {
                        MutableComponent line = Component.literal("- ")
                                .withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(location.getName()).withStyle(ChatFormatting.GRAY));
                        if (Objects.equals(location.getName(), goalObjective)) {
                            line.append(Component.literal(" (Goal)").withStyle(ChatFormatting.GOLD));
                        }
                        lines.add(line);
                    }
                    if (!inaccessible.isEmpty()) {
                        lines.add(Component.literal(""));
                    }
                }

                if (!accessible.isEmpty()) {
                    lines.add(Component.literal(String.format("In Logic Checks: (%s)", accessible.size()))
                            .withStyle(ChatFormatting.YELLOW));
                    for (Location location : accessible) {
                        MutableComponent line = Component.literal("- ")
                                .withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(location.getName()).withStyle(ChatFormatting.GRAY));
                        if (Objects.equals(location.getName(), goalObjective)) {
                            line.append(Component.literal(" (Goal)").withStyle(ChatFormatting.GOLD));
                        }
                        lines.add(line);
                    }
                    if (!inaccessible.isEmpty()) {
                        lines.add(Component.literal(""));
                    }
                }

                if (!inaccessible.isEmpty()) {
                    lines.add(Component.literal(String.format("Inaccessible Checks: (%s)", inaccessible.size()))
                            .withStyle(ChatFormatting.LIGHT_PURPLE));
                    for (Location location : inaccessible) {
                        MutableComponent line = Component.literal("- ")
                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                                .append(Component.literal(location.getName()).withStyle(ChatFormatting.GRAY));
                        if (Objects.equals(location.getName(), goalObjective)) {
                            line.append(Component.literal(" (Goal)").withStyle(ChatFormatting.GOLD));
                        }
                        lines.add(line);
                    }
                }
            }
        }

        int textureWidth = Texture.MAP_INFO_TOOLTIP_CENTER.width();
        float centerHeight = (float) (lines.size() * 10 + 5);
        RenderUtils.drawTexturedRect(guiGraphics, Texture.MAP_INFO_TOOLTIP_TOP, (float) xOffset, (float) yOffset);
        RenderUtils.drawScalingTexturedRect(
                guiGraphics,
                Texture.MAP_INFO_TOOLTIP_CENTER.identifier(),
                (float) xOffset,
                (float) (Texture.MAP_INFO_TOOLTIP_TOP.height() + yOffset),
                (float) textureWidth,
                centerHeight,
                textureWidth,
                Texture.MAP_INFO_TOOLTIP_CENTER.height());
        RenderUtils.drawTexturedRect(
                guiGraphics,
                Texture.MAP_INFO_NAME_BOX,
                (float) xOffset,
                (float) Texture.MAP_INFO_TOOLTIP_TOP.height() + centerHeight + (float) yOffset);
        float renderYOffset = (float) (10 + yOffset);

        RenderUtils.enableScissor(guiGraphics, 10 + xOffset, 0, textureWidth - 20, guiGraphics.guiHeight());

        for (Component line : lines) {
            FontRenderer.getInstance()
                    .renderText(
                            guiGraphics,
                            StyledText.fromComponent(line),
                            10.0F + (float) xOffset,
                            renderYOffset,
                            CommonColors.WHITE,
                            HorizontalAlignment.LEFT,
                            VerticalAlignment.TOP,
                            TextShadow.OUTLINE,
                            1.0F);
            renderYOffset += 10.0F;
        }

        RenderUtils.disableScissor(guiGraphics);

        FontRenderer.getInstance()
                .renderAlignedTextInBox(
                        guiGraphics,
                        StyledText.fromString(territoryPoi.getName()),
                        (float) (7 + xOffset),
                        (float) (textureWidth + xOffset),
                        (float) Texture.MAP_INFO_TOOLTIP_TOP.height() + centerHeight + (float) yOffset,
                        (float) Texture.MAP_INFO_TOOLTIP_TOP.height()
                                + centerHeight
                                + (float) Texture.MAP_INFO_NAME_BOX.height()
                                + (float) yOffset,
                        0.0F,
                        CommonColors.WHITE,
                        HorizontalAlignment.LEFT,
                        VerticalAlignment.MIDDLE,
                        TextShadow.OUTLINE);
    }
}
