package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

class PathfinderGoalNPCTryMine extends PathfinderGoal {
    private final double speed;
    private final int radius;
    private final NavigationAbstract navigation;
    private PathEntity destination;
    private Location destinationLocation;
    private NPCHelper thisNPC;
    private int currentBreakProgress;
    private LocalDateTime lastBreakAttempt;
    private boolean inventoryFull;
    private boolean mineStones;
    private boolean mineCobble;
    private MineBlockLocationInfo TargetLocation;
    private int lastOreSearchTicks;
    private int oreSearchCoolDown;
    private HashSet<Block> nearbyOreBlocks;
    private int lastOreCleanTicks;
    private int oreCleanCoolDown;


    public PathfinderGoalNPCTryMine(EntityInsentient entity, double speed, int radius) {
        this.thisNPC = (NPCHelper) entity;
        this.navigation = thisNPC.getNPCNavigation();
        this.speed = speed;
        this.radius = radius;
        currentBreakProgress = 0;
        lastBreakAttempt = LocalDateTime.now();
        inventoryFull = false;
        mineStones = false;
        mineCobble = false;
        oreSearchCoolDown = 20;
        lastOreSearchTicks = 0;
        nearbyOreBlocks = new HashSet<>();
        lastOreCleanTicks = 0;
        oreCleanCoolDown = 100;
    }

    public boolean b() {  //net/minecraft/world/entity/ai/goal/Goal.canUse()
        if (!thisNPC.canPickBestPickaxe()) return false; //canPickBestPickaxe will only check if the NPC has a suitable pickaxe
        thisNPC.tryCheckForChests();
        if (thisNPC.isEnemyNear(5)){
            thisNPC.setMining(false);
            return false;
        }

        if (!(thisNPC.isNormal() || thisNPC.isFreeToWander())) {
            thisNPC.setMining(false);
            return false;
        }

        if (inventoryFull) {
            if (tryCleanInventory()) return true;
        }
        tryCleanBlocksFarAway();
        return tryMine();
    }

    private void tryCleanBlocksFarAway(){
        lastOreCleanTicks += 1;
        if (lastOreCleanTicks < oreCleanCoolDown) return;
        List<Block> blocksToRemove = new ArrayList<>();
        for (Block block : nearbyOreBlocks){
            if (!NPCUtils.isOre(block.getWorld().getBlockAt(block.getLocation()))) blocksToRemove.add(block);
            if (NPCUtils.getDistance(thisNPC.getBukkitEntity(), block) > 15){
                blocksToRemove.add(block);
            }
        }
        for (Block blockToRemove : blocksToRemove){
            nearbyOreBlocks.remove(blockToRemove);
        }
    }

    public boolean V_() {
        return false;
    } //net/minecraft/world/entity/ai/goal/Goal.requiresUpdateEveryTick()

    public void a() { //net/minecraft/world/entity/ai/goal/Goal.tick()
        thisNPC.tryPickBestPickaxe();
        this.navigation.a(this.destination, speed);
        thisNPC.getLocation().setDirection(destinationLocation.subtract(thisNPC.getLocation().toVector()).toVector());
    }

    public void d() { //net/minecraft/world/entity/ai/goal/Goal.start()
    }

    private boolean tryCleanInventory() {
        Location npcLocation = thisNPC.getLocation().clone();
        npcLocation.add(0, 1.8, 0); //Use head height;
        Chest nearestReachableChest = thisNPC.getNearestReachableChest(radius);
        if (nearestReachableChest != null) {
            if (thisNPC.canReachBlock(nearestReachableChest.getBlock())) {
                doChestInteraction(nearestReachableChest);
                return false;
            } else {
                return navigateToLocation(nearestReachableChest.getLocation());
            }
        }
        if (thisNPC.getLastChestSeen() != null){
            if (thisNPC.canReachBlock(thisNPC.getLastChestSeen().getBlock())) {
                doChestInteraction(thisNPC.getLastChestSeen());
            } else {
                return navigateToLocation(thisNPC.getLastChestSeen().getLocation());
            }
        }
        return false;
    }


    public static NPCHelper.ItemValidator.ValidationResult isMinedBlock(ItemStack stack){
        boolean valid;
        if (stack == null) return new NPCHelper.ItemValidator.ValidationResult(false, null);
        valid = (NPCUtils.isOreDrop(stack) || stack.getType() == Material.STONE || stack.getType() == Material.COBBLESTONE);
        return new NPCHelper.ItemValidator.ValidationResult(valid, stack);
    }

    private void doChestInteraction(Chest chestBlock) {
        inventoryFull = !thisNPC.doChestInteraction(chestBlock, PathfinderGoalNPCTryMine::isMinedBlock);
        if (this.mineCobble)thisNPC.addItem(new ItemStack(Material.COBBLESTONE, 1));
        if (this.mineStones)thisNPC.addItem(new ItemStack(Material.STONE, 1));
    }

    private boolean tryMine() {
        if (thisNPC.isHarvesting()) return false;

        Location npcLocation = thisNPC.getLocation().clone();
        npcLocation.add(0, 1.8, 0); //Use head height;
        mineStones = (thisNPC.inventoryContains(Material.STONE));
        mineCobble = (thisNPC.inventoryContains(Material.COBBLESTONE));

        if (TargetLocation != null){
            boolean NPCCanReachBlock = thisNPC.canReachBlock(TargetLocation.block);
            boolean NPCCanSeeBlock = thisNPC.canSeeBlock(TargetLocation.block);
            if (NPCCanReachBlock) {
                if (!thisNPC.tryPickBestPickaxe()) return false; //tryPickBestPickaxe will set the best available pickaxe in the hand
                doBlockMineInteraction(TargetLocation.block);
                thisNPC.setMining(true);
                return false;
            } else {
                thisNPC.setMining(false);
                if (NPCCanSeeBlock) {
                    return navigateToLocation(TargetLocation.location);
                } else {
                    return false;
                }
            }
        }
        tryFindNearbyOreBlocks(npcLocation, radius);
        TargetLocation = thisNPC.findNearestSuitableLocation(nearbyOreBlocks);
        return false;
    }

    private int calculatedMineSpeed(){
        switch (thisNPC.getEquipment().getItemInMainHand().getType()){
            case WOODEN_PICKAXE:
                return 60;
            case STONE_PICKAXE:
            case GOLDEN_PICKAXE:
                return 30;
            case IRON_PICKAXE:
                return 15;
            case DIAMOND_PICKAXE:
                return 10;
            case NETHERITE_PICKAXE:
                return 8;
            default:
                return 120;
        }
    }

    private void doBlockMineInteraction(Block blockToMine) {
        if (!NPCUtils.isOReOrStones(blockToMine)){
            nearbyOreBlocks.remove(blockToMine);
            TargetLocation = null;
            return;
        }
        thisNPC.tryPickBestPickaxe();
        thisNPC.setNPCLookAtBlock(blockToMine);
        thisNPC.playHitAnimation();
        if (lastBreakAttempt.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 5) {
            currentBreakProgress = 0;
        }
        currentBreakProgress += 1;
        lastBreakAttempt = LocalDateTime.now();
        int speed = calculatedMineSpeed();
        if (currentBreakProgress < speed) return;
        thisNPC.setMining(false);
        currentBreakProgress = 0;
        Collection<ItemStack> drops = blockToMine.getDrops(thisNPC.getEquipment().getItemInMainHand());

        List<HashMap<Integer, ItemStack>> nonStoredItems = new ArrayList<>();
        for (ItemStack drop : drops) {
            nonStoredItems.add(thisNPC.addItem(drop));
        }
        inventoryFull = false; //Reset inventory full status

        for (HashMap<Integer, ItemStack> stackHashMap : nonStoredItems) {
            for (ItemStack items : stackHashMap.values()) {
                blockToMine.getWorld().dropItem(blockToMine.getLocation(), items); //Drop items at block location
                inventoryFull = true; //Items had to be dropped, so inventory is full.
            }
        }

        if (!inventoryFull) { //Maybe the inventory is full but items weren't dropped.
            for (ItemStack drop : drops) {
                if (thisNPC.performItemProbe(drop.getType())){
                    inventoryFull = true;
                }
            }
        }

        if (!inventoryFull) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> {
                thisNPC.getLocation().getWorld().playSound(thisNPC.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.05f, 1);
            }, 3L);
        }
        nearbyOreBlocks.remove(blockToMine);
        thisNPC.breakBlock(blockToMine);
    }

    private void tryFindNearbyOreBlocks(Location location, int searchRadius) {
        lastOreSearchTicks += 1;
        if (lastOreSearchTicks < oreSearchCoolDown) return;
        lastOreSearchTicks = 0;
        Set<Block> blocks = NPCUtils.getBlocksSurroundingLocationLowHeight(location, searchRadius);
        for (Block block : blocks){
            if (block.getType().isAir()) continue;
            if (mineStones && block.getType() == Material.STONE) nearbyOreBlocks.add(block);
            if (mineCobble && block.getType() == Material.COBBLESTONE) nearbyOreBlocks.add(block);
            if (NPCUtils.isOre(block) && !NPCUtils.isPS(block)) nearbyOreBlocks.add(block);
        }
    }

    private boolean navigateToLocation(Location location) {
        thisNPC.setMining(false);
        destination = this.navigation.a(location.getX(), location.getY(), location.getZ(), 1);
        destinationLocation = location.clone();
        thisNPC.setNPCLookAtLocation(destinationLocation);
        return true;
    }


}