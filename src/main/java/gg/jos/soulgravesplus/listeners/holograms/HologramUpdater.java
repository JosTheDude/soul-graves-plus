package gg.jos.soulgravesplus.listeners.holograms;

import gg.jos.soulgravesplus.SoulGravesPlus;

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
        for (var hologram : HologramListener.activeHolograms.values()) {
            listener.queueUpdate(hologram);
        }
    }
}
