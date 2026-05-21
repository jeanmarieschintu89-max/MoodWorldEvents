package fr.moodcraft.meteo.task;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.climate.WeatherManager;
import fr.moodcraft.meteo.climate.WeatherType;

import java.util.Random;

public class ClimateTask implements Runnable {

    private final Random random = new Random();

    @Override
    public void run() {
        if (ClimateManager.phenomenonBlocksNaturalWeather()) {
            WeatherManager.applyWeather();
            return;
        }

        WeatherType current = ClimateManager.getWeather();
        Season season = ClimateManager.getSeason();
        WeatherType next = current;

        int roll = random.nextInt(100);
        double temperature = ClimateManager.getTemperature();
        double humidity = ClimateManager.getHumidity();

        if (roll <= 88) {
            WeatherManager.applyWeather();
            return;
        }

        switch (current) {
            case SOLEIL -> {
                if (temperature >= 32 && humidity <= 0.35 && season == Season.ETE) {
                    next = WeatherType.CANICULE;
                } else if (humidity >= 0.78) {
                    next = WeatherType.PLUIE;
                } else if (season == Season.AUTOMNE && humidity >= 0.60) {
                    next = WeatherType.BROUILLARD;
                } else if (humidity <= 0.62) {
                    next = WeatherType.SOLEIL;
                } else {
                    next = roll < 94 ? WeatherType.SOLEIL : WeatherType.BROUILLARD;
                }
            }
            case PLUIE -> {
                if (temperature <= 1) {
                    next = WeatherType.NEIGE;
                } else if (humidity >= 0.86 && roll < 94) {
                    next = WeatherType.TEMPETE;
                } else if (humidity >= 0.72 && roll < 90) {
                    next = WeatherType.PLUIE;
                } else {
                    next = WeatherType.SOLEIL;
                }
            }
            case TEMPETE -> {
                if (temperature <= 0) next = WeatherType.BLIZZARD;
                else if (roll < 65) next = WeatherType.PLUIE;
                else next = WeatherType.SOLEIL;
            }
            case BROUILLARD -> {
                if (humidity >= 0.78 && roll > 82) next = WeatherType.PLUIE;
                else next = WeatherType.SOLEIL;
            }
            case CANICULE -> {
                if (season != Season.ETE || temperature < 26) next = WeatherType.SOLEIL;
                else next = roll < 92 ? WeatherType.CANICULE : WeatherType.SOLEIL;
            }
            case NEIGE -> {
                if (temperature > 3) next = WeatherType.SOLEIL;
                else if (roll < 84) next = WeatherType.NEIGE;
                else next = WeatherType.BLIZZARD;
            }
            case BLIZZARD -> {
                if (temperature > 2) next = WeatherType.SOLEIL;
                else if (roll < 75) next = WeatherType.NEIGE;
                else next = WeatherType.BROUILLARD;
            }
        }

        if (season == Season.HIVER || temperature <= 1) {
            if (next == WeatherType.PLUIE) next = WeatherType.NEIGE;
            if (next == WeatherType.TEMPETE) next = WeatherType.BLIZZARD;
        }

        if (season == Season.ETE && temperature > 12) {
            if (next == WeatherType.NEIGE || next == WeatherType.BLIZZARD) next = WeatherType.SOLEIL;
        }

        if (temperature >= 34 && humidity <= 0.30) next = WeatherType.CANICULE;

        ClimateManager.setWeather(next);
        WeatherManager.applyWeather();
    }
}
