package com.kleptotech;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KleptoChatCommandExec implements CommandExecutor{
	
	private KleptoChat plugin;
	 
	public KleptoChatCommandExec(KleptoChat plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kc")) {

			if(args.length < 1 || args.length > 1) return false;
			
			else if(args.length == 1){
				if(args[0].equalsIgnoreCase("reload")){
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (!player.hasPermission("kleptochat.admin")){
							sender.sendMessage(ChatColor.DARK_RED + "You don't have permssion for that.");
							return true;
						}
					}
					
					plugin.reloadConfig();
					plugin.reload();
					return true;
				}
				return false;
			}
		}
		
		//Default
		return false;
		
	}
}
