package gg.jos.soulgravesplus.listeners.holograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import dev.faultyfunctions.soulgraves.SoulGraves;
import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.SoulGravesPlus;

public class FancyHologramsUpdater implements Runnable {
    private final SoulGravesPlus soulGravesPlus;
    private final HologramManager hologramManager;

    public FancyHologramsUpdater(SoulGravesPlus soulGravesPlus, HologramManager hologramManager) {
        this.soulGravesPlus = soulGravesPlus;
        this.hologramManager = hologramManager;
    }

    @Override
    public void run() {
        if (!soulGravesPlus.hologramEnabled)
            return;

        for (Soul soul : SoulGraves.Companion.getSoulList()) {
            String hologramName = "grave_hologram_" + soul.getMarkerUUID();

            hologramManager.getHologram(hologramName).ifPresent(hologram -> {
                if (hologram.getData() instanceof TextHologramData hologramData) {
                    hologramData.setText(soulGravesPlus.parseHologramLines(soul));
                    hologram.queueUpdate();
                }
            });
        }
    }
}
