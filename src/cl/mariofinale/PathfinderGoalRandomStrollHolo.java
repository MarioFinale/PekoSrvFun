package cl.mariofinale;

import java.util.EnumSet;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;

class PathfinderGoalRandomStrollHolo extends PathfinderGoalRandomStroll {
    private final EntityCreature entity;


    public PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1) {
        this(var0, var1, 120);
    }

    private PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1, int var3) {
        this(var0, var1, var3, true);
    }

    private PathfinderGoalRandomStrollHolo(EntityCreature var0, double var1, int var3, boolean var4) {
        super(var0, var1, var3, var4);
        this.entity = var0;
    }

    public boolean a() {
        if (((PekoSrvFun_HoloPet) this.entity).getStatus().equals("Sitting")) return false;
        Disguise disguise = DisguiseAPI.getDisguise(entity.getBukkitEntity());
        FlagWatcher watcher = disguise.getWatcher();
        if (watcher.isSleeping()) return false;
        return super.a();
    }
}
