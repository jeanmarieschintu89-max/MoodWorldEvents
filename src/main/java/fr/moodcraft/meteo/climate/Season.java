package fr.moodcraft.meteo.climate;

public enum Season {

    //
    // 🌸 PRINTEMPS
    //

    PRINTEMPS(

            "🌸",

            "§dPrintemps",

            0.55,

            18,

            26
    ),

    //
    // ☀ ÉTÉ
    //

    ETE(

            "☀",

            "§6Été",

            0.30,

            26,

            42
    ),

    //
    // 🍂 AUTOMNE
    //

    AUTOMNE(

            "🍂",

            "§6Automne",

            0.70,

            12,

            22
    ),

    //
    // ❄ HIVER
    //

    HIVER(

            "❄",

            "§bHiver",

            0.85,

            -8,

            10
    );

    //
    // 🎨 ICÔNE
    //

    private final String icon;

    //
    // 📛 NOM
    //

    private final String display;

    //
    // 💧 HUMIDITÉ
    //

    private final double humidity;

    //
    // 🌡 TEMP MIN
    //

    private final int minTemp;

    //
    // 🌡 TEMP MAX
    //

    private final int maxTemp;

    //
    // 🔧 CONSTRUCTOR
    //

    Season(

            String icon,

            String display,

            double humidity,

            int minTemp,

            int maxTemp
    ) {

        this.icon = icon;

        this.display = display;

        this.humidity = humidity;

        this.minTemp = minTemp;

        this.maxTemp = maxTemp;
    }

    //
    // 🎨 ICON
    //

    public String getIcon() {

        return icon;
    }

    //
    // 📛 DISPLAY
    //

    public String getDisplay() {

        return display;
    }

    //
    // 💧 HUMIDITY
    //

    public double getHumidity() {

        return humidity;
    }

    //
    // 🌡 MIN
    //

    public int getMinTemp() {

        return minTemp;
    }

    //
    // 🌡 MAX
    //

    public int getMaxTemp() {

        return maxTemp;
    }
}