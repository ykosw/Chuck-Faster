package fr.tbscorps.chuckfaster.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ItemUtils {

    public static final NamespacedKey MAGNETITE_KEY = new NamespacedKey("chuckfaster", "magnetite_block");

    public static ItemStack createMagnetiteBlock() {
        var plugin = fr.tbscorps.chuckfaster.ChuckFaster.getInstance();
        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta meta = item.getItemMeta();
        
        // Nom configurable
        String blockName = plugin.getConfigManager().getBlockName();
        meta.displayName(Component.text(blockName)
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.ITALIC, false));
        
        // Description configurable
        java.util.List<String> descriptionLines = plugin.getConfigManager().getBlockDescription();
        java.util.List<Component> loreComponents = new java.util.ArrayList<>();
        
        for (String line : descriptionLines) {
            if (line.isEmpty()) {
                loreComponents.add(Component.text("", NamedTextColor.GRAY));
            } else {
                NamedTextColor color = line.contains("diamants") || line.contains("efficacité") ? 
                    NamedTextColor.YELLOW : 
                    (line.contains("TBS Corps") ? NamedTextColor.DARK_GRAY : NamedTextColor.GRAY);
                loreComponents.add(Component.text(line, color)
                        .decoration(TextDecoration.ITALIC, false));
            }
        }
        
        meta.lore(loreComponents);
        meta.getPersistentDataContainer().set(MAGNETITE_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        
        return item;
    }

    public static boolean isMagnetiteBlock(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(MAGNETITE_KEY, PersistentDataType.BOOLEAN);
    }
}