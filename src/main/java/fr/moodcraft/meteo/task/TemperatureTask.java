package fr.moodcraft.meteo.task;

import fr.moodcraft.meteo.climate.AtmosphericPhenomenon;
import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.climate.WeatherType;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import org.bukkit.entity.Player;

public class TemperatureTask implements Runnable {

    @Override
    public void run() {

        for (Player player : WorldGuard.mainWorldPlayers()) {

            World world =
                    player.getWorld();

            //
            // SÉCURITÉ WORLD ONLY
            //

            if (!WorldGuard.isMainWorld(world)) {
                continue;
            }

            //
            // BASE
            //

            double temp =
                    17;

            //
            // SAISON
            //

            Season season =
                    ClimateManager.getSeason();

            switch (season) {

                case PRINTEMPS -> temp +=
                        4;

                case ETE -> temp +=
                        10;

                case AUTOMNE -> temp -=
                        2;

                case HIVER -> temp -=
                        9;
            }

            //
            // MÉTÉO
            //

            WeatherType weather =
                    ClimateManager.getWeather();

            switch (weather) {

                case SOLEIL -> temp +=
                        2;

                case PLUIE -> temp -=
                        3;

                case TEMPETE -> temp -=
                        5;

                case NEIGE -> temp -=
                        7;

                case BLIZZARD -> temp -=
                        14;

                case CANICULE -> temp +=
                        12;

                case BROUILLARD -> temp -=
                        1;
            }

            //
            // PHÉNOMÈNE ATMOSPHÉRIQUE
            //

            AtmosphericPhenomenon phenomenon =
                    ClimateManager.getPhenomenon();

            switch (phenomenon) {

                case ECLIPSE -> temp -=
                        6;

                case SUPERCELLULE -> temp -=
                        4;

                case TEMPETE_SABLE -> temp +=
                        3;

                case AURORE -> temp -=
                        2;

                case NONE -> {
                }
            }

            //
            // BIOME
            //

            Biome biome =
                    player.getLocation()
                            .getBlock()
                            .getBiome();

            String biomeName =
                    biome.name();

            if (biomeName.contains("DESERT")) {
                temp += 11;
            }

            if (biomeName.contains("BADLANDS")) {
                temp += 8;
            }

            if (biomeName.contains("SAVANNA")) {
                temp += 5;
            }

            if (biomeName.contains("JUNGLE")) {
                temp += 4;
            }

            if (biomeName.contains("SWAMP")) {
                temp += 2;
            }

            if (biomeName.contains("OCEAN")
                    || biomeName.contains("RIVER")) {
                temp -= 2;
            }

            if (biomeName.contains("SNOW")) {
                temp -= 14;
            }

            if (biomeName.contains("ICE")) {
                temp -= 17;
            }

            if (biomeName.contains("FROZEN")) {
                temp -= 19;
            }

            if (biomeName.contains("TAIGA")) {
                temp -= 7;
            }

            if (biomeName.contains("PEAK")
                    || biomeName.contains("SLOPE")
                    || biomeName.contains("GROVE")) {
                temp -= 9;
            }

            //
            // ALTITUDE
            //

            int y =
                    player.getLocation()
                            .getBlockY();

            if (y >= 170) {

                temp -=
                        14;

            } else if (y >= 140) {

                temp -=
                        10;

            } else if (y >= 110) {

                temp -=
                        6;

            } else if (y >= 90) {

                temp -=
                        3;
            }

            if (y <= 30) {
                temp += 2;
            }

            //
            // NUIT
            //

            long time =
                    world.getTime();

            boolean night =
                    time >= 13000
                            && time <= 23000;

            if (night) {
                temp -= 4;
            }

            //
            // AUBE
            //

            if (time >= 22500
                    || time <= 1000) {

                temp -=
                        2;
            }

            //
            // EXPOSITION SOLAIRE
            //

            if (world.isClearWeather()
                    && phenomenon != AtmosphericPhenomenon.ECLIPSE
                    && world.getHighestBlockYAt(
                    player.getLocation()
            ) <= player.getLocation().getY() + 1
                    && !night) {

                temp +=
                        3;
            }

            //
            // DANS L'EAU
            //

            if (player.isInWater()) {

                temp -=
                        5;
            }

            //
            // SOUS PLUIE
            //

            if (world.hasStorm()
                    && phenomenon != AtmosphericPhenomenon.TEMPETE_SABLE
                    && player.getLocation().getBlockY()
                    >= world.getHighestBlockYAt(
                    player.getLocation()
            ) - 1) {

                temp -=
                        2;
            }

            //
            // SOURCES CHAUDES
            //

            temp += getHeatSources(
                    player.getLocation()
            );

            //
            // SOURCES FROIDES
            //

            temp -= getColdSources(
                    player.getLocation()
            );

            //
            // LISSAGE
            //

            double old =
                    ClimateManager.getTemperature();

            temp =
                    (old * 0.75)
                            + (temp * 0.25);

            //
            // LIMITES
            //

            if (temp > 50) {
                temp = 50;
            }

            if (temp < -35) {
                temp = -35;
            }

            //
            // SAVE
            //

            ClimateManager.setTemperature(
                    temp
            );

            //
            // ACTIONBAR DISCRÈTE WORLD ONLY
            //

            player.sendActionBar(
                    "§8☁ §f"
                            + Math.round(temp)
                            + "°C §8• §7"
                            + getDisplayWeather(
                            weather,
                            phenomenon,
                            night
                    )
            );
        }
    }

    //
    // DISPLAY
    //

    private String getDisplayWeather(
            WeatherType weather,
            AtmosphericPhenomenon phenomenon,
            boolean night
    ) {

        if (phenomenon != null
                && phenomenon.isVisibleInMeteo()) {

            return phenomenon.getIcon()
                    + " "
                    + phenomenon.getDisplay();
        }

        if (weather == WeatherType.SOLEIL
                && night) {

            return "☾ §9Nuit";
        }

        return weather.getIcon()
                + " "
                + weather.getDisplay();
    }

    //
    // SOURCES CHAUDES
    //

    private double getHeatSources(
            Location location
    ) {

        double heat =
                0;

        for (int x = -4; x <= 4; x++) {

            for (int y = -2; y <= 2; y++) {

                for (int z = -4; z <= 4; z++) {

                    Block block =
                            location.clone()
                                    .add(
                                            x,
                                            y,
                                            z
                                    )
                                    .getBlock();

                    Material material =
                            block.getType();

                    switch (material) {

                        case LAVA, MAGMA_BLOCK -> heat +=
                                0.9;

                        case FIRE, SOUL_FIRE, CAMPFIRE, SOUL_CAMPFIRE -> heat +=
                                0.5;

                        case TORCH, WALL_TORCH, LANTERN -> heat +=
                                0.1;
                    }
                }
            }
        }

        return heat;
    }

    //
    // SOURCES FROIDES
    //

    private double getColdSources(
            Location location
    ) {

        double cold =
                0;

        for (int x = -4; x <= 4; x++) {

            for (int y = -2; y <= 2; y++) {

                for (int z = -4; z <= 4; z++) {

                    Block block =
                            location.clone()
                                    .add(
                                            x,
                                            y,
                                            z
                                    )
                                    .getBlock();

                    Material material =
                            block.getType();

                    switch (material) {

                        case SNOW, SNOW_BLOCK, POWDER_SNOW, ICE, PACKED_ICE, BLUE_ICE -> cold +=
                                0.35;
                    }
                }
            }
        }

        return cold;
    }
}
