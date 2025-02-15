package gg.jos.soulgravesplus.events;

import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;

import java.util.Arrays;

public class GraveSpawnListener implements Listener {
    private final Plugin plugin;

    public GraveSpawnListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGraveSpawn(SoulSpawnEvent event) {
        // Get the grave location and adjust it to be above the grave

        Location location = event.getSoulLocation().add(0.0, 2.0, 0.0);

        // Define hologram content
        String graveOwner = event.getGrave().getOwnerName();
        String deathTime = event.getGrave().getDeathTime().toString();
        int itemCount = event.getGrave().getItemCount();

        // Define hologram lines
        String[] hologramLines = {
                "§eHere lies " + graveOwner,
                "§7Died on: " + deathTime,
                "§7Items Stored: " + itemCount
        };

        // Create a unique hologram name
        String hologramName = "grave_hologram_" + event.getGrave().getId();

        // Create the hologram
        Hologram hologram = DHAPI.createHologram(hologramName, location, true, Arrays.asList(hologramLines));

        // Optional settings for the hologram
        hologram.setVisibleByDefault(true);
        hologram.setAllowPlaceholders(true);
    }
}
