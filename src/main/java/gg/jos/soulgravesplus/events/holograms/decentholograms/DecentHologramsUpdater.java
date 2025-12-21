package gg.jos.soulgravesplus.events.holograms.decentholograms;

import dev.faultyfunctions.soulgraves.SoulGraves;
import dev.faultyfunctions.soulgraves.utils.Soul;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import gg.jos.soulgravesplus.SoulGravesPlus;

public class DecentHologramsUpdater implements Runnable {
    private final SoulGravesPlus soulGravesPlus;

    public DecentHologramsUpdater(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @Override
    public void run() {
        if (!soulGravesPlus.hologramEnabled)
            return;

        for (Soul soul : SoulGraves.Companion.getSoulList()) {
            String hologramName = "grave_hologram_" + soul.getMarkerUUID();

            Hologram hologram = DHAPI.getHologram(hologramName);
            if (hologram == null)
                continue;

            DHAPI.setHologramLines(hologram, soulGravesPlus.parseHologramLines(soul));
        }
    }
}
