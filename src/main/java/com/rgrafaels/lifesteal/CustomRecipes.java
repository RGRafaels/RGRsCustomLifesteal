package com.rgrafaels.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class HeartCommand implements CommandExecutor {
    private final RGRsCustomLifestealWeaponPlugin plugin;

    public HeartCommand(RGRsCustomLifestealWeaponPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("recipe")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.recipe-list", "&6Recipe list.")));
            return true;
        }

        if (command.getName().equalsIgnoreCase("heartgive")) {
            if (!sender.hasPermission("lifesteal.admin")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.command-usage", "&cUsage: /heartgive <player> <amount>")));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found.")));
                return true;
            }
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Amount must be a number.");
                return true;
            }
            plugin.getHeartManager().addHearts(target, amount);
            plugin.getHeartManager().syncPlayerHealth(target);
            plugin.getHeartManager().save();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.heart-give", "&aYou gave {player} {amount} hearts.").replace("{player}", target.getName()).replace("{amount}", String.valueOf(amount))));
            return true;
        }

        if (command.getName().equalsIgnoreCase("heartremove")) {
            if (!sender.hasPermission("lifesteal.admin")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.command-usage", "&cUsage: /heartremove <player> <amount>")));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found.")));
                return true;
            }
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Amount must be a number.");
                return true;
            }
            plugin.getHeartManager().removeHearts(target, amount);
            plugin.getHeartManager().syncPlayerHealth(target);
            plugin.getHeartManager().save();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.heart-remove", "&aYou removed {amount} hearts from {player}.").replace("{player}", target.getName()).replace("{amount}", String.valueOf(amount))));
            return true;
        }

        if (command.getName().equalsIgnoreCase("heartwithdraw")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                return true;
            }
            if (!sender.hasPermission("lifesteal.use")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
                return true;
            }
            if (plugin.getHeartManager().getHearts(player) <= 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.heart-insufficient", "&cYou do not have enough hearts to withdraw.")));
                return true;
            }
            plugin.getHeartManager().removeHearts(player, 1);
            plugin.getHeartManager().syncPlayerHealth(player);
            ItemStack heart = plugin.getCustomItems().createHeartItem();
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), heart);
            } else {
                player.getInventory().addItem(heart);
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.heart-withdraw", "&aYou withdrew a heart item.")));
            plugin.getHeartManager().save();
            return true;
        }

        if (command.getName().equalsIgnoreCase("heartrevive")) {
            if (!sender.hasPermission("lifesteal.revive")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.command-usage", "&cUsage: /heartrevive <player>")));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found.")));
                return true;
            }
            if (!plugin.isDeathBanned(target)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.revive-failed", "&cThat player is not death-banned.")));
                return true;
            }
            plugin.revivePlayer(target, sender instanceof Player ? (Player) sender : null);
            return true;
        }

        if (command.getName().equalsIgnoreCase("heart")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GOLD + "RGRs Custom Lifesteal Weapon");
                sender.sendMessage(ChatColor.GRAY + "/heart reload - Reload config");
                sender.sendMessage(ChatColor.GRAY + "/heartgive <player> <amount> - Give hearts");
                sender.sendMessage(ChatColor.GRAY + "/heartremove <player> <amount> - Remove hearts");
                sender.sendMessage(ChatColor.GRAY + "/heartwithdraw - Withdraw a heart");
                sender.sendMessage(ChatColor.GRAY + "/heartrevive <player> - Revive a death-banned player");
                sender.sendMessage(ChatColor.GRAY + "/recipe - Show custom recipes");
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("lifesteal.admin")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission", "&cNo permission.")));
                    return true;
                }
                plugin.reloadPluginConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reload-success", "&aConfiguration reloaded.")));
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "RGRs Custom Lifesteal Weapon");
            sender.sendMessage(ChatColor.GRAY + "Use /heart reload or /recipe for details.");
            return true;
        }

        return false;
    }
}
