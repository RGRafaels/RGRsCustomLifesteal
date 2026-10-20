package com.rgrafaels.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HeartManager {
    private final RGRsCustomLifestealWeaponPlugin plugin;
    private final Map<UUID, Integer> hearts = new HashMap<>();

    public HeartManager(RGRsCustomLifestealWeaponPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        hearts.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("player-hearts");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                hearts.put(uuid, Math.max(0, section.getInt(key, plugin.getDefaultHearts())));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void save() {
        ConfigurationSection section = plugin.getConfig().createSection("player-hearts");
        for (Map.Entry<UUID, Integer> entry : hearts.entrySet()) {
            section.set(entry.getKey().toString(), entry.getValue());
        }
        plugin.saveConfig();
    }

    public int getHearts(Player player) {
        return getHearts(player.getUniqueId());
    }

    public int getHearts(UUID uuid) {
        return hearts.getOrDefault(uuid, plugin.getDefaultHearts());
    }

    public void setHearts(Player player, int amount) {
        setHearts(player.getUniqueId(), amount);
    }

    public void setHearts(UUID uuid, int amount) {
        int clamped = Math.max(0, Math.min(plugin.getMaxHearts(), amount));
        hearts.put(uuid, clamped);
    }

    public void addHearts(Player player, int amount) {
        addHearts(player.getUniqueId(), amount);
    }

    public void addHearts(UUID uuid, int amount) {
        setHearts(uuid, getHearts(uuid) + amount);
    }

    public void removeHearts(Player player, int amount) {
        removeHearts(player.getUniqueId(), amount);
    }

    public void removeHearts(UUID uuid, int amount) {
        setHearts(uuid, getHearts(uuid) - amount);
    }

    public void syncPlayerHealth(Player player) {
        double maxHealth = 40.0;
        player.setMaxHealth(maxHealth);
        int currentHearts = getHearts(player);
        double healthToSet = Math.max(0.1, Math.min(maxHealth, currentHearts * 2.0));
        player.setHealth(healthToSet);
    }

    public void initializePlayer(Player player) {
        if (!hearts.containsKey(player.getUniqueId())) {
            hearts.put(player.getUniqueId(), plugin.getDefaultHearts());
        }
        syncPlayerHealth(player);
        save();
    }
}
