package cl.mariofinale.NPC;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathEntity;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

class PathfinderGoalNPCTryHarvestCrops extends PathfinderGoal {

    private final double speed;
    private final int radius;
    private final NavigationAbstract navigation;
    private PathEntity destination;
    private final NPCHelper thisNPC;
    private LocalDateTime lastBreakAttempt;
    private boolean inventoryFull;
    private final Random npcRandom;
    private MineBlockLocationInfo TargetLocation;
    private int lastCropSearchTicks;
    private final int cropSearchCoolDown;
    private final Set<Block> nearbyCrops;
    private int lastCropCleanTicks;
    private final int oreCleanCoolDown;

    public PathfinderGoalNPCTryHarvestCrops(EntityInsentient entity, double speed, int radius) {
        this.thisNPC = (NPCHelper) entity;
        this.navigation = thisNPC.getNPCNavigation();
        this.speed = speed;
        this.radius = radius;
        lastBreakAttempt = LocalDateTime.now();
        inventoryFull = false;
        npcRandom = new Random();
        lastCropSearchTicks = 0;
        cropSearchCoolDown = 30;
        nearbyCrops = new HashSet<>();
        lastCropCleanTicks = 0;
        oreCleanCoolDown = 100;
    }

    public boolean V_() {
        return false;
    } //requiresUpdateEveryTick

    public void a() {
        this.navigation.a(this.destination, speed);
    } //tick

    public void e() {

    } //stop

    public boolean b() { //canUse
        if (!thisNPC.canPickBestHoe()) return false;
        thisNPC.tryCheckForChests();
        if (thisNPC.isEnemyNear(5)){
            thisNPC.setHarvesting(false);
            return false;
        }

        if (!(thisNPC.isNormal() || thisNPC.isFreeToWander())) {
            thisNPC.setHarvesting(false);
            return false;
        }

        if (inventoryFull) {
            if (tryCleanInventory()) return true;
        }

        cleanCropsFarAway();
        tryHarvest();
        if (TargetLocation == null){
            if (tryPlantCrops()){
                return true;
            }else{
                thisNPC.setHarvesting(false);
                return false;
            }
        }
        return true;
    }

    private void cleanCropsFarAway(){
        lastCropCleanTicks += 1;
        if (lastCropCleanTicks < oreCleanCoolDown) return;
        List<Block> blocksToRemove = new ArrayList<>();
        for (Block block : nearbyCrops){
            if (!NPCUtils.isCrop(block.getWorld().getBlockAt(block.getLocation()))) blocksToRemove.add(block);
            if (NPCUtils.getDistance(thisNPC.getBukkitEntity(), block) > 15){
                blocksToRemove.add(block);
            }
        }
        for (Block blockToRemove : blocksToRemove){
            nearbyCrops.remove(blockToRemove);
        }
    }

    private boolean tryHarvest() {
        Location npcLocation = thisNPC.getLocation().clone().add(0, 1.8, 0); // Use head height
        TargetLocation = thisNPC.findNearestSuitableLocation(nearbyCrops);
        boolean targetLocationNotNull = TargetLocation != null;
        if (targetLocationNotNull){
            boolean canReachBlock = thisNPC.canReachBlock(TargetLocation.block);
            boolean successfulHarvestInteraction = handleHarvestInteraction();
            if (canReachBlock && successfulHarvestInteraction ) {
                return true;
            }
        }

        tryFindNearbySuitableCrops(npcLocation, radius);
        return TargetLocation != null && navigateToLocation(TargetLocation.location);
    }

    private boolean handleHarvestInteraction() {
        if (!thisNPC.canReachBlock(TargetLocation.block)) {
            thisNPC.setHarvesting(false);
            return thisNPC.canSeeBlock(TargetLocation.block) && navigateToLocation(TargetLocation.location);
        }

        if (thisNPC.tryPickBestHoe()) {
            doCropHarvestInteraction(TargetLocation.block);
            thisNPC.setHarvesting(true);
            return true;
        }
        return false;
    }


    private void tryFindNearbySuitableCrops(Location location, int searchRadius) {
        lastCropSearchTicks += 1;
        if (lastCropSearchTicks < cropSearchCoolDown) return;
        lastCropSearchTicks = 0;
        Set<Block> nearbyBlocks = NPCUtils.getBlocksSurroundingLocationLowHeight(thisNPC.getLocation(),radius);
        for (Block block : nearbyBlocks){
            if (NPCUtils.isCrop(block)){
                if (NPCUtils.isCropSuitableToHarvest(block) && thisNPC.canSeeBlock(block)){
                    if (block.getType() == Material.BEE_NEST || block.getType() == Material.BEEHIVE){
                        if (thisNPC.inventoryContains(Material.GLASS_BOTTLE)){
                            nearbyCrops.add(block);
                        }
                    }else{
                        nearbyCrops.add(block);
                    }
                }
            }
        }
    }

    private void doCropHarvestInteraction(Block blockToHarvest){
        if (!NPCUtils.isCrop(blockToHarvest)){
            nearbyCrops.remove(blockToHarvest);
            return;
        }
        thisNPC.tryPickBestHoe();
        thisNPC.setNPCLookAtBlock(blockToHarvest);
        World npcWorld = thisNPC.getLocation().getWorld();
        if (npcWorld == null) return;
        if (!(lastBreakAttempt.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 1)) return;
        if (blockToHarvest.getType() == Material.BEEHIVE || blockToHarvest.getType() == Material.BEE_NEST ){
            doHoneyHarvestInteraction(blockToHarvest);
            return;
        }

        List<Block> blocksToHarvest = new ArrayList<>();
        blocksToHarvest.add(blockToHarvest);

        if (blockToHarvest.getType() == Material.SUGAR_CANE || blockToHarvest.getType() == Material.CACTUS ||
                blockToHarvest.getType() == Material.BAMBOO){
            for (int i = 1; i <= 50; i++){
                Block upperBlock = blockToHarvest.getWorld().getBlockAt(blockToHarvest.getX(), blockToHarvest.getY() + i, blockToHarvest.getZ());
                if (upperBlock.getType() == blockToHarvest.getType()){
                    blocksToHarvest.add(upperBlock);
                }
            }
            nearbyCrops.remove(blockToHarvest); //remove base block;
        }

        if (blocksToHarvest.isEmpty()) return;

        List<ItemStack> drops = new ArrayList<>();
        for (Block block : blocksToHarvest){
            drops.addAll(block.getDrops(thisNPC.getEquipment().getItemInMainHand()));
        }
        List<Block> tempBlocks = new ArrayList<>(blocksToHarvest);
        for (Block block : tempBlocks){
            if (!NPCUtils.isCrop(block))blocksToHarvest.remove(block);
        }

        if (blockToHarvest.getType() == Material.SWEET_BERRY_BUSH){
            int fortuneLvl = thisNPC.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.FORTUNE);
            ItemStack berries = new ItemStack(Material.SWEET_BERRIES, npcRandom.nextInt(2) + 1 + fortuneLvl);
            drops.add(berries);
        }

        List<HashMap<Integer, ItemStack>> nonStoredItems = new ArrayList<>();
        for (ItemStack drop : drops) {
            nonStoredItems.add(thisNPC.addItem(drop));
        }
        inventoryFull = false; //Reset inventory full status

        for (HashMap<Integer, ItemStack> stackHashMap : nonStoredItems) {
            for (ItemStack items : stackHashMap.values()) {
                blockToHarvest.getWorld().dropItem(blockToHarvest.getLocation(), items); //Drop items at block location
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
                npcWorld.playSound(thisNPC.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.05f, 1);
            }, 3L);
        }

        thisNPC.playHitAnimation();
        lastBreakAttempt = LocalDateTime.now();

        for (Block block : blocksToHarvest){
            if (blockToHarvest.getType() == Material.SWEET_BERRY_BUSH){
                Ageable ageable = (Ageable) blockToHarvest.getBlockData();
                ageable.setAge(1);
                thisNPC.setBlockData(blockToHarvest, ageable);
                nearbyCrops.remove(blockToHarvest);
            } else {
                Material cropType = block.getType();
                Material seedType = NPCUtils.getReplantSeedType(block);
                thisNPC.breakBlock(block);
                nearbyCrops.remove(blockToHarvest);
                tryPlantCrop(seedType, cropType, block);
            }
        }
    }

    public void doHoneyHarvestInteraction(Block blockToHarvest){
        Location npcLocation = thisNPC.getLocation();
        if (npcLocation == null) return;
        World npcWorld = npcLocation.getWorld();
        if (npcWorld == null) return;
        if (blockToHarvest.getType() != Material.BEEHIVE && blockToHarvest.getType() != Material.BEE_NEST) return;
        if (thisNPC.findItem(Material.GLASS_BOTTLE) == null) return; // Direct null check
        BlockState state = blockToHarvest.getState();
        if (!(state.getBlockData() instanceof Beehive beehive)) return; // Safety check
        if (beehive.getHoneyLevel() != beehive.getMaximumHoneyLevel()) return;

        if (thisNPC.isInventoryFull()) { //Honey glasses don't stack so we just check for a full inventory
            inventoryFull = true;
            return;
        }
        nearbyCrops.remove(blockToHarvest);

        thisNPC.playHitAnimation();
        lastBreakAttempt = LocalDateTime.now();
        npcWorld.playSound(npcLocation, Sound.ITEM_BOTTLE_FILL, 1f, 1);
        thisNPC.removeItem(new ItemStack(Material.GLASS_BOTTLE, 1)); // Use removeItem
        thisNPC.addItem(new ItemStack(Material.HONEY_BOTTLE, 1)); // Use addItem
        beehive.setHoneyLevel(0);
        thisNPC.setBlockData(blockToHarvest, beehive);

        tryCraftHoneyBlock(npcLocation, npcWorld);
    }

    public void tryCraftHoneyBlock(@NotNull Location npcLocation, @NotNull World npcWorld){
        if (thisNPC.getItemCount(Material.HONEY_BOTTLE) < 4) return; // Use getItemCount
        if (thisNPC.isInventoryFull()) { //Honey glasses don't stack so we just check for a full inventory after harvesting honey
            inventoryFull = true;
            return;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> {
            thisNPC.removeItem(new ItemStack(Material.HONEY_BOTTLE, 4));
            thisNPC.addItem(new ItemStack(Material.GLASS_BOTTLE, 4));
            thisNPC.addItem(new ItemStack(Material.HONEY_BLOCK, 1));
            npcWorld.playSound(npcLocation, Sound.BLOCK_HONEY_BLOCK_FALL, 1, 1);
        }, 20L);
    }

    public boolean tryPlantCrops() {

        // Check if there are replant seeds in the inventory
        if (!inventoryHasReplantSeeds()) {
            return false; // No seeds available, no action needed
        }

        // Get the nearest suitable farm land
        MineBlockLocationInfo nearestSuitableFarmLand = getNearestSuitableFarmLand();
        if (nearestSuitableFarmLand == null) {
            return false; // No suitable farm land found, no action needed
        }

        // Check if NPC can reach the farm land block

        if (thisNPC.canReachBlock(nearestSuitableFarmLand.block)) {
            Block blockOverFarmLand = nearestSuitableFarmLand.block.getWorld().getBlockAt(nearestSuitableFarmLand.block.getLocation());
            // Check if the block over the farm land is clear (AIR)
            if (blockOverFarmLand.getType() != Material.AIR) {
                return false; // Block is not clear, no action needed
            }

            // Attempt to plant crops in adjacent blocks
            boolean cropsPlanted = tryPlantAdjacentCrops(blockOverFarmLand);

            if (!cropsPlanted) {
                // If no crops were planted adjacent, plant a random seed from inventory
                Material randomSeed = getRandomReplantSeedMaterialInInventory();
                if (randomSeed != null) {
                    Material cropType = NPCUtils.getCropTypeFromReplantSeedType(randomSeed);
                    return tryPlantCrop(randomSeed, cropType, blockOverFarmLand);
                }
            }

            return false; // No action needed (crops planted or no suitable seed found)
        } else {
            // NPC cannot reach the farm land, navigate to the location
            thisNPC.setHarvesting(true);
            return navigateToLocation(nearestSuitableFarmLand.location);
        }
    }

    private boolean tryPlantAdjacentCrops(Block blockOverFarmLand) {
        boolean cropsPlanted = false;
        thisNPC.tryPickBestHoe();

        // Attempt to plant crops in adjacent blocks (east, west, south, north)
        cropsPlanted |= tryPlantAdjacentCrop(blockOverFarmLand, 1, 0); // East
        cropsPlanted |= tryPlantAdjacentCrop(blockOverFarmLand, -1, 0); // West
        cropsPlanted |= tryPlantAdjacentCrop(blockOverFarmLand, 0, 1); // South
        cropsPlanted |= tryPlantAdjacentCrop(blockOverFarmLand, 0, -1); // North

        return cropsPlanted;
    }

    private boolean tryPlantAdjacentCrop(Block blockOverFarmLand, int offsetX, int offsetZ) {
        Block adjacentCrop = blockOverFarmLand.getWorld().getBlockAt(blockOverFarmLand.getLocation().add(offsetX, 0, offsetZ));
        Material cropType = adjacentCrop.getType();
        Material seedType = NPCUtils.getReplantSeedType(cropType);

        if (tryPlantCrop(seedType, cropType, adjacentCrop)) {
            return true; // Crop planted successfully
        }

        return false; // No action needed or planting failed
    }

    private boolean tryPlantCrop(Material seedType,Material cropType, Block blockToPlant){
        if (seedType != null && thisNPC.inventoryContains(seedType) && blockToPlant.getType() == Material.AIR) {
            thisNPC.removeItem(new ItemStack(seedType, 1));
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> {
                thisNPC.setNPCLookAtBlock(blockToPlant);
                thisNPC.playHitAnimation();
                thisNPC.setBlockData(blockToPlant, cropType.createBlockData());
                blockToPlant.getWorld().playSound(blockToPlant.getLocation(), blockToPlant.getBlockData().getSoundGroup().getPlaceSound() , 0.05f, 1);
            }, 3L);
            return true;
        }
        return false;
    }

    public boolean inventoryHasReplantSeeds() {
        for (Material seedType : NPCStaticVariables.ReplantSeedTypes) {
            if (thisNPC.inventoryContains(seedType)) {
                return true;
            }
        }
        return false;
    }

    public Material getRandomReplantSeedMaterialInInventory() {
        List<Material> materials = new ArrayList<>();
        for (Material seedType : NPCStaticVariables.ReplantSeedTypes) {
            if (thisNPC.inventoryContains(seedType)) {
                materials.add(seedType);
            }
        }
        if (materials.isEmpty()) return null;
        return materials.get(npcRandom.nextInt(materials.size()));
    }

    private MineBlockLocationInfo getNearestSuitableFarmLand(){
        Set<Block> nearbyBlocks = NPCUtils.getBlocksSurroundingLocationLowHeight(thisNPC.getLocation(),radius);
        Set<Block> nearbySuitableCrops = new HashSet<>();
        for (Block block : nearbyBlocks){
            if (block.isEmpty()) continue;
            if (block.getType() == Material.FARMLAND){
                if (block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).getType() == Material.AIR){
                    nearbySuitableCrops.add(block.getWorld().getBlockAt(block.getLocation().add(0,1,0)));
                }
            }
        }
        return thisNPC.findNearestSuitableLocation(nearbySuitableCrops);
    }

    private boolean navigateToLocation(Location location) {
        thisNPC.tryPickBestHoe();
        destination = this.navigation.a(location.getX(), location.getY(), location.getZ(), 1);
        Location destinationLocation = location.clone();
        thisNPC.setNPCLookAtLocation(destinationLocation);
        thisNPC.setHarvesting(false);
        return true;
    }

    private boolean tryCleanInventory() {
        Location npcLocation = thisNPC.getLocation().clone();
        npcLocation.add(0, 1.8, 0); //Use head height;
        Chest nearestChest = thisNPC.getNearestReachableChest(radius);
        if (nearestChest != null) {
            if (thisNPC.canReachBlock(nearestChest.getBlock())) {
                doChestInteraction(nearestChest);
                return false;
            } else {
                return navigateToLocation(nearestChest.getLocation());
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

    private void doChestInteraction(@NotNull Chest chest) {
        inventoryFull = !thisNPC.doChestInteraction(chest, NPCUtils::isCropDrop);
    }

}
