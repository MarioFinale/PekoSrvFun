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
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;

class PathfinderGoalRandomLookaroundHolo extends PathfinderGoalRandomLookaround {
    private final EntityInsentient entity;

    public PathfinderGoalRandomLookaroundHolo(EntityInsentient var0) {
        super(var0);
        entity = var0;
    }

    public boolean a() {
        if (((PekoSrvFun_HoloPet) this.entity).getStatus().equals("Sitting")) return false;
        return super.a();
    }

}

