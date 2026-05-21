package fr.moodcraft.meteo.util;

import org.bukkit.Material;

import org.bukkit.enchantments.Enchantment;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SafeGUI {

    public static ItemStack item(
            Material material,
            String name,
            String... lore
    ) {

        ItemStack item =
                new ItemStack(normalizeMaterial(material, name));

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(name == null ? " " : name);

        if (lore != null && lore.length > 0) {
            meta.setLore(normalizeLore(lore));
        }

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );

        try {
            meta.addItemFlags(ItemFlag.valueOf("HIDE_ITEM_SPECIFICS"));
        } catch (IllegalArgumentException ignored) {
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack glow(ItemStack item) {

        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    private static List<String> normalizeLore(String... lore) {

        List<String> lines = new ArrayList<>();

        for (String line : lore) {
            lines.add(normalizeLine(line));
        }

        return lines;
    }

    private static String normalizeLine(String line) {

        if (line == null || line.isBlank()) {
            return "";
        }

        String trimmed = line.trim().replace("✘", "✖");

        if (trimmed.startsWith("§8•")
                || trimmed.startsWith("§e➜")
                || trimmed.startsWith("§a✔")
                || trimmed.startsWith("§c✖")) {
            return trimmed;
        }

        if (trimmed.startsWith("§eClique")
                || trimmed.startsWith("§eOuvrir")
                || trimmed.startsWith("§eAppliquer")) {
            return "§e➜ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§a")) {
            return "§a✔ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§c")) {
            return "§c✖ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§7") || trimmed.startsWith("§8")) {
            return "§8• §7" + cleanPrefix(trimmed);
        }

        return "§8• §7" + cleanPrefix(trimmed);
    }

    private static Material normalizeMaterial(Material material, String name) {

        if (isReturnButton(name)) {
            return Material.BARRIER;
        }

        return material == null ? Material.BARRIER : material;
    }

    private static boolean isReturnButton(String name) {

        if (name == null) {
            return false;
        }

        String clean = name
                .replaceAll("§.", "")
                .replace("✦", "")
                .trim()
                .toLowerCase();

        return clean.equals("retour")
                || clean.equals("fermer")
                || clean.equals("annuler")
                || clean.equals("revenir");
    }

    private static String cleanPrefix(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replaceFirst("^§[0-9a-fk-or]", "")
                .replaceFirst("^➜\\s*", "")
                .replaceFirst("^✔\\s*", "")
                .replaceFirst("^✘\\s*", "")
                .replaceFirst("^✖\\s*", "")
                .replaceFirst("^•\\s*", "")
                .trim();
    }
}