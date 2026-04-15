package fr.tbscorps.chuckfaster.listeners;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.data.MagnetiteBlock;
import fr.tbscorps.chuckfaster.gui.MagnetiteGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerListener implements Listener {

    private final ChuckFaster plugin;

    public PlayerListener(ChuckFaster plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        MagnetiteBlock magnetite = plugin.getMagnetiteManager().getMagnetiteBlock(event.getClickedBlock().getLocation());
        if (magnetite == null) {
            return;
        }

        if (!event.getPlayer().hasPermission("chuckfaster.use")) {
            event.getPlayer().sendMessage(Component.text("Vous n'avez pas la permission d'utiliser les blocs de magnétite.", NamedTextColor.RED));
            return;
        }

        event.setCancelled(true);

        // Clic droit avec Shift = ouvrir GUI
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            if (event.getPlayer().isSneaking()) {
                // Shift + clic droit = GUI
                try {
                    plugin.getLogger().info("Tentative d'ouverture du GUI pour " + event.getPlayer().getName());
                    MagnetiteGUI gui = new MagnetiteGUI(plugin, magnetite);
                    gui.open(event.getPlayer());
                    plugin.getLogger().info("GUI ouvert avec succès");
                } catch (Exception e) {
                    plugin.getLogger().severe("Erreur lors de l'ouverture du GUI: " + e.getMessage());
                    e.printStackTrace();
                    event.getPlayer().sendMessage(Component.text("§cErreur lors de l'ouverture du GUI. Voir console.", NamedTextColor.RED));
                }
            } else {
                // Clic droit normal = ajouter diamant si possible
                handleDirectDiamondAdd(event, magnetite);
            }
        } else if (event.getAction().toString().contains("LEFT_CLICK") && !event.getPlayer().isSneaking()) {
            // Clic gauche = informations
            showMagnetiteInfo(event, magnetite);
        }
    }

    private void handleDirectDiamondAdd(PlayerInteractEvent event, MagnetiteBlock magnetite) {
        if (event.getItem() == null || event.getItem().getType() != Material.DIAMOND_BLOCK) {
            // Pas de diamant en main, afficher les infos
            showMagnetiteInfo(event, magnetite);
            return;
        }

        if (magnetite.getDiamonds() >= plugin.getConfigManager().getMaxDiamondsPerBlock()) {
            event.getPlayer().sendMessage(Component.text("Ce Chuck Faster est déjà au maximum de sa capacité !", NamedTextColor.RED));
            return;
        }

        // Retirer un bloc de diamant de l'inventaire
        event.getItem().setAmount(event.getItem().getAmount() - 1);
        magnetite.addDiamond();

        double speedBonus = magnetite.getSpeedBonus();
        String message = plugin.getConfigManager().getMessage("diamond-added")
            .replace("{speed}", String.format("%.1f", speedBonus));
        event.getPlayer().sendMessage(Component.text(message));
        
        // Mettre à jour l'hologramme
        String hologramKey = magnetite.getLocation().getWorld().getName() + "_" + 
                           magnetite.getLocation().getBlockX() + "_" + 
                           magnetite.getLocation().getBlockY() + "_" + 
                           magnetite.getLocation().getBlockZ();
        plugin.getHologramManager().createOrUpdateHologram(hologramKey, magnetite.getLocation(), magnetite);
        
        plugin.getMagnetiteManager().saveData();
    }
    
    private void showMagnetiteInfo(PlayerInteractEvent event, MagnetiteBlock magnetite) {
        double speedBonus = magnetite.getSpeedBonus();
        int diamonds = magnetite.getDiamonds();
        int maxDiamonds = plugin.getConfigManager().getMaxDiamondsPerBlock();

        event.getPlayer().sendMessage(Component.text("=== Chuck Faster ===", NamedTextColor.GOLD));
        event.getPlayer().sendMessage(Component.text("Blocs de diamant: " + diamonds + "/" + maxDiamonds, NamedTextColor.YELLOW));
        event.getPlayer().sendMessage(Component.text("Bonus de vitesse: " + String.format("%.1f", speedBonus) + "%", NamedTextColor.GREEN));
        event.getPlayer().sendMessage(Component.text("Clic droit avec bloc de diamant pour alimenter", NamedTextColor.GRAY));
        event.getPlayer().sendMessage(Component.text("Shift + Clic droit pour ouvrir l'interface", NamedTextColor.GRAY));
    }
}