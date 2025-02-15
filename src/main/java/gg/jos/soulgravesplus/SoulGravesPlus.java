package gg.jos.soulgravesplus;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import gg.jos.soulgravesplus.commands.ReloadCommand;
import gg.jos.soulgravesplus.events.SoulExplodeListener;
import gg.jos.soulgravesplus.events.SoulPickupListener;
import gg.jos.soulgravesplus.events.SoulSpawnListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SoulGravesPlus extends JavaPlugin {

    // Hologram Config
    public boolean hologramEnabled;
    public double hologramXOffset;
    public double hologramYOffset;
    public double hologramZOffset;
    public boolean hologramBackground;
    public String[] hologramBackgroundColor;
    public List<String> hologramLines;

    @Override
    public void onEnable() {

        if (getServer().getPluginManager().getPlugin("SoulGraves") == null) {
            this.getLogger().warning("SoulGraves not found, disabling SoulGravesPlus");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Config
        saveDefaultConfig();
        updateConfig(this);

        // Feature SUbsets
        if (this.hologramEnabled) {
            hologramFeatures(this);
        } else {
            this.getLogger().warning("Hologram features are disabled in the config.yml.");
        }

        // Commands
        this.getCommand("soulgravesplus").setExecutor(new ReloadCommand(this));

    }

    private void hologramFeatures(SoulGravesPlus plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("FancyHolograms") != null) {
            HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

            plugin.getServer().getPluginManager().registerEvents(new SoulSpawnListener(this, manager, this), this);
            plugin.getServer().getPluginManager().registerEvents(new SoulPickupListener(this, manager), this);
            plugin.getServer().getPluginManager().registerEvents(new SoulExplodeListener(this, manager), this);

            plugin.getLogger().warning("FancyHolograms found! Hologram features enabled.");
        }
    }

    public void updateConfig(SoulGravesPlus plugin) {
        plugin.hologramEnabled = plugin.getConfig().getBoolean("hologram.enabled");

        plugin.hologramXOffset = plugin.getConfig().getDouble("hologram.x-offset");
        plugin.hologramYOffset = plugin.getConfig().getDouble("hologram.y-offset");
        plugin.hologramZOffset = plugin.getConfig().getDouble("hologram.z-offset");

        plugin.hologramBackground = plugin.getConfig().getBoolean("hologram.background");
        plugin.hologramBackgroundColor = plugin.getConfig().getString("hologram.background-color").split(",");

        plugin.hologramLines = plugin.getConfig().getStringList("hologram.lines");

    }
    
    
}
