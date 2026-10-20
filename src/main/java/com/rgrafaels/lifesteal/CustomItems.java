package com.rgrafaels.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CustomItems {
    private final RGRsCustomLifestealWeaponPlugin plugin;
    private final NamespacedKey heartKey;
    private final NamespacedKey reviveKey;

    public CustomItems(RGRsCustomLifestealWeaponPlugin plugin) {
        this.plugin = plugin;
        this.heartKey = new NamespacedKey(plugin, "lifesteal_heart_item");
        this.reviveKey = new NamespacedKey(plugin, "lifesteal_revive_item");
    }

    public ItemStack createHeartItem() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNullElse(plugin.getConfig().getString("heart-withdraw-item-name"), "&cHardcore Heart")));
        List<String> translatedLore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("heart-item-lore")) {
            translatedLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(translatedLore);
        meta.getPersistentDataContainer().set(heartKey, PersistentDataType.BYTE, (byte) 1);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createReviveBlockItem() {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNullElse(plugin.getConfig().getString("revive-block-name"), "&dRevive Beacon")));
        List<String> translatedLore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("revive-block-lore")) {
            translatedLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(translatedLore);
        meta.getPersistentDataContainer().set(reviveKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isHeartItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(heartKey, PersistentDataType.BYTE);
    }

    public boolean isReviveItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(reviveKey, PersistentDataType.BYTE);
    }

    public static String formatPlayerName(org.bukkit.entity.Player player) {
        if (player == null) {
            return "Unknown";
        }
        String name = Bukkit.getOfflinePlayer(player.getUniqueId()).getName();
        return name != null ? name : player.getName();
    }
}
