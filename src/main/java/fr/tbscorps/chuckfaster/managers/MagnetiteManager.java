package fr.tbscorps.chuckfaster.managers;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.data.MagnetiteBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MagnetiteManager {

    private final ChuckFaster plugin;
    private final Map<String, MagnetiteBlock> magnetiteBlocks;
    private File dataFile;
    private FileConfiguration dataConfig;

    public MagnetiteManager(ChuckFaster plugin) {
        this.plugin = plugin;
        this.magnetiteBlocks = new HashMap<>();
        loadData();
    }

    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "magnetite_data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("magnetite_data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        // Charger les blocs existants
        if (dataConfig.getConfigurationSection("blocks") != null) {
            for (String key : dataConfig.getConfigurationSection("blocks").getKeys(false)) {
                String path = "blocks." + key;
                
                String worldName = dataConfig.getString(path + ".world");
                if (worldName == null || Bukkit.getWorld(worldName) == null) {
                    continue;
                }
                
                Location location = new Location(
                    Bukkit.getWorld(worldName),
                    dataConfig.getInt(path + ".x"),
                    dataConfig.getInt(path + ".y"),
                    dataConfig.getInt(path + ".z")
                );
                
                int diamonds = dataConfig.getInt(path + ".diamonds", 0);
                MagnetiteBlock magnetite = new MagnetiteBlock(location, diamonds);
                magnetiteBlocks.put(locationToString(location), magnetite);
            }
        }
    }

    public void saveData() {
        dataConfig.set("blocks", null);
        
        for (Map.Entry<String, MagnetiteBlock> entry : magnetiteBlocks.entrySet()) {
            String key = entry.getKey();
            MagnetiteBlock magnetite = entry.getValue();
            Location loc = magnetite.getLocation();
            
            String path = "blocks." + key;
            dataConfig.set(path + ".world", loc.getWorld().getName());
            dataConfig.set(path + ".x", loc.getBlockX());
            dataConfig.set(path + ".y", loc.getBlockY());
            dataConfig.set(path + ".z", loc.getBlockZ());
            dataConfig.set(path + ".diamonds", magnetite.getDiamonds());
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde des données: " + e.getMessage());
        }
    }

    public void addMagnetiteBlock(Location location) {
        String key = locationToString(location);
        magnetiteBlocks.put(key, new MagnetiteBlock(location, 0));
        saveData();
    }

    public void removeMagnetiteBlock(Location location) {
        String key = locationToString(location);
        magnetiteBlocks.remove(key);
        saveData();
    }

    public MagnetiteBlock getMagnetiteBlock(Location location) {
        return magnetiteBlocks.get(locationToString(location));
    }

    public boolean isMagnetiteBlock(Location location) {
        return magnetiteBlocks.containsKey(locationToString(location));
    }

    public Map<String, MagnetiteBlock> getAllMagnetiteBlocks() {
        return new HashMap<>(magnetiteBlocks);
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + ":" + 
               location.getBlockX() + ":" + 
               location.getBlockY() + ":" + 
               location.getBlockZ();
    }
}