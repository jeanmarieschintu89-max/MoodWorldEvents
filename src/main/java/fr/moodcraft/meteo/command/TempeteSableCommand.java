package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.Main;

import fr.moodcraft.meteo.climate.AtmosphericPhenomenon;
import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.WeatherType;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.block.Biome;

import org.bukkit.block.data.BlockData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

public class TempeteSableCommand implements CommandExecutor {

    private static boolean active =
            false;

    private final BlockData sandDust =
            Material.SAND.createBlockData();

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
            sender.sendMessage("§c✖ §fUne tempête de sable est déjà active.");
            sender.sendMessage("§8-----------------------------");

            return true;
        }

        if (WorldGuard.mainWorld() == null) {

            WorldGuard.sendMissingWorld(
                    sender
            );

            return true;
        }

        active =
                true;

        ClimateManager.setPhenomenon(
                AtmosphericPhenomenon.TEMPETE_SABLE
        );

        ClimateManager.setWeather(
                WeatherType.BROUILLARD
        );

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Tempête de Sable ✦ §8-----",
                "§e➜ §7De puissants vents désertiques obscurcissent l'horizon.",
                "§e➜ §7Zones fortes : §eDéserts §7et §eBadlands",
                "§e➜ §7/météo affiche maintenant la tempête de sable active.",
                "§8-----------------------------"
        );

        if (!(sender instanceof Player player)
                || !WorldGuard.isInMainWorld(player)) {

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Tempête de Sable ✦ §8-----");
            sender.sendMessage("§a✔ §fTempête lancée dans §eworld§a.");
            sender.sendMessage("§8-----------------------------");
        }

        for (Player player : WorldGuard.mainWorldPlayers()) {

            player.playSound(
                    player.getLocation(),
                    Sound.WEATHER_RAIN_ABOVE,
                    1f,
                    0.5f
            );
        }

        new BukkitRunnable() {

            int timer =
                    0;

            @Override
            public void run() {

                if (timer >= 180) {

                    cancel();

                    if (ClimateManager.getPhenomenon() == AtmosphericPhenomenon.TEMPETE_SABLE) {
                        ClimateManager.clearPhenomenon();
                    }

                    WorldGuard.broadcastMainWorld(
                            "",
                            "§8----- §6✦ Tempête de Sable ✦ §8-----",
                            "§e➜ §7Les vents désertiques s'apaisent.",
                            "§8-----------------------------"
                    );

                    active =
                            false;

                    return;
                }

                for (Player player : WorldGuard.mainWorldPlayers()) {

                    Biome biome =
                            player.getLocation()
                                    .getBlock()
                                    .getBiome();

                    String name =
                            biome.name();

                    boolean dryBiome =
                            name.contains("DESERT")
                                    || name.contains("BADLANDS")
                                    || name.contains("SAVANNA");

                    int dustAmount = dryBiome
                            ? 120
                            : 18;

                    double radius = dryBiome
                            ? 8
                            : 3;

                    player.spawnParticle(
                            Particle.FALLING_DUST,
                            player.getLocation().add(
                                    0,
                                    1,
                                    0
                            ),
                            dustAmount,
                            radius,
                            2,
                            radius,
                            1,
                            sandDust
                    );

                    if (dryBiome) {

                        player.spawnParticle(
                                Particle.CLOUD,
                                player.getLocation().add(
                                        0,
                                        2,
                                        0
                                ),
                                20,
                                5,
                                1,
                                5,
                                0.03
                        );
                    }

                    if (timer % 8 == 0) {

                        player.playSound(
                                player.getLocation(),
                                Sound.WEATHER_RAIN,
                                dryBiome ? 0.7f : 0.25f,
                                0.4f
                        );
                    }
                }

                timer++;
            }

        }.runTaskTimer(
                Main.getInstance(),
                20L,
                10L
        );

        return true;
    }
}
