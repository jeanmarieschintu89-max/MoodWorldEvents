package fr.moodcraft.meteo.climate;

import org.bukkit.util.Vector;

import java.util.Random;

public class WindManager {

    //
    // 🌪 RANDOM
    //

    private static final Random random =
            new Random();

    //
    // 🌬 INTENSITÉ
    // 0 → calme
    // 1 → léger
    // 2 → modéré
    // 3 → violent
    //

    private static int intensity =
            0;

    //
    // 🧭 DIRECTION
    //

    private static Vector direction =
            new Vector(1, 0, 0);

    //
    // 🌬 UPDATE
    //

    public static void updateWind() {

        WeatherType weather =
                ClimateManager.getWeather();

        //
        // 🌦 FORCE SELON MÉTÉO
        //

        switch (weather) {

            case SOLEIL ->
                    intensity = random.nextInt(2);

            case BROUILLARD ->
                    intensity = 0;

            case PLUIE ->
                    intensity = 1;

            case TEMPETE ->
                    intensity = 3;

            case BLIZZARD ->
                    intensity = 3;

            case NEIGE ->
                    intensity = 2;

            case CANICULE ->
                    intensity = 2;
        }

        //
        // 🧭 DIRECTION
        //

        double x =
                -1 + (2 * random.nextDouble());

        double z =
                -1 + (2 * random.nextDouble());

        direction =
                new Vector(x, 0, z)
                        .normalize();
    }

    //
    // 🌪 GET FORCE
    //

    public static int getIntensity() {

        return intensity;
    }

    //
    // 🧭 GET DIRECTION
    //

    public static Vector getDirection() {

        return direction.clone();
    }

    //
    // 🌬 NOM
    //

    public static String getWindName() {

        return switch (intensity) {

            case 0 -> "§7Calme";

            case 1 -> "§aBrise légère";

            case 2 -> "§6Vent modéré";

            default -> "§cVent violent";
        };
    }

    //
    // 🧭 CARDINAL
    //

    public static String getDirectionName() {

        double x =
                direction.getX();

        double z =
                direction.getZ();

        if (Math.abs(x) > Math.abs(z)) {

            return x > 0
                    ? "Est"
                    : "Ouest";
        }

        return z > 0
                ? "Sud"
                : "Nord";
    }
}