package com.endercrest.pl3xnpc.npc;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.endercrest.pl3xnpc.SlotType;
import com.endercrest.pl3xnpc.packet.PacketGenerator;
import com.endercrest.pl3xnpc.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates NPC
 */
public class NPC implements InventoryHolder {
    private Integer id;
    private MobType mob;
    private Location loc;
    private Integer entityID;
    private String name;
    private Float yaw;
    private Float pitch;
    private Float headyaw;
    private String owner;
    private Double lookAtRadius;
    private Location faceLocation;
    private Double messageRadius;
    private String message = "";
    private Boolean showMobName = true;
    private Inventory inventory;
    private PacketGenerator packetGenerator;
    private DataWatcher datawatcher;

    public NPC(Integer id, Integer entityID, Location loc, String name, Player player){
        this.id = id;
        this.loc = loc;
        if (name.length() > 16)
            name = name.substring(0, 15);
        this.name = name;
        this.entityID = entityID;
        if (player != null) {
            this.yaw = ReflectionUtil.getYaw(player);
            this.pitch = ReflectionUtil.getPitch(player);
            this.headyaw = ReflectionUtil.getHeadYaw(player);
            this.owner = player.getName();
        }
        this.inventory = Bukkit.getServer().createInventory(this, 9, "NPC Inventory");
        this.packetGenerator = new PacketGenerator(this);
        this.createDefaultDatawatcher();
    }

    /**
     * Get ID
     * @return id
     */
    public int getId(){
        return id;
    }

    /**
     * Get Entity id
     * @return entityID
     */
    public int getEntityId(){
        return entityID;
    }

    /**
     * Get Location
     * @return loc
     */
    public Location getLocation(){
        return loc;
    }

    /**
     * Set The Mob Type
     * @param mob The MobType
     */
    public void setMob(MobType mob){
        this.mob = mob;
        createDefaultDatawatcher();
        if (mob != null) {
            if (showMobName) {
                this.getDataWatcher().set(10, name);
                this.getDataWatcher().set(11, (byte) 1);
            } else {
                this.getDataWatcher().set(10, "");
                this.getDataWatcher().set(11, (byte) 0);
            }
        }
    }

    /**
     * Get the mob type
     * @return mob
     */
    public MobType getMob(){
        return mob;
    }

    /**
     * Get Sound Select
     * @return sound
     */
    public Sound getSoundSelect(){
        if (mob != null)
            return getMob().getSoundSelect();
        return null;
    }

    /**
     * Toggle to show MobName
     * @param flag true/false
     */
    public void showMobName(Boolean flag){
        showMobName = flag;
        setMob(mob);
    }

    /**
     * Set the name of the NPC
     * @param name The name
     */
    public void setName(String name){
        if (name.length() > 16)
            name = name.substring(0, 15);
        this.name = name;
        if (mob != null)
            setMob(mob);
        despawn();
        spawn(loc.getWorld());
    }

    /**
     * Get the name of the NPC
     * @return name
     */
    public String getName(){
        return name;
    }

    /**
     * Set the owner of the npc
     * @param owner The Player
     */
    public void setOwner(String owner){
        this.owner = owner;
    }

    /**
     * Get the owner of the npc
     * @return owner
     */
    public String getOwner(){
        return owner;
    }

    /**
     * Set the lookat radius
     * @param radius The Radius
     */
    public void setLookAtRadius(Double radius){
        lookAtRadius = radius;
    }

    /**
     * Get the lookat radius
     * @param radius The Radius
     * @return radius
     */
    public Double getLookAtRadius(Double radius){
        if (radius < 1 || radius > 50)
            radius = 10D;
        return (lookAtRadius != null) ? lookAtRadius : radius;
    }

    /**
     * Get the face location
     * @return faceLocation
     */
    public Location getFaceLocation(){
        return faceLocation;
    }

    /**
     * Set the face Location
     * @param loc The location
     */
    public void setFaceLocation(Location loc){
        faceLocation = loc;
    }

    /**
     * Set the message radius
     * @param radius The Radius
     */
    public void setMsgRadius(Double radius){
        messageRadius = radius;
    }

    /**
     * Get the message radius
     * @param radius The Radius
     * @return radius
     */
    public Double getMsgRadius(Double radius){
        if (radius < 1 || radius > 50)
            radius = 5D;
        return (messageRadius != null) ? messageRadius : radius;
    }

    /**
     * Set the message of the NPC
     * @param msg The Message
     */
    public void setMsg(String msg){
        message = msg;
    }

    /**
     * Get the Message
     * @return message
     */
    public String getMsg(){
        return message;
    }

    /**
     * Get the data watcher
     * @return dataWatcher
     */
    public DataWatcher getDataWatcher(){
        return datawatcher;
    }

    /**
     * Get the yaw of the NPC
     * @return yaw
     */
    public Float getYaw(){
        return yaw;
    }

    /**
     * Get the pitch of the NPC
     * @return pitch
     */
    public Float getPitch(){
        return pitch;
    }

    /**
     * Get the headyaw of the NPC
     * @return headyaw
     */
    public Float getHeadYaw(){
        return headyaw;
    }

    /**
     * Get the item(Armor or Item)
     * @param type The SlotType
     * @return ItemStack
     */
    public ItemStack getItem(SlotType type){
        return inventory.getItem(type.getId());
    }

    /**
     * Set the itme(Armor or Item)
     * @param stack The item
     * @param type the slot type
     * @return ItemStack
     */
    public ItemStack setItem(ItemStack stack, SlotType type){
        ItemStack item = stack.clone();
        item.setAmount(1);
        ItemStack oldItem = inventory.getItem(type.getId());
        inventory.setItem(type.getId(), item);
        showItems();
        return oldItem;
    }

    /**
     * Get the inventory
     * @return inventory
     */
    @Override
    public Inventory getInventory(){
        return inventory;
    }

    /**
     * Look at location
     * @param point The Location
     */
    public void lookAt(Location point){
        Location npcLoc = getEyeLocation();

        double xDiff = point.getX() - npcLoc.getX();
        double yDiff = point.getY() - npcLoc.getY();
        double zDiff = point.getZ() - npcLoc.getZ();

        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
        double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
        if (zDiff < 0.0)
            newYaw = newYaw + Math.abs(180 - newYaw) * 2;

        newYaw = (newYaw - 90);
        yaw = (float) newYaw;
        pitch = (float) newPitch;
        headyaw = (float) newYaw;

        updatePosition();
    }

    /**
     * Get the eye location
     * @return location
     */
    public Location getEyeLocation(){
        Location l = getLocation().clone();
        l.setY(l.getY() + getHeadHeight());
        return l;
    }

    /**
     * Get the head height
     * @return float
     */
    public Float getHeadHeight(){
        Float height = (float) 1.62D;
        if (mob != null)
            height = mob.getLength();
        return height * 0.85F;
    }

    /**
     * The soundSelect
     * @param player The Player
     */
    public void soundSelect(Player player){
        Sound sound = getSoundSelect();
        if (sound != null)
            loc.getWorld().playSound(loc, sound, 1.0F, 1.0F);
    }

    /**
     * Get The PacketGenerator
     * @return packetGenerator
     */
    public PacketGenerator getPacketGenerator(){
        return packetGenerator;
    }

    /**
     * Create Default DataWatcher
     */
    private void createDefaultDatawatcher(){
        datawatcher = new DataWatcher();
        datawatcher.set(0, (byte) 0);
        if(mob != null) {
            if (mob == MobType.Zombie)
                datawatcher.set(12, (byte) 0);
            if (mob == MobType.PigZombie)
                datawatcher.set(12, (byte) 0);
            if (mob == MobType.WitherSkeleton)
                datawatcher.set(13, (byte) 1);
            if (mob == MobType.Horse) {
                datawatcher.set(16, (int) 	0);
            }
            if (mob == MobType.Donkey) {
                datawatcher.set(16, 0);
                datawatcher.set(19, (byte) 1);
            }
            if (mob == MobType.Mule) {
                datawatcher.set(16, 0);
                datawatcher.set(19, (byte) 2);
            }
            if (mob == MobType.SkeletonHorse) {
                datawatcher.set(16, 0);
                datawatcher.set(19, (byte) 4);
            }
            if (mob == MobType.ZombieHorse) {
                datawatcher.set(16, 0);
                datawatcher.set(19, (byte) 3);
            }
        } else
            datawatcher.set(12, (int) 0);
    }

    /**
     * Spawn the NPC
     * @param world The World
     */
    public void spawn(World world){
        CommonPacket packet = getSpawnPacket();
        for(Player player : world.getPlayers())
            PacketUtil.sendPacket(player, packet, true);
        updatePosition();
        showItems();
    }

    /**
     * Spawn the NPC
     * @param player The Player
     */
    public void spawn(Player player){
        CommonPacket packet = getSpawnPacket();
        PacketUtil.sendPacket(player, packet, true);
        updatePosition(player);
        showItems();
    }

    /**
     * Despawn the seleted NPC
     */
    public void despawn(){
        CommonPacket packet = packetGenerator.getDespawnPacket();
        for(Player player : loc.getWorld().getPlayers())
            PacketUtil.sendPacket(player, packet, false);
    }

    /**
     * Despawn the selected NPC
     * @param player The Player
     */
    public void despawn(Player player){
        CommonPacket packet = packetGenerator.getDespawnPacket();
        PacketUtil.sendPacket(player, packet, false);
    }

    /**
     * Update the DataWatcher
     */
    public void updateDataWatcher(){
        CommonPacket packet = packetGenerator.getMetadataPacket();
        for(Player player : loc.getWorld().getPlayers())
            PacketUtil.sendPacket(player, packet, true);
    }

    /**
     * Update The DataWatcher
     * @param player The Player
     */
    public void updateDataWatcher(Player player){
        CommonPacket packet = packetGenerator.getMetadataPacket();
        PacketUtil.sendPacket(player, packet, true);
    }

    /**
     * Animate the Arm swing on the NPC
     * @param player The Player
     */
    public void animateArmSwing(Player player){
        CommonPacket packet = packetGenerator.getArmAnimationPacket(1);
        PacketUtil.sendPacket(player, packet, true);
    }

    /**
     * Update the position
     */
    public void updatePosition(){
        CommonUtil.nextTick(new RotationFix(loc.getWorld(), this));
    }

    /**
     * Update the position
     * @param player The Player
     */
    public void updatePosition(Player player){
        CommonUtil.nextTick(new RotationFix(player, this));
    }

    /**
     * Show the items on the NPC
     */
    public void showItems(){
        for (SlotType type : SlotType.values()) {
            CommonPacket packet = packetGenerator.getEquipmentChangePacket(inventory.getItem(type.getId()), type.getId());
            for (Player p : getLocation().getWorld().getPlayers()) {
                PacketUtil.sendPacket(p, packet, true);
            }
        }
    }

    /**
     * Get the spawn packet
     * @return CommonPacket
     */
    private CommonPacket getSpawnPacket(){
        if(mob != null)
            return packetGenerator.getMobSpawnPacket();
        else
            return packetGenerator.getPlayerSpawnPacket();
    }

    private static class RotationFix implements Runnable{
        private PacketGenerator gen;
        private List<Player> players = new ArrayList<Player>();
        public RotationFix(Player player, NPC npc) {
            players.add(player);
            gen = npc.getPacketGenerator();
        }
        public RotationFix(World world, NPC npc) {
            players = world.getPlayers();
            gen = npc.getPacketGenerator();
        }
        @Override
        public void run() {
            CommonPacket bodyPacket = gen.getBodyRotationPacket();
            CommonPacket headPacket = gen.getHeadRotationPacket();
            for(Player player : players) {
                PacketUtil.sendPacket(player, bodyPacket);
                PacketUtil.sendPacket(player, headPacket);
            }
        }
    }
}
