package fr.moodcraft.meteo.climate;

public enum WeatherType {

    //
    // ☀ CLAIR
    //

    SOLEIL(

            "☀",

            "§6Ensoleillé",

            false,

            false,

            false,

            0.15
    ),

    //
    // 🌧 PLUIE
    //

    PLUIE(

            "🌧",

            "§7Pluie",

            true,

            false,

            false,

            0.45
    ),

    //
    // ⛈ TEMPÊTE
    //

    TEMPETE(

            "⛈",

            "§8Tempête",

            true,

            true,

            false,

            0.80
    ),

    //
    // ❄ NEIGE
    //

    NEIGE(

            "❄",

            "§fNeige",

            true,

            false,

            true,

            0.60
    ),

    //
    // 🌫 BROUILLARD
    //

    BROUILLARD(

            "🌫",

            "§7Brouillard",

            false,

            false,

            false,

            0.35
    ),

    //
    // 🔥 CANICULE
    //

    CANICULE(

            "🔥",

            "§cCanicule",

            false,

            false,

            false,

            0.05
    ),

    //
    // 🌨 BLIZZARD
    //

    BLIZZARD(

            "🌨",

            "§bBlizzard",

            true,

            true,

            true,

            0.95
    );

    //
    // 🎨 ICON
    //

    private final String icon;

    //
    // 📛 NOM FORMATÉ
    //

    private final String display;

    //
    // 🌧 PLUIE
    //

    private final boolean storm;

    //
    // ⚡ TONNERRE
    //

    private final boolean thunder;

    //
    // ❄ FROID
    //

    private final boolean cold;

    //
    // 💧 HUMIDITÉ
    //

    private final double humidity;

    //
    // 🔧 CONSTRUCTOR
    //

    WeatherType(

            String icon,

            String display,

            boolean storm,

            boolean thunder,

            boolean cold,

            double humidity
    ) {

        this.icon = icon;

        this.display = display;

        this.storm = storm;

        this.thunder = thunder;

        this.cold = cold;

        this.humidity = humidity;
    }

    //
    // 🎨 GET ICON
    //

    public String getIcon() {

        return icon;
    }

    //
    // 📛 GET DISPLAY
    //

    public String getDisplay() {

        return display;
    }

    //
    // 🌧 STORM
    //

    public boolean isStorm() {

        return storm;
    }

    //
    // ⚡ THUNDER
    //

    public boolean hasThunder() {

        return thunder;
    }

    //
    // ❄ COLD
    //

    public boolean isCold() {

        return cold;
    }

    //
    // 💧 HUMIDITY
    //

    public double getHumidity() {

        return humidity;
    }
}