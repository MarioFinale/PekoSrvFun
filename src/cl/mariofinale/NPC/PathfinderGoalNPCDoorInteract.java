package cl.mariofinale.NPC;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.goal.PathfinderGoalDoorInteract;

import net.minecraft.world.level.block.BlockDoor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;

public class PathfinderGoalNPCDoorInteract extends PathfinderGoalDoorInteract {

    private final boolean closesDoors;
    private int forgetTime;
    private NPCHelper thisNPC;
    private BlockPosition doorPos;
    boolean operatingDoor;

    public PathfinderGoalNPCDoorInteract(NPCHelper var0) {
        super(var0);
        this.thisNPC = var0;
        this.closesDoors = true;
        this.doorPos = this.e;
        operatingDoor = false;
    }

    public boolean c() { //canContinueToUse
        Location location_a = thisNPC.getLocation().add(1,1,0);
        Location location_b = thisNPC.getLocation().add(-1,1,0);
        Location location_c = thisNPC.getLocation().add(0,1,1);
        Location location_d = thisNPC.getLocation().add(0,1,-1);
        Block block_a = thisNPC.getWorld().getBlockAt(location_a);
        Block block_b = thisNPC.getWorld().getBlockAt(location_b);
        Block block_c = thisNPC.getWorld().getBlockAt(location_c);
        Block block_d = thisNPC.getWorld().getBlockAt(location_d);
        return (block_a instanceof BlockDoor || block_b instanceof BlockDoor || block_c instanceof BlockDoor || block_d instanceof BlockDoor); //hasDoorNear
    }

    public boolean b() { //canUse
        return this.closesDoors && this.forgetTime > 0 && super.b();
    }

    public void a() { //tick
        this.forgetTime = 20;
        this.a(true);
    }

    public void d() {
        this.a(false);
    }

    public void e() {
        --this.forgetTime;
        for(Block block : thisNPC.getLineOfSight(null, 1)){
            if (block.getType().toString().contains("DOOR") && !block.getType().toString().contains("IRON") && !block.getType().toString().contains("TRAP")){
                Door door = (Door) block.getBlockData();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(thisNPC.plugin(), () -> {
                    if (!door.isOpen() && !operatingDoor){
                        operatingDoor = true;
                        thisNPC.setNPCLookAtLocation(block.getLocation());
                        thisNPC.playHitAnimation();
                        door.setOpen(true);
                        thisNPC.setBlockData(block, door);
                        block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 0.2f, 1f);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(thisNPC.plugin(), () -> {
                            door.setOpen(false);
                            thisNPC.setBlockData(block, door);
                            block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.2f, 1f);
                            operatingDoor = false;
                        }, 20L);
                    }
                }, 5L);
            }
        }
        super.e();
    }
}
