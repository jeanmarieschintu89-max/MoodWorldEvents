#!/usr/bin/env bash
set -euo pipefail

ROOT="$(pwd)"
METEO="$ROOT/MoodCraftMeteo-main"
EVENT="$ROOT/MoodEvent-main"

[ -d "$METEO/src/main/java" ] || { echo "Dossier manquant: MoodCraftMeteo-main/src/main/java"; exit 1; }
[ -d "$EVENT/src/main/java" ] || { echo "Dossier manquant: MoodEvent-main/src/main/java"; exit 1; }

echo "== MoodWorldEvents : fusion propre =="
mkdir -p src/main/java src/main/resources

cp -R "$METEO/src/main/java/"* src/main/java/
cp -R "$EVENT/src/main/java/"* src/main/java/

python3 <<'PY'
from pathlib import Path

# Convertit l'ancien plugin MoodEvent en module interne appelé par MoodWorldEvents.
event_main = Path('src/main/java/fr/moodcraft/event/Main.java')
event_main.write_text('''package fr.moodcraft.event;

import fr.moodcraft.event.command.EventAdminCommand;
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
        EventHypeAnnouncer.start();
        EventLaunchBufferManager.start();
        SurvivalFloorLagGuard.start();

        EventCommand eventCommand = new EventCommand();
        EventAdminCommand adminCommand = new EventAdminCommand();
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
''')

# Appelle le module Event depuis le Main météo.
meteo_main = Path('src/main/java/fr/moodcraft/meteo/Main.java')
text = meteo_main.read_text()
if 'fr.moodcraft.event.Main.enable(this);' not in text:
    text = text.replace('        saveDefaultConfig();\n', '        saveDefaultConfig();\n\n        fr.moodcraft.event.Main.enable(this);\n')
if 'fr.moodcraft.event.Main.disable();' not in text:
    text = text.replace('    public void onDisable() {\n', '    public void onDisable() {\n        fr.moodcraft.event.Main.disable();\n')
text = text.replace('☁ MoodCraftMeteo activé', '☁ MoodWorldEvents activé')
text = text.replace('☁ MoodCraftMeteo désactivé', '☁ MoodWorldEvents désactivé')
meteo_main.write_text(text)
PY

# Ressources utiles hors plugin.yml/config.yml.
find "$METEO/src/main/resources" -type f ! -name "plugin.yml" ! -name "config.yml" -print0 2>/dev/null | while IFS= read -r -d '' file; do
  rel="${file#$METEO/src/main/resources/}"
  mkdir -p "src/main/resources/$(dirname "$rel")"
  cp "$file" "src/main/resources/$rel"
done

find "$EVENT/src/main/resources" -type f ! -name "plugin.yml" ! -name "config.yml" -print0 2>/dev/null | while IFS= read -r -d '' file; do
  rel="${file#$EVENT/src/main/resources/}"
  mkdir -p "src/main/resources/$(dirname "$rel")"
  cp "$file" "src/main/resources/$rel"
done

cat > src/main/resources/config.yml <<'EOF'
# MoodWorldEvents Config
# Fusion de MoodCraftMeteo et MoodEvent.

prefix: "§6[§aMood§6Craft§6] §f"

temperature:
  enabled: true

weather:
  enabled: true

event:
  name: ""
  description: ""
  type: "AUTRE"
  queue-open: false
  location:
    world: ""
    x: 0.0
    y: 0.0
    z: 0.0
    yaw: 0.0
    pitch: 0.0
EOF

cat > src/main/resources/plugin.yml <<'EOF'
name: MoodWorldEvents
version: 1.0
main: fr.moodcraft.meteo.Main
api-version: '1.21'
author: MoodCraft
description: Météo, climat et événements immersifs MoodCraft
softdepend: [Vault]

commands:
  meteo:
    description: Consulte la météo actuelle
    aliases: [météo, weather]
    permission: moodcraft.meteo.use
  meteogui:
    description: Interface admin météo
    permission: moodcraft.meteo.admin
    aliases: [climatset, weatheradmin]
  meteoadmin:
    description: Administration complète MoodWorldEvents
    usage: /meteoadmin
    permission: moodcraft.meteo.admin
    aliases: [madminmeteo, meteoctl]
  soleil:
    description: Active le soleil
    permission: moodcraft.meteo.admin
    aliases: [clear]
  pluie:
    description: Active la pluie
    permission: moodcraft.meteo.admin
    aliases: [rain]
  tempete:
    description: Active une tempête
    permission: moodcraft.meteo.admin
    aliases: [orage, storm, thunder]
  neige:
    description: Active la neige
    permission: moodcraft.meteo.admin
    aliases: [snow]
  blizzard:
    description: Active un blizzard
    permission: moodcraft.meteo.admin
  brouillard:
    description: Active le brouillard
    permission: moodcraft.meteo.admin
    aliases: [fog]
  canicule:
    description: Active une canicule
    permission: moodcraft.meteo.admin
    aliases: [heatwave]
  jour:
    permission: moodcraft.meteo.admin
    aliases: [day]
  matin:
    permission: moodcraft.meteo.admin
    aliases: [morning]
  midi:
    permission: moodcraft.meteo.admin
    aliases: [noon]
  soir:
    permission: moodcraft.meteo.admin
    aliases: [evening]
  nuit:
    permission: moodcraft.meteo.admin
    aliases: [night]
  minuit:
    permission: moodcraft.meteo.admin
    aliases: [midnight]
  aube:
    permission: moodcraft.meteo.admin
    aliases: [sunrise]
  crepuscule:
    permission: moodcraft.meteo.admin
    aliases: [sunset, dusk]
  saison:
    description: Gestion des saisons
    permission: moodcraft.meteo.admin
    aliases: [season]
  climat:
    description: Informations climatiques
    permission: moodcraft.meteo.admin
    aliases: [climate]
  temperature:
    description: Voir la température
    aliases: [temp, thermo]
  forecast:
    description: Prévisions météo
    aliases: [prevision, meteoavenir]
  aurore:
    description: Déclenche une aurore boréale
    permission: moodcraft.meteo.admin
    aliases: [aurora]
  eclipse:
    description: Déclenche une éclipse
    permission: moodcraft.meteo.admin
    aliases: [lunar]
  supercellule:
    description: Déclenche une supercellule
    permission: moodcraft.meteo.admin
    aliases: [superstorm]
  tempetesable:
    description: Déclenche une tempête de sable
    permission: moodcraft.meteo.admin
    aliases: [sandstorm]
  event:
    description: Rejoindre ou consulter l'événement en cours.
    usage: /event
    aliases: [évent, evt, events]
  eventadmin:
    description: Administration des événements MoodCraft.
    usage: /eventadmin
    permission: moodevent.admin
    permission-message: "§c✖ §fAccès réservé à l'administration événementielle."
    aliases: [eventaide, eventhelp, eventsadmin]
  eventmenu:
    description: Ouvre le centre événementiel admin.
    usage: /eventmenu
    permission: moodevent.admin
    permission-message: "§c✖ §fAccès réservé à l'administration événementielle."
    aliases: [eventgui, éventgui, eventpanel]
  eventcreer:
    description: Créer un événement.
    usage: /eventcreer <nom>
    permission: moodevent.admin
    aliases: [eventcréer, eventcreate, eventnew]
  eventdescription:
    description: Définir la description de l'événement.
    usage: /eventdescription <description>
    permission: moodevent.admin
    aliases: [eventdesc]
  eventtype:
    description: Définir le type de mini-jeu.
    usage: /eventtype <course|jump|labyrinthe|pvp|quiz|build>
    permission: moodevent.admin
    aliases: [eventmode, eventjeu]
  eventdepart:
    description: Définir le point de départ de l'événement.
    usage: /eventdepart
    permission: moodevent.admin
    aliases: [eventdépart, eventset, eventspawn, eventpos]
  eventarrivee:
    description: Définir le point d'arrivée de l'événement.
    usage: /eventarrivee
    permission: moodevent.admin
    aliases: [eventarrivée, eventsetfinish, eventfinishset]
  eventsalleattente:
    description: Générer la salle d'attente restaurable.
    usage: /eventsalleattente <mini|petite|moyenne|grande|tresgrande|festival>
    permission: moodevent.admin
    aliases: [eventsalle, eventattente, eventbuildwaiting, eventgenerersalle, eventgénérersalle]
  eventrestaurersalle:
    description: Restaurer la zone et supprimer la salle d'attente.
    usage: /eventrestaurersalle
    permission: moodevent.admin
    aliases: [eventrestaurerattente, eventrestorewaiting, eventclearwaiting]
  eventtpsalle:
    description: Se téléporter dans la salle d'attente.
    usage: /eventtpsalle
    permission: moodevent.admin
    aliases: [eventtpattente, eventwaitingtp]
  eventfinirjoueur:
    description: Valider manuellement l'arrivée d'un joueur.
    usage: /eventfinirjoueur <joueur>
    permission: moodevent.admin
    aliases: [eventfinishplayer]
  eventouvrir:
    description: Ouvrir la file d'attente de l'événement.
    usage: /eventouvrir
    permission: moodevent.admin
    aliases: [eventopen]
  eventfermer:
    description: Fermer la file d'attente et envoyer les joueurs en salle d'attente.
    usage: /eventfermer
    permission: moodevent.admin
    aliases: [eventclose]
  eventlancer:
    description: Lancer l'événement et téléporter la file d'attente.
    usage: /eventlancer
    permission: moodevent.admin
    aliases: [eventgo, eventstart]
  eventstop:
    description: Terminer l'événement, distribuer les récompenses et renvoyer les joueurs.
    usage: /eventstop
    permission: moodevent.admin
    aliases: [eventterminer, eventend, eventfinish]
  eventannuler:
    description: Annuler l'événement sans récompense.
    usage: /eventannuler
    permission: moodevent.admin
    aliases: [eventcancel]

permissions:
  moodcraft.meteo.use:
    description: Consulter la météo
    default: true
  moodcraft.meteo.admin:
    description: Accès administration météo
    default: op
  moodevent.admin:
    description: Administration des événements MoodCraft
    default: op
EOF

cat > pom.xml <<'EOF'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>fr.moodcraft</groupId>
    <artifactId>MoodWorldEvents</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>
    <name>MoodWorldEvents</name>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <repositories>
        <repository>
            <id>papermc</id>
            <url>https://repo.papermc.io/repository/maven-public/</url>
        </repository>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>io.papermc.paper</groupId>
            <artifactId>paper-api</artifactId>
            <version>1.21.1-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.github.MilkBowl</groupId>
            <artifactId>VaultAPI</artifactId>
            <version>1.7.1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>MoodWorldEvents</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration><release>21</release></configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF

rm -rf MoodCraftMeteo-main MoodEvent-main
rm -f MoodCraftMeteo-main.zip MoodEvent-main.zip

echo "== Fusion MoodWorldEvents terminée =="
echo "Lance : mvn clean package"
