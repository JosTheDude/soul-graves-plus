package gg.jos.soulgravesplus.listeners.holograms;

import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulDeleteEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;

public class FancyHologramsListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;
    private final HologramManager hologramManager;

    public FancyHologramsListener(SoulGravesPlus soulGravesPlus, HologramManager hologramManager) {
        this.soulGravesPlus = soulGravesPlus;
        this.hologramManager = hologramManager;
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
        TextHologramData hologramData = new TextHologramData(hologramName, soulLocation);

        hologramData.setText(soulGravesPlus.parseHologramLines(event.getSoul()));
        hologramData.setPersistent(false);

        if (soulGravesPlus.hologramBackground) {
            try {
                int a = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[0]);
                int r = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[1]);
                int g = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[2]);
                int b = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[3]);

                hologramData.setBackground(Color.fromARGB(a, r, g, b));
            } catch (NumberFormatException e) {
                soulGravesPlus.getLogger().warning(
                        "Invalid hologram background color configured; expected integer ARGB values but got: "
                                + String.join(",", soulGravesPlus.hologramBackgroundColor));
            }
        }

        if (soulGravesPlus.hologramTextShadow) {
            hologramData.setTextShadow(true);
        }

        Hologram hologram = hologramManager.create(hologramData);
        hologramManager.addHologram(hologram);
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
        Hologram hologram = hologramManager.getHologram(hologramName).orElse(null);

        if (hologram == null)
            return;
        hologramManager.removeHologram(hologram);
    }
}
