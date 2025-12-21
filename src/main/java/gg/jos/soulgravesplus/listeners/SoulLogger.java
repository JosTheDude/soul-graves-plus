package gg.jos.soulgravesplus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;

public class SoulLogger implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulLogger(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    public void logActivity(String configMessage, OfflinePlayer player, Location location) {
        if (!soulGravesPlus.loggerEnabled)
            return;

        String worldName = soulGravesPlus.getWorldAlias(location.getWorld().getName());
        String logMessage = ChatColor.translateAlternateColorCodes('&', configMessage)
                .replace("{soulOwner}", player.getName())
                .replace("{x}", String.valueOf(location.getBlockX()))
                .replace("{y}", String.valueOf(location.getBlockY()))
                .replace("{z}", String.valueOf(location.getBlockZ()))
                .replace("{world}", worldName);

        Bukkit.getConsoleSender().sendMessage(logMessage);
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {
        if (!soulGravesPlus.logSoulSpawns)
            return;
        logActivity(soulGravesPlus.logSoulSpawnsMessage, event.getPlayer(), event.getSoulLocation());
    }

    @EventHandler
    public void onSoulPickup(SoulPickupEvent event) {
        if (!soulGravesPlus.logSoulPickups)
            return;
        logActivity(soulGravesPlus.logSoulPickupsMessage, event.getPlayer(), event.getSoulLocation());
    }

    @EventHandler
    public void onSoulExplode(SoulExplodeEvent event) {
        if (!soulGravesPlus.logSoulExplosions)
            return;
        logActivity(soulGravesPlus.logSoulExplosionsMessage, event.getOwner(), event.getSoulLocation());
    }
}
