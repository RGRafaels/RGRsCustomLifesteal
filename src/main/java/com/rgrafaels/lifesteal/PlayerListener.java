package com.rgrafaels.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerListener implements Listener {
    private final RGRsCustomLifestealWeaponPlugin plugin;
    private final Set<UUID> deathBanned = new HashSet<>();

    public PlayerListener(RGRsCustomLifestealWeaponPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getHeartManager().initializePlayer(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        deathBanned.add(player.getUniqueId());
        plugin.getHeartManager().setHearts(player.getUniqueId(), 0);
        plugin.getHeartManager().save();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.death-banned", "&cYou were death-banned after dying.")));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (deathBanned.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.death-banned", "&cYou were death-banned after dying.")));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == null || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (plugin.getCustomItems().isHeartItem(item)) {
            event.setCancelled(true);
            int maxHearts = plugin.getMaxHearts();
            if (plugin.getHeartManager().getHearts(player) >= maxHearts) {
                player.sendMessage(ChatColor.RED + "Your heart count is already at the maximum.");
                return;
            }
            int amount = item.getAmount();
            if (amount <= 0) {
                return;
            }
            plugin.getHeartManager().addHearts(player, 1);
            plugin.getHeartManager().syncPlayerHealth(player);
            item.setAmount(amount - 1);
            player.sendMessage(ChatColor.GREEN + "You used a heart and gained 1 heart of life.");
            plugin.getHeartManager().save();
            return;
        }

        if (plugin.getCustomItems().isReviveItem(item)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "Use /heartrevive <player> to revive a death-banned player.");
        }
    }

    public boolean isDeathBanned(Player player) {
        return deathBanned.contains(player.getUniqueId());
    }

    public void markDeathBanned(Player player) {
        deathBanned.add(player.getUniqueId());
    }

    public void clearDeathBanned(Player player) {
        deathBanned.remove(player.getUniqueId());
    }
}
