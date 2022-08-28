package cl.mariofinale;

import java.util.EnumSet;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;

public class PathfinderGoalRandomStrollHolo extends PathfinderGoal {
    public static final int a = 120;
    protected final EntityCreature entity;
    protected double c;
    protected double d;
    protected double e;
    protected final double f;
    protected int g;
    protected boolean h;
    private final boolean i;

    public PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1) {
        this(var0, var1, 120);
    }

    public PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1, int var3) {
        this(var0, var1, var3, true);
    }

    public PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1, int var3, boolean var4) {
        this.entity = var0;
        this.f = var1;
        this.g = var3;
        this.i = var4;
        this.a(EnumSet.of(Type.a));
    }

    public boolean a() {
        Disguise disguise = DisguiseAPI.getDisguise(entity.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (watcher.isSleeping()) return false;
        if (this.entity.bJ()) {
            return false;
        } else {
            if (!this.h) {
                if (this.i && this.entity.dV() >= 100) {
                    return false;
                }

                if (this.entity.dQ().a(b(this.g)) != 0) {
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

    @Nullable
    protected Vec3D h() {
        return DefaultRandomPos.a(this.entity, 10, 7);
    }

    public boolean b() {
        return !this.entity.D().l() && !this.entity.bJ();
    }

    public void c() {
        this.entity.D().a(this.c, this.d, this.e, this.f);
    }

    public void d() {
        this.entity.D().n();
        super.d();
    }

    public void i() {
        this.h = true;
    }

    public void c(int var0) {
        this.g = var0;
    }
}
