package com.endercrest.pl3xnpc;

import com.endercrest.pl3xnpc.listeners.PlayerListener;
import com.endercrest.pl3xnpc.npc.NPCManager;
import com.endercrest.pl3xnpc.utils.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 *
 * @author Thomas on 4/24/2014.
 */
public class Pl3xNPC extends JavaPlugin {

    private Integer taskID;
    public boolean allowVNP = false;
    public boolean allowWG = false;

    @Override
    public void onEnable(){
        PluginManager pm = Bukkit.getPluginManager();
        loadConfiguration();
        if(!pm.isPluginEnabled("BKCommonLib")){
            log("&4[ERROR] Missing a required dependency, BKCommonLib was not found!");
            log("&4[ERROR] Pl3xNPC requires BKCommonLib to be installed and enabled to use");
            log("&4[ERROR] Please download and istall BKCommonLib and restart server!");
            log("&4[ERROR] Pl3xNPC will now disable itself");
            pm.disablePlugin(this);
            return;
        }
        if(pm.isPluginEnabled("VanishNoPacket")){
            log("&3Detected VanishNoPacket. Enabling vanish support!");
            allowVNP = true;
        }else{
            log("&e[WARNING] VanishNoPacket not found! Disabling vanish support!");
        }
        if(pm.isPluginEnabled("WorldGuard")){
            log("&3Detected WorldGuard. Enabling protected region support");
            allowWG = true;
        }else {
            log("&e[WARNING] WorldGuard not found! Disabling protected region support!");
        }
        NPCManager.loadAll(this);
        pm.registerEvents(new PlayerListener(this), this);
        getCommand("npc").setExecutor(new CmdNPC(this));
        new PacketHandler(this);
        restartRenderTask();
        log("&av" + this.getDescription().getVersion() + " by EnderCrest and BillyGalbreath");
    }

    @Override
    public void onDisable(){

    }

    /**
     * loads the default config
     */
    public void loadConfiguration() {
        if (!getConfig().contains("any-item-on-head")) getConfig().addDefault("any-item-on-head", false);
        if (!getConfig().contains("sound-on-select")) getConfig().addDefault("sound-on-select", true);
        if (!getConfig().contains("animate-on-select")) getConfig().addDefault("animate-on-select", true);
        if (!getConfig().contains("message-format")) getConfig().addDefault("message-format", "<{npc}> {message}");
        if (!getConfig().contains("message-radius")) getConfig().addDefault("message-radius", 5D);
        if (!getConfig().contains("look-at-radius")) getConfig().addDefault("look-at-radius", 10D);
        if (!getConfig().contains("update-interval")) getConfig().addDefault("update-interval", 3);
        if (!getConfig().contains("color-logs")) getConfig().addDefault("color-logs", true);
        if (!getConfig().contains("debug-mode")) getConfig().addDefault("debug-mode", false);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /**
     * This replaces minecraft colorcodes to add colour
     * @param str String that needs converting
     * @return returns the coloured text
     */
    public static String colorize(String str){ return str.replaceAll("(?i)&([a-f0-9k-or])", "\u00a7$1");}

    /**
     * Sends messages to the console
     * @param obj - object to be sent to console.
     */
    public void log(Object obj){
        if(getConfig().getBoolean("color-logs", true)){
            getServer().getConsoleSender().sendMessage(colorize("&3[&d" + getName() + "&3] &r" + obj));
        }else{
            Bukkit.getLogger().log(Level.INFO, "[" + getName() + "] " + (colorize((String) obj)).replaceAll("(?)\u00a7([a-f0-9k-or])", ""));
        }
    }

    /**
     * Restarts task for the rendering of the NPC's
     */
    public void restartRenderTask(){
        if(taskID != null) {
            getServer().getScheduler().cancelTask(taskID);
        }
            taskID = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new RenderTask(this), 0, getConfig().getInt("update-interval", 3));
            if (getConfig().getBoolean("debug-mode"))
                log("&dRestarting Rendering Task");
    }
}
