package cl.mariofinale;


import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class PathfinderGoalWalkNearPlayer extends PathfinderGoal
{
    private double speed;
    private EntityInsentient entity;
    private String player;
    private Location loc;
    private NavigationAbstract navigation;

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
        if (tPlayer == null) return true;
        if (!tPlayer.isOnline()) return true;
            c();
        return true;
    }

    public void c()
    {
        if (!this.entity.getBukkitEntity().getWorld().getName().equals(Bukkit.getPlayer(player).getWorld().getName())){
            this.entity.getBukkitEntity().teleport(Bukkit.getPlayer(player).getLocation());
            return;
        }

        if (this.entity.getBukkitEntity().getLocation().distance(Bukkit.getPlayer(player).getLocation()) > 30){
            this.entity.getBukkitEntity().teleport(Bukkit.getPlayer(player).getLocation());
            return;
        }

        if (this.entity.getBukkitEntity().getLocation().distance(Bukkit.getPlayer(player).getLocation()) > 10){
            int randomX = ThreadLocalRandom.current().nextInt(Bukkit.getPlayer(player).getLocation().getBlockX() - 3, Bukkit.getPlayer(player).getLocation().getBlockX() + 3);
            int randomY = ThreadLocalRandom.current().nextInt(Bukkit.getPlayer(player).getLocation().getBlockY() - 3, Bukkit.getPlayer(player).getLocation().getBlockY() + 3);
            int randomZ = ThreadLocalRandom.current().nextInt(Bukkit.getPlayer(player).getLocation().getBlockZ() - 3, Bukkit.getPlayer(player).getLocation().getBlockZ() + 3);
            this.loc = new Location(Bukkit.getPlayer(player).getWorld(), (double) randomX, (double) randomY, (double) randomZ);
            PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ(), 1);
            this.navigation.a(pathEntity, speed);
        }
    }

}