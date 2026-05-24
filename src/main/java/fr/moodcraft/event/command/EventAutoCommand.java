package fr.moodcraft.event.command;

import fr.moodcraft.event.manager.EventAutoManager;
import fr.moodcraft.event.util.MoodStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventAutoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            sender.sendMessage("§6MoodEvent §8» " + EventAutoManager.getStatusLine());
            sender.sendMessage("§7Commandes : §e/eventauto on§7, §eoff§7, §einterval <min>§7, §eopen <min>§7, §eminplayers <nb>§7, §eanchor§7, §enow");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "on", "enable", "activer" -> {
                EventAutoManager.setEnabled(true);
                sender.sendMessage("§6MoodEvent §8» §aAutomatisation activée.");
                return true;
            }
            case "off", "disable", "desactiver", "désactiver" -> {
                EventAutoManager.setEnabled(false);
                sender.sendMessage("§6MoodEvent §8» §cAutomatisation désactivée.");
                return true;
            }
            case "interval", "temps", "delai", "délai" -> {
                Integer value = readPositiveInt(sender, args, "§e/eventauto interval <minutes>");
                if (value == null) return true;
                EventAutoManager.setIntervalMinutes(value);
                sender.sendMessage("§6MoodEvent §8» §aIntervalle défini à §e" + EventAutoManager.getIntervalMinutes() + " minutes§a.");
                return true;
            }
            case "open", "ouverture", "file" -> {
                Integer value = readPositiveInt(sender, args, "§e/eventauto open <minutes>");
                if (value == null) return true;
                EventAutoManager.setQueueOpenMinutes(value);
                sender.sendMessage("§6MoodEvent §8» §aOuverture de file définie à §e" + EventAutoManager.getQueueOpenMinutes() + " minutes§a.");
                return true;
            }
            case "minplayers", "minimum", "min", "joueurs" -> {
                Integer value = readPositiveInt(sender, args, "§e/eventauto minplayers <nombre>");
                if (value == null) return true;
                EventAutoManager.setMinPlayers(value);
                sender.sendMessage("§6MoodEvent §8» §aMinimum joueurs défini à §e" + EventAutoManager.getMinPlayers() + "§a.");
                return true;
            }
            case "anchor", "ancre", "position", "setpos" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cCommande joueur uniquement pour définir l'ancre.");
                    return true;
                }
                EventAutoManager.setAnchor(player);
                MoodStyle.successMessage(player, MoodStyle.MODULE,
                        "Point d'ancrage event-auto défini.",
                        MoodStyle.detail("Les arènes automatiques seront générées autour d'ici."),
                        MoodStyle.detail("Commande test : §e/eventauto now"));
                return true;
            }
            case "now", "test", "force", "lancer" -> {
                EventAutoManager.triggerNow(sender);
                return true;
            }
            default -> {
                sender.sendMessage("§6MoodEvent §8» §cSous-commande inconnue.");
                sender.sendMessage("§7Commandes : §e/eventauto status§7, §eon§7, §eoff§7, §einterval <min>§7, §eopen <min>§7, §eminplayers <nb>§7, §eanchor§7, §enow");
                return true;
            }
        }
    }

    private Integer readPositiveInt(CommandSender sender, String[] args, String usage) {
        if (args.length < 2) {
            sender.sendMessage("§6MoodEvent §8» §cValeur manquante. §7Utilisation : " + usage);
            return null;
        }
        try {
            int value = Integer.parseInt(args[1]);
            if (value <= 0) throw new NumberFormatException("negative");
            return value;
        } catch (NumberFormatException exception) {
            sender.sendMessage("§6MoodEvent §8» §cNombre invalide. §7Utilisation : " + usage);
            return null;
        }
    }
}
