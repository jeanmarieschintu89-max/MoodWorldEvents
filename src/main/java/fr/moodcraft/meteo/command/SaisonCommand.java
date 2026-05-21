package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SaisonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (args.length == 0) {

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Saisons MoodCraft ✦ §8-----");
            sender.sendMessage("§e➜ §7/saison printemps");
            sender.sendMessage("§e➜ §7/saison ete");
            sender.sendMessage("§e➜ §7/saison automne");
            sender.sendMessage("§e➜ §7/saison hiver");
            sender.sendMessage("§8-----------------------------");

            return true;
        }

        Season season;

        switch (args[0].toLowerCase()) {

            case "printemps" -> season = Season.PRINTEMPS;
            case "ete" -> season = Season.ETE;
            case "automne" -> season = Season.AUTOMNE;
            case "hiver" -> season = Season.HIVER;

            default -> {
                sender.sendMessage("");
                sender.sendMessage("§8----- §6✦ Saisons MoodCraft ✦ §8-----");
                sender.sendMessage("§c✖ §fSaison invalide.");
                sender.sendMessage("§e➜ §7Choix : §eprintemps§7, §eete§7, §eautomne§7, §ehiver");
                sender.sendMessage("§8-----------------------------");

                return true;
            }
        }

        ClimateManager.setSeason(
                season
        );

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Saisons MoodCraft ✦ §8-----",
                "§a✔ §fSaison modifiée.",
                "§e➜ §7Nouvelle saison : §f"
                        + season.getIcon()
                        + " "
                        + season.getDisplay(),
                "§8-----------------------------"
        );

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ Saisons MoodCraft ✦ §8-----");
        sender.sendMessage("§a✔ §fSaison appliquée.");
        sender.sendMessage("§e➜ §7Nouvelle saison : §f"
                + season.getIcon()
                + " "
                + season.getDisplay());
        sender.sendMessage("§8-----------------------------");

        return true;
    }
}
