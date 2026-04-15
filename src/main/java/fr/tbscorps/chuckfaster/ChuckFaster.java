package fr.tbscorps.chuckfaster;

import fr.tbscorps.chuckfaster.commands.ChFastCommand;
import fr.tbscorps.chuckfaster.listeners.BlockListener;
import fr.tbscorps.chuckfaster.listeners.PlayerListener;
import fr.tbscorps.chuckfaster.managers.MagnetiteManager;
import fr.tbscorps.chuckfaster.tasks.GrowthTask;
import fr.tbscorps.chuckfaster.tasks.ParticleTask;
import fr.tbscorps.chuckfaster.utils.ConfigManager;
import fr.tbscorps.chuckfaster.utils.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChuckFaster extends JavaPlugin {

    private static ChuckFaster instance;
    private ConfigManager configManager;
    private MagnetiteManager magnetiteManager;
    private HologramManager hologramManager;
    private GrowthTask growthTask;
    private ParticleTask particleTask;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialisation des managers
        configManager = new ConfigManager(this);
        magnetiteManager = new MagnetiteManager(this);
        hologramManager = new HologramManager(this);
        
        // Hologrammes natifs activés
        getLogger().info("Système d'hologrammes natif activé ! (ArmorStands invisibles)");
        
        // Enregistrement des commandes
        getCommand("chfast").setExecutor(new ChFastCommand(this));
        getCommand("cfreload").setExecutor(new ChFastCommand(this));
        
        // Enregistrement des listeners
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Démarrage des tâches
        startTasks();
        
        // Messages de démarrage configurables
        getLogger().info("ChuckFaster v" + getDescription().getVersion() + " activé !");
        getLogger().info("Développé par SEBmyG");
        
        // Message dans la console et aux joueurs connectés
        String enableMessage = configManager.getMessage("plugin-enabled");
        getLogger().info(org.bukkit.ChatColor.stripColor(enableMessage));
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("chuckfaster.admin")) {
                player.sendMessage(configManager.getPrefix() + enableMessage);
            }
        });
    }

    @Override
    public void onDisable() {
        if (growthTask != null) {
            growthTask.cancel();
        }
        if (particleTask != null) {
            particleTask.cancel();
        }
        
        // Sauvegarde des données
        if (magnetiteManager != null) {
            magnetiteManager.saveData();
        }
        
        // Nettoyer les hologrammes
        if (hologramManager != null) {
            hologramManager.removeAllHolograms();
        }
        
        // Messages de désactivation configurables
        String disableMessage = configManager != null ? configManager.getMessage("plugin-disabled") : "ChuckFaster désactivé !";
        getLogger().info(org.bukkit.ChatColor.stripColor(disableMessage));
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("chuckfaster.admin")) {
                player.sendMessage((configManager != null ? configManager.getPrefix() : "") + disableMessage);
            }
        });
    }
    
    private void startTasks() {
        // Tâche de croissance des plantes (toutes les 20 ticks = 1 seconde)
        growthTask = new GrowthTask(this);
        growthTask.runTaskTimer(this, 20L, 20L);
        
        // Tâche des particules (toutes les 10 ticks = 0.5 secondes)
        particleTask = new ParticleTask(this);
        particleTask.runTaskTimer(this, 10L, 10L);
    }
    
    public void reload() {
        configManager.reloadConfig();
        String reloadMessage = configManager.getMessage("config-reloaded");
        getLogger().info(org.bukkit.ChatColor.stripColor(reloadMessage));
    }
    
    // Getters
    public static ChuckFaster getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MagnetiteManager getMagnetiteManager() {
        return magnetiteManager;
    }
    
    public HologramManager getHologramManager() {
        return hologramManager;
    }
}