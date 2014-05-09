package com.endercrest.pl3xnpc.listeners;

import com.endercrest.pl3xnpc.Pl3xNPC;
import com.endercrest.pl3xnpc.RenderTask;
import com.endercrest.pl3xnpc.npc.NPC;
import com.endercrest.pl3xnpc.npc.NPCManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The PlayerListener to listen to Respawn, Quit, and Kick Event
 */
public class PlayerListener implements Listener {
    private Pl3xNPC plugin;

    public PlayerListener(final Pl3xNPC plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(final PlayerRespawnEvent event){
        for(NPC npc : NPCManager.getNPCList()) {
            if (!RenderTask.render.containsKey(npc.getId())) {
                continue;
            }
            List<String> list = RenderTask.render.get(npc.getId());
            List<String> remove = new ArrayList<String>();
            for (String s : list)
                if (s.equals(event.getPlayer().getName()))
                    remove.add(s);
            for (String s : remove)
                list.remove(s);
            RenderTask.render.put(npc.getId(), list);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event){
        NPCManager.setSelected(event.getPlayer().getName(), null);
        if (plugin.getConfig().getBoolean("debug-mode"))
            plugin.log("&dUnselected NPC for &7" + event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(final PlayerKickEvent event){
        NPCManager.setSelected(event.getPlayer().getName(), null);
        if (plugin.getConfig().getBoolean("debug-mode"))
            plugin.log("&dUnselected NPC for &7" + event.getPlayer().getName());
    }
}
