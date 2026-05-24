package fr.moodcraft.event;

import fr.moodcraft.event.command.EventAdminCommand;
import fr.moodcraft.event.command.EventAutoCommand;
import fr.moodcraft.event.command.EventCommand;
import fr.moodcraft.event.generator.GeneratedGameManager;
import fr.moodcraft.event.hook.VaultHook;
import fr.moodcraft.event.listener.EventAdminGUIListener;
import fr.moodcraft.event.listener.EventChatListener;
import fr.moodcraft.event.listener.EventDeathGuard;
import fr.moodcraft.event.listener.EventLootListener;
import fr.moodcraft.event.listener.EventMobGuard;
import fr.moodcraft.event.listener.EventProgressListener;
import fr.moodcraft.event.listener.EventProtectionListener;
import fr.moodcraft.event.listener.GeneratorInputManager;
import fr.moodcraft.event.listener.GoldRushInventoryGuard;
import fr.moodcraft.event.listener.GoldRushPressureReminder;
import fr.moodcraft.event.listener.GoldRushStopGuard;
import fr.moodcraft.event.listener.GoldRushTask;
import fr.moodcraft.event.listener.PrisonEscapeListener;
import fr.moodcraft.event.listener.SurvivalFloorTask;
import fr.moodcraft.event.loot.EventLootManager;
import fr.moodcraft.event.manager.EventAutoManager;
import fr.moodcraft.event.manager.EventAutoStartTask;
import fr.moodcraft.event.manager.EventHypeAnnouncer;
import fr.moodcraft.event.manager.EventLaunchBufferManager;
import fr.moodcraft.event.manager.EventLogManager;
import fr.moodcraft.event.manager.EventManager;
import fr.moodcraft.event.manager.EventReturnSafety;
import fr.moodcraft.event.manager.EventSecurityManager;
import fr.moodcraft.event.manager.RewardManager;
import fr.moodcraft.event.manager.SurvivalFloorLagGuard;
import fr.moodcraft.event.manager.WaitingRoomManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main {
    private static JavaPlugin instance;
    private Main() {}
    public static JavaPlugin getInstance() { return instance; }

    public static void enable(JavaPlugin plugin) {
        instance = plugin;
        VaultHook.setup();
        EventManager.load();
        WaitingRoomManager.load();
        RewardManager.load();
        GeneratedGameManager.load();
        EventLootManager.load();
        EventLogManager.load();
        EventSecurityManager.load();
        EventReturnSafety.start();
        EventAutoStartTask.start();
        EventAutoManager.start();
        EventHypeAnnouncer.start();
        EventLaunchBufferManager.start();
        SurvivalFloorLagGuard.start();

        EventCommand eventCommand = new EventCommand();
        EventAdminCommand adminCommand = new EventAdminCommand();
        EventAutoCommand autoCommand = new EventAutoCommand();
        registerCommand("event", eventCommand);
        registerCommand("eventadmin", adminCommand);
        registerCommand("eventmenu", adminCommand);
        registerCommand("eventcreer", adminCommand);
        registerCommand("eventdescription", adminCommand);
        registerCommand("eventtype", adminCommand);
        registerCommand("eventdepart", adminCommand);
        registerCommand("eventarrivee", adminCommand);
        registerCommand("eventsalleattente", adminCommand);
        registerCommand("eventrestaurersalle", adminCommand);
        registerCommand("eventtpsalle", adminCommand);
        registerCommand("eventfinirjoueur", adminCommand);
        registerCommand("eventouvrir", adminCommand);
        registerCommand("eventfermer", adminCommand);
        registerCommand("eventlancer", adminCommand);
        registerCommand("eventstop", adminCommand);
        registerCommand("eventannuler", adminCommand);
        registerCommand("eventauto", autoCommand);

        Bukkit.getPluginManager().registerEvents(new EventAdminGUIListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EventChatListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GeneratorInputManager(), plugin);
        Bukkit.getPluginManager().registerEvents(new EventLootListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EventMobGuard(), plugin);
        Bukkit.getPluginManager().registerEvents(new EventProtectionListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EventProgressListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PrisonEscapeListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new EventDeathGuard(), plugin);
        Bukkit.getPluginManager().registerEvents(new SurvivalFloorTask(), plugin);
        Bukkit.getPluginManager().registerEvents(new GoldRushTask(), plugin);
        Bukkit.getPluginManager().registerEvents(new GoldRushPressureReminder(), plugin);
        Bukkit.getPluginManager().registerEvents(new GoldRushStopGuard(), plugin);
        Bukkit.getPluginManager().registerEvents(new GoldRushInventoryGuard(), plugin);
        plugin.getLogger().info("MoodEvent intégré à MoodWorldEvents.");
    }

    public static void disable() {
        EventAutoManager.stop();
        EventHypeAnnouncer.stop();
        EventManager.save();
        WaitingRoomManager.save();
        RewardManager.save();
        GeneratedGameManager.save();
        EventLootManager.save();
        EventLogManager.save();
        EventSecurityManager.save();
    }

    private static void registerCommand(String name, CommandExecutor executor) {
        if (instance.getCommand(name) != null) instance.getCommand(name).setExecutor(executor);
    }
}
