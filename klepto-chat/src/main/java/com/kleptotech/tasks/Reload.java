package com.kleptotech.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.KleptoChat;

public class Reload extends BukkitRunnable {
	 
    private KleptoChat plugin;
 
    public Reload(KleptoChat plugin) {
        this.plugin = plugin;
    }
 
    public void run() {
    	plugin.reload();
    }
}
 
