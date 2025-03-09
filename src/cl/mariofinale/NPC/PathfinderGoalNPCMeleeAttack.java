package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PathfinderGoalNPCMeleeAttack extends PathfinderGoalMeleeAttack {
    private int ticksUntilNextPathRecalculation = 0;
    private int ticksUntilNextAttack = 0; //ticksUntilNextAttack
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private double speedModifier;
    private boolean followingTargetEvenIfNotSeen;

    private final double attackRange;
    private final int attackDelay;
    private NPCHelper thisNPC;

    public PathfinderGoalNPCMeleeAttack(EntityCreature var0, double var1, boolean var2, double aRange, int aDelayTicks) {
        super(var0,var1,var2);
        this.thisNPC = (NPCHelper) this.a;
        this.speedModifier = var1;
        this.followingTargetEvenIfNotSeen = var2;
        this.attackRange = aRange;
        this.attackDelay = aDelayTicks;
    }

    public boolean b(){ //canUse
        boolean isMonsterNear = false;
        boolean isMonsterFar = false;
        if (thisNPC.isEnemyNear(3.5)){
            isMonsterNear = true;
            thisNPC.pickBestMeleeWeapon();
            thisNPC.setMining(false);
        }
        if (thisNPC.isEnemyNear(15)){
            isMonsterFar = true;
            thisNPC.setMining(false);
        }
        if (isMonsterNear){
            thisNPC.pickBestMeleeWeapon();
        }
        if (isMonsterFar && !isMonsterNear){
            thisNPC.checkEquipBow();
        }
        return super.b();
    }

    public void a() { //tick
        LivingEntity targetEntity = this.thisNPC.getNPCTarget(); //getNPCTarget
        if (targetEntity != null) {
            if (targetEntity instanceof Player){
                if (!targetEntity.getWorld().getPVP()){
                    return;
                }
            }
            thisNPC.setNPCLookAtEntity(targetEntity);
            ticksUntilNextPathRecalculation = Math.max(ticksUntilNextPathRecalculation - 1, 0);
            if ((followingTargetEvenIfNotSeen ||  thisNPC.hasNPCLineOfSight(targetEntity)) &&
                    ticksUntilNextPathRecalculation <= 0 &&
                    (pathedTargetX == 0.0D && pathedTargetY == 0.0D && pathedTargetZ == 0.0D ||
                            NPCUtils.getEntityDistanceToLocation(targetEntity, pathedTargetX, pathedTargetY, pathedTargetZ) >= 1.0D || thisNPC.getNPCHorizontalSpeed() < 0.05F)) {
                pathedTargetX = targetEntity.getLocation().getX();
                pathedTargetY =  targetEntity.getLocation().getY();
                pathedTargetZ = targetEntity.getLocation().getZ();
                ticksUntilNextPathRecalculation = 4 + thisNPC.getRandomNextInt(7);
                double var1 = thisNPC.getDistanceSquared(targetEntity);
                if (var1 > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                } else if (var1 > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 1;
                }
                if (!thisNPC.moveTo(targetEntity, speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 5;
                }
                this.ticksUntilNextPathRecalculation = this.a(this.ticksUntilNextPathRecalculation);
            }
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.thisNPC.checkAndPerformAttack(targetEntity);
        }
    }
}
