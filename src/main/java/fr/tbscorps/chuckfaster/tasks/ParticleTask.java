package fr.tbscorps.chuckfaster.tasks;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.data.MagnetiteBlock;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class ParticleTask extends BukkitRunnable {

    private final ChuckFaster plugin;

    public ParticleTask(ChuckFaster plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getConfigManager().isParticlesEnabled() && 
            !plugin.getConfigManager().isHologramEnabled()) {
            return;
        }

        for (Map.Entry<String, MagnetiteBlock> entry : plugin.getMagnetiteManager().getAllMagnetiteBlocks().entrySet()) {
            String key = entry.getKey();
            MagnetiteBlock magnetite = entry.getValue();
            
            if (magnetite.isEmpty()) {
                // Retirer l'hologramme si le bloc est vide
                plugin.getHologramManager().removeHologram(key);
                continue;
            }

            Location loc = magnetite.getLocation();
            World world = loc.getWorld();
            if (world == null) continue;

            // Particules
            if (plugin.getConfigManager().isParticlesEnabled()) {
                spawnParticles(loc, magnetite);
            }

            // Hologramme
            if (plugin.getConfigManager().isHologramEnabled()) {
                plugin.getHologramManager().createOrUpdateHologram(key, loc, magnetite);
            }
        }
    }

    private void spawnParticles(Location loc, MagnetiteBlock magnetite) {
        Location particleLoc = loc.clone().add(0.5, 1.2, 0.5);
        
        // Particules principales - effet magique
        loc.getWorld().spawnParticle(Particle.ENCHANT, particleLoc, 5, 0.3, 0.3, 0.3, 0.1);
        
        // Particules secondaires basées sur l'efficacité
        double efficiency = magnetite.getSpeedBonus();
        if (efficiency > 50) {
            // Très efficace - particules dorées
            loc.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 3, 0.2, 0.2, 0.2, 0.05);
        } else if (efficiency > 25) {
            // Moyennement efficace - particules vertes
            loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, particleLoc, 2, 0.2, 0.2, 0.2, 0);
        }
        
        // Particules en spirale autour du bloc
        double time = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < 3; i++) {
            double angle = time + (i * Math.PI * 2 / 3);
            double x = Math.cos(angle) * 0.8;
            double z = Math.sin(angle) * 0.8;
            double y = Math.sin(time * 2) * 0.3;
            
            Location spiralLoc = loc.clone().add(0.5 + x, 1.5 + y, 0.5 + z);
            loc.getWorld().spawnParticle(Particle.PORTAL, spiralLoc, 1, 0, 0, 0, 0);
        }
    }
}