package com.endercrest.pl3xnpc.utils;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.endercrest.pl3xnpc.Pl3xNPC;
import com.endercrest.pl3xnpc.listeners.PlayerPacketListener;

/**
 * The Packet Handler
 */
public class PacketHandler {
    public PacketHandler(Pl3xNPC plugin){
        PacketUtil.addPacketListener(plugin, new PlayerPacketListener(plugin), PacketType.IN_USE_ENTITY);
    }
}
