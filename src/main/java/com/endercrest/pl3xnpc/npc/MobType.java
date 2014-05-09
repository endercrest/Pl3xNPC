package com.endercrest.pl3xnpc.npc;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

/**
 * Created by Thomas on 4/25/2014.
 */
public enum MobType {
    Bat(65, 0.9F, Sound.BAT_IDLE),
    Blaze(61, 1.8F, Sound.BLAZE_BREATH),
    CaveSpider(59, 0.5F, Sound.SPIDER_IDLE),
    Chicken(93, 0.7F, Sound.CHICKEN_IDLE),
    Cow(92, 1.3F, Sound.COW_IDLE),
    Creeper(50, 1.8F,Sound.CREEPER_HISS),
    EnderDragon(63, 8.0F, Sound.ENDERDRAGON_GROWL),
    Enderman(58, 2.9F, Sound.ENDERMAN_IDLE),
    Ghast(56, 4.0F, Sound.GHAST_MOAN),
    Giant(53, 10.799999F, Sound.ZOMBIE_IDLE),
    IronGolem(99, 2.9F, Sound.IRONGOLEM_WALK),
    MagmaCube(62, 0.6F, Sound.MAGMACUBE_JUMP),
    MushroomCow(96, 1.3F, Sound.COW_IDLE),
    Ocelot(98, 0.8F, Sound.CAT_PURREOW),
    Pig(90, 0.9F, Sound.PIG_IDLE),
    PigZombie(57, 1.8F, Sound.ZOMBIE_PIG_IDLE),
    Sheep(91, 1.3F, Sound.SHEEP_IDLE),
    Silverfish(60, 0.7F, Sound.SILVERFISH_IDLE),
    Skeleton(51, 1.8F, Sound.SKELETON_IDLE),
    Slime(55, 2.4F, Sound.SLIME_ATTACK),
    Snowman(97, 1.8F, Sound.DIG_SNOW),
    Spider(52, 0.9F, Sound.SPIDER_IDLE),
    Squid(94, 0.95F, Sound.SPLASH2),
    Villager(120, 1.8F, Sound.HURT_FLESH),
    Witch(66, 1.8F, Sound.HURT_FLESH),
    Wither(64, 4.0F, Sound.WITHER_IDLE),
    WitherSkeleton(51, 1.8F, Sound.SKELETON_IDLE),
    Wolf(95, 0.8F, Sound.WOLF_HOWL),
    Zombie(54, 1.8F, Sound.ZOMBIE_IDLE),
    Horse(100, 1.8F, Sound.HORSE_BREATHE),
    Donkey(100, 1.8F, Sound.DONKEY_ANGRY),
    Mule(100, 1.8F, Sound.DONKEY_ANGRY),
    SkeletonHorse(100, 1.8F, Sound.HORSE_SKELETON_IDLE),
    ZombieHorse(100, 1.8F, Sound.HORSE_ZOMBIE_IDLE);

    private byte id;
    private float length;
    private Sound soundSelect;

    MobType(int id, float length, Sound select){
        this.id = (byte)id;
        this.length = length;
        this.soundSelect = select;
    }

    /**
     * Get the id
     * @return
     */
    public byte getId(){
        return id;
    }

    /**
     * Get the length
     * @return
     */
    public float getLength(){
        return length;
    }

    /**
     * Get the sound
     * @return
     */
    public Sound getSoundSelect(){
        return soundSelect;
    }

    public static MobType fromString(String text){
        for (MobType m : MobType.values())
            if (text.equalsIgnoreCase(m.name()))
                return m;
        return null;
    }

    public static EntityType toEntityType(String toParse){
        for(MobType type : MobType.values())
            if(type.toString().equalsIgnoreCase(toParse))
                return EntityType.fromId(type.id);
        return null;
    }

    public static EntityType toEntityType(MobType toParse){
        return toEntityType(toParse.toString());
    }
}
