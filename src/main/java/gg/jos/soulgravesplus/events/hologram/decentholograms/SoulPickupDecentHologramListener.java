package gg.jos.soulgravesplus.events.hologram.decentholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulPickupDecentHologramListener implements Listener {
    private final Plugin plugin;

    public SoulPickupDecentHologramListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSoulPickup(SoulPickupEvent event) {

        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        DHAPI.removeHologram(hologramName);

    }
}
