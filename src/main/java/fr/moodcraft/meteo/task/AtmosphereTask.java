package fr.moodcraft.meteo.task;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.WeatherType;
import fr.moodcraft.meteo.climate.WindManager;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import java.util.Random;

public class AtmosphereTask implements Runnable {

    private final Random random =
            new Random();

    @Override
    public void run() {

        //
        // UPDATE WIND
        //

        WindManager.updateWind();

        //
        // WEATHER
        //

        WeatherType weather =
                ClimateManager.getWeather();

        //
        // LOOP PLAYERS WORLD ONLY
        //

        for (Player player : WorldGuard.mainWorldPlayers()) {

            switch (weather) {

                //
                // SOLEIL
                //

                case SOLEIL -> {

                    if (random.nextInt(100) < 3) {

                        player.playSound(
                                player.getLocation(),
                                Sound.AMBIENT_BASALT_DELTAS_ADDITIONS,
                                0.05f,
                                2f
                        );
                    }
                }

                //
                // PLUIE
                //

                case PLUIE -> {

                    if (random.nextInt(100) < 4) {

                        player.playSound(
                                player.getLocation(),
                                Sound.WEATHER_RAIN,
                                0.12f,
                                0.8f
                        );
                    }

                    if (random.nextInt(100) < 4) {

                        player.spawnParticle(
                                Particle.RAIN,
                                player.getLocation().add(
                                        0,
                                        5,
                                        0
                                ),
                                8,
                                2,
                                1,
                                2,
                                0.01
                        );
                    }
                }

                //
                // TEMPÊTE
                //

                case TEMPETE -> {

                    if (random.nextInt(100) < 3) {

                        player.playSound(
                                player.getLocation(),
                                Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                                0.2f,
                                0.7f
                        );
                    }

                    if (random.nextInt(100) < 4) {

                        player.spawnParticle(
                                Particle.CLOUD,
                                player.getLocation().add(
                                        0,
                                        6,
                                        0
                                ),
                                12,
                                3,
                                1,
                                3,
                                0.01
                        );
                    }
                }

                //
                // NEIGE
                //

                case NEIGE -> {

                    if (random.nextInt(100) < 4) {

                        player.spawnParticle(
                                Particle.SNOWFLAKE,
                                player.getLocation().add(
                                        0,
                                        4,
                                        0
                                ),
                                10,
                                2,
                                1,
                                2,
                                0.01
                        );
                    }

                    if (random.nextInt(100) < 2) {

                        player.playSound(
                                player.getLocation(),
                                Sound.BLOCK_SNOW_FALL,
                                0.15f,
                                1f
                        );
                    }
                }

                //
                // BLIZZARD
                //

                case BLIZZARD -> {

                    if (random.nextInt(100) < 8) {

                        player.spawnParticle(
                                Particle.SNOWFLAKE,
                                player.getLocation().add(
                                        0,
                                        5,
                                        0
                                ),
                                18,
                                3,
                                2,
                                3,
                                0.02
                        );
                    }

                    if (random.nextInt(100) < 4) {

                        player.playSound(
                                player.getLocation(),
                                Sound.WEATHER_RAIN_ABOVE,
                                0.2f,
                                0.5f
                        );
                    }
                }

                //
                // BROUILLARD
                //

                case BROUILLARD -> {

                    if (random.nextInt(100) < 3) {

                        player.spawnParticle(
                                Particle.CLOUD,
                                player.getLocation().add(
                                        0,
                                        1,
                                        0
                                ),
                                6,
                                1,
                                0.5,
                                1,
                                0.005
                        );
                    }

                    if (random.nextInt(100) < 2) {

                        player.playSound(
                                player.getLocation(),
                                Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD,
                                0.08f,
                                0.7f
                        );
                    }
                }

                //
                // CANICULE
                //

                case CANICULE -> {

                    if (random.nextInt(100) < 2) {

                        player.playSound(
                                player.getLocation(),
                                Sound.BLOCK_FIRE_AMBIENT,
                                0.08f,
                                1.4f
                        );
                    }

                    if (random.nextInt(100) < 3) {

                        player.spawnParticle(
                                Particle.ASH,
                                player.getLocation().add(
                                        0,
                                        2,
                                        0
                                ),
                                4,
                                1,
                                0.5,
                                1,
                                0.005
                        );
                    }
                }
            }
        }
    }
}