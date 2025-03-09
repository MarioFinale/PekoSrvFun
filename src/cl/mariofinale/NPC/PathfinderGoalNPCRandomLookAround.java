package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;

class PathfinderGoalNPCRandomLookAround extends PathfinderGoalRandomLookaround {
    private NPCHelper thisNPC;

    public PathfinderGoalNPCRandomLookAround(EntityInsentient var0) {
        super(var0);
        this.thisNPC = (NPCHelper) var0;
    }
    public boolean b() { //canUse
        if (thisNPC.isMining()) return false;
        if (thisNPC.isSleeping()) return false;
        return super.b();
    }
}
