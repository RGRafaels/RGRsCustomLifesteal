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
        UUID uuid = player.getUniqueId();
        deathBanned.add(uuid);
        plugin.getHeartManager().setHearts(uuid, 0);
        plugin.getHeartManager().save();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.death-banned", "&cYou were death-banned...")));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (deathBanned.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.death-banned", "&cYou were death-banned...")));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == null || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (plugin.getCustomItems().isHeartItem(item)) {
            event.setCancelled(true);
            if (plugin.getHeartManager().getHearts(player) >= plugin.getMaxHearts()) {
                player.sendMessage(ChatColor.RED + "Your heart count is already at the maximum.");
                return;
            }
            plugin.getHeartManager().addHearts(player, 1);
            plugin.getHeartManager().syncPlayerHealth(player);
            item.setAmount(item.getAmount() - 1);
            player.sendMessage(ChatColor.GREEN + "You used a heart and gained 1 heart of life.");
            return;
        }

        if (plugin.getCustomItems().isReviveItem(item)) {
            event.setCancelled(true);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say " + player.getName() + " has triggered the revive beacon item.");
            player.sendMessage(ChatColor.YELLOW + "Use /heartrevive <player> or right click a revive beacon block to revive a death-banned player.");
        }
    }
}
