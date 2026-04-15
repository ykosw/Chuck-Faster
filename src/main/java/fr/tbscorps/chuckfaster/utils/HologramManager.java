package fr.tbscorps.chuckfaster.utils;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.data.MagnetiteBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class HologramManager {
    
    private final ChuckFaster plugin;
    private final Map<String, Object> holograms;
    private boolean holographicDisplaysEnabled;

    public HologramManager(ChuckFaster plugin) {
        this.plugin = plugin;
        this.holograms = new HashMap<>();
        this.holographicDisplaysEnabled = checkHolographicDisplays();
    }

    private boolean checkHolographicDisplays() {
        // Toujours activé car utilise le système natif ArmorStand
        return true;
    }

    public void createOrUpdateHologram(String key, Location location, MagnetiteBlock magnetite) {
        if (!holographicDisplaysEnabled) {
            return;
        }

        try {
            // Utiliser l'API GHolo si disponible
            removeHologram(key);
            
            double speedBonus = magnetite.getSpeedBonus();
            String hologramFormat = plugin.getConfigManager().getHologramFormat();
            String text = hologramFormat.replace("{speed}", String.format("%.1f", speedBonus));
            
            // Utiliser la hauteur configurable pour l'hologramme
            double hologramHeight = plugin.getConfigManager().getHologramHeight();
            Location hologramLoc = location.clone().add(0.5, hologramHeight, 0.5);
            
            // Utiliser reflection pour éviter les dépendances de compilation avec GHolo
            // API GHolo simplifiée - création manuelle d'entité ArmorStand invisible
            Class<?> armorStandClass = Class.forName("org.bukkit.entity.ArmorStand");
            Object armorStand = hologramLoc.getWorld().spawn(hologramLoc, 
                (Class<org.bukkit.entity.ArmorStand>) armorStandClass);
            
            // Configuration de l'ArmorStand comme hologramme
            ((org.bukkit.entity.ArmorStand) armorStand).setVisible(false);
            ((org.bukkit.entity.ArmorStand) armorStand).setGravity(false);
            ((org.bukkit.entity.ArmorStand) armorStand).setCustomNameVisible(true);
            // Convertir les codes de couleur & en § pour Minecraft
            String coloredText = org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
            ((org.bukkit.entity.ArmorStand) armorStand).setCustomName(coloredText);
            ((org.bukkit.entity.ArmorStand) armorStand).setMarker(true);
            
            Object hologram = armorStand;
            
            holograms.put(key, hologram);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la création de l'hologramme GHolo: " + e.getMessage());
        }
    }

    public void removeHologram(String key) {
        if (!holographicDisplaysEnabled) {
            return;
        }

        try {
            Object hologram = holograms.get(key);
            if (hologram != null && hologram instanceof org.bukkit.entity.ArmorStand) {
                ((org.bukkit.entity.ArmorStand) hologram).remove();
                holograms.remove(key);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la suppression de l'hologramme: " + e.getMessage());
        }
    }

    public void removeAllHolograms() {
        if (!holographicDisplaysEnabled) {
            return;
        }

        for (String key : new HashMap<>(holograms).keySet()) {
            removeHologram(key);
        }
        holograms.clear();
    }

    public boolean isHolographicDisplaysEnabled() {
        return holographicDisplaysEnabled;
    }
}