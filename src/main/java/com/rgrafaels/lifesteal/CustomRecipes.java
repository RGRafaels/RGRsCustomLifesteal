package com.rgrafaels.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public final class CustomRecipes {
    private final RGRsCustomLifestealWeaponPlugin plugin;
    private final CustomItems customItems;

    public CustomRecipes(RGRsCustomLifestealWeaponPlugin plugin) {
        this.plugin = plugin;
        this.customItems = plugin.getCustomItems();
    }

    public void registerRecipes() {
        ItemStack heart = customItems.createHeartItem();
        NamespacedKey heartKey = new NamespacedKey(plugin, "heart_item_recipe");
        ShapedRecipe heartRecipe = new ShapedRecipe(heartKey, heart);
        heartRecipe.shape("GAG", "ADA", "GAG");
        heartRecipe.setIngredient('G', Material.GOLD_BLOCK);
        heartRecipe.setIngredient('A', Material.DIAMOND);
        heartRecipe.setIngredient('D', Material.NETHERITE_BLOCK);
        Bukkit.addRecipe(heartRecipe);

        ItemStack revive = customItems.createReviveBlockItem();
        NamespacedKey reviveKey = new NamespacedKey(plugin, "revive_block_recipe");
        ShapedRecipe reviveRecipe = new ShapedRecipe(reviveKey, revive);
        reviveRecipe.shape("DND", "NSN", "DND");
        reviveRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        reviveRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        reviveRecipe.setIngredient('S', Material.NETHER_STAR);
        Bukkit.addRecipe(reviveRecipe);
    }
}
