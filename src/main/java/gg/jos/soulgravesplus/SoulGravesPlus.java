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

    public List<String> hologramLines;
    public double hologramXOffset;
    public double hologramYOffset;
    public double hologramZOffset;
    public boolean hologramBackground;
    public String[] hologramBackgroundColor;

    @Override
    public void onEnable() {

        if (getServer().getPluginManager().getPlugin("SoulGraves") == null) {
            getLogger().warning("SoulGraves not found, disabling SoulGravesPlus");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (getServer().getPluginManager().getPlugin("FancyHolograms") == null) {
            getLogger().warning("FancyHolograms not found, disabling SoulGravesPlus");
            getServer().getPluginManager().disablePlugin(this);
        }

        // API Managers
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

        // Config
        saveDefaultConfig();
        updateConfig(this);

        // Events
        getServer().getPluginManager().registerEvents(new SoulSpawnListener(this, manager, this), this);
        getServer().getPluginManager().registerEvents(new SoulPickupListener(this, manager), this);
        getServer().getPluginManager().registerEvents(new SoulExplodeListener(this, manager), this);

        // Commands
        this.getCommand("soulgravesplus").setExecutor(new ReloadCommand(this));

    }

    public static void updateConfig(SoulGravesPlus plugin) {
        plugin.hologramLines = plugin.getConfig().getStringList("hologram.lines");

        plugin.hologramXOffset = plugin.getConfig().getDouble("hologram.x-offset");
        plugin.hologramYOffset = plugin.getConfig().getDouble("hologram.y-offset");
        plugin.hologramZOffset = plugin.getConfig().getDouble("hologram.z-offset");

        plugin.hologramBackground = plugin.getConfig().getBoolean("hologram.background");
        plugin.hologramBackgroundColor = plugin.getConfig().getString("hologram.background-color").split(",");

    }
}
