package fr.tbscorps.chuckfaster.commands;

import fr.tbscorps.chuckfaster.ChuckFaster;
import fr.tbscorps.chuckfaster.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChFastCommand implements CommandExecutor, TabCompleter {

    private final ChuckFaster plugin;

    public ChFastCommand(ChuckFaster plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        // Gestion de la commande cfreload séparément
        if (command.getName().equalsIgnoreCase("cfreload")) {
            return handleReload(sender);
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                return handleGive(sender, args);
            case "reload":
                return handleReload(sender);
            case "info":
                return handleInfo(sender);
            case "help":
                sendHelp(sender);
                return true;
            default:
                sender.sendMessage(Component.text("Commande inconnue. Tapez /chfast help pour l'aide.", NamedTextColor.RED));
                return true;
        }
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("chuckfaster.give")) {
            sender.sendMessage(Component.text("Vous n'avez pas la permission d'utiliser cette commande.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /chfast give <joueur> [quantité]", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            String message = plugin.getConfigManager().getCommandMessage("player-not-found")
                .replace("{player}", args[1]);
            sender.sendMessage(Component.text(message));
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                    sender.sendMessage(Component.text("La quantité doit être positive.", NamedTextColor.RED));
                    return true;
                }
            } catch (NumberFormatException e) {
                String message = plugin.getConfigManager().getCommandMessage("invalid-amount")
                    .replace("{amount}", args[2]);
                sender.sendMessage(Component.text(message));
                return true;
            }
        }

        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(ItemUtils.createMagnetiteBlock());
        }

        String blockName = plugin.getConfigManager().getBlockName();
        String senderMessage = plugin.getConfigManager().getCommandMessage("give-success-sender")
            .replace("{amount}", String.valueOf(amount))
            .replace("{item}", blockName)
            .replace("{player}", target.getName());
        String targetMessage = plugin.getConfigManager().getCommandMessage("give-success-target")
            .replace("{amount}", String.valueOf(amount))
            .replace("{item}", blockName)
            .replace("{sender}", sender.getName());
            
        sender.sendMessage(Component.text(senderMessage));
        target.sendMessage(Component.text(targetMessage));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("chuckfaster.admin")) {
            sender.sendMessage(Component.text("Vous n'avez pas la permission d'utiliser cette commande.", NamedTextColor.RED));
            return true;
        }

        plugin.reload();
        sender.sendMessage(Component.text(plugin.getConfigManager().getMessage("config-reloaded")));
        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage(Component.text("===== ChuckFaster =====", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Version: " + plugin.getDescription().getVersion(), NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Développeur: SEBmyG", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Entreprise: TBS Corps", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Site web: https://tbscorps.fr", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Description: Plugin pour accélérer la croissance des plantes", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Contact: sebmyg@tbscorps.fr", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("=======================", NamedTextColor.GOLD));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("===== Aide ChuckFaster =====", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/chfast give <joueur> [quantité] - Donne des blocs de magnétite", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/chfast reload - Recharge la configuration", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/chfast info - Affiche les informations du plugin", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/chfast help - Affiche cette aide", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("=============================", NamedTextColor.GOLD));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("give", "reload", "info", "help");
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}