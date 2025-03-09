package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;

class PathfinderGoalNPCRandomStroll extends PathfinderGoalRandomStroll {
    private NPCHelper thisNPC;

    public PathfinderGoalNPCRandomStroll(EntityCreature var0, double var1) {
        this(var0, var1, 120);
    }

    private PathfinderGoalNPCRandomStroll(EntityCreature var0, double var1, int var3) {
        this(var0, var1, var3, true);
    }

    private PathfinderGoalNPCRandomStroll(EntityCreature var0, double var1, int var3, boolean var4) {
        super(var0, var1, var3, var4);
        this.thisNPC = (NPCHelper) var0;
    }

    public boolean b() { //canUse
        if (thisNPC.isMining()) return false;
        if (thisNPC.isHarvesting()) return false;
        if (thisNPC.isSitting()) return false;
        if (thisNPC.isSleeping()) return false;
        return super.b();
    }
}
