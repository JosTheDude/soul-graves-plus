package gg.jos.soulgravesplus.events.hologram.decentholograms;

import dev.faultyfunctions.soulgraves.api.event.SoulDeleteEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoulDeleteDecentHologramListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulDeleteDecentHologramListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulExplode(SoulDeleteEvent event) {

        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        DHAPI.removeHologram(hologramName);

    }
}
