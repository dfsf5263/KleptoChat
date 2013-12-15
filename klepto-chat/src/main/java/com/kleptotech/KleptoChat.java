package com.kleptotech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.kleptotech.tasks.Reload;

public final class KleptoChat extends JavaPlugin {
	
	private Map<String, String> userPrefixes;
	private Collection<String> coreGroups;
	private Collection<String> donatorGroups;
	private Collection<String> staffGroups;
	private KleptoChat plugin = this;
	public static Permission permission = null;
   
	@Override
	public void onEnable() {
		getLogger().info("Klepto Chat is booting up. It's funky fresh!");
		saveDefaultConfig();

		userPrefixes = new HashMap<String, String>();
		
		if(!setupPermissions()){
			getLogger().log(Level.SEVERE, "Vault connection failed... Ruh roh.");
			getLogger().log(Level.SEVERE, "Klepto Chat shutting down. Sorry about that.");
			setEnabled(false);
			return;
		}
		
		reload();
		
		getServer().getPluginManager().registerEvents(new Listener() {
			
			@edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void playerJoin(PlayerJoinEvent event) {
				userPrefixes.put(event.getPlayer().getName(), getPrefix(event.getPlayer()));
            }
			
			@edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void pexServerCommandWasRun(ServerCommandEvent event) {
				if("pex".equalsIgnoreCase(event.getCommand().trim().substring(0, 3))) new Reload(plugin).runTaskLater(plugin, 20);
            }
			
			@edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void pexPlayerCommandWasRun(PlayerCommandPreprocessEvent event) {
				if("pex".equalsIgnoreCase(event.getMessage().trim().substring(1, 4))) new Reload(plugin).runTaskLater(plugin, 20);
            }
			
			@edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void playerSendMessage(AsyncPlayerChatEvent event) {
				event.getPlayer().setDisplayName(
						ChatColor.translateAlternateColorCodes('&', userPrefixes.get(event.getPlayer().getName())) 
						+ ChatColor.stripColor(event.getPlayer().getDisplayName()));
				if(event.getMessage().contains("klepto")){
					event.setMessage(event.getMessage().replace("klepto", ChatColor.MAGIC + "klepto" + ChatColor.RESET + ChatColor.DARK_GREEN));
					event.setMessage(event.getMessage().replace("Klepto", ChatColor.MAGIC + "Klepto" + ChatColor.RESET + ChatColor.DARK_GREEN));
				}
				event.setFormat(event.getPlayer().getDisplayName() + ChatColor.WHITE + ": " + ChatColor.DARK_GREEN + event.getMessage());
            }
            
        }, this);
		
		getCommand("kc").setExecutor(new KleptoChatCommandExec(this));
		
		getLogger().info("Klepto Chat is enabled! That got a bit hairy for a second...");
	}

	@Override
	public void onDisable() {
		getLogger().info("Klepto Chat is shutting down. Good Bye!");
	}
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	private String getPrefix(Player player){
		String prefix = "";
		Collection<String> groups = new ArrayList<String>(Arrays.asList(permission.getPlayerGroups(player)));

		//Check for donator group
		boolean groupPresent = false;
		for(String group : groups){
			if(donatorGroups.contains(group)){
				groupPresent = true;
				break;
			}
		}
		if(groupPresent){
			prefix += getConfig().getString("donatorPrefix") + ChatColor.RESET;
		}
		
		//Check for staff group
		groupPresent = false;
		String staffGroup = "";
		for(String group : groups){
			if(staffGroups.contains(group)){
				groupPresent = true;
				staffGroup = group;
				break;
			}
		}
		if(groupPresent){
			prefix += getConfig().getString("prefixes." + staffGroup);
			return prefix;
		}
		
		//Check for core group
		groupPresent = false;
		String coreGroup = "";
		for(String group : groups){
			if(coreGroups.contains(group)){
				groupPresent = true;
				coreGroup = group;
				break;
			}
		}
		if(groupPresent){
			prefix += getConfig().getString("prefixes." + coreGroup);
		}
		return prefix;
	}
	
	public void reload(){
		Collection<Player> players = new ArrayList<Player>(Arrays.asList(getServer().getOnlinePlayers()));
		coreGroups = getConfig().getStringList("core");
		donatorGroups = getConfig().getStringList("donator");
		staffGroups = getConfig().getStringList("staff");
		
		
		for(Player player : players){
			userPrefixes.put(player.getName(), getPrefix(player));
		}
	}
}
