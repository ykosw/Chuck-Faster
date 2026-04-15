package fr.tbscorps.chuckfaster.tasks;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.data.MagnetiteBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GrowthTask extends BukkitRunnable {

    private final ChuckFaster plugin;
    private final Random random;
    private final Set<Material> growableCrops;

    public GrowthTask(ChuckFaster plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.growableCrops = new HashSet<>();
        initializeGrowableCrops();
    }

    private void initializeGrowableCrops() {
        // Ajouter tous les types de cultures
        growableCrops.add(Material.WHEAT);
        growableCrops.add(Material.CARROTS);
        growableCrops.add(Material.POTATOES);
        growableCrops.add(Material.BEETROOTS);
        growableCrops.add(Material.NETHER_WART);
        growableCrops.add(Material.COCOA);
        growableCrops.add(Material.SWEET_BERRY_BUSH);
        growableCrops.add(Material.BAMBOO);
        growableCrops.add(Material.SUGAR_CANE);
        growableCrops.add(Material.CACTUS);
        growableCrops.add(Material.KELP);
        growableCrops.add(Material.SEA_PICKLE);
        
        // Arbres sapling
        growableCrops.add(Material.OAK_SAPLING);
        growableCrops.add(Material.BIRCH_SAPLING);
        growableCrops.add(Material.SPRUCE_SAPLING);
        growableCrops.add(Material.JUNGLE_SAPLING);
        growableCrops.add(Material.ACACIA_SAPLING);
        growableCrops.add(Material.DARK_OAK_SAPLING);
        growableCrops.add(Material.CHERRY_SAPLING);
        growableCrops.add(Material.MANGROVE_PROPAGULE);
        
        // Champignons
        growableCrops.add(Material.BROWN_MUSHROOM);
        growableCrops.add(Material.RED_MUSHROOM);
        growableCrops.add(Material.CRIMSON_FUNGUS);
        growableCrops.add(Material.WARPED_FUNGUS);
    }

    @Override
    public void run() {
        for (MagnetiteBlock magnetite : plugin.getMagnetiteManager().getAllMagnetiteBlocks().values()) {
            if (magnetite.isEmpty()) {
                continue; // Pas de diamants = pas d'effet
            }

            processChunkAroundMagnetite(magnetite);
        }
    }

    private void processChunkAroundMagnetite(MagnetiteBlock magnetite) {
        Location center = magnetite.getLocation();
        World world = center.getWorld();
        if (world == null) return;

        Chunk centerChunk = center.getChunk();
        double speedMultiplier = 1.0 + (magnetite.getSpeedBonus() / 100.0);

        // Parcourir le chunk
        int chunkX = centerChunk.getX();
        int chunkZ = centerChunk.getZ();

        for (int x = chunkX * 16; x < (chunkX + 1) * 16; x++) {
            for (int z = chunkZ * 16; z < (chunkZ + 1) * 16; z++) {
                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                    Block block = world.getBlockAt(x, y, z);
                    
                    if (shouldGrow(block, speedMultiplier)) {
                        growPlant(block);
                    }
                }
            }
        }
    }

    private boolean shouldGrow(Block block, double speedMultiplier) {
        if (!growableCrops.contains(block.getType())) {
            return false;
        }

        // Calculer la chance de croissance basée sur le multiplicateur de vitesse
        double baseChance = plugin.getConfigManager().getBaseGrowthChance();
        double enhancedChance = baseChance * speedMultiplier;
        
        return random.nextDouble() < enhancedChance;
    }

    private void growPlant(Block block) {
        Material type = block.getType();
        
        if (block.getBlockData() instanceof Ageable ageable) {
            // Cultures avec âge (blé, carottes, etc.)
            if (ageable.getAge() < ageable.getMaximumAge()) {
                ageable.setAge(ageable.getAge() + 1);
                block.setBlockData(ageable);
                
                // Effet de particules
                block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, 
                    block.getLocation().add(0.5, 1, 0.5), 3, 0.3, 0.3, 0.3, 0);
            }
        } else {
            // Autres types de plantes (canne à sucre, cactus, bambou, etc.)
            handleSpecialGrowth(block);
        }
    }

    private void handleSpecialGrowth(Block block) {
        Material type = block.getType();
        Location loc = block.getLocation();
        
        switch (type) {
            case SUGAR_CANE:
            case BAMBOO:
            case CACTUS:
                // Croissance vers le haut
                Block above = block.getRelative(0, 1, 0);
                if (above.getType() == Material.AIR && canGrowUpward(block)) {
                    above.setType(type);
                    spawnGrowthParticles(above);
                }
                break;
                
            case KELP:
                Block aboveKelp = block.getRelative(0, 1, 0);
                if (aboveKelp.getType() == Material.WATER) {
                    aboveKelp.setType(Material.KELP_PLANT);
                    spawnGrowthParticles(aboveKelp);
                }
                break;
                
            default:
                // Croissance naturelle pour les saplings et champignons
                if (random.nextDouble() < 0.1) { // 10% de chance pour éviter trop de croissance
                    // Simuler l'effet de la bone meal de manière naturelle
                    if (type.name().contains("SAPLING") || type.name().contains("MUSHROOM") || type.name().contains("FUNGUS")) {
                        // Pour les saplings et champignons, on peut essayer de faire grandir l'arbre/champignon
                        // En pratique, cela nécessiterait l'utilisation d'événements Bukkit
                        spawnGrowthParticles(block);
                    }
                }
                break;
        }
    }

    private boolean canGrowUpward(Block block) {
        Material type = block.getType();
        int height = 1;
        
        // Compter la hauteur actuelle
        Block current = block;
        while (current.getRelative(0, -1, 0).getType() == type) {
            current = current.getRelative(0, -1, 0);
            height++;
        }
        
        // Limites de croissance
        return switch (type) {
            case SUGAR_CANE -> height < 3;
            case CACTUS -> height < 3;
            case BAMBOO -> height < 16;
            default -> false;
        };
    }

    private void spawnGrowthParticles(Block block) {
        block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, 
            block.getLocation().add(0.5, 1, 0.5), 3, 0.3, 0.3, 0.3, 0);
    }
}