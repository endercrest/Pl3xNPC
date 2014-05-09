package com.endercrest.pl3xnpc;

import com.bergerkiller.bukkit.common.Task;
import com.endercrest.pl3xnpc.npc.NPC;
import com.endercrest.pl3xnpc.npc.NPCManager;
import com.endercrest.pl3xnpc.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Does all rendering task
 */
public class RenderTask extends Task {

    private Pl3xNPC plugin;
    public static Map<Integer,List<String>> render = new HashMap<Integer, List<String>>();
    private Map<String, Boolean> talkto = new HashMap<String, Boolean>();
    private TreeMap<Double,String> lookat = new TreeMap<Double,String>();

    public RenderTask(Pl3xNPC plugin){
        super(plugin);
        this.plugin = plugin;
        run();
    }

    @Override
    public void run(){
        for(NPC npc: NPCManager.getNPCList()){
            checkShow(npc);
            checkLook(npc);
            checkTalk(npc);
        }
    }

    /**
     * Check if NPC is being shown
     * @param npc The NPC that is being checked
     */
    private void checkShow(NPC npc){
        List<String> list = new ArrayList<String>();
        if (render.containsKey(npc.getId()))
            list = render.get(npc.getId());
        List<String> remove = new ArrayList<String>();
        for (String s : list)
            if (plugin.getServer().getPlayer(s) == null)
                remove.add(s);
        for (String s : remove)
            list.remove(s);
        Location nLoc = npc.getLocation();
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            Location pLoc = player.getLocation();
            String name = player.getName();
            if (pLoc.getWorld() != nLoc.getWorld()) {
                if (list.contains(name))
                    list.remove(name);
                continue;
            }
            Double distance = pLoc.distance(nLoc);
            Integer maxDistance = Bukkit.getViewDistance() * 10;
            if(distance < maxDistance && !list.contains(name)) {
                npc.spawn(player);
                list.add(name);
            } else if (distance >= maxDistance && list.contains(name))
                list.remove(name);
            render.put(npc.getId(), list);
        }
    }

    /**
     * Checks if NPC is looking
     * @param npc The NPC that is being checked
     */
    private void checkLook(NPC npc){
        lookat.clear();
        Location face = npc.getFaceLocation();
        if (face != null) {
            npc.lookAt(face);
            return;
        }
        Location nLoc = npc.getLocation();
        for(Player player : nLoc.getWorld().getPlayers()) {
            if (plugin.allowVNP && Utils.isVanished(plugin, player))
                continue;
            Location pLoc = player.getLocation();
            Double distance = pLoc.distance(nLoc);
            Double defaultRadius = plugin.getConfig().getDouble("look-at-radius", 10D);
            if(distance > npc.getLookAtRadius(defaultRadius))
                continue;
            lookat.put(nLoc.distanceSquared(player.getEyeLocation()), player.getName());
        }
        if (lookat.size() > 0) {
            if (lookat.isEmpty())
                return;
            String name = lookat.get(lookat.firstKey());
            Player player = plugin.getServer().getPlayer(name);
            if (player == null)
                return;
            npc.lookAt(player.getEyeLocation());
        }
    }

    /**
     * Checks the player around
     * @param npc The NPC that is being checked
     */
    private void checkTalk(NPC npc){
        Location nLoc = npc.getLocation();
        for(Player player : nLoc.getWorld().getPlayers()) {
            if (plugin.allowVNP && Utils.isVanished(plugin, player))
                continue;
            String msg = npc.getMsg();
            if (msg == null || msg.equals(""))
                continue;
            Location pLoc = player.getLocation();
            Double distance = pLoc.distance(nLoc);
            Integer eid = npc.getEntityId();
            String pname = player.getName();
            Double defaultRadius = plugin.getConfig().getDouble("look-at-radius", 10D);
            if(distance > npc.getMsgRadius(defaultRadius)) {
                if (talkto.containsKey(eid + ";" + pname))
                    talkto.remove(eid + ";" + pname);
                continue;
            }
            if (talkto.containsKey(eid + ";" + pname))
                continue;
            talkto.put(eid + ";" + pname, true);
            npcSay(npc, player, msg);
        }
    }

    /**
     * NPC sends message to
     * @param npc The NPC
     * @param player The Player that the msg is being sent to
     * @param msg The msg
     */
    private void npcSay(NPC npc, Player player, String msg){
        String pname = player.getName();
        String pdispname = player.getDisplayName();
        String message = plugin.getConfig().getString("message-format");
        message = message.replaceAll("(?i)\\{message\\}", msg);
        message = message.replaceAll("(?i)\\{npc\\}", npc.getName());
        message = message.replaceAll("(?i)\\{name\\}", pname);
        message = message.replaceAll("(?i)\\{dispname\\}", (pdispname != null) ? pdispname : pname);
        message = message.replaceAll("(?i)\\{world\\}", npc.getLocation().getWorld().getName());
        message = message.replaceAll("(?i)\u00a7[a-f0-9k-or]", "");
        message = message.replaceAll("(?i)&([a-f0-9k-or])", "\u00a7$1");
        player.sendMessage(Pl3xNPC.colorize(message));
        plugin.getServer().getConsoleSender().sendMessage(Pl3xNPC.colorize(message));
    }
}
