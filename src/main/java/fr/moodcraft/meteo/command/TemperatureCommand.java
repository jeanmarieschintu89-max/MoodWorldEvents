package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.WeatherType;

import org.bukkit.World;
import org.bukkit.block.Biome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class TemperatureCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {

            sender.sendMessage(
                    "§c✖ §fCommande joueur uniquement."
            );

            return true;
        }

        double temp =
                ClimateManager.getTemperature();

        WeatherType weather =
                ClimateManager.getWeather();

        Biome biome =
                p.getLocation()
                        .getBlock()
                        .getBiome();

        int y =
                p.getLocation()
                        .getBlockY();

        World world =
                p.getWorld();

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

        double humidity =
                ClimateManager.getHumidity();

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

        String air;

        if (weather == WeatherType.TEMPETE) {
            air = "§8Atmosphère électrique";
        } else if (weather == WeatherType.BROUILLARD) {
            air = "§7Visibilité réduite";
        } else if (weather == WeatherType.CANICULE) {
            air = "§6Air brûlant";
        } else if (weather == WeatherType.BLIZZARD) {
            air = "§bVent glacial";
        } else {
            air = "§aAir stable";
        }

        long time =
                world.getTime();

        String cycle;

        if (time >= 23000 || time <= 1000) {
            cycle = "§eAube";
        } else if (time < 6000) {
            cycle = "§6Matin";
        } else if (time < 12000) {
            cycle = "§eJour";
        } else if (time < 14000) {
            cycle = "§6Crépuscule";
        } else {
            cycle = "§9Nuit";
        }

        p.sendMessage("");
        p.sendMessage("§8----- §6✦ Analyse Thermique ✦ §8-----");
        p.sendMessage("§e➜ §7Température : §6"
                + Math.round(temp)
                + "°C");
        p.sendMessage("§e➜ §7Ressenti : "
                + feeling);
        p.sendMessage("§e➜ §7Météo : "
                + weather.getIcon()
                + " "
                + weather.getDisplay());
        p.sendMessage("§e➜ §7Humidité : "
                + humidityText);
        p.sendMessage("§e➜ §7Atmosphère : "
                + air);
        p.sendMessage("§e➜ §7Cycle : "
                + cycle);
        p.sendMessage("§e➜ §7Biome : §f"
                + biome.name());
        p.sendMessage("§e➜ §7Altitude : §fY "
                + y);
        p.sendMessage("§8-----------------------------");

        return true;
    }
}
