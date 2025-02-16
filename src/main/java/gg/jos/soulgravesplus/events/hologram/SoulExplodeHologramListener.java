package gg.jos.soulgravesplus.events.hologram;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulExplodeHologramListener implements Listener {
    private final Plugin plugin;
    private final HologramManager manager;

    public SoulExplodeHologramListener(Plugin plugin, HologramManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onSoulExplode(SoulExplodeEvent event) {

        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        Hologram hologram = manager.getHologram(hologramName).orElse(null);

        if (hologram == null) {
            return;
        }

        manager.removeHologram(hologram);
    }
}
