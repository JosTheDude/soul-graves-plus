package gg.jos.soulgravesplus;

import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.commands.ReloadCommand;
import gg.jos.soulgravesplus.listeners.DeathListener;
import gg.jos.soulgravesplus.listeners.SoulLogger;
import gg.jos.soulgravesplus.listeners.holograms.HologramListener;
import gg.jos.soulgravesplus.listeners.holograms.HologramUpdater;
import gg.jos.soulgravesplus.utils.UpdateChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class SoulGravesPlus extends JavaPlugin {

    public boolean loggerEnabled;
    public boolean logSoulSpawns;
    public boolean logSoulPickups;
    public boolean logSoulExplosions;

    public String logSoulSpawnsMessage;
    public String logSoulPickupsMessage;
    public String logSoulExplosionsMessage;

    public boolean hologramEnabled;
    public boolean hologramPersistent;
    public double hologramXOffset;
    public double hologramYOffset;
    public double hologramZOffset;
    public Vector hologramScale;
    public Display.Billboard hologramBillboard;
    public TextDisplay.TextAlignment hologramTextAlignment;
    public boolean hologramSeeThrough;
    public boolean hologramBackground;
    public String[] hologramBackgroundColor;
    public boolean hologramTextShadow;
    public int hologramShadowRadius;
    public float hologramShadowStrength;
    public List<String> hologramLines;
    public long hologramUpdateTicks;

    public boolean deathCoordinatesEnabled;
    public String deathCoordinatesMessage;

    public SimpleDateFormat dateTimeFormatter;
    public String timeFormat;
    private ConcurrentHashMap<String, String> worldNameAliases;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final int resourceId = 122635;

    @Override
    public void onEnable() {

        if (getServer().getPluginManager().getPlugin("SoulGraves") == null) {
            this.getLogger().warning("SoulGraves not found, disabling SoulGravesPlus");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!getServer().getPluginManager().isPluginEnabled("SoulGraves")) {
            this.getLogger().warning("SoulGraves was found but was not enabled, disabling SoulGravesPlus");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Startup Messages
        Bukkit.getConsoleSender().sendMessage(parseMiniMessage("<green>Enabled!</green> <light_purple>Made by JosTheDude with</light_purple> <red><3</red> <light_purple>and cookies!</light_purple>"));
        Bukkit.getConsoleSender().sendMessage(parseMiniMessage("<yellow>Please report any issues here:</yellow> <light_purple><underlined>https://github.com/JosTheDude/SoulGravesPlus</underlined></light_purple>"));
        Bukkit.getConsoleSender().sendMessage(parseMiniMessage("<yellow>Like my work? Support me here:</yellow> <light_purple><underlined>https://ko-fi.com/jossydev</underlined></light_purple>"));

        new UpdateChecker(this, resourceId).getVersion(version -> {
            if (this.getPluginMeta().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(parseMiniMessage("<green>You are using the latest version!</green>"));
            } else {
                Bukkit.getConsoleSender().sendMessage(parseMiniMessage("<red>A new version of SoulGravesPlus is available! Download it here:</red> <light_purple>https://www.spigotmc.org/resources/soulgravesplus.122635/</light_purple>"));
            }
        });

        // Config & Feature Subsets
        saveDefaultConfig();
        updateConfig();
        featureSubsets();

        // Commands
        this.getCommand("soulgravesplus").setExecutor(new ReloadCommand(this));
    }

    public void updateConfig() {
        // Logger Feature
        this.loggerEnabled = this.getConfig().getBoolean("logger.enabled", true);
        this.logSoulSpawns = this.getConfig().getBoolean("logger.log-soul-spawns", true);
        this.logSoulPickups = this.getConfig().getBoolean("logger.log-soul-pickups", true);
        this.logSoulExplosions = this.getConfig().getBoolean("logger.log-soul-explosions", true);

        this.logSoulSpawnsMessage = this.getConfig().getString(
                "logger.log-soul-spawns-message",
                "<gray>[SoulTracker]</gray> <green>Soul spawned for <yellow><soul_owner></yellow> at <yellow><x>, <y>, <z></yellow></green>"
        );
        this.logSoulPickupsMessage = this.getConfig().getString(
                "logger.log-soul-pickups-message",
                "<gray>[SoulTracker]</gray> <yellow><soul_owner></yellow><green> picked up a soul at <yellow><x>, <y>, <z></yellow></green>"
        );
        this.logSoulExplosionsMessage = this.getConfig().getString(
                "logger.log-soul-explosions-message",
                "<gray>[SoulTracker]</gray> <red>Soul for <yellow><soul_owner></yellow> exploded at <yellow><x>, <y>, <z></yellow></red>"
        );

        // Hologram Features
        this.hologramEnabled = this.getConfig().getBoolean("hologram.enabled", true);

        // Persistence
        this.hologramPersistent = this.getConfig().getBoolean("hologram.persistent", false);

        // Position & Scale
        this.hologramXOffset = this.getConfig().getDouble("hologram.x-offset", 0.0);
        this.hologramYOffset = this.getConfig().getDouble("hologram.y-offset", 3.5);
        this.hologramZOffset = this.getConfig().getDouble("hologram.z-offset", 0.0);

        String scaleString = this.getConfig().getString("hologram.scale", "1.0,1.0,1.0");
        String[] scaleArgs = scaleString != null ? scaleString.split(",") : new String[0];
        if (scaleArgs.length == 3) {
            try {
                double x = Double.parseDouble(scaleArgs[0].trim());
                double y = Double.parseDouble(scaleArgs[1].trim());
                double z = Double.parseDouble(scaleArgs[2].trim());
                this.hologramScale = new Vector(x, y, z);
            } catch (Exception e) {
                this.getLogger().warning("Invalid hologram scale format! Expected 'x,y,z', using default.");
                this.hologramScale = new Vector(1, 1, 1);
            }
        } else {
            this.getLogger().warning("Invalid hologram scale format! Expected 'x,y,z', using default.");
            this.hologramScale = new Vector(1, 1, 1);
        }

        // Appearance
        try {
            this.hologramBillboard = Display.Billboard.valueOf(
                this.getConfig().getString("hologram.billboard", "CENTER").toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            this.getLogger().warning("Invalid billboard value: " + this.getConfig().getString("hologram.billboard") + ", using default CENTER");
            this.hologramBillboard = Display.Billboard.CENTER;
        }

        try {
            this.hologramTextAlignment = TextDisplay.TextAlignment.valueOf(
                this.getConfig().getString("hologram.text-alignment", "CENTER").toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            this.getLogger().warning("Invalid text-alignment value: " + this.getConfig().getString("hologram.text-alignment") + ", using default CENTER");
            this.getLogger().warning("Invalid text alignment value: " + this.getConfig().getString("hologram.text-alignment") + ", using default CENTER");
            this.hologramTextAlignment = TextDisplay.TextAlignment.CENTER;
        }

        this.hologramSeeThrough = this.getConfig().getBoolean("hologram.see-through", false);

        // Background
        this.hologramBackground = this.getConfig().getBoolean("hologram.custom-background", false);
        this.hologramBackgroundColor = this.getConfig().getString("hologram.background-color", "1,100,255,79").split(",");

        // Shadow
        this.hologramTextShadow = this.getConfig().getBoolean("hologram.text-shadow", false);
        this.hologramShadowRadius = this.getConfig().getInt("hologram.shadow-radius", 0);
        this.hologramShadowStrength = (float) this.getConfig().getDouble("hologram.shadow-strength", 1.0);

        // Content
        this.hologramLines = this.getConfig().getStringList("hologram.lines");
        this.hologramUpdateTicks = this.getConfig().getLong("hologram.update-ticks", 10L);

        // Death Coordinates Feature
        this.deathCoordinatesEnabled = this.getConfig().getBoolean("death-coordinates.enabled", true);
        this.deathCoordinatesMessage = this.getConfig().getString("death-coordinates.message", "<red>☠ Your soul has spawned at <white><x>, <y>, <z></white> in the <white><world></white>.</red>");

        // Formatting
        this.dateTimeFormatter = new SimpleDateFormat(this.getConfig().getString("options.date-format", "yyyy-MM-dd HH:mm:ss"));
        this.timeFormat = this.getConfig().getString("options.time-format", "<m>:<s>");

        this.worldNameAliases = new ConcurrentHashMap<>();
        ConfigurationSection aliasesSection = this.getConfig().getConfigurationSection("options.world-name-aliases");
        if (aliasesSection != null) {
            for (String key : aliasesSection.getKeys(false)) {
                String alias = aliasesSection.getString(key);
                if (alias != null) {
                    this.worldNameAliases.put(key, alias);
                } else {
                    this.getLogger().warning("World name alias '" + key + "' is missing a value in config.yml.");
                }
            }
        } else {
            this.getLogger().warning("No world name aliases section found in config.yml.");
        }
    }

    private void featureSubsets() {
        // Logger Feature
        if (this.loggerEnabled) {
            loggerFeatures();
        } else {
            this.getLogger().warning("Logger features are disabled in the config.yml.");
        }

        // Hologram Features
        if (this.hologramEnabled) {
            hologramFeatures();
        } else {
            this.getLogger().warning("Hologram features are disabled in the config.yml.");
        }

        // Death Coordinates Feature
        if (this.deathCoordinatesEnabled) {
            deathCoordinatesFeatures();
        } else {
            this.getLogger().warning("Death coordinates features are disabled in the config.yml.");
        }
    }

    private void loggerFeatures() {
        this.getServer().getPluginManager().registerEvents(new SoulLogger(this), this);
        this.getLogger().info("Logger features enabled.");
    }

    private void hologramFeatures() {
        this.getLogger().info("Using Display Entities for holograms.");
            
        HologramListener hologramListener = new HologramListener(this);
        this.getServer().getPluginManager().registerEvents(hologramListener, this);
            
        this.getServer().getGlobalRegionScheduler().runAtFixedRate(
            this,
            task -> new HologramUpdater(this, hologramListener).run(),
            this.hologramUpdateTicks,
            this.hologramUpdateTicks
        );
            
        this.getLogger().info("Hologram features enabled.");
    }

    private void deathCoordinatesFeatures() {
        this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        this.getLogger().info("Death coordinates features enabled.");
    }

    public String formatTime(long time) {
        if (time < 0) time = 0;
        long days = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(time) % 100;

        return this.timeFormat
                .replace("<d>", String.format("%02d", days))
                .replace("<h>", String.format("%02d", hours))
                .replace("<s>", String.format("%02d", seconds))
                .replace("<m>", String.format("%02d", minutes))
                .replace("<x>", String.format("%02d", milliseconds));
    }

    public List<String> parseHologramLines(Soul soul) {
        return this.hologramLines.stream()
                .map(line -> replaceSoulPlaceholders(line, soul))
                .toList();
    }

    public String replaceSoulPlaceholders(String message, Soul soul) {
        long timeLeft;
        OfflinePlayer player = Bukkit.getOfflinePlayer(soul.getOwnerUUID());

        if (this.getServer().getPluginManager().getPlugin("SoulGraves").getConfig().getBoolean("offline-owner-timer-freeze", false)) {
            if (!player.isOnline() && soul.getFreezeTime() > 0) {
                timeLeft = soul.getTimeLeft() * 1000L;
            } else {
                timeLeft = soul.getExpireTime() - System.currentTimeMillis();
            }
        } else {
            timeLeft = soul.getExpireTime() - System.currentTimeMillis();
        }

        // Define placeholders
        String soulOwner = player.getName();
        String formattedDeathTime = this.dateTimeFormatter.format(new Date(soul.getDeathTime()));
        String formattedExpireTime = this.dateTimeFormatter.format(new Date(soul.getExpireTime()));
        String formattedTimeLeft = this.formatTime(timeLeft);
        String rawDeathTime = String.valueOf(soul.getDeathTime());
        String rawExpireTime = String.valueOf(soul.getExpireTime());
        String rawTimeLeft = String.valueOf(timeLeft);
        String invAmount = String.valueOf(soul.getInventory().size());
        String expAmount = String.valueOf(soul.getXp());

        return message
                .replace("<soul_owner>", Objects.requireNonNullElse(soulOwner, "unknown"))
                .replace("<soul_formatted_death_time>", formattedDeathTime)
                .replace("<soul_formatted_expire_time>", formattedExpireTime)
                .replace("<soul_formatted_time_left>", formattedTimeLeft)
                .replace("<soul_raw_death_time>", rawDeathTime)
                .replace("<soul_raw_expire_time>", rawExpireTime)
                .replace("<soul_raw_time_left>", rawTimeLeft)
                .replace("<soul_inventory_amount>", invAmount)
                .replace("<soul_experience_amount>", expAmount);
    }

    public Component parseMiniMessage(String message) {
        return this.miniMessage.deserialize(message);
    }

    public Component parseMiniMessage(String message, Map<String, String> placeholders) {
        TagResolver[] resolvers = placeholders.entrySet().stream()
                .map(entry -> Placeholder.parsed(entry.getKey(), entry.getValue()))
                .toArray(TagResolver[]::new);
        return this.miniMessage.deserialize(message, resolvers);
    }

    public Map<String, String> createLocationPlaceholders(String soulOwner, String world, int x, int y, int z) {
        Map<String, String> placeholders = new LinkedHashMap<>();
        placeholders.put("soul_owner", Objects.requireNonNullElse(soulOwner, "unknown"));
        placeholders.put("world", world);
        placeholders.put("x", String.valueOf(x));
        placeholders.put("y", String.valueOf(y));
        placeholders.put("z", String.valueOf(z));
        return placeholders;
    }

    public String getWorldAlias(String worldName) {
        return this.worldNameAliases.getOrDefault(worldName, worldName);
    }
}
