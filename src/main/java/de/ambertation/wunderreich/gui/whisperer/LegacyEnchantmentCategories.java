package de.ambertation.wunderreich.gui.whisperer;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public enum LegacyEnchantmentCategories {
    ARMOR, ARMOR_FEET, ARMOR_LEGS, ARMOR_CHEST,
    ARMOR_HEAD,
    WEAPON,
    DIGGER,
    FISHING_ROD,
    TRIDENT,
    BREAKABLE,
    BOW,
    WEARABLE,
    CROSSBOW,
    VANISHABLE, Other;

    private static boolean hasSlot(Enchantment.EnchantmentDefinition d, EquipmentSlotGroup slot) {
        return d.slots().contains(slot);
    }

    public static LegacyEnchantmentCategories fromEnchantment(Holder<Enchantment> h) {
        if (h.is(Enchantments.VANISHING_CURSE)) return VANISHABLE;
        if (h.is(Enchantments.BINDING_CURSE)) return WEARABLE;
        if (h.is(Enchantments.MENDING)) return BREAKABLE;
        if (h.is(Enchantments.UNBREAKING)) return BREAKABLE;

        final Enchantment e = h.value();
        final Enchantment.EnchantmentDefinition d = e.definition();
        if (hasSlot(d, EquipmentSlotGroup.HEAD)) return ARMOR_HEAD;
        if (hasSlot(d, EquipmentSlotGroup.FEET)) return ARMOR_FEET;
        if (hasSlot(d, EquipmentSlotGroup.LEGS)) return ARMOR_LEGS;
        if (e.canEnchant(new ItemStack(Items.IRON_BOOTS)) &&
                e.canEnchant(new ItemStack(Items.IRON_LEGGINGS)) &&
                e.canEnchant(new ItemStack(Items.IRON_CHESTPLATE)) &&
                e.canEnchant(new ItemStack(Items.IRON_HELMET))) return ARMOR;

        if (e.canEnchant(new ItemStack(Items.IRON_LEGGINGS))) return ARMOR_LEGS;
        if (e.canEnchant(new ItemStack(Items.IRON_BOOTS))) return ARMOR_FEET;
        if (e.canEnchant(new ItemStack(Items.IRON_SWORD))) return WEAPON;
        if (e.canEnchant(new ItemStack(Items.IRON_PICKAXE))) return DIGGER;
        if (e.canEnchant(new ItemStack(Items.TRIDENT))) return TRIDENT;
        if (e.canEnchant(new ItemStack(Items.BOW))) return BOW;
        if (e.canEnchant(new ItemStack(Items.CROSSBOW))) return CROSSBOW;
        if (e.canEnchant(new ItemStack(Items.FISHING_ROD))) return FISHING_ROD;
        if (e.canEnchant(new ItemStack(Items.MACE))) return WEAPON;
        if (e.canEnchant(new ItemStack(Items.IRON_CHESTPLATE))) return ARMOR_CHEST;
        if (hasSlot(d, EquipmentSlotGroup.ARMOR)) return ARMOR;
        return Other;
    }
}
