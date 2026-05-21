package fr.moodcraft.meteo.gui;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.climate.WeatherManager;
import fr.moodcraft.meteo.climate.WeatherType;

import fr.moodcraft.meteo.util.WorldGuard;

import org.bukkit.Sound;
import org.bukkit.World;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

public class ClimateAdminHandler implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent event
    ) {

        if (!event.getView()
                .getTitle()
                .equals(ClimateAdminGUI.TITLE)) {

            return;
        }

        event.setCancelled(
                true
        );

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot =
                event.getRawSlot();

        if (slot < 0
                || slot >= event.getInventory().getSize()) {

            return;
        }

        //
        // CLOSE
        //

        if (slot == 49) {

            player.closeInventory();

            player.playSound(
                    player.getLocation(),
                    Sound.UI_BUTTON_CLICK,
                    0.7f,
                    1.2f
            );

            return;
        }

        //
        // MÉTÉO
        //

        WeatherType weather =
                switch (slot) {

                    case 10 -> WeatherType.SOLEIL;
                    case 11 -> WeatherType.PLUIE;
                    case 12 -> WeatherType.TEMPETE;
                    case 13 -> WeatherType.NEIGE;
                    case 14 -> WeatherType.BLIZZARD;
                    case 15 -> WeatherType.CANICULE;
                    case 16 -> WeatherType.BROUILLARD;

                    default -> null;
                };

        if (weather != null) {

            applyWeather(
                    player,
                    weather
            );

            return;
        }

        //
        // SAISONS
        //

        Season season =
                switch (slot) {

                    case 19 -> Season.PRINTEMPS;
                    case 20 -> Season.ETE;
                    case 21 -> Season.AUTOMNE;
                    case 22 -> Season.HIVER;

                    default -> null;
                };

        if (season != null) {

            applySeason(
                    player,
                    season
            );

            return;
        }

        //
        // TEMPS / HEURE
        //

        Long time =
                switch (slot) {

                    case 28 -> 0L;
                    case 29 -> 6000L;
                    case 30 -> 12000L;
                    case 31 -> 14000L;
                    case 32 -> 18000L;
                    case 33 -> 23000L;

                    default -> null;
                };

        if (time != null) {

            applyTime(
                    player,
                    time,
                    slot
            );

            return;
        }

        //
        // ÉVÉNEMENTS
        //

        switch (slot) {

            case 37 -> runEvent(
                    player,
                    "eclipse"
            );

            case 38 -> runEvent(
                    player,
                    "aurore"
            );

            case 39 -> runEvent(
                    player,
                    "supercellule"
            );

            case 40 -> runEvent(
                    player,
                    "tempetesable"
            );

            default -> {
            }
        }
    }

    //
    // APPLY WEATHER
    //

    private void applyWeather(
            Player player,
            WeatherType weather
    ) {

        if (WorldGuard.mainWorld() == null) {

            WorldGuard.sendMissingWorld(
                    player
            );

            return;
        }

        ClimateManager.setWeather(
                weather
        );

        //
        // IMPORTANT :
        // true = force l'application immédiate.
        // Ça bypass l'inertie de 4 minutes.
        //

        WeatherManager.applyWeather(
                true
        );

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Météo MoodCraft ✦ §8-----",
                "§a✔ Temps changé par l'administration.",
                "§8• §7Nouveau temps : §f"
                        + weather.getIcon()
                        + " "
                        + weather.getDisplay(),
                "§8-----------------------------"
        );

        player.playSound(
                player.getLocation(),
                Sound.UI_BUTTON_CLICK,
                0.8f,
                1.4f
        );

        ClimateAdminGUI.open(
                player
        );
    }

    //
    // APPLY SEASON
    //

    private void applySeason(
            Player player,
            Season season
    ) {

        ClimateManager.setSeason(
                season
        );

        player.sendMessage("");
        player.sendMessage("§8----- §6✦ Météo MoodCraft ✦ §8-----");
        player.sendMessage("§a✔ Saison modifiée.");
        player.sendMessage("§8• §7Nouvelle saison : §f"
                + season.getIcon()
                + " "
                + season.getDisplay());
        player.sendMessage("§8-----------------------------");

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                0.8f,
                1.3f
        );

        ClimateAdminGUI.open(
                player
        );
    }

    //
    // APPLY TIME
    //

    private void applyTime(
            Player player,
            long time,
            int slot
    ) {

        World world =
                WorldGuard.mainWorld();

        if (world == null) {

            WorldGuard.sendMissingWorld(
                    player
            );

            return;
        }

        world.setTime(
                time
        );

        String label =
                switch (slot) {

                    case 28 -> "Matin";
                    case 29 -> "Midi";
                    case 30 -> "Soir";
                    case 31 -> "Nuit";
                    case 32 -> "Minuit";
                    case 33 -> "Aube";

                    default -> "Temps";
                };

        WorldGuard.broadcastMainWorld(
                "",
                "§8----- §6✦ Cycle du Monde ✦ §8-----",
                "§a✔ Heure modifiée par l'administration.",
                "§8• §7Moment : §e" + label,
                "§8• §7Monde : §eworld",
                "§8-----------------------------"
        );

        player.playSound(
                player.getLocation(),
                Sound.UI_BUTTON_CLICK,
                0.8f,
                1.4f
        );

        ClimateAdminGUI.open(
                player
        );
    }

    //
    // RUN EVENT
    //

    private void runEvent(
            Player player,
            String command
    ) {

        player.closeInventory();

        player.playSound(
                player.getLocation(),
                Sound.UI_BUTTON_CLICK,
                0.8f,
                1.2f
        );

        player.performCommand(
                command
        );
    }
}