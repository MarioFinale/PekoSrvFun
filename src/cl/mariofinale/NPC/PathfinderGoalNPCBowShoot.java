package cl.mariofinale.NPC;

import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IRangedEntity;

public class PathfinderGoalNPCBowShoot<T extends EntityMonster & IRangedEntity> extends PathfinderGoalBowShoot {

    private final NPCHelper thisNPC;

    public PathfinderGoalNPCBowShoot(EntityMonster var0, double var1, int var3, float var4) {
        super(var0, var1, var3, var4);
        thisNPC = (NPCHelper) var0;
    }
    public boolean b(){ //canUse
        if (thisNPC.isEnemyNear(2.5)){
            return false;
        }
        return super.b();
    }
}
