package fr.moodcraft.meteo;

import fr.moodcraft.meteo.command.AuroreCommand;
import fr.moodcraft.meteo.command.ClimateCommand;
import fr.moodcraft.meteo.command.EclipseCommand;
import fr.moodcraft.meteo.command.ForecastCommand;
import fr.moodcraft.meteo.command.MeteoAdminCommand;
import fr.moodcraft.meteo.command.MeteoCommand;
import fr.moodcraft.meteo.command.SaisonCommand;
import fr.moodcraft.meteo.command.SupercelluleCommand;
import fr.moodcraft.meteo.command.TemperatureCommand;
import fr.moodcraft.meteo.command.TempeteSableCommand;
import fr.moodcraft.meteo.command.TimeCommand;

import fr.moodcraft.meteo.gui.ClimateAdminHandler;

import fr.moodcraft.meteo.task.AtmosphereTask;
import fr.moodcraft.meteo.task.ClimateTask;
import fr.moodcraft.meteo.task.TemperatureTask;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.World;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private boolean sunriseSent = false;
    private boolean sunsetSent = false;
    private boolean midnightSent = false;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        MeteoCommand meteoCommand = new MeteoCommand();

        registerCommand("meteo", meteoCommand);
        registerCommand("meteogui", meteoCommand);
        registerCommand("meteoadmin", new MeteoAdminCommand());

        registerCommand("saison", new SaisonCommand());
        registerCommand("temperature", new TemperatureCommand());
        registerCommand("forecast", new ForecastCommand());
        registerCommand("climat", new ClimateCommand());

        registerCommand("soleil", meteoCommand);
        registerCommand("pluie", meteoCommand);
        registerCommand("tempete", meteoCommand);
        registerCommand("neige", meteoCommand);
        registerCommand("blizzard", meteoCommand);
        registerCommand("brouillard", meteoCommand);
        registerCommand("canicule", meteoCommand);

        TimeCommand timeCommand = new TimeCommand();

        registerCommand("jour", timeCommand);
        registerCommand("matin", timeCommand);
        registerCommand("midi", timeCommand);
        registerCommand("soir", timeCommand);
        registerCommand("nuit", timeCommand);
        registerCommand("minuit", timeCommand);
        registerCommand("aube", timeCommand);
        registerCommand("crepuscule", timeCommand);

        registerCommand("aurore", new AuroreCommand());
        registerCommand("eclipse", new EclipseCommand());
        registerCommand("supercellule", new SupercelluleCommand());
        registerCommand("tempetesable", new TempeteSableCommand());

        Bukkit.getPluginManager().registerEvents(
                new ClimateAdminHandler(),
                this
        );

        World world = WorldGuard.mainWorld();

        if (world != null) {
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        } else {
            getLogger().warning("Monde principal introuvable : world");
        }

        Bukkit.getScheduler().runTaskTimer(
                this,
                new ClimateTask(),
                20L,
                20L * 60 * 4
        );

        Bukkit.getScheduler().runTaskTimer(
                this,
                new TemperatureTask(),
                40L,
                20L * 8
        );

        Bukkit.getScheduler().runTaskTimer(
                this,
                new AtmosphereTask(),
                60L,
                20L * 6
        );

        Bukkit.getScheduler().runTaskTimer(
                this,
                () -> {

                    World mainWorld = WorldGuard.mainWorld();

                    if (mainWorld == null) {
                        return;
                    }

                    long worldTime = mainWorld.getTime();

                    if (worldTime >= 0 && worldTime <= 150 && !sunriseSent) {
                        sunriseSent = true;
                        sunsetSent = false;
                        midnightSent = false;

                        for (Player player : WorldGuard.mainWorldPlayers()) {
                            player.sendActionBar("§6☀ Aube §8• §7Le jour se lève");
                            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 0.12f, 1.5f);
                        }
                    }

                    if (worldTime >= 12300 && worldTime <= 12450 && !sunsetSent) {
                        sunsetSent = true;
                        sunriseSent = false;
                        midnightSent = false;

                        for (Player player : WorldGuard.mainWorldPlayers()) {
                            player.sendActionBar("§6Crépuscule §8• §7Le ciel s'assombrit");
                            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.10f, 0.9f);
                        }
                    }

                    if (worldTime >= 17900 && worldTime <= 18100 && !midnightSent) {
                        midnightSent = true;
                        sunriseSent = false;
                        sunsetSent = false;

                        for (Player player : WorldGuard.mainWorldPlayers()) {
                            player.sendActionBar("§9Minuit §8• §7Le monde s'endort");
                            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 0.06f, 0.7f);
                        }
                    }

                },
                40L,
                20L * 5
        );

        getLogger().info("=================================");
        getLogger().info("☁ MoodCraftMeteo activé");
        getLogger().info("Monde météo : world uniquement");
        getLogger().info("Nether et End ignorés");
        getLogger().info("Climat dynamique chargé");
        getLogger().info("Température immersive active");
        getLogger().info("Météo intelligente active");
        getLogger().info("Atmosphère dynamique active");
        getLogger().info("Cycles jour/nuit synchronisés");
        getLogger().info("Événements atmosphériques actifs");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("☁ MoodCraftMeteo désactivé");
    }

    private void registerCommand(
            String name,
            org.bukkit.command.CommandExecutor executor
    ) {

        if (getCommand(name) != null) {
            getCommand(name).setExecutor(executor);
        }
    }
}
