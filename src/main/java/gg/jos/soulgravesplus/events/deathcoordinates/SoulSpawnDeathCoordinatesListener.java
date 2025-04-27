package gg.jos.soulgravesplus.events.deathcoordinates;

import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoulSpawnDeathCoordinatesListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulSpawnDeathCoordinatesListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {

        if (!this.soulGravesPlus.deathCoordinatesEnabled) {
            return;
        }

        // Get the grave location and adjust it to be above the grave
        Location soulLocation = event.getSoulLocation();
        Player player = event.getPlayer();
        String soulOwner = player.getName();

        double x = Math.round(soulLocation.getX() * 100.0) / 100.0;
        double y = Math.round(soulLocation.getY() * 100.0) / 100.0;
        double z = Math.round(soulLocation.getZ() * 100.0) / 100.0;
        String worldName = soulGravesPlus.getWorldAlias(soulLocation.getWorld().getName());

        player.sendMessage(this.soulGravesPlus.deathCoordinatesMessage
                .replace("{soulOwner}", soulOwner)
                .replace("{x}", String.valueOf(x))
                .replace("{y}", String.valueOf(y))
                .replace("{z}", String.valueOf(z))
                .replace("{world}", worldName)
                .replace("&", "§")
        );
    }
}
