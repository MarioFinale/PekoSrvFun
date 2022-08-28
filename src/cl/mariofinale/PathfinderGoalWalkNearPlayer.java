package cl.mariofinale;


import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

public class PathfinderGoalWalkNearPlayer extends PathfinderGoal
{
    private double speed;
    private EntityInsentient entity;
    private String player;
    private Location loc;
    private NavigationAbstract navigation;
    PathEntity destination;
    private Block lastSleepingBlock;

    public PathfinderGoalWalkNearPlayer(EntityInsentient entity, double speed, String player)
    {
        this.player = player;
        this.entity = entity;
        this.navigation = this.entity.D();
        this.speed = speed;
    }

    public boolean a()
    {
        if (((PekoSrvFun_HoloPet) this.entity).Sitting) return false;
        Player tPlayer = Bukkit.getPlayer(player);
        if (tPlayer == null) return false;
        if (!tPlayer.isOnline()) return false;
        Disguise disguise = DisguiseAPI.getDisguise(entity.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (tPlayer.isSleeping() && watcher.isSleeping()) return false;
        if (!watcher.isSleeping() && tPlayer.isSleeping()){
            Block standingBlock = this.entity.getBukkitEntity().getLocation().subtract(0,0.5,0).getBlock();
            String blockTypeName = standingBlock.getType().toString();
            BlockState state = standingBlock.getState();
            BlockData data = standingBlock.getBlockData();
            if (blockTypeName.matches(".+?_BED")){
                Location standingBlockLocation = standingBlock.getLocation();
                if (data instanceof Bed){
                    Bed bed = (Bed) data;
                    Bed.Part bedpart = bed.getPart();
                    if (!bed.isOccupied() && !Utils.getCustomBedOcuppied(standingBlock)){
                        if (bedpart == Bed.Part.HEAD){
                            Location loc1 = standingBlock.getLocation();
                            Location loc2 = standingBlock.getLocation();
                            Location newLocation = loc1.add(0.5, 0.6,0.5);
                            this.entity.getBukkitEntity().teleport(newLocation);
                            String entityID = entity.getBukkitEntity().getUniqueId().toString();
                            String command = "data merge entity " + entityID +" {SleepingX:" + loc2.getBlockX() + ",SleepingY:" + (loc2.getBlockY()) + ",SleepingZ:"  + loc2.getBlockZ() + "}"; //Maybe there's a better way???
                            Utils.setCustomBedOcuppied(standingBlock, true);
                            lastSleepingBlock = standingBlock;
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            watcher.setSleeping(true);
                            return false;

                        }else {
                            Block option1 = standingBlockLocation.getWorld().getBlockAt(standingBlockLocation.clone().subtract(1,0,0));
                            Block option2 = standingBlockLocation.getWorld().getBlockAt(standingBlockLocation.clone().add(1,0,0));
                            Block option3 = standingBlockLocation.getWorld().getBlockAt(standingBlockLocation.clone().subtract(0,0,1));
                            Block option4 = standingBlockLocation.getWorld().getBlockAt(standingBlockLocation.clone().add(0,0,1));

                            if (option1.getBlockData() instanceof Bed){
                                if (((Bed) option1.getBlockData()).getPart() == Bed.Part.HEAD){
                                    this.entity.getBukkitEntity().teleport(option1.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                            if (option2.getBlockData() instanceof Bed){
                                if (((Bed) option2.getBlockData()).getPart() == Bed.Part.HEAD){
                                    this.entity.getBukkitEntity().teleport(option2.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                            if (option3.getBlockData() instanceof Bed){
                                if (((Bed) option3.getBlockData()).getPart() == Bed.Part.HEAD){
                                    this.entity.getBukkitEntity().teleport(option3.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                            if (option4.getBlockData() instanceof Bed){
                                if (((Bed) option4.getBlockData()).getPart() == Bed.Part.HEAD){
                                    this.entity.getBukkitEntity().teleport(option4.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                        }

                    }
                }

            }
            int radius = 10;
            Location location = entity.getBukkitEntity().getLocation();

            for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
                for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                    for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                        Block block = location.getWorld().getBlockAt(x, y, z);
                        BlockData blockData = block.getBlockData();
                        if (blockData instanceof Bed){
                            Bed bed = (Bed) blockData;
                            Bed.Part bedPart = bed.getPart();
                            if (bedPart == Bed.Part.HEAD){
                                if (!(bed.isOccupied()||Utils.getCustomBedOcuppied(block))){
                                    destination = this.navigation.a(block.getX(), block.getY() + 0.6, block.getZ(), 1);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

        }else {
            if (watcher.isSleeping()){
                watcher.setSleeping(false);
                if (lastSleepingBlock != null){
                    Utils.setCustomBedOcuppied(lastSleepingBlock, false);
                }

            }

        }

        if (!this.entity.getBukkitEntity().getWorld().getName().equals(Bukkit.getPlayer(player).getLocation().getWorld().getName())) return false;

        if (this.entity.getBukkitEntity().getLocation().distance(Bukkit.getPlayer(player).getLocation()) > 30){
            this.entity.getBukkitEntity().teleport(Bukkit.getPlayer(player).getLocation());
            return false;
        }

        if (this.entity.getBukkitEntity().getLocation().distance(Bukkit.getPlayer(player).getLocation()) > 15){
            int randomX = ThreadLocalRandom.current().nextInt(Bukkit.getPlayer(player).getLocation().getBlockX() - 3, Bukkit.getPlayer(player).getLocation().getBlockX() + 3);
            int randomY = Bukkit.getPlayer(player).getLocation().getBlockY(); //same height
            int randomZ = ThreadLocalRandom.current().nextInt(Bukkit.getPlayer(player).getLocation().getBlockZ() - 3, Bukkit.getPlayer(player).getLocation().getBlockZ() + 3);
            this.loc = new Location(Bukkit.getPlayer(player).getWorld(), (double) randomX, (double) randomY, (double) randomZ);
            destination = this.navigation.a(loc.getX(), loc.getY(), loc.getZ(), 1);
            return true;
        }
        return false;
    }

    public boolean b(){
        return false;
    }

    public void c() {
        this.navigation.a(this.destination, speed);
    }

    public void d() {
    }


}