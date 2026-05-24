package fr.moodcraft.event.manager;

import fr.moodcraft.event.Main;
import fr.moodcraft.event.generator.GeneratedGameManager;
import fr.moodcraft.event.generator.GeneratedGameSize;
import fr.moodcraft.event.generator.GeneratedGameType;
import fr.moodcraft.event.generator.GeneratedWaitingRoomLocator;
import fr.moodcraft.event.util.MoodStyle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class EventAutoManager {

    private static final Random RANDOM = new Random();
    private static BukkitTask task;
    private static long nextRunAt;
    private static boolean closingScheduled;

    private EventAutoManager() {}

    public static void start() {
        ensureDefaults();
        scheduleNext(false);
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), EventAutoManager::tick, 20L * 30L, 20L * 30L);
    }

    public static void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        closingScheduled = false;
    }

    public static boolean isEnabled() {
        return config().getBoolean("event-auto.enabled", true);
    }

    public static void setEnabled(boolean enabled) {
        config().set("event-auto.enabled", enabled);
        save();
        if (enabled) scheduleNext(false);
    }

    public static int getIntervalMinutes() {
        return Math.max(15, config().getInt("event-auto.interval-minutes", 120));
    }

    public static void setIntervalMinutes(int minutes) {
        config().set("event-auto.interval-minutes", Math.max(15, minutes));
        save();
        scheduleNext(false);
    }

    public static int getQueueOpenMinutes() {
        return Math.max(1, config().getInt("event-auto.queue-open-minutes", 5));
    }

    public static void setQueueOpenMinutes(int minutes) {
        config().set("event-auto.queue-open-minutes", Math.max(1, minutes));
        save();
    }

    public static int getMinPlayers() {
        return Math.max(1, config().getInt("event-auto.min-players", 5));
    }

    public static void setMinPlayers(int players) {
        config().set("event-auto.min-players", Math.max(1, players));
        save();
    }

    public static String getStatusLine() {
        Location anchor = getAnchor();
        String anchorText = anchor == null || anchor.getWorld() == null
                ? "§cnon défini"
                : "§a" + anchor.getWorld().getName() + " §7" + anchor.getBlockX() + " " + anchor.getBlockY() + " " + anchor.getBlockZ();
        long seconds = Math.max(0, (nextRunAt - System.currentTimeMillis()) / 1000L);
        long minutes = seconds / 60L;
        return "§fAuto: " + (isEnabled() ? "§aON" : "§cOFF")
                + " §8• §fIntervalle: §e" + getIntervalMinutes() + "min"
                + " §8• §fOuverture: §e" + getQueueOpenMinutes() + "min"
                + " §8• §fMinimum: §e" + getMinPlayers() + " joueurs"
                + " §8• §fProchain: §e" + minutes + "min"
                + " §8• §fAnchor: " + anchorText;
    }

    public static void setAnchor(Player player) {
        Location location = player.getLocation();
        config().set("event-auto.anchor.world", location.getWorld().getName());
        config().set("event-auto.anchor.x", location.getX());
        config().set("event-auto.anchor.y", location.getY());
        config().set("event-auto.anchor.z", location.getZ());
        config().set("event-auto.anchor.yaw", location.getYaw());
        config().set("event-auto.anchor.pitch", location.getPitch());
        save();
    }

    public static void triggerNow(CommandSender sender) {
        attemptLaunch(sender, true);
    }

    private static void tick() {
        if (!isEnabled()) return;
        if (System.currentTimeMillis() < nextRunAt) return;
        attemptLaunch(Bukkit.getConsoleSender(), false);
    }

    private static void attemptLaunch(CommandSender sender, boolean forced) {
        if (!forced && Bukkit.getOnlinePlayers().size() < getMinPlayers()) {
            scheduleRetry(10);
            return;
        }
        if (Bukkit.getOnlinePlayers().size() < getMinPlayers()) {
            send(sender, "§cPas assez de joueurs connectés. Minimum : §e" + getMinPlayers());
            scheduleRetry(10);
            return;
        }
        if (EventManager.isCreated() || EventManager.isRunning() || EventManager.isQueueOpen() || GeneratedGameManager.hasStructure() || WaitingRoomManager.hasRoom()) {
            send(sender, "§cImpossible : un événement ou une structure est déjà actif.");
            scheduleRetry(10);
            return;
        }
        Location anchor = getAnchor();
        if (anchor == null || anchor.getWorld() == null) {
            send(sender, "§cAnchor event-auto non défini. Utilise §e/eventauto anchor§c à l'endroit de génération.");
            scheduleRetry(10);
            return;
        }
        Player actor = findActor();
        if (actor == null) {
            send(sender, "§cAucun joueur connecté pour préparer l'événement.");
            scheduleRetry(10);
            return;
        }
        GeneratedGameType type = randomType();
        GeneratedGameSize size = readSize();
        String waitingSize = config().getString("event-auto.waiting-room-size", "moyenne");
        Location back = actor.getLocation().clone();
        try {
            actor.sendActionBar("§6Event auto §8• §7Préparation en cours...");
            actor.teleport(anchor);
            GeneratedGameManager.generate(actor, type, size);
            Location waitingRoom = GeneratedWaitingRoomLocator.nearActiveGame(actor, waitingSize);
            if (waitingRoom != null) actor.teleport(waitingRoom);
            WaitingRoomManager.build(actor, waitingSize);
            actor.teleport(back);
            actor.playSound(actor.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.2f);
            EventManager.openQueue(actor);
            announceAutoOpen(type);
            scheduleCloseQueue();
            scheduleNext(false);
            send(sender, "§aEvent automatique lancé : §e" + type.getDisplayName());
        } catch (Exception exception) {
            try { actor.teleport(back); } catch (Exception ignored) {}
            Main.getInstance().getLogger().warning("Erreur EventAuto : " + exception.getMessage());
            send(sender, "§cErreur pendant l'event auto : §f" + exception.getMessage());
            scheduleRetry(10);
        }
    }

    private static void scheduleCloseQueue() {
        if (closingScheduled) return;
        closingScheduled = true;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            closingScheduled = false;
            if (!EventManager.isCreated() || EventManager.isRunning() || !EventManager.isQueueOpen()) return;
            Player actor = findActor();
            if (actor == null) return;
            if (EventManager.getQueueSize() <= 0) {
                EventManager.cancelEvent(actor);
                return;
            }
            EventManager.closeQueue(actor);
        }, 20L * 60L * getQueueOpenMinutes());
    }

    private static void announceAutoOpen(GeneratedGameType type) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MoodStyle.send(player, MoodStyle.MODULE,
                    MoodStyle.hype("Événement automatique !"),
                    MoodStyle.detail("Épreuve : §e" + type.getDisplayName()),
                    MoodStyle.detail("File ouverte pendant : §e" + getQueueOpenMinutes() + " minutes"),
                    MoodStyle.detail("Joueurs minimum requis : §e" + getMinPlayers()),
                    MoodStyle.info("Rejoins avec §e/event"));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.35f, 1.15f);
        }
    }

    private static Player findActor() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("moodevent.admin")) return player;
        }
        for (Player player : Bukkit.getOnlinePlayers()) return player;
        return null;
    }

    private static GeneratedGameType randomType() {
        List<GeneratedGameType> types = new ArrayList<>();
        List<String> configured = config().getStringList("event-auto.types");
        if (configured.isEmpty()) {
            types.add(GeneratedGameType.SURVIE_ETAGES);
            types.add(GeneratedGameType.RUEE_OR);
            types.add(GeneratedGameType.WATER_JUMP);
            types.add(GeneratedGameType.MUR_ESCALADE);
            types.add(GeneratedGameType.LABYRINTHE);
            types.add(GeneratedGameType.PRISON_BREAK);
        } else {
            for (String raw : configured) {
                try {
                    types.add(GeneratedGameType.valueOf(raw.trim().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (types.isEmpty()) types.add(GeneratedGameType.RUEE_OR);
        return types.get(RANDOM.nextInt(types.size()));
    }

    private static GeneratedGameSize readSize() {
        String raw = config().getString("event-auto.generated-size", "MOYEN");
        try {
            return GeneratedGameSize.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception exception) {
            return GeneratedGameSize.MOYEN;
        }
    }

    private static Location getAnchor() {
        String worldName = config().getString("event-auto.anchor.world", "");
        if (worldName == null || worldName.isBlank()) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world,
                config().getDouble("event-auto.anchor.x"),
                config().getDouble("event-auto.anchor.y"),
                config().getDouble("event-auto.anchor.z"),
                (float) config().getDouble("event-auto.anchor.yaw"),
                (float) config().getDouble("event-auto.anchor.pitch"));
    }

    private static void scheduleNext(boolean immediate) {
        nextRunAt = System.currentTimeMillis() + (immediate ? 0L : getIntervalMinutes() * 60_000L);
    }

    private static void scheduleRetry(int minutes) {
        nextRunAt = System.currentTimeMillis() + Math.max(1, minutes) * 60_000L;
    }

    private static void ensureDefaults() {
        FileConfiguration config = config();
        config.addDefault("event-auto.enabled", true);
        config.addDefault("event-auto.interval-minutes", 120);
        config.addDefault("event-auto.queue-open-minutes", 5);
        config.addDefault("event-auto.min-players", 5);
        config.addDefault("event-auto.generated-size", "MOYEN");
        config.addDefault("event-auto.waiting-room-size", "moyenne");
        config.addDefault("event-auto.types", List.of("SURVIE_ETAGES", "RUEE_OR", "WATER_JUMP", "MUR_ESCALADE", "LABYRINTHE", "PRISON_BREAK"));
        config.options().copyDefaults(true);
        save();
    }

    private static FileConfiguration config() {
        return Main.getInstance().getConfig();
    }

    private static void save() {
        Main.getInstance().saveConfig();
    }

    private static void send(CommandSender sender, String message) {
        if (sender != null) sender.sendMessage("§6MoodEvent §8» §f" + message);
    }
}
