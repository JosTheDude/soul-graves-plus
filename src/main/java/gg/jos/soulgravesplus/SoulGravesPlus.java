package gg.jos.soulgravesplus;

import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.commands.ReloadCommand;
import gg.jos.soulgravesplus.listeners.DeathListener;
import gg.jos.soulgravesplus.listeners.SoulLogger;
import gg.jos.soulgravesplus.listeners.holograms.HologramListener;
import gg.jos.soulgravesplus.listeners.holograms.HologramUpdater;
import gg.jos.soulgravesplus.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        this.getLogger().info("\u001B[32mEnabled!\u001B[0m \u001B[35mMade by\u001B[0m \u001B[35mJosTheDude\u001B[0m \u001B[35mwith\u001B[0m \u001B[31m<3\u001B[0m \u001B[35mand cookies!\u001B[0m");
        this.getLogger().info("\u001B[33mPlease report any issues here:\u001B[0m \u001B[35m\u001B[4mhttps://github.com/JosTheDude/SoulGravesPlus\u001B[0m");
        this.getLogger().info("\u001B[33mLike my work? Support me here:\u001B[0m \u001B[35m\u001B[4mhttps://ko-fi.com/jossydev\u001B[0m");

        new UpdateChecker(this, resourceId).getVersion(version -> {
            if (this.getPluginMeta().getVersion().equals(version)) {
                getLogger().info("\u001B[32mYou are using the latest version!\u001B[0m");
            } else {
                getLogger().info("\u001B[31mA new version of SoulGravesPlus is available! Download it here: \u001B[0m \u001B[35mhttps://www.spigotmc.org/resources/soulgravesplus.122635/\u001B[0m");
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
                "\u001B[37m[SoulTracker] \u001B[92mSoul spawned for \u001B[93m{soulOwner}\u001B[92m at \u001B[93m{x} {y} {z}\u001B[0m"
        );
        this.logSoulPickupsMessage = this.getConfig().getString(
                "logger.log-soul-pickups-message",
                "\u001B[37m[SoulTracker] \u001B[93m{soulOwner}\u001B[92m picked up a soul at \u001B[93m{x} {y} {z}\u001B[0m"
        );
        this.logSoulExplosionsMessage = this.getConfig().getString(
                "logger.log-soul-explosions-message",
                "\u001B[37m[SoulTracker] \u001B[91mSoul for \u001B[93m{soulOwner}\u001B[91m exploded at \u001B[93m{x} {y} {z}\u001B[0m"
        );

        // Hologram Features
        this.hologramEnabled = this.getConfig().getBoolean("hologram.enabled", true);

        // Persistence
        this.hologramPersistent = this.getConfig().getBoolean("hologram.persistent", false);

        // Position & Scale
        this.hologramXOffset = this.getConfig().getDouble("hologram.x-offset", 0.0);
        this.hologramYOffset = this.getConfig().getDouble("hologram.y-offset", 3.5);
        this.hologramZOffset = this.getConfig().getDouble("hologram.z-offset", 0.0);

        String[] scaleArgs = this.getConfig().getString("hologram.scale", "1.0,1.0,1.0").split(",");
        try {
            double x = Double.parseDouble(scaleArgs[0]);
            double y = Double.parseDouble(scaleArgs[1]);
            double z = Double.parseDouble(scaleArgs[2]);
            this.hologramScale = new Vector(x, y, z);
        } catch (Exception e) {
            this.getLogger().warning("Invalid hologram scale format! Expected 'x,y,z', using default.");
            this.hologramScale = new Vector(1, 1, 1);
        }

        // Appearance
        try {
            this.hologramBillboard = Display.Billboard.valueOf(
                this.getConfig().getString("hologram.billboard", "CENTER").toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            this.hologramBillboard = Display.Billboard.CENTER;
        }

        try {
            this.hologramTextAlignment = TextDisplay.TextAlignment.valueOf(
                this.getConfig().getString("hologram.text-alignment", "CENTER").toUpperCase()
            );
        } catch (IllegalArgumentException e) {
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
        this.deathCoordinatesMessage = this.getConfig().getString("death-coordinates.message", "&c☠ You died at {x} {y} {z}");

        // Formatting
        this.dateTimeFormatter = new SimpleDateFormat(this.getConfig().getString("options.date-format", "yyyy-MM-dd HH:mm:ss"));
        this.timeFormat = this.getConfig().getString("options.time-format", "{m}m {s}s");
        
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
            
        this.getServer().getScheduler().runTaskTimer(
            this, 
            new HologramUpdater(this, hologramListener), 
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
                .replace("{d}", String.format("%02d", days))
                .replace("{h}", String.format("%02d", hours))
                .replace("{s}", String.format("%02d", seconds))
                .replace("{m}", String.format("%02d", minutes))
                .replace("{x}", String.format("%02d", milliseconds));
    }

    public List<String> parseHologramLines(Soul soul) {
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

        // Replace placeholders in the config lines
        return this.hologramLines.stream()
                .map(line -> line
                        .replace("{soulOwner}", Objects.requireNonNullElse(soulOwner, "unknown"))
                        .replace("{soulFormattedDeathTime}", formattedDeathTime)
                        .replace("{soulFormattedExpireTime}", formattedExpireTime)
                        .replace("{soulFormattedTimeLeft}", formattedTimeLeft)
                        .replace("{soulRawDeathTime}", rawDeathTime)
                        .replace("{soulRawExpireTime}", rawExpireTime)
                        .replace("{soulRawTimeLeft}", rawTimeLeft)
                        .replace("{soulInventoryAmount}", invAmount)
                        .replace("{soulExperienceAmount}", expAmount)
                        .replace("&", "§"))
                .toList();
    }

    public String getWorldAlias(String worldName) {
        return this.worldNameAliases.getOrDefault(worldName, worldName);
    }
}
