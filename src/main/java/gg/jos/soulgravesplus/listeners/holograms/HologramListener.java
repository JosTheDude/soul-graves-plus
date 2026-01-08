package gg.jos.soulgravesplus.listeners.holograms;

import dev.faultyfunctions.soulgraves.api.event.SoulDeleteEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.SoulGravesPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;
    public static final Map<UUID, UUID> activeHolograms = new HashMap<>();

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
                display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            }

            Transformation transformation = display.getTransformation();
            transformation.getScale().set(
                    (float) soulGravesPlus.hologramScale.getX(),
                    (float) soulGravesPlus.hologramScale.getY(),
                    (float) soulGravesPlus.hologramScale.getZ());
            display.setTransformation(transformation);

            updateText(display, event.getSoul());

            activeHolograms.put(event.getSoul().getMarkerUUID(), display.getUniqueId());
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
        UUID entityUUID = activeHolograms.remove(soulUUID);
        if (entityUUID != null) {
            var entity = org.bukkit.Bukkit.getEntity(entityUUID);
            if (entity instanceof TextDisplay) {
                entity.remove();
            }
        }
    }

    public void updateText(TextDisplay display, Soul soul) {
        var lines = soulGravesPlus.parseHologramLines(soul);

        String joined = String.join("\n", lines);

        Component content = LegacyComponentSerializer.legacyAmpersand().deserialize(joined);

        display.text(content);
    }
}
