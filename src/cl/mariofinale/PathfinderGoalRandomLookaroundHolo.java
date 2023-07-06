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

class PathfinderGoalRandomLookaroundHolo extends PathfinderGoal {
    private final EntityInsentient entity;
    private double b;
    private double c;
    private int d;

    public PathfinderGoalRandomLookaroundHolo(EntityInsentient var0) {
        this.entity = var0;
        this.a(EnumSet.of(Type.a, Type.b));
    }

    public boolean a() {
        if (((PekoSrvFun_HoloPet) this.entity).getStatus().equals("Sitting")) return false;
        Disguise disguise = DisguiseAPI.getDisguise(entity.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (watcher.isSleeping()) return false;
        return this.entity.ec().i() < 0.02F;
    }

    public boolean b() {
        return this.d >= 0;
    }

    public void c() {
        double var0 = 6.283185307179586D * this.entity.ec().j();
        this.b = Math.cos(var0);
        this.c = Math.sin(var0);
        this.d = 20 + this.entity.ec().a(20);
    }

    public boolean K_() {
        return true;
    }
    public void e() {
        --this.d;
        this.entity.E().a(this.entity.dn() + this.b, this.entity.dr(), this.entity.dt() + this.c);
    }



}

