package cl.mariofinale;

import java.util.EnumSet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;

public class PathfinderGoalMeleeAttackHolo extends PathfinderGoal {
    private double range;
    protected final EntityCreature a;
    private final double b;
    private final boolean c;
    private PathEntity d;
    private double e;
    private double f;
    private double g;
    private int h;
    private int i;
    private final int j = 20;
    private long k;
    private static final long l = 20L;

    public PathfinderGoalMeleeAttackHolo(EntityCreature var0, double var1, boolean var3, double aRange) {
        this.a = var0;
        this.b = var1;
        this.c = var3;
        this.range = aRange;
        this.a(EnumSet.of(Type.a, Type.b));
    }

    public boolean a() {
        long var0 = this.a.s.U();
        if (var0 - this.k < 20L) {
            return false;
        } else {
            this.k = var0;
            EntityLiving var2 = this.a.G();
            if (var2 == null) {
                return false;
            } else if (!var2.bo()) {
                return false;
            } else {
                this.d = this.a.D().a(var2, 0);
                if (this.d != null) {
                    return true;
                } else {
                    return this.a(var2) >= this.a.h(var2.df(), var2.dh(), var2.dl());
                }
            }
        }
    }

    public boolean b() {
        EntityLiving var0 = this.a.G();
        if (var0 == null) {
            return false;
        } else if (!var0.bo()) {
            return false;
        } else if (!this.c) {
            return !this.a.D().l();
        } else if (!this.a.a(var0.da())) {
            return false;
        } else {
            return !(var0 instanceof EntityHuman) || !var0.B_() && !((EntityHuman)var0).f();
        }
    }

    public void c() {
        this.a.D().a(this.d, this.b);
        this.a.u(true);
        this.h = 0;
        this.i = 0;
    }

    public void d() {
        EntityLiving var0 = this.a.G();
        if (!IEntitySelector.e.test(var0)) {
            this.a.h((EntityLiving)null);
        }

        this.a.u(false);
        this.a.D().n();
    }

    public boolean E_() {
        return true;
    }

    public void e() {
        EntityLiving var0 = this.a.G();
        if (var0 != null) {
            this.a.z().a(var0, 30.0F, 30.0F);
            double var1 = this.a.h(var0.df(), var0.dh(), var0.dl());
            this.h = Math.max(this.h - 1, 0);
            if ((this.c || this.a.E().a(var0)) && this.h <= 0 && (this.e == 0.0D && this.f == 0.0D && this.g == 0.0D || var0.h(this.e, this.f, this.g) >= 1.0D || this.a.dQ().i() < 0.05F)) {
                this.e = var0.df();
                this.f = var0.dh();
                this.g = var0.dl();
                this.h = 4 + this.a.dQ().a(7);
                if (var1 > 1024.0D) {
                    this.h += 10;
                } else if (var1 > 256.0D) {
                    this.h += 5;
                }

                if (!this.a.D().a(var0, this.b)) {
                    this.h += 15;
                }

                this.h = this.a(this.h);
            }

            this.i = Math.max(this.i - 1, 0);
            this.a(var0, var1);
        }
    }

    protected void a(EntityLiving var0, double var1) {
        double var3 = this.a(var0);
        if (var1 <= var3 && this.i <= 0) {
            this.h();
            this.a.a(EnumHand.a);
            this.a.z(var0);
        }

    }

    protected void h() {
        this.i = this.a(20);
    }

    protected boolean i() {
        return this.i <= 0;
    }

    protected int k() {
        return this.i;
    }

    protected int l() {
        return this.a(20);
    }

    protected double a(EntityLiving var0) {
        return (double)(this.a.cW() * range * this.a.cW() * range + var0.cW());
    }
}
