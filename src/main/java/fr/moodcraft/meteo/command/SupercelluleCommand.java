package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.Main;

import fr.moodcraft.meteo.climate.AtmosphericPhenomenon;
import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.WeatherType;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Sound;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SupercelluleCommand implements CommandExecutor {

    private static boolean active =
            false;

    private final Random random =
            new Random();

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (active) {

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Météo MoodCraft ✦ §8-----");
            sender.sendMessage("§c✖ §fUne supercellule est déjà active.");
            sender.sendMessage("§8-----------------------------");

            return true;
        }

        World world =
                WorldGuard.mainWorld();

        if (world == null) {

            WorldGuard.sendMissingWorld(
                    sender
            );

            return true;
        }

        active =
                true;

        ClimateManager.setPhenomenon(
                AtmosphericPhenomenon.SUPERCELLULE
        );

        ClimateManager.setWeather(
                WeatherType.TEMPETE
        );

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Supercellule Atmosphérique ✦ §8-----",
                "§e➜ §7Une masse orageuse gigantesque traverse le ciel.",
                "§e➜ §7Monde : §eworld",
                "§e➜ §7/météo affiche maintenant la supercellule active.",
                "§8-----------------------------"
        );

        if (!(sender instanceof Player player)
                || !WorldGuard.isInMainWorld(player)) {

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Supercellule Atmosphérique ✦ §8-----");
            sender.sendMessage("§a✔ §fSupercellule lancée dans §eworld§a.");
            sender.sendMessage("§8-----------------------------");
        }

        world.setStorm(
                true
        );

        world.setThundering(
                true
        );

        for (Player player : WorldGuard.mainWorldPlayers()) {

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                    10f,
                    0.7f
            );
        }

        new BukkitRunnable() {

            int timer =
                    0;

            @Override
            public void run() {

                if (timer >= 120) {

                    cancel();

                    world.setStorm(
                            false
                    );

                    world.setThundering(
                            false
                    );

                    if (ClimateManager.getPhenomenon() == AtmosphericPhenomenon.SUPERCELLULE) {
                        ClimateManager.clearPhenomenon();
                    }

                    WorldGuard.broadcastMainWorld(
                            "",
                            "§8----- §6✦ Supercellule ✦ §8-----",
                            "§e➜ §7La supercellule se dissipe.",
                            "§8-----------------------------"
                    );

                    active =
                            false;

                    return;
                }

                for (Player player : WorldGuard.mainWorldPlayers()) {

                    if (random.nextInt(100) < 35) {

                        player.getWorld().strikeLightning(
                                player.getLocation().add(
                                        random.nextInt(30) - 15,
                                        0,
                                        random.nextInt(30) - 15
                                )
                        );
                    }
                }

                timer++;
            }

        }.runTaskTimer(
                Main.getInstance(),
                40L,
                20L * 2
        );

        return true;
    }
}
