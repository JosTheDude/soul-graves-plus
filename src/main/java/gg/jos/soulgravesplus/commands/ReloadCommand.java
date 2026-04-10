package gg.jos.soulgravesplus.commands;

import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final SoulGravesPlus soulGravesPlus;

    public ReloadCommand(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player && !sender.hasPermission("soulgravesplus.admin")) {
            sender.sendMessage(soulGravesPlus.parseMiniMessage("<red>You do not have permission to use this command.</red> <gray>(soulgravesplus.admin)</gray>"));
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(soulGravesPlus.parseMiniMessage("<red>Usage: /soulgravesplus reload</red>"));
            return true;
        }

        this.soulGravesPlus.reloadConfig();
        this.soulGravesPlus.updateConfig();

        sender.sendMessage(soulGravesPlus.parseMiniMessage("<green>SoulGravesPlus configuration reloaded.</green>"));
        return true;
    }
}
