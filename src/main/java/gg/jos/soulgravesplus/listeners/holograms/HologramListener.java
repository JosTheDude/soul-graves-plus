package gg.jos.soulgravesplus.listeners.holograms;

import dev.faultyfunctions.soulgraves.api.event.SoulDeleteEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Transformation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class HologramListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;
    public static final Map<UUID, ActiveHologram> activeHolograms = new ConcurrentHashMap<>();

    public HologramListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {
        if (!soulGravesPlus.hologramEnabled)
            return;

        Location soulLocation = event.getSoulLocation().clone().add(
                soulGravesPlus.hologramXOffset,
                soulGravesPlus.hologramYOffset,
                soulGravesPlus.hologramZOffset);

        if (soulGravesPlus.hologramLines.isEmpty()) {
            soulGravesPlus.getLogger().warning("Hologram lines are missing in the config!");
            return;
        }

        soulLocation.getWorld().spawn(soulLocation, TextDisplay.class, display -> {
            display.setPersistent(soulGravesPlus.hologramPersistent);

            display.setBillboard(soulGravesPlus.hologramBillboard);
            display.setAlignment(soulGravesPlus.hologramTextAlignment);

            display.setShadowed(soulGravesPlus.hologramTextShadow);
            display.setShadowRadius(soulGravesPlus.hologramShadowRadius);
            display.setShadowStrength(soulGravesPlus.hologramShadowStrength);
            display.setSeeThrough(soulGravesPlus.hologramSeeThrough);

            if (soulGravesPlus.hologramBackground) {
                if (soulGravesPlus.hologramBackgroundColor != null
                        && soulGravesPlus.hologramBackgroundColor.length == 4) {
                    try {
                        int a = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[0]);
                        int r = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[1]);
                        int g = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[2]);
                        int b = Integer.parseInt(soulGravesPlus.hologramBackgroundColor[3]);
                        display.setBackgroundColor(Color.fromARGB(a, r, g, b));
                    } catch (Exception e) {
                        display.setBackgroundColor(Color.fromARGB(79, 1, 100, 255));
                    }
                } else {
                    soulGravesPlus.getLogger().warning("Failed to parse hologram background color, using default.");
                    display.setBackgroundColor(Color.fromARGB(79, 1, 100, 255));
                }
            } else {
                display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            }

            Transformation transformation = display.getTransformation();
            transformation.getScale().set(
                    (float) soulGravesPlus.hologramScale.getX(),
                    (float) soulGravesPlus.hologramScale.getY(),
                    (float) soulGravesPlus.hologramScale.getZ());
            display.setTransformation(transformation);

            updateText(display, event.getSoul());

            activeHolograms.put(event.getSoul().getMarkerUUID(), new ActiveHologram(event.getSoul(), display));
        });
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
        ActiveHologram hologram = activeHolograms.remove(soulUUID);
        if (hologram == null)
            return;

        runForDisplay(hologram.display(), TextDisplay::remove);
    }

    public void updateText(TextDisplay display, Soul soul) {
        var lines = soulGravesPlus.parseHologramLines(soul);
        String joined = String.join("\n", lines);
        display.text(soulGravesPlus.parseMiniMessage(joined));
    }

    public void queueUpdate(ActiveHologram hologram) {
        runForDisplay(hologram.display(), display -> updateText(display, hologram.soul()));
    }

    private void runForDisplay(TextDisplay display, Consumer<TextDisplay> action) {
        display.getScheduler().run(
                soulGravesPlus,
                task -> action.accept(display),
                () -> activeHolograms.values().removeIf(active -> active.display().getUniqueId().equals(display.getUniqueId()))
        );
    }

    public record ActiveHologram(Soul soul, TextDisplay display) {
    }
}
