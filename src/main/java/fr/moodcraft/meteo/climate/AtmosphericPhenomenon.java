package fr.moodcraft.meteo.climate;

public enum AtmosphericPhenomenon {

    NONE(
            "",
            "§7Aucun phénomène",
            false,
            false,
            false
    ),

    AURORE(
            "✦",
            "§bAurore boréale",
            false,
            false,
            true
    ),

    ECLIPSE(
            "☾",
            "§5Éclipse solaire",
            true,
            false,
            true
    ),

    SUPERCELLULE(
            "⛈",
            "§8Supercellule atmosphérique",
            true,
            true,
            true
    ),

    TEMPETE_SABLE(
            "⌁",
            "§6Tempête de sable",
            true,
            false,
            true
    );

    private final String icon;
    private final String display;
    private final boolean blocksNaturalWeather;
    private final boolean dangerous;
    private final boolean visibleInMeteo;

    AtmosphericPhenomenon(
            String icon,
            String display,
            boolean blocksNaturalWeather,
            boolean dangerous,
            boolean visibleInMeteo
    ) {

        this.icon = icon;
        this.display = display;
        this.blocksNaturalWeather = blocksNaturalWeather;
        this.dangerous = dangerous;
        this.visibleInMeteo = visibleInMeteo;
    }

    public String getIcon() {
        return icon;
    }

    public String getDisplay() {
        return display;
    }

    public boolean blocksNaturalWeather() {
        return blocksNaturalWeather;
    }

    public boolean isDangerous() {
        return dangerous;
    }

    public boolean isVisibleInMeteo() {
        return visibleInMeteo;
    }
}
