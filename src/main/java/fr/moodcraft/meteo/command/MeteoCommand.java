package fr.moodcraft.meteo.command;

import fr.moodcraft.meteo.climate.AtmosphericPhenomenon;
import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.WeatherManager;
import fr.moodcraft.meteo.climate.WeatherType;
import fr.moodcraft.meteo.gui.ClimateAdminGUI;
import fr.moodcraft.meteo.util.WorldGuard;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class MeteoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String input = label.toLowerCase(Locale.ROOT);

        if (input.equals("meteo") || input.equals("météo") || input.equals("weather")) {
            World world = WorldGuard.mainWorld();
            if (world == null) {
                WorldGuard.sendMissingWorld(sender);
                return true;
            }

            WeatherType weather = ClimateManager.getWeather();
            AtmosphericPhenomenon phenomenon = ClimateManager.getPhenomenon();
            int temperature = (int) Math.round(ClimateManager.getTemperature());

            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Météo MoodCraft ✦ §8-----");
            sender.sendMessage("§e➜ §7Biomes : §f" + formatBiome(sender));
            sender.sendMessage("§e➜ §7Temps : §f" + formatWeather(weather, world));

            if (phenomenon.isVisibleInMeteo()) {
                sender.sendMessage("§e➜ §7Phénomène : §f" + formatPhenomenon(phenomenon));
            }

            sender.sendMessage("§e➜ §7Température : §e" + temperature + "°C");

            if (sender instanceof Player player && !WorldGuard.isInMainWorld(player)) {
                sender.sendMessage("§c✖ §7Position : §cHors monde météo");
            }

            sender.sendMessage("§8-----------------------------");
            return true;
        }

        if (input.equals("meteogui") || input.equals("weatheradmin") || input.equals("climatset")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§c✖ §fCommande joueur uniquement.");
                return true;
            }
            ClimateAdminGUI.open(player);
            return true;
        }

        WeatherType weather;
        switch (input) {
            case "soleil" -> weather = WeatherType.SOLEIL;
            case "pluie" -> weather = WeatherType.PLUIE;
            case "tempete", "orage" -> weather = WeatherType.TEMPETE;
            case "neige" -> weather = WeatherType.NEIGE;
            case "brouillard" -> weather = WeatherType.BROUILLARD;
            case "canicule" -> weather = WeatherType.CANICULE;
            case "blizzard" -> weather = WeatherType.BLIZZARD;
            default -> {
                sender.sendMessage("");
                sender.sendMessage("§8----- §6✦ Météo MoodCraft ✦ §8-----");
                sender.sendMessage("§c✖ §fMétéo inconnue.");
                sender.sendMessage("§e➜ §7Commandes : §e/soleil§7, §e/pluie§7, §e/tempete§7, §eneige");
                sender.sendMessage("§8-----------------------------");
                return true;
            }
        }

        World world = WorldGuard.mainWorld();
        if (world == null) {
            WorldGuard.sendMissingWorld(sender);
            return true;
        }

        ClimateManager.clearPhenomenon();
        ClimateManager.setWeather(weather);
        WeatherManager.applyWeather(true);

        int temperature = (int) Math.round(ClimateManager.getTemperature());

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Météo MoodCraft ✦ §8-----",
                "§e➜ §7Les conditions climatiques évoluent.",
                "§e➜ §7Biomes : §fmétéo dynamique",
                "§e➜ §7Temps : §f" + formatWeather(weather, world),
                "§e➜ §7Température : §e" + temperature + "°C",
                "§8-----------------------------"
        );

        if (!(sender instanceof Player player) || !WorldGuard.isInMainWorld(player)) {
            sender.sendMessage("");
            sender.sendMessage("§8----- §6✦ Météo MoodCraft ✦ §8-----");
            sender.sendMessage("§a✔ §fMétéo appliquée dans les biomes du monde principal§a.");
            sender.sendMessage("§e➜ §7Temps : §f" + formatWeather(weather, world));
            sender.sendMessage("§e➜ §7Température : §e" + temperature + "°C");
            sender.sendMessage("§e➜ §7Nether et End ignorés.");
            sender.sendMessage("§8-----------------------------");
        }

        return true;
    }

    private String formatBiome(CommandSender sender) {
        if (!(sender instanceof Player player)) return "météo dynamique";
        if (!WorldGuard.isInMainWorld(player)) return "hors monde météo";
        Biome biome = player.getLocation().getBlock().getBiome();
        return formatEnumName(biome.name());
    }

    private String formatWeather(WeatherType weather, World world) {
        AtmosphericPhenomenon phenomenon = ClimateManager.getPhenomenon();
        if (phenomenon.isVisibleInMeteo()) return formatPhenomenon(phenomenon);
        if (weather == WeatherType.SOLEIL && isNight(world)) return "☾ §9Nuit claire";
        String icon = weather.getIcon();
        String display = weather.getDisplay();
        if (icon == null || icon.isBlank()) return display;
        return icon + " " + display;
    }

    private String formatPhenomenon(AtmosphericPhenomenon phenomenon) {
        if (phenomenon == null || phenomenon == AtmosphericPhenomenon.NONE) return "§7Aucun phénomène";
        return phenomenon.getIcon() + " " + phenomenon.getDisplay();
    }

    private String formatEnumName(String raw) {
        if (raw == null || raw.isBlank()) return "inconnu";
        String[] parts = raw.toLowerCase(Locale.ROOT).split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) continue;
            if (!builder.isEmpty()) builder.append(' ');
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder.toString();
    }

    private boolean isNight(World world) {
        if (world == null) return false;
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }
}
