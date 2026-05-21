package fr.moodcraft.meteo.gui;

import fr.moodcraft.meteo.climate.ClimateManager;
import fr.moodcraft.meteo.climate.Season;
import fr.moodcraft.meteo.climate.WeatherType;

import fr.moodcraft.meteo.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public class ClimateAdminGUI {

    public static final String TITLE =
            "§6✦ §8§lCentre Climatique §6✦";

    public static void open(
            Player player
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        //
        // FOND
        //

        for (int i = 0; i < 54; i++) {

            inv.setItem(
                    i,
                    SafeGUI.item(
                            Material.BLACK_STAINED_GLASS_PANE,
                            " "
                    )
            );
        }

        //
        // STATUS
        //

        Season season =
                ClimateManager.getSeason();

        WeatherType weather =
                ClimateManager.getWeather();

        double temp =
                ClimateManager.getTemperature();

        inv.setItem(
                4,
                SafeGUI.glow(
                        SafeGUI.item(
                                Material.BEACON,
                                "§6✦ §fRéseau climatique §6✦",
                                "§8• §7Monde météo : §eworld",
                                "§8• §7Saison : §f"
                                        + season.getIcon()
                                        + " "
                                        + season.getDisplay(),
                                "§8• §7Temps : §f"
                                        + weather.getIcon()
                                        + " "
                                        + weather.getDisplay(),
                                "§8• §7Température : §e"
                                        + Math.round(temp)
                                        + "°C",
                                "",
                                "§8Centre de contrôle MoodCraft"
                        )
                )
        );

        //
        // MÉTÉO
        //

        inv.setItem(
                10,
                weatherItem(
                        Material.SUNFLOWER,
                        "§6☀ Soleil",
                        "Ciel dégagé",
                        weather == WeatherType.SOLEIL
                )
        );

        inv.setItem(
                11,
                weatherItem(
                        Material.WATER_BUCKET,
                        "§9☔ Pluie",
                        "Pluie calme",
                        weather == WeatherType.PLUIE
                )
        );

        inv.setItem(
                12,
                weatherItem(
                        Material.TRIDENT,
                        "§5⛈ Tempête",
                        "Orage violent",
                        weather == WeatherType.TEMPETE
                )
        );

        inv.setItem(
                13,
                weatherItem(
                        Material.SNOWBALL,
                        "§f❄ Neige",
                        "Temps hivernal",
                        weather == WeatherType.NEIGE
                )
        );

        inv.setItem(
                14,
                weatherItem(
                        Material.POWDER_SNOW_BUCKET,
                        "§b❄ Blizzard",
                        "Froid extrême",
                        weather == WeatherType.BLIZZARD
                )
        );

        inv.setItem(
                15,
                weatherItem(
                        Material.BLAZE_POWDER,
                        "§c☀ Canicule",
                        "Chaleur extrême",
                        weather == WeatherType.CANICULE
                )
        );

        inv.setItem(
                16,
                weatherItem(
                        Material.COBWEB,
                        "§7☁ Brouillard",
                        "Brume épaisse",
                        weather == WeatherType.BROUILLARD
                )
        );

        //
        // SAISONS
        //

        inv.setItem(
                19,
                seasonItem(
                        Material.PINK_TULIP,
                        "§dPrintemps",
                        "Climat doux",
                        season == Season.PRINTEMPS
                )
        );

        inv.setItem(
                20,
                seasonItem(
                        Material.SUNFLOWER,
                        "§6Été",
                        "Climat chaud",
                        season == Season.ETE
                )
        );

        inv.setItem(
                21,
                seasonItem(
                        Material.ORANGE_TULIP,
                        "§6Automne",
                        "Climat humide",
                        season == Season.AUTOMNE
                )
        );

        inv.setItem(
                22,
                seasonItem(
                        Material.SNOW_BLOCK,
                        "§bHiver",
                        "Climat froid",
                        season == Season.HIVER
                )
        );

        //
        // TEMPS / HEURE
        //

        inv.setItem(
                28,
                timeItem(
                        Material.CLOCK,
                        "§eMatin",
                        "Place le monde à l'aube"
                )
        );

        inv.setItem(
                29,
                timeItem(
                        Material.SUNFLOWER,
                        "§6Midi",
                        "Place le monde en plein jour"
                )
        );

        inv.setItem(
                30,
                timeItem(
                        Material.ORANGE_TULIP,
                        "§6Soir",
                        "Place le monde au crépuscule"
                )
        );

        inv.setItem(
                31,
                timeItem(
                        Material.BLACK_DYE,
                        "§9Nuit",
                        "Place le monde de nuit"
                )
        );

        inv.setItem(
                32,
                timeItem(
                        Material.ENDER_PEARL,
                        "§1Minuit",
                        "Place le monde à minuit"
                )
        );

        inv.setItem(
                33,
                timeItem(
                        Material.GLOWSTONE_DUST,
                        "§eAube",
                        "Place le monde avant le lever du soleil"
                )
        );

        //
        // ÉVÉNEMENTS
        //

        inv.setItem(
                37,
                eventItem(
                        Material.ENDER_EYE,
                        "§5Éclipse",
                        "Assombrit le ciel"
                )
        );

        inv.setItem(
                38,
                eventItem(
                        Material.AMETHYST_SHARD,
                        "§dAurore",
                        "Lumières célestes"
                )
        );

        inv.setItem(
                39,
                eventItem(
                        Material.LIGHTNING_ROD,
                        "§5Supercellule",
                        "Orage extrême"
                )
        );

        inv.setItem(
                40,
                eventItem(
                        Material.SAND,
                        "§6Tempête de sable",
                        "Déserts et badlands"
                )
        );

        //
        // CLOSE
        //

        inv.setItem(
                49,
                SafeGUI.item(
                        Material.BARRIER,
                        "§c✖ Fermer",
                        "§8• §7Fermer ce menu"
                )
        );

        player.openInventory(
                inv
        );
    }

    //
    // ITEMS
    //

    private static org.bukkit.inventory.ItemStack weatherItem(
            Material material,
            String name,
            String description,
            boolean active
    ) {

        org.bukkit.inventory.ItemStack item =
                SafeGUI.item(
                        material,
                        active
                                ? "§a✔ " + name
                                : "§6✦ §f" + name + " §6✦",
                        "§8• §7" + description,
                        "§8• §7Action : §eappliquer maintenant",
                        "",
                        active
                                ? "§aTemps actuellement actif"
                                : "§eClique pour changer le temps"
                );

        return active
                ? SafeGUI.glow(item)
                : item;
    }

    private static org.bukkit.inventory.ItemStack seasonItem(
            Material material,
            String name,
            String description,
            boolean active
    ) {

        org.bukkit.inventory.ItemStack item =
                SafeGUI.item(
                        material,
                        active
                                ? "§a✔ " + name
                                : "§6✦ §f" + name + " §6✦",
                        "§8• §7" + description,
                        "§8• §7Action : §echanger la saison",
                        "",
                        active
                                ? "§aSaison actuellement active"
                                : "§eClique pour appliquer"
                );

        return active
                ? SafeGUI.glow(item)
                : item;
    }

    private static org.bukkit.inventory.ItemStack timeItem(
            Material material,
            String name,
            String description
    ) {

        return SafeGUI.item(
                material,
                "§6✦ §f" + name + " §6✦",
                "§8• §7" + description,
                "§8• §7Monde : §eworld",
                "",
                "§eClique pour appliquer"
        );
    }

    private static org.bukkit.inventory.ItemStack eventItem(
            Material material,
            String name,
            String description
    ) {

        return SafeGUI.item(
                material,
                "§6✦ §f" + name + " §6✦",
                "§8• §7" + description,
                "§8• §7Monde : §eworld",
                "",
                "§eClique pour déclencher"
        );
    }
}
