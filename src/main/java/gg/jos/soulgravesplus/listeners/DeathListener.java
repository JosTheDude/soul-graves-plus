package gg.jos.soulgravesplus.listeners;

import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DeathListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public DeathListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {
        if (!soulGravesPlus.deathCoordinatesEnabled)
            return;

        Player player = event.getPlayer();
        Location soulLocation = event.getSoulLocation();

        int x = soulLocation.getBlockX();
        int y = soulLocation.getBlockY();
        int z = soulLocation.getBlockZ();
        String worldName = soulGravesPlus.getWorldAlias(soulLocation.getWorld().getName());
        player.sendMessage(soulGravesPlus.parseMiniMessage(
                soulGravesPlus.deathCoordinatesMessage,
                soulGravesPlus.createLocationPlaceholders(player.getName(), worldName, x, y, z)
        ));
    }
}
