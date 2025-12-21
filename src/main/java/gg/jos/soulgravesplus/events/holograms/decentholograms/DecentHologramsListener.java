package gg.jos.soulgravesplus.events.holograms.decentholograms;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dev.faultyfunctions.soulgraves.api.event.SoulDeleteEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import gg.jos.soulgravesplus.SoulGravesPlus;

public class DecentHologramsListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public DecentHologramsListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {
        if (!soulGravesPlus.hologramEnabled)
            return;

        Location soulLocation = event.getSoulLocation().clone().add(soulGravesPlus.hologramXOffset,
                soulGravesPlus.hologramYOffset, soulGravesPlus.hologramZOffset);

        if (soulGravesPlus.hologramLines.isEmpty()) {
            soulGravesPlus.getLogger().warning("Hologram lines are missing in the config!");
            return;
        }

        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();
        DHAPI.createHologram(hologramName, soulLocation, soulGravesPlus.parseHologramLines(event.getSoul()));
    }

    @EventHandler
    public void onSoulPickup(SoulPickupEvent event) {
        removeHologram(event.getSoul().getMarkerUUID());
    }

    @EventHandler
    public void onSoulExplode(SoulExplodeEvent event) {
        removeHologram(event.getSoul().getMarkerUUID());
    }

    @EventHandler
    public void onSoulDelete(SoulDeleteEvent event) {
        removeHologram(event.getSoul().getMarkerUUID());
    }

    private void removeHologram(UUID soulUUID) {
        String hologramName = "grave_hologram_" + soulUUID;
        DHAPI.removeHologram(hologramName);
    }
}
