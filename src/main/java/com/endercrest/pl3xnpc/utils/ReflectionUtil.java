package com.endercrest.pl3xnpc.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import org.bukkit.entity.Player;

/**
 * Created by Thomas on 4/25/2014.
 */
public class ReflectionUtil {
    private static ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("EntityPlayer");
    private static FieldAccessor<Float> yaw = TEMPLATE.getField("yaw");
    private static FieldAccessor<Float> pitch = TEMPLATE.getField("pitch");
    private static FieldAccessor<Float> headyaw = TEMPLATE.getField("aO");

    /**
     * Get the Yaw
     * @param player The Player
     * @return yaw
     */
    public static float getYaw(Player player){
        Object e = Conversion.toEntityHandle.convert(player);
        return yaw.get(e);
    }

    /**
     * Get the pitch
     * @param player The Player
     * @return pitch
     */
    public static float getPitch(Player player){
        Object e = Conversion.toEntityHandle.convert(player);
        return pitch.get(e);
    }

    /**
     * Get the Head Yaw
     * @param player The Player
     * @return yaw
     */
    public static float getHeadYaw(Player player){
        Object e = Conversion.toEntityHandle.convert(player);
        return pitch.get(e);
    }
}
