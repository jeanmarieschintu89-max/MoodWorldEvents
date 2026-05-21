package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.Main;

import fr.moodcraft.meteo.climate.AtmosphericPhenomenon;
import fr.moodcraft.meteo.climate.ClimateManager;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

public class EclipseCommand implements CommandExecutor {

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
            sender.sendMessage("§c✖ §fUne éclipse est déjà en cours.");
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
                AtmosphericPhenomenon.ECLIPSE
        );

        world.setTime(
                6000
        );

        BossBar bar =
                Bukkit.createBossBar(
                        "§5Éclipse Solaire",
                        BarColor.PURPLE,
                        BarStyle.SEGMENTED_12
                );

        syncBossBar(
                bar
        );

        for (Player player : WorldGuard.mainWorldPlayers()) {

            player.playSound(
                    player.getLocation(),
                    Sound.AMBIENT_BASALT_DELTAS_MOOD,
                    1f,
                    0.6f
            );

            player.sendTitle(
                    "§5Éclipse Solaire",
                    "§7Le ciel commence à changer.",
                    20,
                    80,
                    20
            );
        }

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Phénomène Astral ✦ §8-----",
                "§e➜ §7Une ombre gigantesque traverse lentement le ciel.",
                "§e➜ §7Monde : §eworld",
                "§e➜ §7/météo affiche maintenant l'éclipse active.",
                "§8-----------------------------"
        );

        if (!(sender instanceof Player player)
                || !WorldGuard.isInMainWorld(player)) {

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Phénomène Astral ✦ §8-----");
            sender.sendMessage("§a✔ §fÉclipse lancée dans §eworld§a.");
            sender.sendMessage("§8-----------------------------");
        }

        new BukkitRunnable() {

            double progress =
                    0;

            boolean totality =
                    false;

            @Override
            public void run() {

                syncBossBar(
                        bar
                );

                if (progress >= 1.0) {

                    cancel();

                    world.setStorm(
                            false
                    );

                    world.setThundering(
                            false
                    );

                    world.setTime(
                            1000
                    );

                    bar.removeAll();

                    if (ClimateManager.getPhenomenon() == AtmosphericPhenomenon.ECLIPSE) {
                        ClimateManager.clearPhenomenon();
                    }

                    WorldGuard.broadcastMainWorld(
                            "",
                            "§8----- §6✦ Fin de l'Éclipse ✦ §8-----",
                            "§e➜ §7La lumière reprend progressivement le contrôle du ciel.",
                            "§8-----------------------------"
                    );

                    for (Player player : WorldGuard.mainWorldPlayers()) {

                        player.playSound(
                                player.getLocation(),
                                Sound.BLOCK_BEACON_ACTIVATE,
                                1f,
                                1.3f
                        );

                        player.sendTitle(
                                "§e☀ Lumière retrouvée",
                                "§7L'éclipse se dissipe.",
                                20,
                                80,
                                20
                        );
                    }

                    active =
                            false;

                    return;
                }

                progress +=
                        0.01;

                bar.setProgress(
                        Math.min(
                                progress,
                                1.0
                        )
                );

                long eclipseTime =
                        12000
                                + (long) (progress * 6000);

                world.setTime(
                        eclipseTime
                );

                if (progress >= 0.45
                        && progress <= 0.65) {

                    if (!totality) {

                        totality =
                                true;

                        WorldGuard.broadcastMainWorld(
                                "",
                                "§8----- §6✦ Totalité ✦ §8-----",
                                "§e➜ §7Le soleil disparaît totalement derrière la lune.",
                                "§8-----------------------------"
                        );

                        world.setStorm(
                                true
                        );

                        for (Player player : WorldGuard.mainWorldPlayers()) {

                            player.playSound(
                                    player.getLocation(),
                                    Sound.AMBIENT_CAVE,
                                    1f,
                                    0.4f
                            );

                            player.sendTitle(
                                    "§8Totalité",
                                    "§7Le monde plonge dans l'obscurité.",
                                    20,
                                    100,
                                    20
                            );
                        }
                    }

                } else {

                    totality =
                            false;
                }
            }

        }.runTaskTimer(
                Main.getInstance(),
                20L,
                20L * 2
        );

        return true;
    }

    private void syncBossBar(
            BossBar bar
    ) {

        for (Player player : Bukkit.getOnlinePlayers()) {

            boolean inMainWorld =
                    WorldGuard.isInMainWorld(
                            player
                    );

            if (inMainWorld
                    && !bar.getPlayers().contains(player)) {

                bar.addPlayer(
                        player
                );
            }

            if (!inMainWorld
                    && bar.getPlayers().contains(player)) {

                bar.removePlayer(
                        player
                );
            }
        }
    }
}
