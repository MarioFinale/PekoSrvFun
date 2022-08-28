package cl.mariofinale;

import java.util.EnumSet;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;

public class PathfinderGoalRandomLookaroundHolo extends PathfinderGoal {
    private final EntityInsentient entity;
    private double b;
    private double c;
    private int d;

    public PathfinderGoalRandomLookaroundHolo(EntityInsentient var0) {
        this.entity = var0;
        this.a(EnumSet.of(Type.a, Type.b));
    }

    public boolean a() {
        if (((PekoSrvFun_HoloPet) this.entity).Sitting) return false;
        Disguise disguise = DisguiseAPI.getDisguise(entity.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (watcher.isSleeping()) return false;
        return this.entity.dQ().i() < 0.02F;
    }

    public boolean b() {
        return this.d >= 0;
    }

    public void c() {
        double var0 = 6.283185307179586D * this.entity.dQ().j();
        this.b = Math.cos(var0);
        this.c = Math.sin(var0);
        this.d = 20 + this.entity.dQ().a(20);
    }

    public boolean E_() {
        return true;
    }

    public void e() {
        --this.d;
        this.entity.z().a(this.entity.df() + this.b, this.entity.dj(), this.entity.dl() + this.c);
    }
}

