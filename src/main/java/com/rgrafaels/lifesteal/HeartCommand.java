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
        String commandName = command.getName().toLowerCase();

        switch (commandName) {
            case "recipe":
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.recipe-list", "&6Recipe list.")));
                return true;
            case "heartgive":
                handleGive(sender, args);
                return true;
            case "heartremove":
                handleRemove(sender, args);
                return true;
            case "heartwithdraw":
                handleWithdraw(sender);
                return true;
            case "heartrevive":
                handleRevive(sender, args);
                return true;
            case "heart":
                handleHeart(sender, args);
                return true;
            default:
                return false;
        }
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage(configMessage("messages.no-permission", "&cNo permission."));
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(configMessage("messages.command-usage", "&cUsage: /heartgive <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(configMessage("messages.player-not-found", "&cPlayer not found."));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return;
        }

        plugin.getHeartManager().addHearts(target, amount);
        plugin.getHeartManager().syncPlayerHealth(target);
        plugin.getHeartManager().save();
        sender.sendMessage(configMessage("messages.heart-give", "&aYou gave {player} {amount} hearts.")
            .replace("{player}", target.getName())
            .replace("{amount}", String.valueOf(amount)));
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage(configMessage("messages.no-permission", "&cNo permission."));
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(configMessage("messages.command-usage", "&cUsage: /heartremove <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(configMessage("messages.player-not-found", "&cPlayer not found."));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return;
        }

        plugin.getHeartManager().removeHearts(target, amount);
        plugin.getHeartManager().syncPlayerHealth(target);
        plugin.getHeartManager().save();
        sender.sendMessage(configMessage("messages.heart-remove", "&aYou removed {amount} hearts from {player}.")
            .replace("{player}", target.getName())
            .replace("{amount}", String.valueOf(amount)));
    }

    private void handleWithdraw(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return;
        }
        if (!sender.hasPermission("lifesteal.use")) {
            sender.sendMessage(configMessage("messages.no-permission", "&cNo permission."));
            return;
        }
        if (plugin.getHeartManager().getHearts(player) <= 1) {
            player.sendMessage(configMessage("messages.heart-insufficient", "&cYou do not have enough hearts to withdraw."));
            return;
        }

        plugin.getHeartManager().removeHearts(player, 1);
        plugin.getHeartManager().syncPlayerHealth(player);
        ItemStack heart = plugin.getCustomItems().createHeartItem();
        player.getInventory().addItem(heart);
        player.sendMessage(configMessage("messages.heart-withdraw", "&aYou withdrew a heart item."));
        plugin.getHeartManager().save();
    }

    private void handleRevive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifesteal.revive")) {
            sender.sendMessage(configMessage("messages.no-permission", "&cNo permission."));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(configMessage("messages.command-usage", "&cUsage: /heartrevive <player>"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(configMessage("messages.player-not-found", "&cPlayer not found."));
            return;
        }
        if (!plugin.isDeathBanned(target)) {
            sender.sendMessage(configMessage("messages.revive-failed", "&cThat player is not death-banned."));
            return;
        }

        plugin.revivePlayer(target, sender instanceof Player p ? p : null);
    }

    private void handleHeart(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "RGRs Custom Lifesteal Weapon");
            sender.sendMessage(ChatColor.GRAY + "/heart reload" );
            sender.sendMessage(ChatColor.GRAY + "/heartgive <player> <amount>");
            sender.sendMessage(ChatColor.GRAY + "/heartremove <player> <amount>");
            sender.sendMessage(ChatColor.GRAY + "/heartwithdraw");
            sender.sendMessage(ChatColor.GRAY + "/heartrevive <player>");
            sender.sendMessage(ChatColor.GRAY + "/recipe");
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("lifesteal.admin")) {
                sender.sendMessage(configMessage("messages.no-permission", "&cNo permission."));
                return;
            }
            plugin.reloadPluginConfig();
            sender.sendMessage(configMessage("messages.reload-success", "&aConfiguration reloaded."));
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "RGRs Custom Lifesteal Weapon");
        sender.sendMessage(ChatColor.GRAY + "Use /heart reload or /recipe for details.");
    }

    private String configMessage(String key, String fallback) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(key, fallback));
    }
}
