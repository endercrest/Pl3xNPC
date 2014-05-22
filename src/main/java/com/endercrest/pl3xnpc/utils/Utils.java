package com.endercrest.pl3xnpc.utils;

import com.endercrest.pl3xnpc.Pl3xNPC;
import com.endercrest.pl3xnpc.SlotType;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.kitteh.vanish.VanishPlugin;

/**
 * Created by Thomas on 4/25/2014.
 */
public class Utils {
    private static VanishPlugin vnp = null;
    private static WorldGuardPlugin wg = null;

    public static Boolean isLeatherArmor(ItemStack item){
        Material mat = item.getType();
        switch (mat) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return true;
            default:
        }
        return false;
    }

    public static void setLeatherColor(ItemStack item, Integer rgb){
        if (!isLeatherArmor(item))
            return;
        LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
        Color color = Color.fromRGB(rgb);
        lam.setColor(color);
        item.setItemMeta(lam);
    }

    public static Color getLeatherColor(ItemStack item){
        if (!isLeatherArmor(item))
            return null;
        LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
        return lam.getColor();
    }

    public static boolean isValidArmor(Pl3xNPC plugin, SlotType type, Material mat){
        if (mat == null || mat.equals(Material.AIR))
            return true;
        switch (type) {
            case HELMET:
                switch (mat) {
                    case GOLD_HELMET:
                    case DIAMOND_HELMET:
                    case IRON_HELMET:
                    case CHAINMAIL_HELMET:
                    case LEATHER_HELMET:
                        return true;
                    default:
                        if (plugin.getConfig().getBoolean("any-item-on-head", false))
                            return true;
                }
                break;
            case CHESTPLATE:
                switch (mat) {
                    case GOLD_CHESTPLATE:
                    case DIAMOND_CHESTPLATE:
                    case IRON_CHESTPLATE:
                    case CHAINMAIL_CHESTPLATE:
                    case LEATHER_CHESTPLATE:
                        return true;
                    default:
                }
                break;
            case LEGGINGS:
                switch (mat) {
                    case GOLD_LEGGINGS:
                    case DIAMOND_LEGGINGS:
                    case IRON_LEGGINGS:
                    case CHAINMAIL_LEGGINGS:
                    case LEATHER_LEGGINGS:
                        return true;
                    default:
                }
                break;
            case BOOTS:
                switch (mat) {
                    case GOLD_BOOTS:
                    case DIAMOND_BOOTS:
                    case IRON_BOOTS:
                    case CHAINMAIL_BOOTS:
                    case LEATHER_BOOTS:
                        return true;
                    default:
                }
            case IN_HAND:
                return true;
            default:
                break;
        }
        return false;
    }

    public static Integer getLimit(Player p){
        Integer limit = 0;
        Integer maxlimit = 255;
        if(p.hasPermission("pl3xnpc.limit.*"))
            return -1;
        for(int i = 0; i < maxlimit; i++)
            if(p.hasPermission("pl3xnpc.limit." + i))
                if (i > limit)
                    limit = i;
        return limit;
    }

    public static boolean isVanished(Pl3xNPC plugin, Player p){
        if (!plugin.allowVNP)
            return false;
        if (vnp == null)
            vnp = (VanishPlugin) plugin.getServer().getPluginManager().getPlugin("VanishNoPacket");
        return vnp.getManager().isVanished(p);
    }

    public static boolean canBuild(Pl3xNPC plugin, Player p, Location loc){
        if (!plugin.allowWG)
            return true;
        if (p.hasPermission("pl3xnpc.create.wgoverride"))
            return true;
        if (wg == null)
            wg = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        return wg.canBuild(p, loc);
    }

}
