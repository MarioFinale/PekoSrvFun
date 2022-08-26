package cl.mariofinale;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.bukkit.Location;
import org.bukkit.entity.*;


public class PathfinderTryPreventDeathByCreeper extends PathfinderGoal {
    private double speed;
    private EntityInsentient entity;
    private Location loc;
    private NavigationAbstract navigation;

    public PathfinderTryPreventDeathByCreeper(EntityInsentient entity, double speedMultiplier)
    {
        this.entity = entity;
        this.navigation = this.entity.D();
        this.speed = speedMultiplier;
    }

    public boolean a()
    {
        c();
        return true;
    }

    public void c()
    {
        boolean nearFusedCreeper = false;
        Location creeperLocation = entity.getBukkitEntity().getLocation();

        for (Entity ent : entity.getBukkitEntity().getNearbyEntities(3.5D,3.5D,3.5D)){
            if (ent.getType().equals(EntityType.CREEPER)){
                creeperLocation = ent.getLocation();
                if (((Creeper) ent).getFuseTicks() > 4){
                    nearFusedCreeper = true;
                    break;
                }
            }
        }
        if (nearFusedCreeper){
            double xvalue;
            double zvalue;

            if (this.entity.getBukkitEntity().getLocation().getX() > creeperLocation.getX()){
                xvalue = this.entity.getBukkitEntity().getLocation().getX() + 5;
            }else{
                xvalue = this.entity.getBukkitEntity().getLocation().getX() - 5;
            }

            if (this.entity.getBukkitEntity().getLocation().getZ() > creeperLocation.getZ()){
                zvalue = this.entity.getBukkitEntity().getLocation().getZ() + 5;
            }else{
                zvalue = this.entity.getBukkitEntity().getLocation().getZ() - 5;
            }

            this.loc = new Location( this.entity.getBukkitEntity().getWorld() ,xvalue, this.entity.getBukkitEntity().getLocation().getY() + 1, zvalue);
            PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ(), 3);
            this.navigation.a(pathEntity, speed);

        }
    }

}