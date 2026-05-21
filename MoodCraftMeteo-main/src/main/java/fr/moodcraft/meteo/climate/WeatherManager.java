package fr.moodcraft.meteo.climate;

import fr.moodcraft.meteo.util.WorldGuard;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WeatherManager {

    private static WeatherType lastWeather = null;
    private static long lastChange = 0;

    public static void applyWeather() {
        applyWeather(false);
    }

    public static void applyWeather(boolean force) {
        World world = WorldGuard.mainWorld();
        if (world == null) return;

        WeatherType current = ClimateManager.getWeather();

        if (!force && current == lastWeather) {
            enforceWeatherState(world, current);
            return;
        }

        long now = System.currentTimeMillis();
        if (!force && lastWeather != null && now - lastChange < 240000) {
            enforceWeatherState(world, current);
            return;
        }

        lastChange = now;
        atmosphereTransition(world, current);
        enforceWeatherState(world, current);
        announceWeather(world, current);
        lastWeather = current;
    }

    private static void enforceWeatherState(World world, WeatherType weather) {
        if (weather == null) weather = WeatherType.SOLEIL;

        switch (weather) {
            case SOLEIL, BROUILLARD, CANICULE -> {
                world.setStorm(false);
                world.setThundering(false);
                world.setWeatherDuration(0);
                world.setThunderDuration(0);
                world.setClearWeatherDuration(20 * 60 * 20);
            }
            case PLUIE, NEIGE -> {
                world.setStorm(true);
                world.setThundering(false);
                world.setThunderDuration(0);
                world.setWeatherDuration(20 * 60 * 12);
            }
            case TEMPETE, BLIZZARD -> {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(20 * 60 * 12);
                world.setThunderDuration(20 * 60 * 8);
            }
        }
    }

    private static void announceWeather(World world, WeatherType current) {
        switch (current) {
            case SOLEIL -> {
                if (isNight(world)) announce(world, "§8☾ Nuit Claire", "§7Le ciel se dégage sous les étoiles.", Sound.BLOCK_AMETHYST_BLOCK_CHIME);
                else announce(world, "§6☀ Retour du Soleil", "§7Les nuages se dissipent lentement.", Sound.BLOCK_AMETHYST_BLOCK_CHIME);
            }
            case PLUIE -> announce(world, "§7Arrivée de la Pluie", "§8Une pluie calme traverse la région.", Sound.WEATHER_RAIN);
            case TEMPETE -> announce(world, "§8⛈ Tempête Atmosphérique", "§7Le ciel devient instable.", Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
            case BLIZZARD -> announce(world, "§b❄ Blizzard", "§7Des vents glacials balayent le monde.", Sound.BLOCK_GLASS_BREAK);
            case NEIGE -> announce(world, "§fChute de Neige", "§7Les premiers flocons apparaissent.", Sound.BLOCK_SNOW_BREAK);
            case BROUILLARD -> announce(world, "§8Brouillard Dense", "§7La visibilité diminue progressivement.", Sound.AMBIENT_CAVE);
            case CANICULE -> announce(world, "§cCanicule", "§7Une chaleur écrasante s'installe.", Sound.BLOCK_FIRE_AMBIENT);
        }
    }

    private static boolean isNight(World world) {
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }

    private static void atmosphereTransition(World world, WeatherType type) {
        for (Player player : WorldGuard.mainWorldPlayers()) {
            String display = type.getDisplay();
            String icon = type.getIcon();

            if (type == WeatherType.SOLEIL && isNight(world)) {
                display = "Nuit";
                icon = "☾";
            }

            player.sendActionBar("§8✦ §bTransition Atmosphérique §8• §f" + icon + " " + display);

            switch (type) {
                case SOLEIL -> player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 0.7f, 1.6f);
                case PLUIE -> player.playSound(player.getLocation(), Sound.WEATHER_RAIN, 0.6f, 0.8f);
                case TEMPETE -> player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.7f);
                case NEIGE -> player.playSound(player.getLocation(), Sound.BLOCK_SNOW_FALL, 0.7f, 0.9f);
                case BLIZZARD -> player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5f, 0.5f);
                case BROUILLARD -> player.playSound(player.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 0.4f, 0.6f);
                case CANICULE -> player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1.4f);
            }
        }
    }

    private static void announce(World world, String title, String subtitle, Sound sound) {
        for (Player player : WorldGuard.mainWorldPlayers()) {
            player.sendTitle(title, subtitle, 20, 80, 20);
            player.playSound(player.getLocation(), sound, 0.8f, 1f);
        }
    }
}
