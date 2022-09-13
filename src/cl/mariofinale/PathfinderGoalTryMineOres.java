package cl.mariofinale;


import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

class PathfinderGoalTryMineOres extends PathfinderGoal
{
    private final double speed;
    private final int radius;
    private final EntityInsentient entity;
    private final NavigationAbstract navigation;
    private PathEntity destination;
    private Location destinationLocation;

    public PathfinderGoalTryMineOres(EntityInsentient entity, double speed, int radius)
    {
        this.entity = entity;
        this.navigation = this.entity.D();
        this.speed = speed;
        this.radius = radius;
    }

    public boolean a()
    {
        if (((PekoSrvFun_HoloPet) this.entity).getStatus().equals("Sitting")) return false;
        Location petLocation = this.entity.getBukkitEntity().getLocation().clone();
        petLocation.add(0,0.8,0); //Use head height;
        List<Block> blocks = new ArrayList<>();
        for(double x = petLocation.getX() - radius; x <= petLocation.getX() + radius; x++){
            for(double y = petLocation.getY() - radius; y <= petLocation.getY() + radius; y++){
                for(double z = petLocation.getZ() - radius; z <= petLocation.getZ() + radius; z++){
                    Location loc = new Location(petLocation.getWorld(), x, y, z);
                    Block block = loc.getBlock();
                    if (Utils.isOre(block)){
                        if (block.getLocation().getY() - petLocation.getY() > 5) continue;
                        double dx = loc.getX() - petLocation.getX();
                        double dy = loc.getY() - petLocation.getY();
                        double dz = loc.getZ() - petLocation.getZ();
                        double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
                        double steps = length * 4;
                        boolean obstructed = false;
                        for (int step = 0; step <= steps; step++){
                            double factor = step/steps;
                            double nx = petLocation.getX() + dx * factor;
                            double ny = petLocation.getY() + dy * factor;
                            double nz = petLocation.getZ() + dz * factor;
                            Location pointToCheck = new Location(petLocation.getWorld(), nx, ny, nz);
                            Block blockAtPoint = petLocation.getWorld().getBlockAt(pointToCheck);
                            if (blockAtPoint.getType() != Material.AIR){
                                if (!(blockAtPoint.getLocation().equals(block.getLocation()) || blockAtPoint.getLocation().equals(petLocation.getBlock().getLocation()))){
                                    obstructed = true;
                                    break;
                                }
                            }
                        }
                        if (!obstructed){
                           blocks.add(loc.getBlock());
                        }
                    }
                }
            }
        }

        if (blocks.size() == 0) return false;

        Block nearestBlock = blocks.get(0);
        for (Block block : blocks){
            if (block.getLocation().distance(petLocation) < nearestBlock.getLocation().distance(petLocation)){
                    nearestBlock = block;
            }
        }
        Location loc = nearestBlock.getLocation();
        destination = this.navigation.a(loc.getX(), loc.getY(), loc.getZ(), 1);
        destinationLocation = loc.clone();
        return true;
    }

    public boolean b(){
        return false;
    }

    public void c() {
        this.navigation.a(this.destination, speed);
        this.entity.getBukkitEntity().getLocation().setDirection(destinationLocation.subtract(this.entity.getBukkitEntity().getLocation().toVector()).toVector());
    }

    public void d() {
    }


}