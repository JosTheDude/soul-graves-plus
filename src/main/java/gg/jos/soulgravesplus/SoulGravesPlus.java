package gg.jos.soulgravesplus;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import gg.jos.soulgravesplus.commands.ReloadCommand;
import gg.jos.soulgravesplus.events.hologram.decentholograms.SoulExplodeDecentHologramListener;
import gg.jos.soulgravesplus.events.hologram.decentholograms.SoulPickupDecentHologramListener;
import gg.jos.soulgravesplus.events.hologram.decentholograms.SoulSpawnDecentHologramListener;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.SoulExplodeFancyHologramListener;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.SoulPickupFancyHologramListener;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.SoulSpawnFancyHologramListener;
import gg.jos.soulgravesplus.events.logger.SoulExplodeLoggerListener;
import gg.jos.soulgravesplus.events.logger.SoulPickupLoggerListener;
import gg.jos.soulgravesplus.events.logger.SoulSpawnLoggerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SoulGravesPlus extends JavaPlugin {

    // Logger Config
    public boolean loggerEnabled;
    public boolean logSoulSpawns;
    public boolean logSoulPickups;
    public boolean logSoulExplosions;

    public String logSoulSpawnsMessage;
    public String logSoulPickupsMessage;
    public String logSoulExplosionsMessage;

    // Hologram Config
    public boolean hologramEnabled;
    public double hologramXOffset;
    public double hologramYOffset;
    public double hologramZOffset;
    public boolean hologramBackground;
    public String[] hologramBackgroundColor;
    public List<String> hologramLines;
    public String hologramManager; // Unused but exists for future needs

    @Override
    public void onEnable() {

        if (getServer().getPluginManager().getPlugin("SoulGraves") == null) {
            this.getLogger().warning("SoulGraves not found, disabling SoulGravesPlus");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Logger Messages

        this.getLogger().info("\u001B[35mSoulGravesPlus enabled! Made by JosTheDude with \u001B[31m<3\u001B[35m and cookies\u001B[0m\n");
        this.getLogger().info("\u001B[37mPlease report any issues to \u001B[37m\u001B[4mhttps://github.com/JosTheDude/SoulGravesPlus\u001B[0m");

        // Config & Feature Subsets
        saveDefaultConfig();
        updateConfig(this);

        // Commands
        this.getCommand("soulgravesplus").setExecutor(new ReloadCommand(this));

    }

    public void updateConfig(SoulGravesPlus plugin) {
        // Logger Features
        plugin.loggerEnabled = plugin.getConfig().getBoolean("logger.enabled");
        plugin.logSoulSpawns = plugin.getConfig().getBoolean("logger.log-soul-spawns");
        plugin.logSoulPickups = plugin.getConfig().getBoolean("logger.log-soul-pickups");
        plugin.logSoulExplosions = plugin.getConfig().getBoolean("logger.log-soul-explosions");

        plugin.logSoulSpawnsMessage = plugin.getConfig().getString("logger.log-soul-spawns-message");
        plugin.logSoulPickupsMessage = plugin.getConfig().getString("logger.log-soul-pickups-message");
        plugin.logSoulExplosionsMessage = plugin.getConfig().getString("logger.log-soul-explosions-message");

        // Hologram Features
        plugin.hologramEnabled = plugin.getConfig().getBoolean("hologram.enabled");

        plugin.hologramXOffset = plugin.getConfig().getDouble("hologram.x-offset");
        plugin.hologramYOffset = plugin.getConfig().getDouble("hologram.y-offset");
        plugin.hologramZOffset = plugin.getConfig().getDouble("hologram.z-offset");

        plugin.hologramBackground = plugin.getConfig().getBoolean("hologram.background");
        plugin.hologramBackgroundColor = plugin.getConfig().getString("hologram.background-color").split(",");

        plugin.hologramLines = plugin.getConfig().getStringList("hologram.lines");

        featureSubsets(plugin);

    }

    private void featureSubsets(SoulGravesPlus plugin) {

        // Logger Feature
        if (plugin.loggerEnabled) {
            loggerFeatures(plugin);
        } else {
            plugin.getLogger().warning("Logger features are disabled in the config.yml.");
        }

        // Hologram Features
        if (plugin.hologramEnabled) {
            hologramFeatures(plugin);
        } else {
            plugin.getLogger().warning("Hologram features are disabled in the config.yml.");
        }

    }

    private void loggerFeatures(SoulGravesPlus plugin) {

        plugin.getServer().getPluginManager().registerEvents(new SoulSpawnLoggerListener(this, plugin), this);
        plugin.getServer().getPluginManager().registerEvents(new SoulPickupLoggerListener(this, plugin), this);
        plugin.getServer().getPluginManager().registerEvents(new SoulExplodeLoggerListener(this, plugin), this);

        plugin.getLogger().info("Logger features enabled.");
    }

    private void hologramFeatures(SoulGravesPlus plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("FancyHolograms") != null) {

            plugin.hologramManager = "FancyHolograms";

            HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

            plugin.getServer().getPluginManager().registerEvents(new SoulSpawnFancyHologramListener(this, manager, this), this);
            plugin.getServer().getPluginManager().registerEvents(new SoulPickupFancyHologramListener(this, manager), this);
            plugin.getServer().getPluginManager().registerEvents(new SoulExplodeFancyHologramListener(this, manager), this);

            plugin.getLogger().info("FancyHolograms found! Hologram features enabled.");
        } else if (plugin.getServer().getPluginManager().getPlugin("DecentHolograms") != null) {

            plugin.hologramManager = "DecentHolograms";

            plugin.getServer().getPluginManager().registerEvents(new SoulSpawnDecentHologramListener(this, this), this);
            plugin.getServer().getPluginManager().registerEvents(new SoulPickupDecentHologramListener(this), this);
            plugin.getServer().getPluginManager().registerEvents(new SoulExplodeDecentHologramListener(this), this);

            plugin.getLogger().info("DecentHolograms found! Hologram features enabled.");
        } else {
            plugin.getLogger().warning("FancyHolograms or DecentHolograms not found! Hologram features disabled.");
        }
    }
    
}
