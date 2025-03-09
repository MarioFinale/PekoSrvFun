package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

class PathfinderGoalNPCWalkNearPlayer extends PathfinderGoal
{
    private final double speed;
    private final String player;
    private final NavigationAbstract navigation;
    private PathEntity destination;
    private Block lastSleepingBlock;
    private NPCHelper thisNPC;

    public PathfinderGoalNPCWalkNearPlayer(EntityInsentient entity, double speed, String player)
    {
        this.player = player;
        this.thisNPC = (NPCHelper) entity;
        this.navigation = thisNPC.getNPCNavigation();
        this.speed = speed;
    }

    public boolean V_() {
        return false;
    } //requiresUpdateEveryTick

    public void a() {
        this.navigation.a(this.destination, speed);
    } //tick

    public void e() {

    } //stop

    public boolean b() { //canUse
        if (thisNPC.isSitting()) return false;
        if (thisNPC.isFreeToWander()) return false;
        Player tPlayer = Bukkit.getPlayer(player);
        if (tPlayer == null) return false;
        if (!tPlayer.isOnline()) return false;
        if (tPlayer.isSleeping() && thisNPC.isSleeping()) return false;
        if (!thisNPC.isSleeping() && tPlayer.isSleeping()){
            Block standingBlock = thisNPC.getLocation().subtract(0,0.5,0).getBlock();
            String blockTypeName = standingBlock.getType().toString();
            BlockData data = standingBlock.getBlockData();
            if (blockTypeName.matches(".+?_BED")){
                Location standingBlockLocation = standingBlock.getLocation();
                if (data instanceof Bed){
                    Bed bed = (Bed) data;
                    Bed.Part bedPart = bed.getPart();
                    if (!bed.isOccupied() && !NPCUtils.getCustomBedOcuppied(standingBlock)){
                        if (bedPart == Bed.Part.HEAD){
                            Location loc1 = standingBlock.getLocation();
                            Location loc2 = standingBlock.getLocation();
                            Location newLocation = loc1.add(0.5, 0.6,0.5);
                            thisNPC.teleport(newLocation);
                            String entityID = thisNPC.getUniqueId().toString();
                            String command = "data merge entity " + entityID +" {SleepingX:" + loc2.getBlockX() + ",SleepingY:" + (loc2.getBlockY()) + ",SleepingZ:"  + loc2.getBlockZ() + "}"; //Maybe there's a better way???
                            NPCUtils.setCustomBedOcuppied(standingBlock, true);
                            lastSleepingBlock = standingBlock;
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            thisNPC.setSleeping();
                            return false;

                        }else {
                            World world = standingBlock.getWorld();
                            Block option1 = world.getBlockAt(standingBlockLocation.clone().subtract(1,0,0));
                            Block option2 = world.getBlockAt(standingBlockLocation.clone().add(1,0,0));
                            Block option3 = world.getBlockAt(standingBlockLocation.clone().subtract(0,0,1));
                            Block option4 = world.getBlockAt(standingBlockLocation.clone().add(0,0,1));

                            if (option1.getBlockData() instanceof Bed){
                                if (((Bed) option1.getBlockData()).getPart() == Bed.Part.HEAD){
                                    thisNPC.teleport(option1.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                            if (option2.getBlockData() instanceof Bed){
                                if (((Bed) option2.getBlockData()).getPart() == Bed.Part.HEAD){
                                    thisNPC.teleport(option2.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                            if (option3.getBlockData() instanceof Bed){
                                if (((Bed) option3.getBlockData()).getPart() == Bed.Part.HEAD){
                                    thisNPC.teleport(option3.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                            if (option4.getBlockData() instanceof Bed){
                                if (((Bed) option4.getBlockData()).getPart() == Bed.Part.HEAD){
                                    thisNPC.teleport(option4.getLocation().clone().add(0.5,0.6,0.5));
                                    return false;
                                }
                            }
                        }

                    }
                }

            }
            int radius = 10;
            Location location = thisNPC.getLocation();

            for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
                for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                    for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                        Block block = location.getWorld().getBlockAt(x, y, z);
                        BlockData blockData = block.getBlockData();
                        if (blockData instanceof Bed){
                            Bed bed = (Bed) blockData;
                            Bed.Part bedPart = bed.getPart();
                            if (bedPart == Bed.Part.HEAD){
                                if (!(bed.isOccupied()||NPCUtils.getCustomBedOcuppied(block))){
                                    destination = this.navigation.a(block.getX(), block.getY() + 0.6, block.getZ(), 1);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

        }else {
            if (thisNPC.isSleeping()){
                thisNPC.setNotSleeping();
                if (lastSleepingBlock != null){
                    NPCUtils.setCustomBedOcuppied(lastSleepingBlock, false);
                }

            }

        }

        if (!thisNPC.getWorld().getName().equals(tPlayer.getLocation().getWorld().getName())) return false;

        if (thisNPC.getLocation().distance(tPlayer.getLocation()) > 30){
            thisNPC.teleport(tPlayer.getLocation());
            return false;
        }

        if (thisNPC.getLocation().distance(tPlayer.getLocation()) > 15){
            int randomX = ThreadLocalRandom.current().nextInt(tPlayer.getLocation().getBlockX() - 3, tPlayer.getLocation().getBlockX() + 3);
            int randomY = tPlayer.getLocation().getBlockY(); //same height
            int randomZ = ThreadLocalRandom.current().nextInt(tPlayer.getLocation().getBlockZ() - 3, tPlayer.getLocation().getBlockZ() + 3);
            Location loc = new Location(tPlayer.getWorld(), randomX, randomY, randomZ);
            destination = this.navigation.a(loc.getX(), loc.getY(), loc.getZ(), 1);
            return true;
        }
        return false;
    }



}