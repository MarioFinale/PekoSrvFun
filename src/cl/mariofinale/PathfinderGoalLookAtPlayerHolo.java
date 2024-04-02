package cl.mariofinale;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;


class PathfinderGoalLookAtPlayerHolo extends PathfinderGoalLookAtPlayer {

    public PathfinderGoalLookAtPlayerHolo(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2) {
        super(var0, var1, var2);
    }

    public PathfinderGoalLookAtPlayerHolo(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2, float var3) {
        super(var0, var1, var2, var3);
    }

    public PathfinderGoalLookAtPlayerHolo(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2, float var3, boolean var4) {
        super(var0, var1, var2, var3, var4);
    }

    public boolean a() {
        Disguise disguise = DisguiseAPI.getDisguise(b.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (watcher.isSleeping()) return false;
        return super.a();
    }

}

