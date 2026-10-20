package com.rgrafaels.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class RGRsCustomLifestealWeaponPlugin extends JavaPlugin {
    private final Set<UUID> deathBanned = new HashSet<>();
    private HeartManager heartManager;
    private CustomItems customItems;
    private CustomRecipes customRecipes;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        heartManager = new HeartManager(this);
        customItems = new CustomItems(this);
        customRecipes = new CustomRecipes(this);

        getCommand("heart").setExecutor(new HeartCommand(this));
        getCommand("heartgive").setExecutor(new HeartCommand(this));
        getCommand("heartremove").setExecutor(new HeartCommand(this));
        getCommand("heartwithdraw").setExecutor(new HeartCommand(this));
        getCommand("heartrevive").setExecutor(new HeartCommand(this));
        getCommand("recipe").setExecutor(new HeartCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        customRecipes.registerRecipes();

        for (Player player : Bukkit.getOnlinePlayers()) {
            heartManager.initializePlayer(player);
        }

        getLogger().info("RGRs Custom Lifesteal Weapon enabled.");
    }

    @Override
    public void onDisable() {
        if (heartManager != null) {
            heartManager.save();
        }
        getLogger().info("RGRs Custom Lifesteal Weapon disabled.");
    }

    public int getMaxHearts() {
        return getConfig().getInt("max-hearts", 20);
    }

    public int getDefaultHearts() {
        return getConfig().getInt("start-hearts", 10);
    }

    public int getReviveStartHearts() {
        return getConfig().getInt("revive-start-hearts", 5);
    }

    public HeartManager getHeartManager() {
        return heartManager;
    }

    public CustomItems getCustomItems() {
        return customItems;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        if (heartManager != null) {
            heartManager.load();
        }
        if (customRecipes != null) {
            customRecipes.registerRecipes();
        }
    }

    public boolean isDeathBanned(Player player) {
        return deathBanned.contains(player.getUniqueId());
    }

    public void revivePlayer(Player target, Player reviver) {
        deathBanned.remove(target.getUniqueId());
        heartManager.setHearts(target.getUniqueId(), getReviveStartHearts());
        heartManager.syncPlayerHealth(target);
        heartManager.save();

        String actor = reviver == null ? "Server" : reviver.getName();
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a[Revive] &f" + target.getName() + " was revived by " + actor + " and started with 5 hearts."));
        target.sendMessage(ChatColor.GREEN + "You have been revived and now have 5 hearts.");
    }

    public void markDeathBanned(Player player) {
        deathBanned.add(player.getUniqueId());
    }
}
