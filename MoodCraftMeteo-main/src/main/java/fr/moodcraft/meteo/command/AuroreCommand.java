package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.Main;

import fr.moodcraft.meteo.climate.AtmosphericPhenomenon;
import fr.moodcraft.meteo.climate.ClimateManager;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

public class AuroreCommand implements CommandExecutor {

    private static boolean active =
            false;

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
            sender.sendMessage("§c✖ §fUne aurore est déjà visible.");
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
                AtmosphericPhenomenon.AURORE
        );

        world.setTime(
                18000
        );

        world.setStorm(
                false
        );

        world.setThundering(
                false
        );

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Aurore Boréale ✦ §8-----",
                "§e➜ §7Des lumières célestes apparaissent dans le ciel.",
                "§e➜ §7Monde : §eworld",
                "§e➜ §7/météo affiche maintenant l'aurore active.",
                "§8-----------------------------"
        );

        if (!(sender instanceof Player player)
                || !WorldGuard.isInMainWorld(player)) {

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Aurore Boréale ✦ §8-----");
            sender.sendMessage("§a✔ §fAurore lancée dans §eworld§a.");
            sender.sendMessage("§8-----------------------------");
        }

        for (Player player : WorldGuard.mainWorldPlayers()) {

            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_AMETHYST_BLOCK_RESONATE,
                    1f,
                    1.5f
            );
        }

        new BukkitRunnable() {

            int timer =
                    0;

            @Override
            public void run() {

                if (timer >= 180) {

                    cancel();

                    active =
                            false;

                    if (ClimateManager.getPhenomenon() == AtmosphericPhenomenon.AURORE) {
                        ClimateManager.clearPhenomenon();
                    }

                    WorldGuard.broadcastMainWorld(
                            "",
                            "§8----- §6✦ Aurore Boréale ✦ §8-----",
                            "§e➜ §7L'aurore disparaît lentement.",
                            "§8-----------------------------"
                    );

                    return;
                }

                for (Player player : WorldGuard.mainWorldPlayers()) {

                    player.spawnParticle(
                            Particle.END_ROD,
                            player.getLocation().add(
                                    0,
                                    8,
                                    0
                            ),
                            25,
                            8,
                            3,
                            8,
                            0.02
                    );

                    player.spawnParticle(
                            Particle.WAX_ON,
                            player.getLocation().add(
                                    0,
                                    10,
                                    0
                            ),
                            15,
                            10,
                            4,
                            10,
                            0.01
                    );
                }

                timer++;
            }

        }.runTaskTimer(
                Main.getInstance(),
                20L,
                20L
        );

        return true;
    }
}
