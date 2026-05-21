package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.Main;
import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.climate.WeatherManager;
import fr.moodcraft.meteo.climate.WeatherType;
import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class MeteoAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("moodcraft.meteo.admin")) {
            error(sender, "Accès réservé à l'administration météo.");
            return true;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "help", "aide" -> help(sender);
            case "status", "etat", "état" -> status(sender);
            case "reload" -> reload(sender);
            case "meteo", "weather" -> setWeather(sender, args);
            case "saison", "season" -> setSeason(sender, args);
            case "heure", "time" -> setTime(sender, args);
            case "temp", "temperature" -> setTemperature(sender, args);
            case "humidity", "humidite", "humidité" -> setHumidity(sender, args);
            case "stability", "stabilite", "stabilité" -> setStability(sender, args);
            case "resetworld" -> resetWorld(sender);
            default -> help(sender);
        }

        return true;
    }

    private void status(CommandSender sender) {
        World world = WorldGuard.mainWorld();

        header(sender, "Admin Météo");
        sender.sendMessage("§e➜ §7Monde météo : §e" + WorldGuard.MAIN_WORLD_NAME);
        sender.sendMessage("§e➜ §7Monde chargé : " + (world == null ? "§cnon" : "§aoui"));
        sender.sendMessage("§e➜ §7Saison : §f" + ClimateManager.getSeason().getIcon() + " " + ClimateManager.getSeason().getDisplay());
        sender.sendMessage("§e➜ §7Météo : §f" + ClimateManager.getWeather().getIcon() + " " + ClimateManager.getWeather().getDisplay());
        sender.sendMessage("§e➜ §7Température : §e" + Math.round(ClimateManager.getTemperature()) + "°C");
        sender.sendMessage("§e➜ §7Humidité : §e" + Math.round(ClimateManager.getHumidity() * 100) + "%");
        sender.sendMessage("§e➜ §7Stabilité : §e" + Math.round(ClimateManager.getStability() * 100) + "%");
        if (world != null) {
            sender.sendMessage("§e➜ §7Heure world : §e" + world.getTime());
        }
        footer(sender);
    }

    private void reload(CommandSender sender) {
        Main.getInstance().reloadConfig();
        success(sender, "Configuration météo rechargée.");
    }

    private void setWeather(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/meteoadmin meteo <soleil|pluie|tempete|neige|blizzard|brouillard|canicule>");
            return;
        }

        WeatherType type = parseWeather(args[1]);
        if (type == null) {
            error(sender, "Météo inconnue.");
            return;
        }

        ClimateManager.setWeather(type);
        WeatherManager.applyWeather(true);
        success(sender, "Météo appliquée : §e" + type.getDisplay());
    }

    private void setSeason(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/meteoadmin saison <printemps|ete|automne|hiver>");
            return;
        }

        Season season = parseSeason(args[1]);
        if (season == null) {
            error(sender, "Saison inconnue.");
            return;
        }

        ClimateManager.setSeason(season);
        success(sender, "Saison appliquée : §e" + season.getDisplay());
    }

    private void setTime(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/meteoadmin heure <0-24000|jour|midi|nuit|minuit>");
            return;
        }

        World world = WorldGuard.mainWorld();
        if (world == null) {
            WorldGuard.sendMissingWorld(sender);
            return;
        }

        Long time = parseTime(args[1]);
        if (time == null) {
            error(sender, "Heure invalide.");
            return;
        }

        world.setTime(time);
        success(sender, "Heure appliquée dans world : §e" + time);
    }

    private void setTemperature(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/meteoadmin temp <valeur>");
            return;
        }

        try {
            double value = Double.parseDouble(args[1].replace(",", "."));
            ClimateManager.setTemperature(value);
            success(sender, "Température forcée : §e" + Math.round(value) + "°C");
        } catch (Exception e) {
            error(sender, "Température invalide.");
        }
    }

    private void setHumidity(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/meteoadmin humidity <0-100>");
            return;
        }

        try {
            double value = Double.parseDouble(args[1].replace(",", "."));
            if (value > 1) {
                value = value / 100.0;
            }
            ClimateManager.setHumidity(value);
            success(sender, "Humidité forcée : §e" + Math.round(ClimateManager.getHumidity() * 100) + "%");
        } catch (Exception e) {
            error(sender, "Humidité invalide.");
        }
    }

    private void setStability(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/meteoadmin stability <0-100>");
            return;
        }

        try {
            double value = Double.parseDouble(args[1].replace(",", "."));
            if (value > 1) {
                value = value / 100.0;
            }
            ClimateManager.setStability(value);
            success(sender, "Stabilité forcée : §e" + Math.round(ClimateManager.getStability() * 100) + "%");
        } catch (Exception e) {
            error(sender, "Stabilité invalide.");
        }
    }

    private void resetWorld(CommandSender sender) {
        World world = WorldGuard.mainWorld();
        if (world == null) {
            WorldGuard.sendMissingWorld(sender);
            return;
        }

        world.setStorm(false);
        world.setThundering(false);
        world.setTime(1000L);
        ClimateManager.setWeather(WeatherType.SOLEIL);
        WeatherManager.applyWeather(true);
        success(sender, "World remis en météo claire.");
    }

    private WeatherType parseWeather(String input) {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "soleil", "clear", "sun" -> WeatherType.SOLEIL;
            case "pluie", "rain" -> WeatherType.PLUIE;
            case "tempete", "tempête", "storm", "orage" -> WeatherType.TEMPETE;
            case "neige", "snow" -> WeatherType.NEIGE;
            case "blizzard" -> WeatherType.BLIZZARD;
            case "brouillard", "fog" -> WeatherType.BROUILLARD;
            case "canicule", "heatwave" -> WeatherType.CANICULE;
            default -> null;
        };
    }

    private Season parseSeason(String input) {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "printemps" -> Season.PRINTEMPS;
            case "ete", "été" -> Season.ETE;
            case "automne" -> Season.AUTOMNE;
            case "hiver" -> Season.HIVER;
            default -> null;
        };
    }

    private Long parseTime(String input) {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "jour", "day" -> 1000L;
            case "matin", "morning" -> 0L;
            case "midi", "noon" -> 6000L;
            case "soir", "evening" -> 12000L;
            case "nuit", "night" -> 14000L;
            case "minuit", "midnight" -> 18000L;
            case "aube", "sunrise" -> 23000L;
            default -> parseLong(input);
        };
    }

    private Long parseLong(String input) {
        try {
            long value = Long.parseLong(input);
            if (value < 0 || value > 24000) {
                return null;
            }
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    private void help(CommandSender sender) {
        header(sender, "Admin Météo");
        sender.sendMessage("§e➜ §7/meteoadmin status");
        sender.sendMessage("§e➜ §7/meteoadmin reload");
        sender.sendMessage("§e➜ §7/meteoadmin meteo <type>");
        sender.sendMessage("§e➜ §7/meteoadmin saison <saison>");
        sender.sendMessage("§e➜ §7/meteoadmin heure <moment|tick>");
        sender.sendMessage("§e➜ §7/meteoadmin temp <valeur>");
        sender.sendMessage("§e➜ §7/meteoadmin humidity <0-100>");
        sender.sendMessage("§e➜ §7/meteoadmin stability <0-100>");
        sender.sendMessage("§e➜ §7/meteoadmin resetworld");
        footer(sender);
    }

    private void usage(CommandSender sender, String usage) {
        header(sender, "Admin Météo");
        sender.sendMessage("§c✖ §fCommande incomplète.");
        sender.sendMessage("§e➜ §7Utilisation : §e" + usage);
        footer(sender);
    }

    private void success(CommandSender sender, String message) {
        header(sender, "Admin Météo");
        sender.sendMessage("§a✔ §f" + message);
        footer(sender);
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Admin Météo");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
    }
}
