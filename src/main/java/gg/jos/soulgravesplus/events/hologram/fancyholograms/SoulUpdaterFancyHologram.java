package gg.jos.soulgravesplus.events.hologram.fancyholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.SoulGraves;
import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.SoulGravesPlus;

public class SoulUpdaterFancyHologram implements Runnable {
    private final HologramManager manager;
    private final SoulGravesPlus soulGravesPlus;

    public SoulUpdaterFancyHologram(SoulGravesPlus soulGravesPlus, HologramManager manager) {
        this.soulGravesPlus = soulGravesPlus;
        this.manager = manager;
    }

    @Override
    public void run() {
        if (!this.soulGravesPlus.hologramEnabled)
            return;

        for (Soul soul : SoulGraves.Companion.getSoulList()) {
            String hologramName = "grave_hologram_" + soul.getMarkerUUID();

            Hologram hologram = manager.getHologram(hologramName).orElse(null);

            if (hologram == null) continue;

            if (!(hologram.getData() instanceof TextHologramData data)) continue;

            data.setText(this.soulGravesPlus.parseHologramLines(soul));
            hologram.forceUpdate();
        }
    }
}
