package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;

import org.bukkit.Bukkit;

import java.util.Random;


class PathfinderGoalNPCLookAtPlayer extends PathfinderGoalLookAtPlayer {
    private NPCHelper thisHelper;
    public PathfinderGoalNPCLookAtPlayer(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2) {
        super(var0, var1, var2);
        this.thisHelper = (NPCHelper) var0;
    }

    public PathfinderGoalNPCLookAtPlayer(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2, float var3) {
        super(var0, var1, var2, var3);
        this.thisHelper = (NPCHelper) var0;
    }

    public PathfinderGoalNPCLookAtPlayer(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2, float var3, boolean var4) {
        super(var0, var1, var2, var3, var4);
        this.thisHelper = (NPCHelper) var0;
    }

    public boolean b() { //canUse
        if (thisHelper.isMining()) return false;
        if (thisHelper.isSleeping()) return false;
        if (new Random().nextInt(100) == 1 && thisHelper.getVelocity().isZero()){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> {
                thisHelper.getDisguise().getWatcher().setSneaking(true);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> {
                    thisHelper.getDisguise().getWatcher().setSneaking(false);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> {
                        thisHelper.getDisguise().getWatcher().setSneaking(true);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> {
                            thisHelper.getDisguise().getWatcher().setSneaking(false);
                        }, 3L);
                    }, 3L);
                }, 3L);
            }, 3L);
        }
        return super.b();
    }
}
