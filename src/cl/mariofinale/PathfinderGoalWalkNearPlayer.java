package cl.mariofinale;


import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    public PathfinderGoalWalkNearPlayer(EntityInsentient entity, double speed, String player)
    {
        this.player = player;
        this.entity = entity;
        this.navigation = this.entity.D();
        this.speed = speed;
    }

    public boolean a()
    {
        Player tPlayer = Bukkit.getPlayer(player);
        if (tPlayer == null) return false;
        if (!tPlayer.isOnline()) return false;

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