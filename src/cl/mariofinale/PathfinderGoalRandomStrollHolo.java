package cl.mariofinale;

import java.util.EnumSet;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;

class PathfinderGoalRandomStrollHolo extends PathfinderGoal {
    public static final int a = 120;
    private final EntityCreature entity;
    private double c;
    private double d;
    private double e;
    private final double f;
    private int g;
    private boolean h;
    private final boolean i;

    public PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1) {
        this(var0, var1, 120);
    }

    private PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1, int var3) {
        this(var0, var1, var3, true);
    }

    private PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1, int var3, boolean var4) {
        this.entity = var0;
        this.f = var1;
        this.g = var3;
        this.i = var4;
        this.a(EnumSet.of(Type.a));
    }

    public boolean a() {
        if (((PekoSrvFun_HoloPet) this.entity).getStatus().equals("Sitting")) return false;
        Disguise disguise = DisguiseAPI.getDisguise(entity.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (watcher.isSleeping()) return false;
        if (this.entity.bN()) {
            return false;
        } else {
            if (!this.h) {
                if (this.i && this.entity.eh() >= 100) {
                    return false;
                }

                if (this.entity.ec().a(b(this.g)) != 0) {
                    return false;
                }
            }
            Vec3D var0 = this.h();
            if (var0 == null) {
                return false;
            } else {
                this.c = var0.c;
                this.d = var0.d;
                this.e = var0.e;
                this.h = false;
                return true;
            }
        }
    }

    protected Vec3D h() {
        return DefaultRandomPos.a(this.entity, 10, 7);
    }
    public boolean b() {
        return !this.entity.J().l() && !this.entity.bN();
    }
    public void c() {
        this.entity.J().a(this.c, this.d, this.e, this.f);
    }

    public void d() {
        this.entity.J().n();
        super.d();
    }
    public void i() {
        this.h = true;
    }
    public void c(int var0) {
        this.g = var0;
    }
}
