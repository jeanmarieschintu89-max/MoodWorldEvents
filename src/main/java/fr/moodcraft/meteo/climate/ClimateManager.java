package fr.moodcraft.meteo.climate;

public class ClimateManager {

    //
    // 🌸 SAISON
    //

    private static Season season =
            Season.PRINTEMPS;

    //
    // 🌦 MÉTÉO ACTUELLE
    //

    private static WeatherType weather =
            WeatherType.SOLEIL;

    //
    // ✦ PHÉNOMÈNE ATMOSPHÉRIQUE RP
    //

    private static AtmosphericPhenomenon phenomenon =
            AtmosphericPhenomenon.NONE;

    //
    // 🌡 TEMPÉRATURE GLOBALE
    //

    private static double temperature =
            18;

    //
    // ☁ HUMIDITÉ
    //

    private static double humidity =
            0.45;

    //
    // 🌪 STABILITÉ CLIMATIQUE
    // plus haut = météo stable
    //

    private static double stability =
            0.75;

    //
    // ⏳ DURÉE MÉTÉO
    //

    private static long weatherStart =
            System.currentTimeMillis();

    //
    // ⏳ DURÉE PHÉNOMÈNE
    //

    private static long phenomenonStart =
            0;

    //
    // 🌸 GET SAISON
    //

    public static Season getSeason() {

        return season;
    }

    //
    // 🌸 SET SAISON
    //

    public static void setSeason(
            Season newSeason
    ) {

        if (newSeason == null)
            return;

        season = newSeason;
    }

    //
    // 🌦 GET WEATHER
    //

    public static WeatherType getWeather() {

        return weather;
    }

    //
    // 🌦 SET WEATHER
    //

    public static void setWeather(
            WeatherType newWeather
    ) {

        if (newWeather == null)
            return;

        //
        // 🔒 évite reset inutile
        //

        if (weather == newWeather)
            return;

        weather = newWeather;

        weatherStart =
                System.currentTimeMillis();
    }

    //
    // ✦ PHÉNOMÈNE ACTUEL
    //

    public static AtmosphericPhenomenon getPhenomenon() {

        return phenomenon == null
                ? AtmosphericPhenomenon.NONE
                : phenomenon;
    }

    public static void setPhenomenon(
            AtmosphericPhenomenon newPhenomenon
    ) {

        if (newPhenomenon == null) {
            newPhenomenon = AtmosphericPhenomenon.NONE;
        }

        if (phenomenon == newPhenomenon) {
            return;
        }

        phenomenon = newPhenomenon;
        phenomenonStart = newPhenomenon == AtmosphericPhenomenon.NONE
                ? 0
                : System.currentTimeMillis();
    }

    public static void clearPhenomenon() {

        setPhenomenon(
                AtmosphericPhenomenon.NONE
        );
    }

    public static boolean hasActivePhenomenon() {

        return getPhenomenon() != AtmosphericPhenomenon.NONE;
    }

    public static boolean phenomenonBlocksNaturalWeather() {

        return getPhenomenon().blocksNaturalWeather();
    }

    public static long getPhenomenonDuration() {

        if (!hasActivePhenomenon()
                || phenomenonStart <= 0) {
            return 0;
        }

        return System.currentTimeMillis()
                - phenomenonStart;
    }

    //
    // 🌡 GET TEMP
    //

    public static double getTemperature() {

        return temperature;
    }

    //
    // 🌡 SET TEMP
    //

    public static void setTemperature(
            double temp
    ) {

        temperature = temp;
    }

    //
    // 💧 HUMIDITÉ
    //

    public static double getHumidity() {

        return humidity;
    }

    public static void setHumidity(
            double value
    ) {

        if (value < 0)
            value = 0;

        if (value > 1)
            value = 1;

        humidity = value;
    }

    //
    // 🌪 STABILITÉ
    //

    public static double getStability() {

        return stability;
    }

    public static void setStability(
            double value
    ) {

        if (value < 0)
            value = 0;

        if (value > 1)
            value = 1;

        stability = value;
    }

    //
    // ⏳ DURÉE MÉTÉO
    //

    public static long getWeatherDuration() {

        return System.currentTimeMillis()
                - weatherStart;
    }

    //
    // 🌦 CHECK
    //

    public static boolean isStorm() {

        return weather == WeatherType.TEMPETE
                || weather == WeatherType.BLIZZARD
                || getPhenomenon() == AtmosphericPhenomenon.SUPERCELLULE;
    }

    public static boolean isCold() {

        return temperature <= 4;
    }

    public static boolean isHot() {

        return temperature >= 30;
    }
}
