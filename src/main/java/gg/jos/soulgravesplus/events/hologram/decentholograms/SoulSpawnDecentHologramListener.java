package gg.jos.soulgravesplus.events.hologram.decentholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SoulSpawnDecentHologramListener implements Listener {
    private final Plugin plugin;
    private final SoulGravesPlus soulGravesPlus;

    public SoulSpawnDecentHologramListener(Plugin plugin, SoulGravesPlus soulGravesPlus) {
        this.plugin = plugin;
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {

        if (!soulGravesPlus.hologramEnabled) {
            return;
        }

        // Get the grave location and adjust it to be above the grave
        Location soulLocation = event.getSoulLocation();
        Location location = soulLocation.clone().add(soulGravesPlus.hologramXOffset, soulGravesPlus.hologramYOffset, soulGravesPlus.hologramZOffset);

        // Fetch the hologram config
        if (soulGravesPlus.hologramLines.isEmpty()) {
            plugin.getLogger().warning("Hologram lines are missing in the config!");
            return;
        }

        // Define placeholders
        String soulOwner = event.getPlayer().getName();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = now.format(formatter);
        String soulTime = String.valueOf(event.getSoul().getTimeLeft());

        // Replace placeholders in the config lines
        List<String> parsedLines = soulGravesPlus.hologramLines.stream()
                .map(line -> line
                        .replace("{soulOwner}", soulOwner)
                        .replace("{formattedTime}", formattedTime)
                        .replace("{soulTime}", soulTime)
                        .replace("&", "§"))
                .toList();

        // Create a unique hologram name
        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        DHAPI.createHologram(hologramName, location, parsedLines);

    }
}
