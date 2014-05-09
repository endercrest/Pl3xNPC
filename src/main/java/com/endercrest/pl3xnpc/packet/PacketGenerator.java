package com.endercrest.pl3xnpc.packet;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.endercrest.pl3xnpc.Pl3xNPC;
import com.endercrest.pl3xnpc.npc.MobType;
import com.endercrest.pl3xnpc.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Thomas on 4/25/2014.
 */
public class PacketGenerator {
    private NPC npc;

    public PacketGenerator(NPC npc){
        this.npc = npc;
    }

    /**
     * Get the MobSpawnPacket
     * @return CommonPacket
     */
    public CommonPacket getMobSpawnPacket(){
        Location loc = npc.getLocation();
        int EntityId = npc.getEntityId();
        EntityType type = MobType.toEntityType(npc.getMob());
        int typeId = type.getTypeId();
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_SPAWN_LIVING);
        int x = MathUtil.floor(loc.getX() * 32D);
        int y = MathUtil.floor(loc.getY() * 32D);
        int z = MathUtil.floor(loc.getZ() * 32D);
        byte yaw = this.getByteFromDegree(loc.getYaw());
        byte pitch = this.getByteFromDegree(loc.getPitch());
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.entityId, EntityId);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.entityType, typeId);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.x, x);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.y, y);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.z, z);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.motX, 0);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.motY, 0);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.motZ, 0);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.yaw, yaw);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.pitch, pitch);
        packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.headYaw, yaw);
        packet.setDatawatcher(npc.getDataWatcher());
        return packet;
    }

    /**
     * Get Player SpawnPacket
     * @return CommonPacket
     */
    public CommonPacket getPlayerSpawnPacket(){
        Location loc = npc.getLocation();
        int EntityId = npc.getEntityId();
        String name = Pl3xNPC.colorize(npc.getName());
        int itemInHand = 0;
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_SPAWN_NAMED);
        int x = MathUtil.floor(loc.getX() * 32D);
        int y = MathUtil.floor(loc.getY() * 32D);
        int z = MathUtil.floor(loc.getZ() * 32D);
        byte yaw = this.getByteFromDegree(loc.getYaw());
        byte pitch = this.getByteFromDegree(loc.getPitch());
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.entityId, EntityId);
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.profile, name);
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.x, x);
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.y, y);
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.z, z);
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.yaw, yaw);
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.pitch, pitch);
        packet.write(PacketType.OUT_ENTITY_SPAWN_NAMED.heldItemId, itemInHand);
        packet.setDatawatcher(npc.getDataWatcher());
        return packet;
    }

    /**
     * Get MetaDate Packet
     * @return CommonPacket
     */
    public CommonPacket getMetadataPacket(){
        int EntityId = npc.getEntityId();
        DataWatcher datawatcher = npc.getDataWatcher();
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_METADATA);
        packet.write(PacketType.OUT_ENTITY_METADATA.entityId, EntityId);
        packet.write(PacketType.OUT_ENTITY_METADATA.watchedObjects, datawatcher.getAllWatched());
        return packet;
    }

    /**
     * Get Despawn Packet
     * @return CommonPacket
     */
    public CommonPacket getDespawnPacket(){
        int[] EntityId = new int[] {npc.getEntityId()};
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_DESTROY);
        packet.write(PacketType.OUT_ENTITY_DESTROY.entityIds, EntityId);
        return packet;
    }

    /**
     * Get Body Rotation Packet
     * @return CommonPacket
     */
    public CommonPacket getBodyRotationPacket(){
        int EntityId = npc.getEntityId();
        Float y = npc.getYaw();
        Float p = npc.getPitch();
        if (y == null)
            y = 0F;
        if (p == null)
            p = 0F;
        byte yaw = this.getByteFromDegree(y);
        byte pitch = this.getByteFromDegree(p);
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_LOOK);
        packet.write(PacketType.OUT_ENTITY_LOOK.entityId, EntityId);
        packet.write(PacketType.OUT_ENTITY_LOOK.yaw, yaw);
        packet.write(PacketType.OUT_ENTITY_LOOK.pitch, pitch);
        return packet;
    }

    /**
     * Get Head Rotation Packet
     * @return Commonpacket
     */
    public CommonPacket getHeadRotationPacket(){
        int EntityId = npc.getEntityId();
        Float y = npc.getHeadYaw();
        if (y == null)
            y = 0F;
        byte yaw = this.getByteFromDegree(y);
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_HEAD_ROTATION);
        packet.write(PacketType.OUT_ENTITY_HEAD_ROTATION.entityId, EntityId);
        packet.write(PacketType.OUT_ENTITY_HEAD_ROTATION.headYaw, yaw);
        return packet;
    }

    /**
     * Get Equipment Change Packet
     * @param item The Item
     * @param slot The Slot
     * @return CommonPacket
     */
    public CommonPacket getEquipmentChangePacket(ItemStack item, Integer slot){
        int EntityId = npc.getEntityId();
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_EQUIPMENT);
        packet.write(PacketType.OUT_ENTITY_EQUIPMENT.entityId, EntityId);
        packet.write(PacketType.OUT_ENTITY_EQUIPMENT.item, item);
        packet.write(PacketType.OUT_ENTITY_EQUIPMENT.slot, slot);
        return packet;
    }

    /**
     * Get Arm Animation Packet
     * @param animation The Animation
     * @return CommonPacket
     */
    public CommonPacket getArmAnimationPacket(Integer animation){
        int EntityId = npc.getEntityId();
        CommonPacket packet = new CommonPacket(PacketType.OUT_ENTITY_ANIMATION);
        packet.write(PacketType.OUT_ENTITY_ANIMATION.entityId, EntityId);
        packet.write(PacketType.OUT_ENTITY_ANIMATION.animation, animation);
        return packet;
    }

    public byte getByteFromDegree(float degree){
        return (byte) (int)(degree * 256.0F / 360.0F);
    }
}
