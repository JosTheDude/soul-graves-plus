package gg.jos.soulgravesplus.events.logger;

import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulSpawnLoggerListener implements Listener {
    private final Plugin plugin;
    private final SoulGravesPlus soulGravesPlus;

    public SoulSpawnLoggerListener(Plugin plugin, SoulGravesPlus soulGravesPlus) {
        this.plugin = plugin;
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {

        if (!soulGravesPlus.loggerEnabled) {
            return;
        }

        if (!soulGravesPlus.logSoulSpawns) {
            return;
        }

        // Get the grave location and adjust it to be above the grave
        Location soulLocation = event.getSoulLocation();
        Player player = event.getPlayer();
        String soulOwner = player.getName();

        plugin.getLogger().info(soulGravesPlus.logSoulSpawnsMessage
                .replace("{soulOwner}", soulOwner)
                .replace("{x}", String.valueOf(soulLocation.getBlockX()))
                .replace("{y}", String.valueOf(soulLocation.getBlockY()))
                .replace("{z}", String.valueOf(soulLocation.getBlockZ()))
        );

    }
}
