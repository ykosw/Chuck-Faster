package fr.tbscorps.chuckfaster.gui;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.data.MagnetiteBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MagnetiteGUI implements InventoryHolder, Listener {

    private final ChuckFaster plugin;
    private final MagnetiteBlock magnetiteBlock;
    private final Inventory inventory;

    public MagnetiteGUI(ChuckFaster plugin, MagnetiteBlock magnetiteBlock) {
        this.plugin = plugin;
        this.magnetiteBlock = magnetiteBlock;
        
        // Créer un GUI simple et robuste
        this.inventory = Bukkit.createInventory(this, 27, 
            Component.text("Chuck Faster", NamedTextColor.DARK_PURPLE));
        
        setupSimpleInventory();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    private NamedTextColor getNamedTextColor(String colorName) {
        try {
            if (colorName == null || colorName.isEmpty()) {
                return NamedTextColor.DARK_PURPLE;
            }
            
            // Mapping manuel des couleurs supportées
            switch (colorName.toUpperCase().replace("-", "_")) {
                case "BLACK": return NamedTextColor.BLACK;
                case "DARK_BLUE": return NamedTextColor.DARK_BLUE;
                case "DARK_GREEN": return NamedTextColor.DARK_GREEN;
                case "DARK_AQUA": return NamedTextColor.DARK_AQUA;
                case "DARK_RED": return NamedTextColor.DARK_RED;
                case "DARK_PURPLE": return NamedTextColor.DARK_PURPLE;
                case "GOLD": return NamedTextColor.GOLD;
                case "GRAY": case "GREY": return NamedTextColor.GRAY;
                case "DARK_GRAY": case "DARK_GREY": return NamedTextColor.DARK_GRAY;
                case "BLUE": return NamedTextColor.BLUE;
                case "GREEN": return NamedTextColor.GREEN;
                case "AQUA": return NamedTextColor.AQUA;
                case "RED": return NamedTextColor.RED;
                case "LIGHT_PURPLE": return NamedTextColor.LIGHT_PURPLE;
                case "YELLOW": return NamedTextColor.YELLOW;
                case "WHITE": return NamedTextColor.WHITE;
                default:
                    plugin.getLogger().warning("Couleur inconnue: " + colorName + ", utilisation de DARK_PURPLE par défaut");
                    return NamedTextColor.DARK_PURPLE;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors du parsing de couleur: " + colorName + ", utilisation de DARK_PURPLE par défaut");
            return NamedTextColor.DARK_PURPLE;
        }
    }
    
    private void setupFallbackInventory() {
        // GUI de secours en cas d'erreur
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, glass);
        }
        
        ItemStack info = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta meta = info.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Erreur de chargement", NamedTextColor.RED));
            info.setItemMeta(meta);
        }
        inventory.setItem(13, info);
    }

    private void setupSimpleInventory() {
        try {
            // Remplir de vitres grises basiques
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            for (int i = 0; i < 27; i++) {
                inventory.setItem(i, glass);
            }

            // Item au centre - simple
            ItemStack center = new ItemStack(Material.DIAMOND_BLOCK);
            ItemMeta centerMeta = center.getItemMeta();
            if (centerMeta != null) {
                centerMeta.displayName(Component.text("Chuck Faster: " + magnetiteBlock.getDiamonds() + " diamants", NamedTextColor.AQUA));
                center.setItemMeta(centerMeta);
            }
            inventory.setItem(13, center);

            // Bouton détruire simple
            ItemStack destroy = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta destroyMeta = destroy.getItemMeta();
            if (destroyMeta != null) {
                destroyMeta.displayName(Component.text("Détruire", NamedTextColor.RED));
                destroy.setItemMeta(destroyMeta);
            }
            inventory.setItem(15, destroy);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur setupSimpleInventory: " + e.getMessage());
        }
    }



    private ItemStack createDiamondStackItem() {
        try {
            int diamonds = magnetiteBlock.getDiamonds();
            int maxDiamonds = plugin.getConfigManager().getMaxDiamondsPerBlock();
            double speedBonus = magnetiteBlock.getSpeedBonus();

            if (diamonds == 0) {
                return createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "Aucun bloc de diamant",
                    "Placez des blocs de diamant",
                    "pour augmenter la vitesse !");
            }

            // Sécuriser la quantité (Minecraft limite à 64 par stack)
            int displayAmount = Math.min(Math.max(diamonds, 1), 64);
            ItemStack stack = new ItemStack(Material.DIAMOND_BLOCK, displayAmount);
            ItemMeta meta = stack.getItemMeta();
            
            if (meta != null) {
                meta.displayName(Component.text("Blocs de diamant stockés", NamedTextColor.AQUA)
                        .decoration(TextDecoration.ITALIC, false));
                
                java.util.List<Component> loreComponents = java.util.Arrays.asList(
                    Component.text("Quantité: " + diamonds + "/" + maxDiamonds, NamedTextColor.YELLOW)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Bonus de vitesse: " + String.format("%.1f", speedBonus) + "%", NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Clic pour ajouter/retirer", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("des blocs de diamant", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
                );
                meta.lore(loreComponents);
                
                stack.setItemMeta(meta);
            }
            
            return stack;
        } catch (Exception e) {
            // En cas d'erreur, retourner un item par défaut
            plugin.getLogger().warning("Erreur lors de la création de l'item diamant: " + e.getMessage());
            return createItem(Material.DIAMOND_BLOCK, "Blocs de diamant", "Erreur de chargement");
        }
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        try {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            
            if (meta != null) {
                meta.displayName(Component.text(name, NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false));
                
                if (lore.length > 0) {
                    java.util.List<Component> loreComponents = new java.util.ArrayList<>();
                    for (String loreLine : lore) {
                        loreComponents.add(Component.text(loreLine, NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));
                    }
                    meta.lore(loreComponents);
                }
                
                item.setItemMeta(meta);
            }
            
            return item;
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la création d'item: " + e.getMessage());
            return new ItemStack(material);
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // Empêcher toute interaction
        event.setCancelled(true);

        try {
            // Slot du centre (13) - interaction diamants
            if (slot == 13) {
                handleSimpleDiamondClick(player, cursor);
            }
            
            // Bouton détruire (slot 15)
            if (slot == 15) {
                destroyMagnetiteBlock(player);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur dans onInventoryClick: " + e.getMessage());
        }
    }
    
    private void handleSimpleDiamondClick(Player player, ItemStack cursor) {
        try {
            if (cursor != null && cursor.getType() == Material.DIAMOND_BLOCK) {
                // Ajouter un diamant
                if (magnetiteBlock.getDiamonds() < plugin.getConfigManager().getMaxDiamondsPerBlock()) {
                    magnetiteBlock.addDiamond();
                    cursor.setAmount(cursor.getAmount() - 1);
                    player.sendMessage(Component.text("Diamant ajouté ! Total: " + magnetiteBlock.getDiamonds(), NamedTextColor.GREEN));
                    setupSimpleInventory(); // Recharger
                    plugin.getMagnetiteManager().saveData();
                } else {
                    player.sendMessage(Component.text("Chuck Faster au maximum !", NamedTextColor.RED));
                }
            } else if (magnetiteBlock.getDiamonds() > 0) {
                // Retirer un diamant
                magnetiteBlock.removeDiamond();
                player.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK));
                player.sendMessage(Component.text("Diamant récupéré !", NamedTextColor.YELLOW));
                setupSimpleInventory(); // Recharger
                plugin.getMagnetiteManager().saveData();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur handleSimpleDiamondClick: " + e.getMessage());
        }
    }

    private void handleDiamondInteraction(Player player, ItemStack cursor) {
        if (cursor != null && cursor.getType() == Material.DIAMOND_BLOCK) {
            // Ajouter un bloc de diamant
            if (magnetiteBlock.getDiamonds() < plugin.getConfigManager().getMaxDiamondsPerBlock()) {
                magnetiteBlock.addDiamond();
                cursor.setAmount(cursor.getAmount() - 1);
                String message = plugin.getConfigManager().getMessage("diamond-added")
                    .replace("{speed}", String.format("%.1f", magnetiteBlock.getSpeedBonus()));
                player.sendMessage(Component.text(message));
                updateDisplay();
                plugin.getMagnetiteManager().saveData();
                plugin.getHologramManager().createOrUpdateHologram(
                    magnetiteBlock.getLocation().getWorld().getName() + "_" + 
                    magnetiteBlock.getLocation().getBlockX() + "_" + 
                    magnetiteBlock.getLocation().getBlockY() + "_" + 
                    magnetiteBlock.getLocation().getBlockZ(),
                    magnetiteBlock.getLocation(), magnetiteBlock);
            } else {
                player.sendMessage(Component.text("Ce Chuck Faster est déjà au maximum de sa capacité !", NamedTextColor.RED));
            }
        } else if (magnetiteBlock.getDiamonds() > 0) {
            // Clic gauche pour retirer un diamant
            if (magnetiteBlock.removeDiamond()) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK));
                player.sendMessage(Component.text("Bloc de diamant récupéré !", NamedTextColor.YELLOW));
                updateDisplay();
                plugin.getMagnetiteManager().saveData();
                plugin.getHologramManager().createOrUpdateHologram(
                    magnetiteBlock.getLocation().getWorld().getName() + "_" + 
                    magnetiteBlock.getLocation().getBlockX() + "_" + 
                    magnetiteBlock.getLocation().getBlockY() + "_" + 
                    magnetiteBlock.getLocation().getBlockZ(),
                    magnetiteBlock.getLocation(), magnetiteBlock);
            }
        }
    }


    
    private void destroyMagnetiteBlock(Player player) {
        int diamonds = magnetiteBlock.getDiamonds();
        
        // Vérifier l'espace dans l'inventaire (seulement 1 bloc de magnétite)
        int freeSlots = 0;
        
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                freeSlots++;
            }
        }
        
        if (freeSlots < 1) {
            player.sendMessage(Component.text(plugin.getConfigManager().getMessage("inventory-full")));
            return;
        }
        
        // Donner seulement le bloc Chuck Faster personnalisé (les diamants sont perdus)
        player.getInventory().addItem(fr.tbscorps.chuckfaster.utils.ItemUtils.createMagnetiteBlock());
        
        // Supprimer le bloc du monde
        Location blockLoc = magnetiteBlock.getLocation();
        blockLoc.getBlock().setType(Material.AIR);
        
        // Supprimer l'hologramme associé
        String hologramKey = blockLoc.getWorld().getName() + "_" + blockLoc.getBlockX() + "_" + blockLoc.getBlockY() + "_" + blockLoc.getBlockZ();
        plugin.getHologramManager().removeHologram(hologramKey);
        
        // Supprimer de la base de données
        plugin.getMagnetiteManager().removeMagnetiteBlock(blockLoc);
        
        // Fermer l'interface
        player.closeInventory();
        
        // Message de confirmation avec messages configurables
        String destroyMessage = plugin.getConfigManager().getMessage("magnetite-destroyed");
        if (diamonds > 0) {
            destroyMessage = "§cChuck Faster détruit ! " + diamonds + " bloc(s) de diamant ont été perdus. Récupéré: 1 Chuck Faster.";
        }
        player.sendMessage(Component.text(destroyMessage));
    }

    private void updateDisplay() {
        setupSimpleInventory(); // Recharger l'interface
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}