package fr.tbscorps.chuckfaster.utils;

import fr.tbscorps.chuckfaster.ChuckFaster;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final ChuckFaster plugin;
    private FileConfiguration config;

    public ConfigManager(ChuckFaster plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public double getSpeedIncreasePerDiamond() {
        return config.getDouble("magnetite.speed-increase-per-diamond", 2.5);
    }

    public int getMaxDiamondsPerBlock() {
        return config.getInt("magnetite.max-diamonds-per-block", 64);
    }

    public boolean isParticlesEnabled() {
        return config.getBoolean("magnetite.particles.enabled", true);
    }

    public boolean isHologramEnabled() {
        return config.getBoolean("magnetite.hologram.enabled", true);
    }

    public int getChunkRadius() {
        return config.getInt("magnetite.chunk-radius", 1);
    }

    public double getBaseGrowthChance() {
        return config.getDouble("growth.base-chance", 0.1);
    }

    public boolean isDestroyButtonEnabled() {
        return config.getBoolean("gui.enable-destroy-button", true);
    }

    public double getHologramHeight() {
        return config.getDouble("magnetite.hologram.height", 2.2);
    }

    public String getMessage(String path) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', 
            config.getString("messages." + path, "&cMessage non configuré: " + path));
    }

    public String getPrefix() {
        return getMessage("prefix");
    }

    public String getBlockName() {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', 
            config.getString("magnetite.block-name", "Chuck Faster"));
    }

    public java.util.List<String> getBlockDescription() {
        return config.getStringList("magnetite.block-description");
    }

    public String getParticleType() {
        return config.getString("magnetite.particles.type", "HAPPY_VILLAGER");
    }

    public int getParticleCount() {
        return config.getInt("magnetite.particles.count", 3);
    }

    public String getHologramFormat() {
        return config.getString("magnetite.hologram.format", "&e⚡ &a+{speed}% &e⚡");
    }

    public int getGrowthTaskInterval() {
        return config.getInt("growth.growth-task-interval", 20);
    }

    public int getParticleTaskInterval() {
        return config.getInt("growth.particle-task-interval", 10);
    }

    public String getGuiTitle() {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', 
            config.getString("gui.title", "Chuck Faster"));
    }

    public String getGuiTitleColor() {
        return config.getString("gui.title-color", "DARK_PURPLE");
    }

    public boolean isDebugEnabled() {
        return config.getBoolean("debug.enabled", false);
    }

    public boolean showGrowthDetails() {
        return config.getBoolean("debug.show-growth-details", false);
    }

    public boolean showGuiInteractions() {
        return config.getBoolean("debug.show-gui-interactions", false);
    }

    public String getCommandMessage(String path) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', 
            config.getString("commands." + path, "&cMessage non configuré: " + path));
    }
}