package cl.mariofinale;

import java.util.EnumSet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;

class PathfinderGoalMeleeAttackHolo extends PathfinderGoalMeleeAttack {
    private final double range;

    public PathfinderGoalMeleeAttackHolo(EntityCreature var0, double var1, boolean var2, double aRange) {
        super(var0,var1,var2);
        this.range = aRange;
    }
}
