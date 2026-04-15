package fr.tbscorps.chuckfaster.listeners;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final ChuckFaster plugin;

    public BlockListener(ChuckFaster plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ItemUtils.isMagnetiteBlock(event.getItemInHand())) {
            return;
        }

        if (!event.getPlayer().hasPermission("chuckfaster.use")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Vous n'avez pas la permission d'utiliser les blocs de magnétite.", NamedTextColor.RED));
            return;
        }

        // Vérifier que c'est bien un lodestone
        if (event.getBlock().getType() != Material.LODESTONE) {
            event.getBlock().setType(Material.LODESTONE);
        }

        // Enregistrer le bloc comme magnétite avec un délai pour s'assurer que le bloc est placé
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getMagnetiteManager().addMagnetiteBlock(event.getBlock().getLocation());
        }, 1L);
        
        event.getPlayer().sendMessage(Component.text(plugin.getConfigManager().getMessage("magnetite-placed")));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Vérifier d'abord si c'est un lodestone ET s'il est enregistré
        if (event.getBlock().getType() != Material.LODESTONE || 
            !plugin.getMagnetiteManager().isMagnetiteBlock(event.getBlock().getLocation())) {
            return;
        }

        if (!event.getPlayer().hasPermission("chuckfaster.use")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Vous n'avez pas la permission de casser les blocs de magnétite.", NamedTextColor.RED));
            return;
        }

        // Vérifier si le joueur fait Shift + clic gauche pour récupérer
        if (event.getPlayer().isSneaking()) {
            // Empêcher le drop normal
            event.setDropItems(false);
            
            // Récupérer les blocs de diamant stockés
            var magnetite = plugin.getMagnetiteManager().getMagnetiteBlock(event.getBlock().getLocation());
            if (magnetite != null && magnetite.getDiamonds() > 0) {
                // Donner les blocs de diamant au joueur
                for (int i = 0; i < magnetite.getDiamonds(); i++) {
                    event.getPlayer().getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.DIAMOND_BLOCK));
                }
            }

            // Donner le bloc de magnétite personnalisé
            event.getPlayer().getInventory().addItem(ItemUtils.createMagnetiteBlock());

            // Supprimer l'hologramme associé
            String hologramKey = event.getBlock().getLocation().getWorld().getName() + "_" + 
                               event.getBlock().getLocation().getBlockX() + "_" + 
                               event.getBlock().getLocation().getBlockY() + "_" + 
                               event.getBlock().getLocation().getBlockZ();
            plugin.getHologramManager().removeHologram(hologramKey);

            // Supprimer de la base de données
            plugin.getMagnetiteManager().removeMagnetiteBlock(event.getBlock().getLocation());
            String message = plugin.getConfigManager().getMessage("magnetite-broken") + " " + (magnetite != null ? magnetite.getDiamonds() : 0) + " bloc(s) de diamant récupérés !";
            event.getPlayer().sendMessage(Component.text(message));
        } else {
            // Empêcher la casse normale
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Utilisez Shift + clic gauche pour récupérer le bloc de magnétite.", NamedTextColor.YELLOW));
        }
    }
}