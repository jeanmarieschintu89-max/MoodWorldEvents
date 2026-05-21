package fr.moodcraft.meteo.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class WorldGuard {

    public static final String MAIN_WORLD_NAME = "world";

    private WorldGuard() {
    }

    public static World mainWorld() {
        return Bukkit.getWorld(MAIN_WORLD_NAME);
    }

    public static boolean isMainWorld(World world) {
        return world != null
                && world.getName().equalsIgnoreCase(MAIN_WORLD_NAME);
    }

    public static boolean isInMainWorld(Player player) {
        return player != null
                && isMainWorld(player.getWorld());
    }

    public static List<Player> mainWorldPlayers() {
        World world = mainWorld();

        if (world == null) {
            return Collections.emptyList();
        }

        return world.getPlayers();
    }

    public static void broadcastMainWorld(String... messages) {
        for (Player player : mainWorldPlayers()) {
            for (String message : messages) {
                player.sendMessage(message);
            }
        }
    }

    public static void sendMissingWorld(CommandSender sender) {
        if (sender == null) {
            return;
        }

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ Météo MoodCraft ✦ §8-----");
        sender.sendMessage("§c✖ §fMonde principal introuvable.");
        sender.sendMessage("§e➜ §7Le monde §eworld §7doit exister.");
        sender.sendMessage("§8-----------------------------");
    }
}
