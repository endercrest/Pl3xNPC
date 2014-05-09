package com.endercrest.pl3xnpc;

import com.endercrest.pl3xnpc.npc.MobType;
import com.endercrest.pl3xnpc.npc.NPC;
import com.endercrest.pl3xnpc.npc.NPCManager;
import com.endercrest.pl3xnpc.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * All Commands part of Pl3xNPC
 */
public class CmdNPC implements CommandExecutor {

    private Pl3xNPC plugin;

    public CmdNPC(Pl3xNPC plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("npc")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    return npcCreate(cs, args);
                } else if (args[0].equalsIgnoreCase("delete")) {
                    return npcDelete(cs, args);
                } else if (args[0].equalsIgnoreCase("set")) {
                    return npcSet(cs, args);
                } else if (args[0].equalsIgnoreCase("admin")) {
                    return npcAdmin(cs, args);
                } else {
                    return showHelp(cs);
                }
            }
            return showHelp(cs);
        }
        return false;
    }

    /**
     * Create the NPC
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean npcCreate(CommandSender cs, String[] args){
        if (isConsole(cs)) {
            return true;
        }
        Player p = (Player) cs;
        if (!hasPerm(p, "pl3xnpc.create")) {
            return true;
        }
        Integer limit = Utils.getLimit(p);
        Integer count = 0;
        for (com.endercrest.pl3xnpc.npc.NPC npc : NPCManager.getNPCList()) {
            if (npc instanceof com.endercrest.pl3xnpc.npc.NPC) {
                if (npc.getOwner().equals(p.getName())) {
                    count++;
                }
            }
        }
        if ((limit >= 0) && (limit <= count)) {
            cs.sendMessage(Pl3xNPC.colorize("&4You have reached your NPC limit!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&4Player has reached their NPC limit!");
            }
            return true;
        }
        if (!Utils.canBuild(plugin, p, p.getLocation())) {
            cs.sendMessage(Pl3xNPC.colorize("&4You can not create an NPC here!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&4Player has tried to create an NPC in a WorldGuard region they have no access to!");
            }
            return true;
        }
        String name = null;
        if (args.length > 1) {
            name = args[1];
        }
        com.endercrest.pl3xnpc.npc.NPC npc = NPCManager.spawnNPC(name, p, p.getLocation());
        plugin.getConfig().set("npcs." + npc.getId() + ".name", npc.getName());
        plugin.getConfig().set("npcs." + npc.getId() + ".owner", npc.getOwner());
        plugin.getConfig().set("npcs." + npc.getId() + ".world", npc.getLocation().getWorld().getName());
        plugin.getConfig().set("npcs." + npc.getId() + ".x", npc.getLocation().getX());
        plugin.getConfig().set("npcs." + npc.getId() + ".y", npc.getLocation().getY());
        plugin.getConfig().set("npcs." + npc.getId() + ".z", npc.getLocation().getZ());
        plugin.saveConfig();
        cs.sendMessage(Pl3xNPC.colorize("&dCreated NPC."));
        if (p.hasPermission("pl3xnpc.select")) {
            NPCManager.setSelected(p.getName(), npc);
            cs.sendMessage(Pl3xNPC.colorize("&dNPC has been auto selected."));
            if (plugin.getConfig().getBoolean("animate-on-select", true)) {
                for (Player player : npc.getLocation().getWorld().getPlayers()) {
                    npc.animateArmSwing(player);
                }
            }
            if (plugin.getConfig().getBoolean("sound-on-select", true)) {
                npc.soundSelect(p);
            }
        }
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dCreated NPC &7" + npc.getId() + "&d:&7" + npc.getName());
        }
        return true;
    }

    /**
     * Delete the NPC
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean npcDelete(CommandSender cs, String[] args){
        if (isConsole(cs)) {
            return true;
        }
        Player player = (Player) cs;
        if (!hasPerm(player, "pl3xnpc.delete")) {
            return true;
        }
        if (!NPCManager.selectedAllowed(player, false)) {
            return true;
        }
        com.endercrest.pl3xnpc.npc.NPC npc = NPCManager.getSelected(player.getName());
        NPCManager.despawnNPC(npc);
        plugin.getConfig().set("npcs." + npc.getId(), null);
        plugin.saveConfig();
        cs.sendMessage(Pl3xNPC.colorize("&dNPC has been deleted!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dDeleted NPC &7" + npc.getId());
        }
        NPCManager.setSelected(player.getName(), null);
        return true;
    }

    /**
     * Set the NPC
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean npcSet(CommandSender cs, String[] args){
        if (isConsole(cs)) {
            return true;
        }
        if (args.length < 2) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a subcommand for 'set':"));
            return showSetHelp(cs, false);
        }
        Player player = (Player) cs;
        if (!NPCManager.selectedAllowed(player, false)) {
            return true;
        }
        com.endercrest.pl3xnpc.npc.NPC npc = NPCManager.getSelected(player.getName());
        if (args[1].equalsIgnoreCase("face")) {
            return setFaceLocation(0, cs, args, npc);
        } else if (args[1].equalsIgnoreCase("item")) {
            return setItem(0, cs, args, npc);
        } else if (args[1].equalsIgnoreCase("lookat")) {
            return setLookAt(0, cs, args, npc);
        } else if (args[1].equalsIgnoreCase("message")) {
            return setMessage(0, cs, args, npc);
        } else if (args[1].equalsIgnoreCase("mobtype")) {
            return setMobType(0, cs, args, npc);
        } else if (args[1].equalsIgnoreCase("name")) {
            return setName(0, cs, args, npc);
        } else if (args[1].equalsIgnoreCase("owner")) {
            return setOwner(0, cs, args, npc);
        } else if (args[1].equalsIgnoreCase("showmobname")) {
            return showMobName(0, cs, args, npc);
        }
        cs.sendMessage(Pl3xNPC.colorize("&4Unknown subcommand for 'set':"));
        return showSetHelp(cs, false);
    }

    /**
     * The NPC is Admin
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean npcAdmin(CommandSender cs, String[] args){
        if (isConsole(cs)) {
            return true;
        }
        if (!hasPerm((Player) cs, "pl3xnpc.admin")) {
            return true;
        }
        if (args.length < 2) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a subcommand for 'admin':"));
            return showAdminHelp(cs);
        }
        if (args[1].equalsIgnoreCase("delete")) {
            return adminDelete(cs, args);
        } else if (args[1].equalsIgnoreCase("lookat")) {
            return adminLookAt(cs, args);
        } else if (args[1].equalsIgnoreCase("update-interval")) {
            return adminUpdateInterval(cs, args);
        } else if (args[1].equalsIgnoreCase("debug-mode")) {
            return adminDebugMode(cs, args);
        } else if (args[1].equalsIgnoreCase("color-logs")) {
            return adminColorLogs(cs, args);
        } else if (args[1].equalsIgnoreCase("message")) {
            return adminMessage(cs, args);
        } else if (args[1].equalsIgnoreCase("animate-selected")) {
            return adminAnimateSelected(cs, args);
        } else if (args[1].equalsIgnoreCase("sound-selected")) {
            return adminSoundSelected(cs, args);
        } else if (args[1].equalsIgnoreCase("head-items")) {
            return adminHeadItems(cs, args);
        } else if (args[1].equalsIgnoreCase("set")) {
            return adminSet(cs, args);
        } else if (args[1].equalsIgnoreCase("reload")) {
            return adminReload(cs);
        }
        cs.sendMessage(Pl3xNPC.colorize("&4Unknown subcommand for 'admin':"));
        return showAdminHelp(cs);
    }

    /**
     * Set the face location(Direction)
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean setFaceLocation(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (!hasPerm((Player) cs, "pl3xnpc.set.face")) {
            return true;
        }
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4You must specify value where to face!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set face [value]"));
            cs.sendMessage(Pl3xNPC.colorize("&4Valid values: here, closest_player"));
            return true;
        }
        Player p = (Player) cs;
        Integer id = npc.getId();
        if (args[2 + argOffset].equalsIgnoreCase("closest_player")) {
            npc.setFaceLocation(null);
            plugin.getConfig().set("npcs." + id + ".face", null);
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dNPC will now look at the closest player within range."));
            return true;
        } else if (args[2 + argOffset].equalsIgnoreCase("here")) {
            Location loc = p.getEyeLocation();
            npc.setFaceLocation(loc);
            plugin.getConfig().set("npcs." + id + ".face.x", loc.getX());
            plugin.getConfig().set("npcs." + id + ".face.y", loc.getY());
            plugin.getConfig().set("npcs." + id + ".face.z", loc.getZ());
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dNPC will now always face this direction."));
            return true;
        }
        cs.sendMessage(Pl3xNPC.colorize("&4Invalid value specified!"));
        cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set face [value]"));
        cs.sendMessage(Pl3xNPC.colorize("&4Valid values: here, closest_player"));
        return true;
    }

    /**
     * Set the Item of the NPC
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean setItem(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (!hasPerm((Player) cs, "pl3xnpc.set.item")) {
            return true;
        }
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4You must specify which type to set!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set item [type] [value]"));
            cs.sendMessage(Pl3xNPC.colorize("&4Valid item types: helmet, chestplate, leggings, boots, in_hand"));
            return true;
        }
        if (args.length < 4 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4You must specify a value to set!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set item [type] [value]"));
            return true;
        }
        Player p = (Player) cs;
        ItemStack item = getItem(getFinalArg(args, 3));
        if (item == null) {
            if (!args[3].equalsIgnoreCase("none") && !args[3].equalsIgnoreCase("empty")) {
                cs.sendMessage(Pl3xNPC.colorize("&4Unknown item specified!"));
                return true;
            }
            item = new ItemStack(Material.AIR);
        }
        Material mat = item.getType();
        Boolean canSpawn = false;
        if (!mat.equals(Material.AIR) && NPCManager.inventoryHasItem(p.getInventory(), item, false) < 0) {
            if (!cs.hasPermission("pl3xnpc.set.item.canspawn")) {
                cs.sendMessage(Pl3xNPC.colorize("&4You do not have this item to give!"));
                return true;
            }
            canSpawn = true;
        }
        SlotType type = null;
        try {
            type = SlotType.valueOf(args[2 + argOffset].toUpperCase(java.util.Locale.ENGLISH));
        } catch (Exception e) {
            cs.sendMessage(Pl3xNPC.colorize("&4Unknown type specified!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set item [type] [value]"));
            cs.sendMessage(Pl3xNPC.colorize("&4Valid item types: helmet, chestplate, leggings, boots, in_hand"));
            return true;
        }
        NPCManager.setItem(plugin, item, p, npc, canSpawn, type, false);
        return true;
    }

    /**
     * Set the message the NPC says
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean setMessage(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4You must specify what to set!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set message [clear/radius/say] [value]"));
            return true;
        }
        Integer id = npc.getId();
        if (args[2 + argOffset].equalsIgnoreCase("clear")) {
            if (!hasPerm((Player) cs, "pl3xnpc.set.message.clear")) {
                return true;
            }
            plugin.getConfig().set("npcs." + id + ".message.radius", null);
            plugin.getConfig().set("npcs." + id + ".message.say", null);
            plugin.saveConfig();
            npc.setMsgRadius(null);
            npc.setMsg(null);
            cs.sendMessage(Pl3xNPC.colorize("&dMessage say and radius have been cleared!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dNPC cleared message radius and say values &7" + id);
            }
            return true;
        } else if (args[2 + argOffset].equalsIgnoreCase("radius")) {
            if (args.length < 4 + argOffset) {
                cs.sendMessage(Pl3xNPC.colorize("&4You must specify a value to set!"));
                cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set message radius [value]"));
                return true;
            }
            if (!hasPerm((Player) cs, "pl3xnpc.set.message.radius")) {
                return true;
            }
            Double radius;
            try {
                radius = Double.valueOf(args[3 + argOffset]);
            } catch (NumberFormatException e) {
                cs.sendMessage(Pl3xNPC.colorize("&4Please enter a valid number!"));
                return true;
            }
            if ((radius < 1) || (radius > 50)) {
                cs.sendMessage(Pl3xNPC.colorize("&4Value must be between 1 and 50!"));
                return true;
            }
            plugin.getConfig().set("npcs." + id + ".message.radius", radius);
            plugin.saveConfig();
            npc.setMsgRadius(radius);
            cs.sendMessage(Pl3xNPC.colorize("&dNew message aware radius set."));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dNPC changed message aware radius &7" + id);
            }
            return true;
        } else if (args[2 + argOffset].equalsIgnoreCase("say")) {
            if (args.length < 4 + argOffset) {
                cs.sendMessage(Pl3xNPC.colorize("&4You must specify a value to set!"));
                cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set message say [value]"));
                return true;
            }
            if (!hasPerm((Player) cs, "pl3xnpc.set.message.say")) {
                return true;
            }
            String message = getFinalArg(args, 3 + argOffset);
            plugin.getConfig().set("npcs." + id + ".message.say", message);
            plugin.saveConfig();
            npc.setMsg(message);
            cs.sendMessage(Pl3xNPC.colorize("&dNew message set."));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dNPC changed message &7" + id);
            }
            return true;
        }
        cs.sendMessage(Pl3xNPC.colorize("&4Unknown subcommand specified!"));
        cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set message [clear/radius/say] [value]"));
        return true;
    }

    /**
     * Set the mob type of the NPC
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean setMobType(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (!hasPerm((Player) cs, "pl3xnpc.set.mobtype")) {
            return true;
        }
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a mobtype to set!"));
            return true;
        }
        String mobStr = args[2 + argOffset];
        MobType mobType = null;
        if (mobStr.equalsIgnoreCase("player") || mobStr.equalsIgnoreCase("none")) {
            mobStr = null;
        } else {
            if (MobType.toEntityType(mobStr) == null) {
                cs.sendMessage(Pl3xNPC.colorize("&4Unknown mob type."));
                return true;
            }
            mobType = MobType.fromString(mobStr);
        }
        Integer id = npc.getId();
        plugin.getConfig().set("npcs." + id + ".mobtype", mobStr);
        plugin.saveConfig();
        World world = npc.getLocation().getWorld();
        npc.setMob(mobType);
        npc.despawn();
        npc.spawn(world);
        cs.sendMessage(Pl3xNPC.colorize("&dNew mobtype set."));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dNPC changed mobtype &7" + id);
        }
        return true;
    }

    /**
     * Toggle to show the MobName
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean showMobName(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (!hasPerm((Player) cs, "pl3xnpc.set.mobname")) {
            return true;
        }
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
            return true;
        }
        Boolean bool;
        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("yes")) {
            bool = true;
        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("no")) {
            bool = false;
        } else {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid value!"));
            return true;
        }
        Integer id = npc.getId();
        plugin.getConfig().set("npcs." + id + ".showmobname", bool);
        plugin.saveConfig();
        World world = npc.getLocation().getWorld();
        npc.showMobName(bool);
        npc.despawn();
        npc.spawn(world);
        cs.sendMessage(Pl3xNPC.colorize("&dName for this NPC is now " + ((bool) ? "visible" : "hidden") + " for MobTypes."));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dNPC changed showmobname &7" + id);
        }
        return true;
    }

    /**
     * Set the status of the look at
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean setLookAt(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (!hasPerm((Player) cs, "pl3xnpc.set.lookat")) {
            return true;
        }
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4You must specify a value!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set lookat [value]"));
            return true;
        }
        Double radius;
        try {
            radius = Double.valueOf(args[2 + argOffset]);
        } catch (NumberFormatException e) {
            cs.sendMessage(Pl3xNPC.colorize("&4Please enter a valid number!"));
            return true;
        }
        if ((radius < 1) || (radius > 50)) {
            cs.sendMessage(Pl3xNPC.colorize("&4Value must be between 1 and 50!"));
            return true;
        }
        Integer id = npc.getId();
        plugin.getConfig().set("npcs." + id + ".look-at-radius", radius);
        plugin.saveConfig();
        npc.setLookAtRadius(radius);
        cs.sendMessage(Pl3xNPC.colorize("&dNew look-at aware radius set."));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dNPC changed look-at aware radius &7" + id);
        }
        return true;
    }

    /**
     * Set the owner of the NPC
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean setOwner(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (!hasPerm((Player) cs, "pl3xnpc.set.owner")) {
            return true;
        }
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4You must specify a new owner!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set owner [name]"));
            return true;
        }
        Integer id = npc.getId();
        String newowner = args[2 + argOffset];
        npc.setOwner(newowner);
        plugin.getConfig().set("npcs." + id + ".owner", newowner);
        plugin.saveConfig();
        cs.sendMessage(Pl3xNPC.colorize("&dNew owner has been set."));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log(Pl3xNPC.colorize("&dNPC changed owner &7" + id));
        }
        return true;
    }

    /**
     * Set The NPC Name
     * @param argOffset The Offset
     * @param cs The CommandSender
     * @param args The Arguments
     * @param npc The NPC
     * @return true/false
     */
    private Boolean setName(Integer argOffset, CommandSender cs, String[] args, NPC npc){
        if (!hasPerm((Player) cs, "pl3xnpc.set.name")) {
            return true;
        }
        if (args.length < 3 + argOffset) {
            cs.sendMessage(Pl3xNPC.colorize("&4You must specify a new name!"));
            cs.sendMessage(Pl3xNPC.colorize("&4/npc " + ((argOffset > 0) ? "admin " : "") + "set name [newname]"));
            return true;
        }
        Integer id = npc.getId();
        String newname = args[2 + argOffset];
        npc.setName(newname);
        plugin.getConfig().set("npcs." + id + ".name", newname);
        plugin.saveConfig();
        cs.sendMessage(Pl3xNPC.colorize("&dNew name has been set."));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dNPC renamed &7" + id);
        }
        return true;
    }

    /**
     * Admin Delete NPC
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminDelete(CommandSender cs, String[] args){
        if (args.length < 3) {
            Player player = (Player) cs;
            if (!NPCManager.selectedAllowed(player, true)) {
                cs.sendMessage(Pl3xNPC.colorize("&4You must select an NPC first or specify a subcommand for 'admin delete':"));
                cs.sendMessage(Pl3xNPC.colorize("&4Valid subcommands for &7/npc admin delete &4are:"));
                cs.sendMessage(Pl3xNPC.colorize("&4[id], [name], all"));
                return true;
            }
            com.endercrest.pl3xnpc.npc.NPC npc = NPCManager.getSelected(player.getName());
            Integer id = npc.getId();
            NPCManager.despawnNPC(npc);
            plugin.getConfig().set("npcs." + id, null);
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dDeleted the selected NPC!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dDeleted the selected NPC! " + id);
            }
            return true;
        }
        if (args[2].equalsIgnoreCase("all")) {
            plugin.getConfig().set("npcs", null);
            plugin.saveConfig();
            NPCManager.despawnAll();
            cs.sendMessage(Pl3xNPC.colorize("&dDeleted all NPCs!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dDeleted all NPCs!");
            }
            return true;
        }
        try {
            Integer id = Integer.valueOf(args[2]);
            com.endercrest.pl3xnpc.npc.NPC npc = NPCManager.getNPCbyID(id);
            if (npc == null) {
                cs.sendMessage(Pl3xNPC.colorize("&4Failed to delete the NPC!"));
                if (plugin.getConfig().getBoolean("debug-mode")) {
                    plugin.log("&4Failed to delete the NPC!");
                }
                return true;
            }
            NPCManager.despawnNPC(npc);
            plugin.getConfig().set("npcs." + id, null);
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dDeleted the NPC!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dDeleted NPC! " + id);
            }
        } catch (NumberFormatException e) {
            String name = args[2];
            Integer count = 0;
            for (com.endercrest.pl3xnpc.npc.NPC npc : NPCManager.getNPCList()) {
                if (npc instanceof com.endercrest.pl3xnpc.npc.NPC) {
                    if (npc.getName().equals(name)) {
                        count++;
                    }
                }
            }
            if (count > 1) {
                if ((args.length > 3) && (args[3].equalsIgnoreCase("-force"))) {
                    if (plugin.getConfig().getBoolean("debug-mode")) {
                        plugin.log("&dDeleting all NPCs with name: " + name);
                    }
                    for (com.endercrest.pl3xnpc.npc.NPC npc : NPCManager.getNPCList()) {
                        if (npc.getName().equalsIgnoreCase(name)) {
                            int id = npc.getId();
                            NPCManager.despawnNPC(npc);
                            plugin.getConfig().set("npcs." + id, null);
                            plugin.saveConfig();
                            if (plugin.getConfig().getBoolean("debug-mode")) {
                                plugin.log("&dDeleted NPC with ID: " + id);
                            }
                        }
                    }
                    cs.sendMessage(Pl3xNPC.colorize("&dAll NPCs deleted named &7" + name));
                    if (plugin.getConfig().getBoolean("debug-mode")) {
                        plugin.log("&dFinished.");
                    }
                    return true;
                }
                cs.sendMessage(Pl3xNPC.colorize("&4Multiple NPCs found by that name!"));
                cs.sendMessage(Pl3xNPC.colorize("&4Please specify by ID or use -force"));
                return true;
            }
            com.endercrest.pl3xnpc.npc.NPC npc = NPCManager.getNPCbyName(name);
            if (npc == null) {
                cs.sendMessage(Pl3xNPC.colorize("&4No NPCs found by that name!"));
                return true;
            }
            Integer id = npc.getId();
            NPCManager.despawnNPC(npc);
            plugin.getConfig().set("npcs." + id, null);
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dDeleted the NPC!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dDeleted NPC! " + name);
            }
        }
        return true;
    }

    /**
     * Toggle Lookat
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminLookAt(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a subcommand for 'admin lookat':"));
            return showAdminLookatHelp(cs);
        }
        if (args[2].equalsIgnoreCase("radius")) {
            if (args.length < 4) {
                cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
                cs.sendMessage(Pl3xNPC.colorize("&4/npc admin lookat radius [value]"));
                return true;
            }
            Double radius;
            try {
                radius = Double.valueOf(args[3]);
            } catch (NumberFormatException e) {
                cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid number!"));
                return true;
            }
            if (radius < 1 || radius > 50) {
                cs.sendMessage(Pl3xNPC.colorize("&4Must specify a radius between 1 and 50!"));
                return true;
            }
            plugin.getConfig().set("look-at-radius", radius);
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dDefault look-at aware radius is now set to &7" + radius + "&d!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dDefault look-at aware radius is now set to &7" + radius + "&d!");
            }
            return true;
        }
        cs.sendMessage(Pl3xNPC.colorize("&4Unknown subcommand for 'admin lookat':"));
        return showAdminLookatHelp(cs);
    }

    /**
     * Update the time interval
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminUpdateInterval(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
            return true;
        }
        Integer interval;
        try {
            interval = Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid number!"));
            return true;
        }
        if (interval < 1 || interval > 50) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a radius between 1 and 20!"));
            return true;
        }
        plugin.getConfig().set("update-interval", interval);
        plugin.saveConfig();
        plugin.restartRenderTask();
        cs.sendMessage(Pl3xNPC.colorize("&dThe update-interval is now set to &7" + interval + "&d!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dThe update-interval is now set to &7" + interval + "&d!");
        }
        return true;
    }

    /**
     * Toggle Admin Debug Mode
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminDebugMode(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
            return true;
        }
        Boolean bool;
        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("yes")) {
            bool = true;
        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("no")) {
            bool = false;
        } else {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid value!"));
            return true;
        }
        plugin.getConfig().set("debug-mode", bool);
        plugin.saveConfig();
        plugin.restartRenderTask();
        cs.sendMessage(Pl3xNPC.colorize("&dThe debug-mode is now set to &7" + bool + "&d!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dThe debug-mode is now set to &7" + bool + "&d!");
        }
        return true;
    }

    /**
     * Admin Colour Logs
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminColorLogs(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
            return true;
        }
        Boolean bool;
        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("yes")) {
            bool = true;
        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("no")) {
            bool = false;
        } else {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid value!"));
            return true;
        }
        plugin.getConfig().set("color-logs", bool);
        plugin.saveConfig();
        plugin.restartRenderTask();
        cs.sendMessage(Pl3xNPC.colorize("&dThe color-logs is now set to &7" + bool + "&d!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dThe color-logs is now set to &7" + bool + "&d!");
        }
        return true;
    }

    /**
     * Messages sent to admin
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminMessage(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a subcommand for 'admin message':"));
            return showAdminMessageHelp(cs);
        }
        if (args[2].equalsIgnoreCase("radius")) {
            if (args.length < 4) {
                cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
                cs.sendMessage(Pl3xNPC.colorize("&4/npc admin message radius [value]"));
                return true;
            }
            Double radius;
            try {
                radius = Double.valueOf(args[3]);
            } catch (NumberFormatException e) {
                cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid number!"));
                return true;
            }
            if (radius < 1 || radius > 50) {
                cs.sendMessage(Pl3xNPC.colorize("&4Must specify a radius between 1 and 50!"));
                return true;
            }
            plugin.getConfig().set("message-radius", radius);
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dDefault message aware radius is now set to &7" + radius + "&d!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dDefault message aware radius is now set to &7" + radius + "&d!");
            }
            return true;
        } else if (args[2].equalsIgnoreCase("format")) {
            if (args.length < 4) {
                cs.sendMessage(Pl3xNPC.colorize("&4Must specify a format!"));
                cs.sendMessage(Pl3xNPC.colorize("&4/npc admin message format [format]"));
                return true;
            }
            String format = getFinalArg(args, 3);
            plugin.getConfig().set("message-format", format);
            plugin.saveConfig();
            cs.sendMessage(Pl3xNPC.colorize("&dDefault message format is now set to &7" + format + "&d!"));
            if (plugin.getConfig().getBoolean("debug-mode")) {
                plugin.log("&dDefault message format is now set to &7" + format + "&d!");
            }
            return true;
        }
        cs.sendMessage(Pl3xNPC.colorize("&4Unknown subcommand for 'admin message':"));
        return showAdminMessageHelp(cs);
    }

    /**
     * If Animation is played on select
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminAnimateSelected(CommandSender cs, String[] args) {
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
            return true;
        }
        Boolean bool;
        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("yes")) {
            bool = true;
        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("no")) {
            bool = false;
        } else {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid value!"));
            return true;
        }
        plugin.getConfig().set("animate-on-select", bool);
        plugin.saveConfig();
        cs.sendMessage(Pl3xNPC.colorize("&6Animate Selected is now set to &7" + bool + "&d!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&6Animate Selected is now set to &7" + bool + "&d!");
        }
        return true;
    }

    /**
     * Set If sound is played
     * @param cs The CommandSender
     * @param args The Arguments
     * @return true/false
     */
    private Boolean adminSoundSelected(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
            return true;
        }
        Boolean bool;
        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("yes")) {
            bool = true;
        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("no")) {
            bool = false;
        } else {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid value!"));
            return true;
        }
        plugin.getConfig().set("sound-on-select", bool);
        plugin.saveConfig();
        cs.sendMessage(Pl3xNPC.colorize("&6Sound Selected is now set to &7" + bool + "&d!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&6Sound Selected is now set to &7" + bool + "&d!");
        }
        return true;
    }

    /**
     * Set Head Item
     * @param cs The CommandSender
     * @param args The Arguments
     * @return If Worked
     */
    private Boolean adminHeadItems(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a value!"));
            return true;
        }
        Boolean bool;
        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("yes")) {
            bool = true;
        } else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("no")) {
            bool = false;
        } else {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a valid value!"));
            return true;
        }
        plugin.getConfig().set("any-item-on-head", bool);
        plugin.saveConfig();
        cs.sendMessage(Pl3xNPC.colorize("&6Head Items is now set to &7" + bool + "&d!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&6Head Items is now set to &7" + bool + "&d!");
        }
        return true;
    }

    /**
     * Admin Set Command
     * @param cs The CommandSender
     * @param args The Arguments
     * @return if Set
     */
    private Boolean adminSet(CommandSender cs, String[] args){
        if (args.length < 3) {
            cs.sendMessage(Pl3xNPC.colorize("&4Must specify a subcommand for 'admin set':"));
            return showSetHelp(cs, true);
        }
        Player player = (Player) cs;
        if (!NPCManager.selectedAllowed(player, true)) {
            return true;
        }
        com.endercrest.pl3xnpc.npc.NPC npc = NPCManager.getSelected(player.getName());
        if (args[2].equalsIgnoreCase("face")) {
            return setFaceLocation(1, cs, args, npc);
        } else if (args[2].equalsIgnoreCase("item")) {
            return setItem(1, cs, args, npc);
        } else if (args[2].equalsIgnoreCase("lookat")) {
            return setLookAt(1, cs, args, npc);
        } else if (args[2].equalsIgnoreCase("message")) {
            return setMessage(1, cs, args, npc);
        } else if (args[2].equalsIgnoreCase("mobtype")) {
            return setMobType(1, cs, args, npc);
        } else if (args[2].equalsIgnoreCase("name")) {
            return setName(1, cs, args, npc);
        } else if (args[2].equalsIgnoreCase("owner")) {
            return setOwner(1, cs, args, npc);
        } else if (args[2].equalsIgnoreCase("showmobname")) {
            return showMobName(1, cs, args, npc);
        }
        cs.sendMessage(Pl3xNPC.colorize("&4Unknown subcommand for 'admin set':"));
        return showSetHelp(cs, true);
    }

    /**
     * Reload
     * @param cs The CommandSender
     * @return If ran
     */
    private Boolean adminReload(CommandSender cs){
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dDespawning all loaded NPCs!");
        }
        NPCManager.despawnAll();
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dReloading configuration data from file!");
        }
        plugin.reloadConfig();
        plugin.loadConfiguration();
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dRestarting the update task!");
        }
        plugin.restartRenderTask();
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dReloading all the NPCs!");
        }
        NPCManager.loadAll(plugin);
        cs.sendMessage(Pl3xNPC.colorize("&dAll data has been reloaded from config and all NPCs respawned!"));
        if (plugin.getConfig().getBoolean("debug-mode")) {
            plugin.log("&dFinished!");
        }
        return true;
    }

    /**
     * If Player has permissions
     * @param player The Player
     * @param node The Permission node
     * @return If player has node
     */
    private Boolean hasPerm(Player player, String node){
        if (!player.hasPermission(node)) {
            player.sendMessage(Pl3xNPC.colorize("&4You do not have permission to use this command!"));
            return false;
        }
        return true;
    }

    /**
     * If CommandSender Is Console
     * @param cs The CommandSender
     * @return If Console
     */
    private  Boolean isConsole(CommandSender cs){
        if (!(cs instanceof Player)) {
            cs.sendMessage(Pl3xNPC.colorize("&4This command is only available to players!"));
            return true;
        }
        return false;
    }

    /**
     * Get Final Argument
     * @param args The Arguments
     * @param start The Start Point
     * @return The String
     */
    private String getFinalArg(String[] args, int start){
        StringBuilder bldr = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                bldr.append(" ");
            }
            bldr.append(args[i]);
        }
        return bldr.toString();
    }

    /**
     * Get Item
     * @param name The Material(Item)
     * @return The ItemStack
     */
    public ItemStack getItem(String name){
        Short data;
        String datas = null;
        name = name.trim().toUpperCase().replace(" ", "_");
        if (name.contains(":")) {
            if (name.split(":").length < 2) {
                datas = null;
                name = name.split(":")[0];
            } else {
                datas = name.split(":")[1];
                name = name.split(":")[0];
            }
        }
        try {
            data = Short.valueOf(datas);
        } catch (Exception e) {
            data = null;
        }
        Material mat = Material.getMaterial(name);
        if (mat == null) {
            try {
                mat = Material.getMaterial(name);
                if (mat == null) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        ItemStack stack = new ItemStack(mat, 1);
        if (data != null) {
            stack.setDurability(data);
        }
        return stack;
    }

    /**
     * Show Help
     * @param cs The CommandSender
     * @return If Sent
     */
    private Boolean showHelp(CommandSender cs){
        cs.sendMessage(Pl3xNPC.colorize("&4Valid subcommands for &7/npc&4 are:"));
        cs.sendMessage(Pl3xNPC.colorize("&4create, delete, set, admin"));
        return true;
    }

    /**     * Show Set Help
     * @param cs The CommandSender
     * @param isAdmin If Admin
     * @return If Sent
     */
    private Boolean  showSetHelp(CommandSender cs, Boolean isAdmin){
        cs.sendMessage(Pl3xNPC.colorize("&4Valid subcommands for &7/npc " + (isAdmin ? "admin " : "") + "set &4are:"));
        cs.sendMessage(Pl3xNPC.colorize("&4face, item, lookat, message, mobtype, name, owner, showmobname"));
        return true;
    }

    /**
     * Show Admin Help
     * @param cs The CommandSender
     * @return If Sent
     */
    private Boolean showAdminHelp(CommandSender cs){
        cs.sendMessage(Pl3xNPC.colorize("&4Valid subcommands for &7/npc admin &4are:"));
        cs.sendMessage(Pl3xNPC.colorize("&4delete, lookat, update-interval, debug-mode, color-logs, message, animate-selected, sound-selected, head-items, set, reload"));
        return true;
    }

    /**
     * Show Admin Lookat Help
     * @param cs The CommandSender
     * @return If Sent
     */
    private Boolean showAdminLookatHelp(CommandSender cs){
        cs.sendMessage(Pl3xNPC.colorize("&4Valid subcommands for &7/npc admin lookat &4are:"));
        cs.sendMessage(Pl3xNPC.colorize("&4radius"));
        return true;
    }

    /**
     * Shows the Admin Message Help
     * @param cs The CommandSender
     * @return If Sent
     */
    private Boolean showAdminMessageHelp(CommandSender cs){
        cs.sendMessage(Pl3xNPC.colorize("&4Valid subcommands for &7/npc admin message &4are:"));
        cs.sendMessage(Pl3xNPC.colorize("&4format, radius"));
        return true;
    }




}
