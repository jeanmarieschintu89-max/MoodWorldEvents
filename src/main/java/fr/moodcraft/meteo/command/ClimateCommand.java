package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.climate.WeatherType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClimateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        Season season =
                ClimateManager.getSeason();

        WeatherType weather =
                ClimateManager.getWeather();

        double temp =
                ClimateManager.getTemperature();

        double humidity =
                ClimateManager.getHumidity();

        double stability =
                ClimateManager.getStability();

        long duration =
                ClimateManager.getWeatherDuration();

        String feeling;

        if (temp >= 38) {
            feeling = "§cÉtouffant";
        } else if (temp >= 28) {
            feeling = "§6Très chaud";
        } else if (temp >= 18) {
            feeling = "§aTempéré";
        } else if (temp >= 8) {
            feeling = "§2Frais";
        } else if (temp >= 0) {
            feeling = "§3Froid";
        } else {
            feeling = "§bGlacial";
        }

        String humidityText;

        if (humidity >= 0.8) {
            humidityText = "§9Très humide";
        } else if (humidity >= 0.55) {
            humidityText = "§bHumide";
        } else if (humidity >= 0.3) {
            humidityText = "§aÉquilibré";
        } else {
            humidityText = "§6Sec";
        }

        String stabilityText;

        if (stability >= 0.8) {
            stabilityText = "§aStable";
        } else if (stability >= 0.5) {
            stabilityText = "§eVariable";
        } else {
            stabilityText = "§cInstable";
        }

        String sky;

        if (weather == WeatherType.SOLEIL) {
            sky = "§bCiel dégagé";
        } else if (weather == WeatherType.PLUIE) {
            sky = "§7Nuages denses";
        } else if (weather == WeatherType.TEMPETE) {
            sky = "§8Activité orageuse";
        } else if (weather == WeatherType.BLIZZARD) {
            sky = "§fVoile neigeux";
        } else if (weather == WeatherType.BROUILLARD) {
            sky = "§7Brume atmosphérique";
        } else if (weather == WeatherType.CANICULE) {
            sky = "§6Atmosphère sèche";
        } else {
            sky = "§fConditions hivernales";
        }

        long minutes =
                duration / 1000 / 60;

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ Réseau Climatique ✦ §8-----");
        sender.sendMessage("§e➜ §7Saison : §f"
                + season.getIcon()
                + " "
                + season.getDisplay());
        sender.sendMessage("§e➜ §7Météo : §f"
                + weather.getIcon()
                + " "
                + weather.getDisplay());
        sender.sendMessage("§e➜ §7Température : §6"
                + Math.round(temp)
                + "°C");
        sender.sendMessage("§e➜ §7Ressenti : "
                + feeling);
        sender.sendMessage("§e➜ §7Humidité : "
                + humidityText);
        sender.sendMessage("§e➜ §7Atmosphère : "
                + stabilityText);
        sender.sendMessage("§e➜ §7Ciel : "
                + sky);
        sender.sendMessage("§e➜ §7Cycle météo : §f"
                + minutes
                + " min");
        sender.sendMessage("§8-----------------------------");

        return true;
    }
}
