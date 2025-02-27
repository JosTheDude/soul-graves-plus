package gg.jos.soulgravesplus.commands;

import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    private final SoulGravesPlus plugin;

    public ReloadCommand(SoulGravesPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("§cUsage: /soulgravesplus reload");
            return true;
        }

        if (sender instanceof Player && !sender.hasPermission("soulgravesplus.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command. §7(soulgravesplus.admin)");
            return true;
        }

        plugin.reloadConfig();
        plugin.updateConfig(plugin);

        sender.sendMessage("§aSoulGravesPlus configuration reloaded.");
        return true;
    }
}