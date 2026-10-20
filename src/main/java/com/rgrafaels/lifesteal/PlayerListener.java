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
                hearts.put(uuid, Math.max(0, section.getInt(key, 10)));
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
        int max = plugin.getMaxHearts();
        hearts.put(uuid, Math.max(0, Math.min(max, amount)));
    }

    public void addHearts(Player player, int amount) {
        addHearts(player.getUniqueId(), amount);
    }

    public void addHearts(UUID uuid, int amount) {
        int total = getHearts(uuid) + amount;
        setHearts(uuid, total);
    }

    public void removeHearts(Player player, int amount) {
        removeHearts(player.getUniqueId(), amount);
    }

    public void removeHearts(UUID uuid, int amount) {
        int total = getHearts(uuid) - amount;
        setHearts(uuid, total);
    }

    public void syncPlayerHealth(Player player) {
        int heartsValue = getHearts(player);
        double health = Math.max(1.0, Math.min(40.0, heartsValue * 2.0));
        player.setMaxHealth(40.0);
        player.setHealth(Math.min(player.getHealth(), health));
        if (health <= 0.1) {
            player.setHealth(0.1);
        }
    }

    public void initializePlayer(Player player) {
        if (!hearts.containsKey(player.getUniqueId())) {
            hearts.put(player.getUniqueId(), plugin.getDefaultHearts());
        }
        syncPlayerHealth(player);
        save();
    }
}
