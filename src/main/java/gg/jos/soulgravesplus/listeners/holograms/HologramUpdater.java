package gg.jos.soulgravesplus.listeners.holograms;

import dev.faultyfunctions.soulgraves.SoulGraves;
import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

import java.util.UUID;

public class HologramUpdater implements Runnable {
    private final SoulGravesPlus soulGravesPlus;
    private final HologramListener listener;

    public HologramUpdater(SoulGravesPlus soulGravesPlus, HologramListener listener) {
        this.soulGravesPlus = soulGravesPlus;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (!soulGravesPlus.hologramEnabled) return;
        for (var entry : HologramListener.activeHolograms.entrySet()) {
            UUID soulUUID = entry.getKey();
            UUID entityUUID = entry.getValue();

            Soul soul = null;
            for (Soul s : SoulGraves.Companion.getSoulList()) {
                if (s.getMarkerUUID().equals(soulUUID)) {
                    soul = s;
                    break;
                }
            }
            if (soul == null) continue;

            Entity entity = Bukkit.getEntity(entityUUID);
            if (entity instanceof TextDisplay display) {
                listener.updateText(display, soul);
            }
        }
    }
}
