package dev.epicpuppy.wynnpelago.client.unlock;

import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.gear.type.GearType;
import com.wynntils.models.items.items.game.GearItem;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import dev.epicpuppy.wynnpelago.client.archipelago.ArchipelagoOptions;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GearUnlock {
    public static GearLevel maxGearLevel = new GearLevel();
    public static GearLevel maxArmorLevel = new GearLevel();
    public static GearLevel maxAccessoryLevel = new GearLevel();
    public static GearLevel maxWeaponLevel = new GearLevel();

    private static int messageCooldown = 0;

    public static int getMaxLevel(Type type, Rarity rarity) {
        if (ArchipelagoOptions.getGearLockMode() == ArchipelagoOptions.GearLockMode.UNIFIED) {
            return maxGearLevel.getMax(rarity);
        }
        return switch (type) {
            case ARMOR -> maxArmorLevel.getMax(rarity);
            case ACCESSORY -> maxAccessoryLevel.getMax(rarity);
            case WEAPON -> maxWeaponLevel.getMax(rarity);
            case null, default -> Integer.MAX_VALUE;
        };
    }

    public static synchronized void resetMaxLevels() {
        maxGearLevel = new GearLevel();
        maxArmorLevel = new GearLevel();
        maxAccessoryLevel = new GearLevel();
        maxWeaponLevel = new GearLevel();
    }

    public static void processIncrease(String item) {
        List<String> parts = List.of(item.split(" "));
        Rarity rarity;
        if (parts.size() == 2) {
            rarity = Rarity.ALL;
        } else {
            rarity = switch (parts.get(1)) {
                case "Rare" -> Rarity.RARE;
                case "Legendary+" -> Rarity.LEGENDARY;
                default -> Rarity.UNIQUE;
            };
        }
        Type type =
                switch (parts.getLast()) {
                    case "Armor" -> Type.ARMOR;
                    case "Accessories" -> Type.ACCESSORY;
                    case "Weapons" -> Type.WEAPON;
                    default -> Type.GEAR;
                };
        increaseMaxLevel(type, rarity);
    }

    public static synchronized void increaseMaxLevel(Type type, Rarity rarity) {
        int increment = ArchipelagoOptions.getGearLevelIncrement();
        switch (type) {
            case GEAR:
                maxGearLevel.modify(rarity, increment);
                WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("Your can now use ")
                                .append(rarity.getDisplay())
                                .append(" gear up to level "))
                        .append(Component.literal(String.valueOf(maxGearLevel.getMax(rarity)))
                                .withStyle(ChatFormatting.BLUE)));
                break;
            case ARMOR:
                maxArmorLevel.modify(rarity, increment);
                WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("Your can now use ")
                                .append(rarity.getDisplay())
                                .append(" armor up to level "))
                        .append(Component.literal(String.valueOf(maxArmorLevel.getMax(rarity)))
                                .withStyle(ChatFormatting.BLUE)));
                break;
            case ACCESSORY:
                maxAccessoryLevel.modify(rarity, increment);
                WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("Your can now use ")
                                .append(rarity.getDisplay())
                                .append(" accessories up to level "))
                        .append(Component.literal(String.valueOf(maxAccessoryLevel.getMax(rarity)))
                                .withStyle(ChatFormatting.BLUE)));
                break;
            case WEAPON:
                maxWeaponLevel.modify(rarity, increment);
                WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("Your can now use ")
                                .append(rarity.getDisplay())
                                .append(" weapons up to level "))
                        .append(Component.literal(String.valueOf(maxWeaponLevel.getMax(rarity)))
                                .withStyle(ChatFormatting.BLUE)));
                break;
        }
    }

    public static boolean canUseItem(ItemStack item) {
        if (!WynnpelagoClient.enabled) {
            return true;
        }

        GearItem gearItem = Models.Item.asWynnItem(item, GearItem.class).orElse(null);
        if (gearItem == null) {
            return true;
        }

        Type type = Type.fromType(gearItem.getGearType());
        if (type == null) {
            return true;
        }
        int maxLevel = getMaxLevel(type, Rarity.fromTier(gearItem.getGearTier()));
        if (gearItem.getLevel() > maxLevel) {
            if (messageCooldown <= 0) {
                WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("You have not unlocked using this item yet")
                                .withStyle(ChatFormatting.RED)));
                messageCooldown = 20;
            }
            return false;
        }
        return true;
    }

    public GearUnlock() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        UseItemCallback.EVENT.register(this::onUse);
        ClientPreAttackCallback.EVENT.register(this::onAttack);
    }

    private void onTick(Minecraft client) {
        if (messageCooldown > 0) {
            messageCooldown--;
        }
    }

    private InteractionResult onUse(Player player, Level world, InteractionHand hand) {
        if (ArchipelagoOptions.getGearLockMode() == ArchipelagoOptions.GearLockMode.OFF) {
            return InteractionResult.PASS;
        }
        return canUseItem(player.getMainHandItem()) ? InteractionResult.PASS : InteractionResult.FAIL;
    }

    private boolean onAttack(Minecraft client, LocalPlayer player, int clickCount) {
        if (ArchipelagoOptions.getGearLockMode() == ArchipelagoOptions.GearLockMode.OFF) {
            return false;
        }

        if (!canUseItem(player.getMainHandItem())) {
            if (messageCooldown <= 0) {
                WynnpelagoClient.sendClientMessage(WynnpelagoClient.getWPPrefix()
                        .append(Component.literal("You have not unlocked using this weapon")
                                .withStyle(ChatFormatting.RED)));
                messageCooldown = 40;
            }
            return true;
        }

        return false;
    }

    @Getter
    @Setter
    public static class GearLevel {
        private int unique;
        private int rare;
        private int legendary;

        public GearLevel() {
            unique = 0;
            rare = 0;
            legendary = 0;
        }

        int getMax(Rarity rarity) {
            if (rarity == null) {
                return Integer.MAX_VALUE;
            }
            if (ArchipelagoOptions.isSingleGearTier()) {
                return unique;
            }
            return switch (rarity) {
                case ALL, UNIQUE -> unique;
                case RARE -> rare;
                case LEGENDARY -> legendary;
            };
        }

        void modify(Rarity rarity, int amount) {
            if (ArchipelagoOptions.isSingleGearTier()) {
                unique += amount;
                return;
            }
            switch (rarity) {
                case UNIQUE -> unique += amount;
                case RARE -> rare += amount;
                case LEGENDARY -> legendary += amount;
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        GEAR("gear"),
        ARMOR("armor"),
        ACCESSORY("accessories"),
        WEAPON("weapons");

        private final String key;

        public static Type fromType(GearType type) {
            return switch (type) {
                case GearType.HELMET, GearType.CHESTPLATE, GearType.LEGGINGS, GearType.BOOTS -> ARMOR;
                case GearType.BOW, GearType.WAND, GearType.RELIK, GearType.SPEAR, GearType.DAGGER -> WEAPON;
                case GearType.ACCESSORY, GearType.RING, GearType.BRACELET, GearType.NECKLACE -> ACCESSORY;
                default -> null;
            };
        }

        public static Type fromKey(String key) {
            for (Type type : Type.values()) {
                if (Objects.equals(type.getKey(), key)) {
                    return type;
                }
            }
            return null;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum Rarity {
        ALL(Component.literal("Unique+").withStyle(ChatFormatting.GOLD), "all"),
        UNIQUE(Component.literal("Unique").withStyle(ChatFormatting.YELLOW), "unique"),
        RARE(Component.literal("Rare").withStyle(ChatFormatting.LIGHT_PURPLE), "rare"),
        LEGENDARY(Component.literal("Legendary+").withStyle(ChatFormatting.AQUA), "legendary");

        private final Component display;
        private final String key;

        public static Rarity fromTier(GearTier tier) {
            return switch (tier) {
                case GearTier.UNIQUE -> UNIQUE;
                case GearTier.RARE -> RARE;
                case GearTier.LEGENDARY, GearTier.FABLED, GearTier.MYTHIC -> LEGENDARY;
                default -> null;
            };
        }

        public static Rarity fromKey(String key) {
            for (Rarity rarity : Rarity.values()) {
                if (Objects.equals(rarity.getKey(), key)) {
                    return rarity;
                }
            }
            return null;
        }
    }
}
