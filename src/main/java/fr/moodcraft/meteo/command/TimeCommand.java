package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.Locale;

public class TimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        String input =
                label.toLowerCase(Locale.ROOT);

        long time;
        String message;

        switch (input) {

            case "jour" -> {
                time = 1000;
                message = "§e➜ §7Le jour illumine désormais §aMood§6Craft§7.";
            }

            case "matin" -> {
                time = 0;
                message = "§e➜ §7L'aube colore l'horizon.";
            }

            case "midi" -> {
                time = 6000;
                message = "§e➜ §7Le soleil atteint son zénith.";
            }

            case "soir", "crepuscule" -> {
                time = 12000;
                message = "§e➜ §7Le crépuscule enveloppe le ciel.";
            }

            case "nuit" -> {
                time = 14000;
                message = "§e➜ §7La nuit tombe sur §aMood§6Craft§7.";
            }

            case "minuit" -> {
                time = 18000;
                message = "§e➜ §7Minuit approche.";
            }

            case "aube" -> {
                time = 23000;
                message = "§e➜ §7Une nouvelle journée commence.";
            }

            default -> {
                sender.sendMessage("");
                sender.sendMessage("§8----- §6✦ Cycle du Monde ✦ §8-----");
                sender.sendMessage("§c✖ §fTemps inconnu.");
                sender.sendMessage("§e➜ §7Commandes : §e/jour§7, §ematin§7, §emidi§7, §esoir§7, §enuit");
                sender.sendMessage("§8-----------------------------");

                return true;
            }
        }

        World world =
                WorldGuard.mainWorld();

        if (world == null) {

            WorldGuard.sendMissingWorld(
                    sender
            );

            return true;
        }

        world.setTime(
                time
        );

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Cycle du Monde ✦ §8-----",
                message,
                "§e➜ §7Monde : §eworld",
                "§8-----------------------------"
        );

        if (!(sender instanceof Player player)
                || !WorldGuard.isInMainWorld(player)) {

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Cycle du Monde ✦ §8-----");
            sender.sendMessage("§a✔ §fTemps modifié dans §eworld§a.");
            sender.sendMessage("§e➜ §7Nether et End ignorés.");
            sender.sendMessage("§8-----------------------------");
        }

        return true;
    }
}
