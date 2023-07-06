package cl.mariofinale;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.bukkit.Location;
import org.bukkit.entity.*;


class PathfinderTryPreventDeathByCreeper extends PathfinderGoal {
    private final double speed;
    private final EntityInsentient entity;
    private final NavigationAbstract navigation;
    private PathEntity destination;

    public PathfinderTryPreventDeathByCreeper(EntityInsentient entity, double speedMultiplier)
    {
        this.entity = entity;
        this.navigation = this.entity.J();
        this.speed = speedMultiplier;
    }

    public boolean a()
    {
        boolean nearFusedCreeper = false;
        if (entity == null) return  false;
        Location creeperLocation = entity.getBukkitEntity().getLocation();

        for (Entity ent : entity.getBukkitEntity().getNearbyEntities(3.5D,3.5D,3.5D)){
            if (ent.getType().equals(EntityType.CREEPER)){
                creeperLocation = ent.getLocation();
                if (((Creeper) ent).getFuseTicks() > 5){
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

            Location loc = new Location(this.entity.getBukkitEntity().getWorld(), xvalue, this.entity.getBukkitEntity().getLocation().getY() + 1, zvalue);
            destination = this.navigation.a(loc.getX(), loc.getY(), loc.getZ(), 3);
            return true;
        }
        return false;
    }

    public void c()
    {
        this.navigation.a(destination, speed);
    }

    public boolean b(){
        return false;
    }

    public void d(){

    }
}
