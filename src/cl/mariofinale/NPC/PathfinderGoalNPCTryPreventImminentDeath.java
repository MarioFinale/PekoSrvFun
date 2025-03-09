package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;

import org.bukkit.Location;
import org.bukkit.entity.*;

class PathfinderGoalNPCTryPreventImminentDeath extends PathfinderGoal {
    private final double speed;
    private final NavigationAbstract navigation;
    private PathEntity destination;
    private NPCHelper thisNPC;

    public PathfinderGoalNPCTryPreventImminentDeath(EntityInsentient entity, double speedMultiplier)
    {
        thisNPC = (NPCHelper) entity;
        this.navigation = thisNPC.getNPCNavigation();
        this.speed = speedMultiplier;
    }

    public boolean V_() {
        return false;
    } //net/minecraft/world/entity/ai/goal/Goal.requiresUpdateEveryTick()

    public void a() {
        this.navigation.a(this.destination, speed);
    } //net/minecraft/world/entity/ai/goal/Goal.tick()

    public boolean b() { //net/minecraft/world/entity/ai/goal/Goal.canUse()
        boolean nearDanger = false;
        if (thisNPC == null) return  false;
        Location dangerLocation = thisNPC.getLocation();

        for (Entity ent : thisNPC.getNearbyEntities(3.5D,3.5D,3.5D)){
            if (ent instanceof Creeper){
                dangerLocation = ent.getLocation();
                if (((Creeper) ent).getFuseTicks() > 5){
                    nearDanger = true;
                    break;
                }
            }
            if (ent instanceof TNTPrimed){
                dangerLocation = ent.getLocation();
                nearDanger = true;
                break;
            }

            if (ent instanceof Warden){
                dangerLocation = ent.getLocation();
                nearDanger = true;
                break;
            }
        }
        if (nearDanger){
            Location loc = getSafeLocation(dangerLocation);
            destination = this.navigation.a(loc.getX(), loc.getY(), loc.getZ(), 3);
            return true;
        }
        return false;
    }

    private Location getSafeLocation(Location dangerLocation) {
        double xValue;
        double zValue;

        if (thisNPC.getLocation().getX() > dangerLocation.getX()){
            xValue = thisNPC.getLocation().getX() + 5;
        }else{
            xValue = thisNPC.getLocation().getX() - 5;
        }

        if (thisNPC.getLocation().getZ() > dangerLocation.getZ()){
            zValue = thisNPC.getLocation().getZ() + 5;
        }else{
            zValue = thisNPC.getLocation().getZ() - 5;
        }

        Location loc = new Location(thisNPC.getWorld(), xValue, thisNPC.getLocation().getY() + 1, zValue);
        return loc;
    }

}