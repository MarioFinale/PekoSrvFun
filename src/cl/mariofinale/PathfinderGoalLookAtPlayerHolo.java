package cl.mariofinale;


import java.util.EnumSet;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import org.jetbrains.annotations.Nullable;

class PathfinderGoalLookAtPlayerHolo extends PathfinderGoal {
    public static final float a = 0.02F;
    protected final EntityInsentient entity;
    @Nullable
    private Entity c;
    protected final float d;
    private int h;
    protected final float e;
    private final boolean i;
    protected final Class<? extends EntityLiving> targetEntityClass;
    protected final PathfinderTargetCondition g;

    public PathfinderGoalLookAtPlayerHolo(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2) {
        this(var0, var1, var2, 0.02F);
    }

    public PathfinderGoalLookAtPlayerHolo(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2, float var3) {
        this(var0, var1, var2, var3, false);
    }

    public PathfinderGoalLookAtPlayerHolo(EntityInsentient var0, Class<? extends EntityLiving> var1, float var2, float var3, boolean var4) {
        this.entity = var0;
        this.targetEntityClass = var1;
        this.d = var2;
        this.e = var3;
        this.i = var4;
        this.a(EnumSet.of(Type.b));
        if (var1 == EntityHuman.class) {
            this.g = PathfinderTargetCondition.b().a(var2).a((var1x) -> IEntitySelector.b(var0).test(var1x));
        } else {
            this.g = PathfinderTargetCondition.b().a(var2);
        }

    }

    public boolean a() {
        Disguise disguise = DisguiseAPI.getDisguise(entity.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (watcher.isSleeping()) return false;

        if (this.entity.dQ().i() >= this.e) {
            return false;
        } else {
            if (this.entity.G() != null) {
                this.c = this.entity.G();
            }

            if (this.targetEntityClass == EntityHuman.class) {
                this.c = this.entity.s.a(this.g, this.entity, this.entity.df(), this.entity.dj(), this.entity.dl());
            } else {
                this.c = this.entity.s.a(this.entity.s.a(this.targetEntityClass, this.entity.cy().c(this.d, 3.0D, this.d), (var0) -> true), this.g, this.entity, this.entity.df(), this.entity.dj(), this.entity.dl());
            }

            return this.c != null;
        }
    }

    public boolean b() {
        if (!this.c.bo()) {
            return false;
        } else if (this.entity.f(this.c) > (double)(this.d * this.d)) {
            return false;
        } else {
            return this.h > 0;
        }
    }

    public void c() {
        this.h = this.a(40 + this.entity.dQ().a(40));
    }

    public void d() {
        this.c = null;
    }

    public void e() {
        if (this.c.bo()) {
            double var0 = this.i ? this.entity.dj() : this.c.dj();
            this.entity.z().a(this.c.df(), var0, this.c.dl());
            --this.h;
        }
    }
}

