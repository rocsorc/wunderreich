package de.ambertation.wunderreich.gui.whisperer;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;

public class EnchantmentInfo {
    public final ItemStack input;
    public final ItemStack type;
    public final int baseXP;

    public EnchantmentInfo(Holder<Enchantment> enchantment) {
        final var d = enchantment.value().definition();
        int mul = Math.min(6, d.anvilCost());
        var category = LegacyEnchantmentCategories.fromEnchantment(enchantment);
        int rarityMultiplicator = 1;
        if (category == LegacyEnchantmentCategories.ARMOR) {
            input = new ItemStack(Blocks.IRON_BLOCK.asItem(), 2 * mul);
            type = new ItemStack(Items.ARMOR_STAND);
        } else if (category == LegacyEnchantmentCategories.ARMOR_HEAD) {
            input = new ItemStack(Blocks.COPPER_BLOCK.asItem(), 1 * mul);
            type = new ItemStack(Items.LEATHER_HELMET);
        } else if (category == LegacyEnchantmentCategories.ARMOR_CHEST) {
            input = new ItemStack(Blocks.OXIDIZED_COPPER.asItem(), 1 * mul);
            type = new ItemStack(Items.LEATHER_CHESTPLATE);
            rarityMultiplicator = 2;
        } else if (category == LegacyEnchantmentCategories.ARMOR_LEGS) {
            input = new ItemStack(Blocks.EXPOSED_COPPER.asItem(), 1 * mul);
            type = new ItemStack(Items.LEATHER_LEGGINGS);
            rarityMultiplicator = 2;
        } else if (category == LegacyEnchantmentCategories.ARMOR_FEET) {
            input = new ItemStack(Blocks.RAW_COPPER_BLOCK.asItem(), 1 * mul);
            type = new ItemStack(Items.LEATHER_BOOTS);
        } else if (category == LegacyEnchantmentCategories.BOW) {
            input = new ItemStack(Blocks.COAL_BLOCK.asItem(), 2 * mul);
            type = new ItemStack(Items.BOW);
        } else if (category == LegacyEnchantmentCategories.WEAPON) {
            input = new ItemStack(Items.LAPIS_BLOCK, 4 * mul);
            type = new ItemStack(Items.WOODEN_SWORD);
            rarityMultiplicator = 2;
        } else if (category == LegacyEnchantmentCategories.DIGGER) {
            input = new ItemStack(Blocks.SMOOTH_STONE.asItem(), 5 * mul);
            type = new ItemStack(Items.WOODEN_PICKAXE);
        } else if (category == LegacyEnchantmentCategories.FISHING_ROD) {
            input = new ItemStack(Blocks.GRAVEL.asItem(), Math.min(64, 32 * mul));
            type = new ItemStack(Items.FISHING_ROD);
        } else if (category == LegacyEnchantmentCategories.TRIDENT) {
            input = new ItemStack(Blocks.SEA_LANTERN.asItem(), 4 * mul);
            type = new ItemStack(Items.TRIDENT);
            rarityMultiplicator = 3;
        } else if (category == LegacyEnchantmentCategories.CROSSBOW) {
            input = new ItemStack(Blocks.DARK_OAK_LOG.asItem(), 4 * mul);
            type = new ItemStack(Items.CROSSBOW);
        } else if (category == LegacyEnchantmentCategories.VANISHABLE) {
            input = new ItemStack(Blocks.REDSTONE_BLOCK.asItem(), 8 * mul);
            type = new ItemStack(Items.COMPASS);
        } else if (category == LegacyEnchantmentCategories.BREAKABLE) {
            input = new ItemStack(Blocks.EMERALD_BLOCK.asItem(), 1 * mul);
            type = new ItemStack(Items.SMITHING_TABLE);
            rarityMultiplicator = 2;
        } else if (category == LegacyEnchantmentCategories.WEARABLE) {
            input = new ItemStack(Blocks.PINK_WOOL.asItem(), 4 * mul);
            type = new ItemStack(Items.CARVED_PUMPKIN);
        } else {
            input = new ItemStack(Blocks.COAL_BLOCK.asItem(), 2 * mul);
            type = ItemStack.EMPTY;
        }

        baseXP = 2 * mul * rarityMultiplicator;
    }
}
