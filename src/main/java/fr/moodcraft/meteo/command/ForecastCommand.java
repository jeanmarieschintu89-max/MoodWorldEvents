package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.climate.WeatherType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Random;

public class ForecastCommand implements CommandExecutor {

    private final Random random =
            new Random();

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        Season season =
                ClimateManager.getSeason();

        WeatherType current =
                ClimateManager.getWeather();

        WeatherType next =
                predictNext(
                        season,
                        current
                );

        double stability =
                ClimateManager.getStability();

        String stabilityText;

        if (stability >= 0.8) {
            stabilityText = "§aAtmosphère stable";
        } else if (stability >= 0.5) {
            stabilityText = "§eActivité modérée";
        } else {
            stabilityText = "§cPerturbations importantes";
        }

        int temp =
                (int) Math.round(
                        ClimateManager.getTemperature()
                );

        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ Prévisions Atmosphériques ✦ §8-----");
        sender.sendMessage("§e➜ §7Saison : §f"
                + season.getDisplay());
        sender.sendMessage("§e➜ §7Conditions actuelles : §f"
                + current.getIcon()
                + " "
                + current.getDisplay());
        sender.sendMessage("§e➜ §7Température : §6"
                + temp
                + "°C");
        sender.sendMessage("§e➜ §7Stabilité : "
                + stabilityText);
        sender.sendMessage("§e➜ §7Prochaine tendance probable : §f"
                + next.getIcon()
                + " "
                + next.getDisplay());
        sender.sendMessage("§8-----------------------------");

        return true;
    }

    private WeatherType predictNext(
            Season season,
            WeatherType current
    ) {

        int roll =
                random.nextInt(100);

        switch (season) {

            case ETE -> {
                if (current == WeatherType.CANICULE)
                    return WeatherType.SOLEIL;
                if (roll < 55)
                    return WeatherType.SOLEIL;
                if (roll < 75)
                    return WeatherType.CANICULE;
                if (roll < 90)
                    return WeatherType.PLUIE;
                return WeatherType.TEMPETE;
            }

            case HIVER -> {
                if (roll < 40)
                    return WeatherType.NEIGE;
                if (roll < 65)
                    return WeatherType.BLIZZARD;
                if (roll < 80)
                    return WeatherType.BROUILLARD;
                return WeatherType.SOLEIL;
            }

            case AUTOMNE -> {
                if (roll < 45)
                    return WeatherType.PLUIE;
                if (roll < 65)
                    return WeatherType.BROUILLARD;
                if (roll < 85)
                    return WeatherType.TEMPETE;
                return WeatherType.SOLEIL;
            }

            default -> {
                if (roll < 40)
                    return WeatherType.SOLEIL;
                if (roll < 70)
                    return WeatherType.PLUIE;
                if (roll < 85)
                    return WeatherType.BROUILLARD;
                return WeatherType.TEMPETE;
            }
        }
    }
}
