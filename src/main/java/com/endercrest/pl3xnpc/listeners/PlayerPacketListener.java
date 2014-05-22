package com.endercrest.pl3xnpc.listeners;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.endercrest.pl3xnpc.Pl3xNPC;
import com.endercrest.pl3xnpc.SlotType;
import com.endercrest.pl3xnpc.npc.NPC;
import com.endercrest.pl3xnpc.npc.NPCManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * PacketListener
 */
public class PlayerPacketListener implements PacketListener {
    private Pl3xNPC plugin;

    public PlayerPacketListener(Pl3xNPC plugin){
        this.plugin = plugin;
    }
    @Override
    public void onPacketReceive(PacketReceiveEvent event){
        CommonPacket packet = event.getPacket();
        Player player = event.getPlayer();
        if(packet.getType() != PacketType.IN_USE_ENTITY)
            return;
        UseAction type = packet.read(PacketType.IN_USE_ENTITY.useAction);
        if(type == null)
            return;
        int eid = packet.read(PacketType.IN_USE_ENTITY.clickedEntityId);
        NPC npc = null;
        for(NPC s : NPCManager.getNPCList()) {
            if(s.getEntityId() == eid) {
                npc = s;
                break;
            }
        }
        if(npc == null)
            return;
        ItemStack item = player.getItemInHand();
        Material mat = item.getType();
        if (mat == null || mat.equals(Material.AIR)) {
            if (!player.hasPermission("pl3xnpc.select"))
                return;
            NPC selected = NPCManager.getSelected(player.getName());
            if(selected != null && selected == npc) {
                return;
            }
            NPCManager.setSelected(player.getName(), npc);
            player.sendMessage(Pl3xNPC.colorize("&dSelected NPC."));
            player.sendMessage(Pl3xNPC.colorize("&d- ID: &7" + npc.getId()));
            player.sendMessage(Pl3xNPC.colorize("&d- Name: &7" + npc.getName()));
            player.sendMessage(Pl3xNPC.colorize("&d- Owner: &7" + npc.getOwner()));
            if (plugin.getConfig().getBoolean("debug-mode"))
                plugin.log("&7" + player.getName() + "&dselected NPC &7" + npc.getId());
            if (plugin.getConfig().getBoolean("animate-on-select", true))
                for (Player p : npc.getLocation().getWorld().getPlayers())
                    npc.animateArmSwing(p);
            if (plugin.getConfig().getBoolean("sound-on-select", true))
                npc.soundSelect(player);
            return;
        }
        if (!player.hasPermission("pl3xnpc.set.item"))
            return;
        if (NPCManager.getSelected(player.getName()) != npc)
            return;
        if (!NPCManager.selectedAllowed(player, false))
            return;
        SlotType sType;
        switch (mat) {
            case GOLD_BOOTS:
            case DIAMOND_BOOTS:
            case IRON_BOOTS:
            case CHAINMAIL_BOOTS:
            case LEATHER_BOOTS:
                sType = SlotType.BOOTS;
                break;
            case GOLD_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case LEATHER_LEGGINGS:
                sType = SlotType.LEGGINGS;
                break;
            case GOLD_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case IRON_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case LEATHER_CHESTPLATE:
                sType = SlotType.CHESTPLATE;
                break;
            case GOLD_HELMET:
            case DIAMOND_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case LEATHER_HELMET:
                sType = SlotType.HELMET;
                break;
            default:
                sType = SlotType.IN_HAND;
        }
        NPCManager.setItem(plugin, item, player, npc, false, sType, true);
    }

    @Override
    public void onPacketSend(PacketSendEvent event){
        //Do Nothing
    }
}
