package cl.mariofinale.NPC;


import cl.mariofinale.PekoSrvFun;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.sensing.EntitySenses;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.Items;

import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R3.event.CraftEventFactory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static java.lang.Math.round;


public class NPCHelper extends EntityZombie implements InventoryHolder, IRangedEntity {
    private Inventory inventory;
    private String Status;
    private final String Owner;
    private String NPCUserName;
    private String NPCName;
    private CraftEntity NPCEntity;
    private Zombie NPCZombieEntity;
    private EntityInsentient NPCNmsEntity;
    private boolean Mining;
    private boolean Harvesting;
    private double Reach;
    private double MovementSpeed;
    private int TicksUntilNextAttack;
    private int AttackCoolDown;
    private Chest lastChestSeen;
    private int lastChestSearchTick;
    private int lastChestSearchCoolDown;
    private Location spawnLocation;

    //region Initialization

    /**
     * Constructs an NPCHelper entity at a specified location with given owner, username, and name.
     * @param loc The spawn location of the NPC.
     * @param ownerName The name of the NPC's owner.
     * @param userName The username associated with the NPC's disguise.
     * @param npcName The display name of the NPC.
     * @param dataContainer Persistent data container for loading existing NPC data, or null for a new NPC.
     */
    public NPCHelper(Location loc, String ownerName, String userName, String npcName, PersistentDataContainer dataContainer) {
        super(EntityTypes.bN, ((CraftWorld) loc.getWorld()).getHandle()); //net/minecraft/world/entity/EntityType.ZOMBIE
        this.Owner = ownerName;
        for (Entity ent : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
            if (NPCHelper.isNPC(ent)) {
                if (ent.getCustomName().equals(npcName)) {
                    this.remove(RemovalReason.b, EntityRemoveEvent.Cause.DESPAWN);
                    return;
                }
            }
        }
        spawnLocation = loc.clone();
        MovementSpeed = 0.30D;
        Reach = 3;
        lastChestSearchTick = 0;
        lastChestSearchCoolDown = 100;
        this.persist = true;
        this.NPCUserName = userName;
        this.NPCName = npcName;
        this.Status = "Normal";
        this.Mining = false;
        this.Harvesting = false;
        this.NPCEntity = this.getBukkitEntity();
        this.NPCZombieEntity = (Zombie) NPCEntity;
        this.NPCNmsEntity = (EntityInsentient) (NPCEntity).getHandle();

        initializeAttributesAndSpawn(loc);

        if (dataContainer == null) {
            setupNewNPCData();
        } else {
            loadNPCData(dataContainer);
        }

        disguise();

        scheduleInitialTeleport(loc);
    }

    /**
     * Initializes the NPC's equipment and AI goals.
     */
    private void initializeEquipmentAndGoals() {
        this.getEquipment().clear();
        PathfinderGoalSelector goalSelector = NPCNmsEntity.bS; //net/minecraft/world/entity/Mob.goalSelector
        PathfinderGoalSelector targetSelector = NPCNmsEntity.bT; //net/minecraft/world/entity/Mob.targetSelector

        try {
            Field dField = PathfinderGoalSelector.class.getDeclaredField("c"); //net/minecraft/world/entity/ai/goal/GoalSelector.availableGoals
            dField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            dField.set(targetSelector, new LinkedHashSet<>());

            Field cField = PathfinderGoalSelector.class.getDeclaredField("b"); //net/minecraft/world/entity/ai/goal/GoalSelector.lockedFlags
            cField.setAccessible(true);
            cField.set(goalSelector, new EnumMap<>(PathfinderGoal.Type.class));
            cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add movement and behavior goals
        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(0, new PathfinderGoalNPCDoorInteract(this));
        goalSelector.a(1, new PathfinderGoalNPCWalkNearPlayer(this, 1.2D, Owner));
        goalSelector.a(2, new PathfinderGoalNPCBowShoot<>(this, 1.0D, 20, 20));
        goalSelector.a(3, new PathfinderGoalNPCMeleeAttack(this, 1.2D, false, 3.5D, 20));
        goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.5f));
        goalSelector.a(4, new PathfinderGoalNPCTryMine(this, 0.9D, 20));
        goalSelector.a(4, new PathfinderGoalNPCTryHarvestCrops(this, 0.8D, 20));
        goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        goalSelector.a(6, new PathfinderGoalNPCRandomStroll(this, 1D));
        goalSelector.a(7, new PathfinderGoalNPCLookAtPlayer(this, EntityHuman.class, 8.0F));
        goalSelector.a(8, new PathfinderGoalNPCRandomLookAround(this));

        // Add target selection goals
        targetSelector.a(0, new PathfinderGoalNPCTryPreventImminentDeath(this, 1.3D));
        targetSelector.a(0, new PathfinderGoalHurtByTarget(this, EntityPlayer.class));
        targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityCreeper.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntitySpider.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityCaveSpider.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityWither.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityEnderDragon.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, Warden.class, true));
        targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeleton.class, true));
        targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityBlaze.class, true));
        targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonStray.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, Bogged.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, Breeze.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonWither.class, true));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombie.class, true));
        targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombieVillager.class, true));
        targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombieHusk.class, true));
        targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityPillager.class, true));
        targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityWitch.class, true));
        targetSelector.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityVindicator.class, true));
        targetSelector.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityIllagerWizard.class, true));
        targetSelector.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityEvoker.class, true));
        targetSelector.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityHoglin.class, true));
        targetSelector.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityZoglin.class, true));
        targetSelector.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityRavager.class, true));
        targetSelector.a(15, new PathfinderGoalNearestAttackableTarget<>(this, EntitySilverfish.class, true));
        targetSelector.a(15, new PathfinderGoalNearestAttackableTarget<>(this, EntityVex.class, true));
        targetSelector.a(15, new PathfinderGoalNearestAttackableTarget<>(this, EntityPhantom.class, true));
    }

    public static Plugin plugin(){
        return PekoSrvFun.plugin;
    }

    public static NamespacedKey NPCHelperTypeKey(){
        return PekoSrvFun.holoPetTypeKey;
    }

    public static NamespacedKey NPCHelperNameKey(){
        return PekoSrvFun.holoPetNameKey;
    }

    public static NamespacedKey NPCHelperStatusKey(){
        return PekoSrvFun.holoPetStatusKey;
    }

    public static NamespacedKey NPCHelperOwnerKey(){
        return PekoSrvFun.holoPetOwnerKey;
    }

    public static NamespacedKey NPCHelperInventoryKey(){
        return PekoSrvFun.holoPetInventoryKey;
    }

    /**
     * Initializes NPC attributes and spawns it in the world.
     * @param loc The location to spawn the NPC.
     */
    private void initializeAttributesAndSpawn(Location loc) {
        this.craftAttributes.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(5.0D);
        this.craftAttributes.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(MovementSpeed);
        this.craftAttributes.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0D);

        ((CraftWorld) loc.getWorld()).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        NPCZombieEntity.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        NPCZombieEntity.setCanPickupItems(true);
        NPCZombieEntity.setPersistent(true);
        NPCZombieEntity.setRemoveWhenFarAway(false);
        NPCZombieEntity.setCanBreakDoors(false);
        NPCZombieEntity.setAdult();
    }

    /**
     * Sets up persistent data for a new NPC.
     */
    private void setupNewNPCData() {
        PersistentDataContainer container = NPCEntity.getPersistentDataContainer();
        container.set(NPCHelperTypeKey(), PersistentDataType.STRING, NPCUserName);
        container.set(NPCHelperNameKey(), PersistentDataType.STRING, NPCName);
        container.set(NPCHelperOwnerKey(), PersistentDataType.STRING, Owner);
        container.set(NPCHelperStatusKey(), PersistentDataType.STRING, Status);
        this.inventory = createNPCInventory();
        container.set(NPCHelperInventoryKey(), PersistentDataType.STRING, NPCUtils.toBase64(this.inventory));
    }

    /**
     * Loads persistent data for an existing NPC.
     * @param dataContainer The data container with NPC data.
     */
    private void loadNPCData(PersistentDataContainer dataContainer) {
        PersistentDataContainer container = NPCEntity.getPersistentDataContainer();
        if (dataContainer.has(NPCHelperStatusKey(), PersistentDataType.STRING)) {
            setStatus(dataContainer.get(NPCHelperStatusKey(), PersistentDataType.STRING));
        } else {
            setStatus("Normal");
        }
        if (dataContainer.has(NPCHelperTypeKey(), PersistentDataType.STRING)) {
            this.NPCUserName = dataContainer.get(NPCHelperTypeKey(), PersistentDataType.STRING);
        }
        if (dataContainer.has(NPCHelperNameKey(), PersistentDataType.STRING)) {
            this.NPCName = dataContainer.get(NPCHelperNameKey(), PersistentDataType.STRING);
        }
        container.set(NPCHelperTypeKey(), PersistentDataType.STRING, NPCUserName);
        container.set(NPCHelperNameKey(), PersistentDataType.STRING, NPCName);
        container.set(NPCHelperOwnerKey(), PersistentDataType.STRING, Owner);

        if (dataContainer.has(NPCHelperInventoryKey(), PersistentDataType.STRING)) {
            String encodedInv = dataContainer.get(NPCHelperInventoryKey(), PersistentDataType.STRING);
            try {
                this.inventory = NPCUtils.fromBase64(encodedInv);
            } catch (IOException e) {
                plugin().getLogger().severe("[NPC] Error loading pet inventory.");
                plugin().getLogger().severe(e.getMessage());
            }
            if (this.inventory != null) {
                setNPCInventory();
            }
            container.set(NPCHelperInventoryKey(), PersistentDataType.STRING, encodedInv);
        } else {
            this.inventory = createNPCInventory();
            container.set(NPCHelperInventoryKey(), PersistentDataType.STRING, NPCUtils.toBase64(this.inventory));
        }
    }

    /**
     * Schedules a delayed teleport to ensure proper spawning.
     * @param loc The initial spawn location.
     */
    private void scheduleInitialTeleport(Location loc) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
            World spawnWorld = spawnLocation.getWorld();
            if (spawnWorld == null) return;
            if (!spawnWorld.getBlockAt(0, 0, 0).getLocation().getChunk().isLoaded()) {
                spawnWorld.getBlockAt(0, 0, 0).getLocation().getChunk();
            }
            if (!spawnLocation.getChunk().isLoaded()) {
                spawnLocation.getChunk();
            }
            initializeEquipmentAndGoals();
            NPCEntity.teleport(spawnLocation);
            plugin().getLogger().info("Entity ID: " + NPCEntity.getEntityId());
            plugin().getLogger().info("Location: " + NPCEntity.getLocation());
        }, 5L);
    }

    //endregion

    //region Inventory Management

    /**
     * Creates a new inventory for the NPC.
     * @return A new inventory with the NPC's name and health in the title.
     */
    public Inventory createNPCInventory() {
        return Bukkit.createInventory(this, 27, getNPCName() + " | HP: " + round(this.getHealth()) + "/20");
    }

    /**
     * Drops all items from the NPC's inventory at its location.
     */
    public void dropInventory() {
        synchronized (inventory) {
            Set<ItemStack> stacks = new HashSet<>();
            Collections.addAll(stacks, inventory.getContents());
            for (ItemStack stack : stacks) {
                if (stack == null) continue;
                this.getWorld().dropItem(this.getLocation(), stack);
            }
            this.clearInventory();
        }
    }

    /**
     * Checks if the inventory contains a specific material.
     * @param material The material to check for.
     * @return True if the material is present, false otherwise.
     */
    public boolean inventoryContains(Material material) {
        return this.inventory.contains(material);
    }

    /**
     * Checks if the inventory contains a specific item stack.
     * @param stack The item stack to check for.
     * @return True if the item stack is present, false otherwise.
     */
    public boolean inventoryContains(ItemStack stack) {
        return this.inventory.contains(stack);
    }

    /**
     * Updates the NPC's equipment based on its inventory contents.
     */
    public void setNPCInventory() {
        synchronized (inventory) {
            EntityEquipment equipment = this.getEquipment();
            if (equipment == null) return;
            ItemStack helmet = equipment.getHelmet();
            ItemStack chestPlate = equipment.getChestplate();
            ItemStack leggings = equipment.getLeggings();
            ItemStack boots = equipment.getBoots();
            ItemStack mainHand = equipment.getItemInMainHand();
            ItemStack offHand = equipment.getItemInOffHand();

            if (helmet != null && !inventory.contains(helmet.getType())) {
                equipment.setHelmet(new ItemStack(Material.AIR, 1));
            }
            if (chestPlate != null && !inventory.contains(chestPlate.getType())) {
                equipment.setChestplate(new ItemStack(Material.AIR, 1));
            }
            if (leggings != null && !inventory.contains(leggings.getType())) {
                equipment.setLeggings(new ItemStack(Material.AIR, 1));
            }
            if (boots != null && !inventory.contains(boots.getType())) {
                equipment.setBoots(new ItemStack(Material.AIR, 1));
            }
            if (!inventory.contains(mainHand.getType())) {
                equipment.setItemInMainHand(new ItemStack(Material.AIR, 1));
            }
            if (!inventory.contains(offHand.getType())) {
                equipment.setItemInOffHand(new ItemStack(Material.AIR, 1));
            }

            if (inventory.contains(Material.TOTEM_OF_UNDYING)) {
                if (equipment.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
                    equipment.setItemInMainHand(new ItemStack(Material.AIR, 1));
                }
                equipment.setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
            }

            pickBestNPCEquipment();
            saveInventory();
        }
    }

    /**
     * Saves the current inventory to persistent data.
     */
    public void saveInventory() {
        PersistentDataContainer container = this.getBukkitEntity().getPersistentDataContainer();
        container.set(NPCHelperInventoryKey(), PersistentDataType.STRING, NPCUtils.toBase64(inventory));
    }

    /**
     * Probes if an item can be added to the inventory.
     * @param probeMaterial The material to test.
     * @return True if the inventory is full and the item cannot be added, false otherwise.
     */
    public boolean performItemProbe(Material probeMaterial) {
        synchronized (inventory) {
            ItemStack probeItem = new ItemStack(probeMaterial, 1);
            Map<Integer, ItemStack> leftover = inventory.addItem(probeItem);
            boolean inventoryFull = !leftover.isEmpty();
            if (!inventoryFull) {
                inventory.removeItem(probeItem);
            }
            this.saveInventory();
            return inventoryFull;
        }
    }

    /**
     * Checks if the inventory is completely full.
     * @return True if no space is available, false otherwise.
     */
    public boolean isInventoryFull() {
        synchronized (inventory) {
            for (ItemStack item : inventory.getContents()) {
                if (item == null || item.getAmount() < item.getMaxStackSize()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Adds an item to the inventory.
     * @param item The item to add.
     * @return A map of slot indices to items that could not be added.
     */
    public HashMap<Integer, ItemStack> addItem(ItemStack item) {
        synchronized (inventory) {
            HashMap<Integer, ItemStack> result = inventory.addItem(item);
            this.saveInventory();
            return result;
        }
    }

    /**
     * Removes an item from the inventory.
     * @param item The item to remove.
     * @return A map of slot indices to items that could not be removed.
     */
    public HashMap<Integer, ItemStack> removeItem(ItemStack item) {
        if (item == null) return new HashMap<>();
        synchronized (inventory) {
            HashMap<Integer, ItemStack> result = inventory.removeItem(item);
            this.saveInventory();
            return result;
        }
    }

    /**
     * Clears all items from the inventory.
     */
    public void clearInventory() {
        synchronized (inventory) {
            inventory.clear();
            this.saveInventory();
        }
    }

    /**
     * Finds the first occurrence of an item of a specific type.
     * @param type The material type to search for.
     * @return The first matching item stack, or null if not found.
     */
    public ItemStack findItem(Material type) {
        synchronized (inventory) {
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() == type) {
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * Counts the total number of items of a specific type.
     * @param type The material type to count.
     * @return The total amount of items of the specified type.
     */
    public int getItemCount(Material type) {
        synchronized (inventory) {
            int count = 0;
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() == type) {
                    count += item.getAmount();
                }
            }
            return count;
        }
    }

    /**
     * Transfers items from a container to the NPC's inventory.
     * @param containerInventory The inventory to transfer from.
     * @return True if all items were transferred, false if some remained due to full inventory.
     */
    public boolean transferFromContainerToNPCInventory(Inventory containerInventory) {
        synchronized (inventory) {
            List<ItemStack> remainingItems = new ArrayList<>();
            for (ItemStack item : containerInventory.getContents()) {
                if (item != null) {
                    HashMap<Integer, ItemStack> remaining = this.addItem(item);
                    if (!remaining.isEmpty()) {
                        remainingItems.addAll(remaining.values());
                    }
                }
            }
            containerInventory.clear();
            for (ItemStack item : remainingItems) {
                containerInventory.addItem(item);
            }
            this.saveInventory();
            return remainingItems.isEmpty();
        }
    }

    /**
     * Transfers all items from the NPC's inventory to a container.
     * @param containerInventory The inventory to transfer to.
     * @return True if all items were transferred, false if some remained due to full container.
     */
    public boolean transferFromNPCInventoryToContainer(Inventory containerInventory) {
        synchronized (inventory) {
            List<ItemStack> remainingItems = new ArrayList<>();
            for (ItemStack item : this.inventory.getContents()) {
                if (item != null) {
                    HashMap<Integer, ItemStack> remaining = containerInventory.addItem(item);
                    if (!remaining.isEmpty()) {
                        remainingItems.addAll(remaining.values());
                    }
                }
            }
            for (ItemStack item : remainingItems) {
                this.removeItem(item);
            }
            this.saveInventory();
            return remainingItems.isEmpty();
        }
    }

    /**
     * Interface for validating items during transfer.
     */
    public interface ItemValidator {
        ValidationResult validate(ItemStack stack);

        record ValidationResult(boolean valid, ItemStack stack) {
        }
    }

    /**
     * Transfers validated items from the NPC's inventory to a container.
     * @param container The inventory to transfer to.
     * @param validator The validator to determine which items to transfer.
     * @return True if all validated items were transferred, false if some remained.
     */
    public boolean transferFromNPCInventoryToContainer(Inventory container, ItemValidator validator) {
        synchronized (inventory) {
            boolean allItemsTransferred = true;
            List<ItemStack> itemsToTransfer = new ArrayList<>();
            for (ItemStack stack : this.inventory.getContents()) {
                if (stack != null && validator.validate(stack).valid()) {
                    itemsToTransfer.add(stack.clone());
                }
            }
            for (ItemStack stack : itemsToTransfer) {
                Map<Integer, ItemStack> remaining = container.addItem(stack);
                if (!remaining.isEmpty()) {
                    allItemsTransferred = false;
                    for (ItemStack leftover : remaining.values()) {
                        this.addItem(leftover);
                    }
                } else {
                    this.removeItem(stack);
                }
            }
            this.saveInventory();
            return allItemsTransferred;
        }
    }

    /**
     * Interacts with a chest, transferring validated items to it.
     * @param chest The chest to interact with.
     * @param validator The validator for items to transfer.
     * @return True if the interaction was successful, false if it failed or inventory is full afterward.
     */
    public boolean doChestInteraction(@NotNull Chest chest, ItemValidator validator) {
        this.setNPCLookAtBlock(chest.getBlock());
        this.playHitAnimation();
        Location location = chest.getLocation();
        location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 0.1f, 1f);
        chest.open();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
            location.getWorld().playSound(location, Sound.BLOCK_CHEST_CLOSE, 0.1f, 1f);
            chest.close();
        }, 10L);
        try {
            boolean success = this.transferFromNPCInventoryToContainer(chest.getInventory(), validator);
            if (!success) {
                // transferFromNPCInventoryToContainer handles item returning
            }
        } catch (NullPointerException e) {
            plugin().getLogger().severe("Failed to transfer items from NPC " + this.getNPCName() + " to chest. Exception: " + e.getMessage());
        }
        this.saveInventory();
        return true;
    }

    /**
     * Gets the NPC's inventory (direct access is deprecated).
     * @return The NPC's inventory.
     * @deprecated Use encapsulated methods instead.
     */
    @Deprecated
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets a copy of the inventory for player GUI display.
     * @return A new inventory with the same contents and updated title.
     */
    public Inventory getInventoryForPlayerGUI() {
        Inventory newInventory = Bukkit.createInventory(this, 27, getNPCName() + " | HP: " + round(this.getHealth()) + "/20");
        newInventory.setContents(inventory.getContents());
        inventory = newInventory;
        return inventory;
    }

    /**
     * Sets the NPC's inventory to a new one.
     * @param newInventory The new inventory to set.
     */
    public void setInventory(Inventory newInventory) {
        inventory = newInventory;
        this.saveInventory();
    }

    //endregion

    //region Equipment Management

    /**
     * Gets the NPC's equipment.
     * @return The EntityEquipment instance for the NPC.
     */
    public EntityEquipment getEquipment() {
        return NPCZombieEntity.getEquipment();
    }

    /**
     * Sets whether the NPC is gliding.
     * @param value True to enable gliding, false to disable.
     */
    public void setGliding(boolean value) {
        NPCZombieEntity.setGliding(value);
    }

    /**
     * Gets the chestplate item, if any.
     * @return An Optional containing the chestplate, or empty if none.
     */
    public Optional<ItemStack> getChestPlate() {
        return Optional.ofNullable(this.getEquipment()).map(EntityEquipment::getChestplate);
    }

    /**
     * Gets the helmet item, if any.
     * @return An Optional containing the helmet, or empty if none.
     */
    public Optional<ItemStack> getHelmet() {
        return Optional.ofNullable(this.getEquipment()).map(EntityEquipment::getHelmet);
    }

    /**
     * Gets the leggings item, if any.
     * @return An Optional containing the leggings, or empty if none.
     */
    public Optional<ItemStack> getLeggings() {
        return Optional.ofNullable(this.getEquipment()).map(EntityEquipment::getLeggings);
    }

    /**
     * Gets the boots item, if any.
     * @return An Optional containing the boots, or empty if none.
     */
    public Optional<ItemStack> getBoots() {
        return Optional.ofNullable(this.getEquipment()).map(EntityEquipment::getBoots);
    }

    /**
     * Gets the main hand item, if any.
     * @return An Optional containing the main hand item, or empty if none.
     */
    public Optional<ItemStack> getItemInMainHand() {
        return Optional.ofNullable(this.getEquipment()).map(EntityEquipment::getItemInMainHand);
    }

    /**
     * Gets the off-hand item, if any.
     * @return An Optional containing the off-hand item, or empty if none.
     */
    public Optional<ItemStack> getItemInOffHand() {
        return Optional.ofNullable(this.getEquipment()).map(EntityEquipment::getItemInOffHand);
    }

    /**
     * Equips the best available equipment from the inventory.
     */
    public void pickBestNPCEquipment() {
        pickBestHelmet();
        pickBestChestPlate();
        pickBestLeggings();
        pickBestBoots();
        pickBestMeleeWeapon();
        checkEquipBow();
    }

    /**
     * Equips the best helmet from the inventory.
     */
    public void pickBestHelmet() {
        ItemStack bestHelmet = NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.HelmetPriorityArray);
        if (bestHelmet != null) this.getEquipment().setHelmet(bestHelmet);
    }

    /**
     * Equips the best chestplate from the inventory.
     */
    public void pickBestChestPlate() {
        ItemStack bestChestPlate = NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.ChestPlatePriorityArray);
        if (bestChestPlate != null) this.getEquipment().setChestplate(bestChestPlate);
    }

    /**
     * Equips the best leggings from the inventory.
     */
    public void pickBestLeggings() {
        ItemStack bestLeggings = NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.LeggingsPriorityArray);
        if (bestLeggings != null) this.getEquipment().setLeggings(bestLeggings);
    }

    /**
     * Equips the best boots from the inventory.
     */
    public void pickBestBoots() {
        ItemStack bestBoots = NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.BootsPriorityArray);
        if (bestBoots != null) this.getEquipment().setBoots(bestBoots);
    }

    /**
     * Equips the best melee weapon from the inventory.
     */
    public void pickBestMeleeWeapon() {
        ItemStack bestMeleeWeapon = NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.MeleeWeaponPriorityArray);
        if (bestMeleeWeapon != null) this.getEquipment().setItemInMainHand(bestMeleeWeapon);
    }

    /**
     * Attempts to equip the best pickaxe from the inventory.
     * @return True if a pickaxe was equipped, false otherwise.
     */
    public boolean tryPickBestPickaxe() {
        ItemStack bestPickaxe = NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.PickaxePriorityArray);
        if (bestPickaxe != null) {
            this.getEquipment().setItemInMainHand(bestPickaxe);
            return true;
        }
        return false;
    }

    /**
     * Checks if a pickaxe can be equipped.
     * @return True if a pickaxe is available, false otherwise.
     */
    public boolean canPickBestPickaxe() {
        return NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.PickaxePriorityArray) != null;
    }

    /**
     * Attempts to equip the best hoe from the inventory.
     * @return True if a hoe was equipped, false otherwise.
     */
    public boolean tryPickBestHoe() {
        ItemStack bestHoe = NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.HoePriorityArray);
        if (bestHoe != null) {
            this.getEquipment().setItemInMainHand(bestHoe);
            return true;
        }
        return false;
    }

    /**
     * Checks if a hoe can be equipped.
     * @return True if a hoe is available, false otherwise.
     */
    public boolean canPickBestHoe() {
        return NPCUtils.pickBestFromMaterialArray(inventory, NPCStaticVariables.HoePriorityArray) != null;
    }

    /**
     * Equips a bow if available along with arrows.
     * @return True if a bow was equipped, false otherwise.
     */
    public boolean checkEquipBow() {
        if (inventory.contains(Material.BOW) && inventory.contains(Material.ARROW)) {
            for (ItemStack stack : inventory.getStorageContents()) {
                if (stack != null && stack.getType() == Material.BOW) {
                    this.getEquipment().setItemInMainHand(stack);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Attempts to equip the best weapon (bow if arrows are present, otherwise melee).
     */
    public void tryPickBestWeapon() {
        if (inventory.contains(Material.BOW) && inventory.contains(Material.ARROW)) {
            checkEquipBow();
        } else {
            pickBestMeleeWeapon();
        }
    }

    //endregion

    //region NPC Behavior

    /**
     * Breaks a block and handles related events and logging.
     * @param block The block to break.
     */
    public void breakBlock(Block block) {
        BlockData data = Material.AIR.createBlockData();
        EntityChangeBlockEvent event = new EntityChangeBlockEvent(NPCZombieEntity, block, data);
        this.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 0.5f, 1);
        if (PekoSrvFun.CoreProtectPresent) {
            PekoSrvFun.CoreProtectApiInstance.logRemoval("#NPC:" + NPCUserName, block.getLocation(), block.getType(), block.getBlockData());
        }
        block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
        block.getWorld().spawnParticle(Particle.BLOCK, block.getLocation().add(0.5, 0.5, 0.5), 20, 1, 0.1, 0.1, 0.1, block.getBlockData());
        block.setBlockData(data);
    }

    /**
     * Sets block data and handles related events and logging.
     * @param block The block to modify.
     * @param data The new block data.
     */
    public void setBlockData(Block block, BlockData data) {
        EntityChangeBlockEvent event = new EntityChangeBlockEvent(NPCZombieEntity, block, data);
        this.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        block.setBlockData(data);
        if (PekoSrvFun.CoreProtectPresent) {
            PekoSrvFun.CoreProtectApiInstance.logPlacement("#NPC:" + NPCUserName, block.getLocation(), block.getType(), block.getBlockData());
        }
    }

    /**
     * Applies a player disguise to the NPC.
     */
    public void disguise() {
        if (!NPCZombieEntity.isAdult()) NPCZombieEntity.setAdult();
        PlayerDisguise disguise = new PlayerDisguise(NPCName, NPCUserName);
        disguise.setReplaceSounds(true);
        FlagWatcher watcher = disguise.getWatcher();
        disguise.setDynamicName(true);
        watcher.setCustomNameVisible(true);
        if (NPCName.isBlank() || NPCName.isEmpty()) {
            this.setCustomName(NPCUserName);
        } else {
            this.setCustomName(NPCName);
        }
        if (this.isSitting()) {
            watcher.setSneaking(true);
        } else {
            watcher.setSneaking(false);
        }
        DisguiseAPI.disguiseToAll(NPCZombieEntity, disguise);
    }

    /**
     * Gets the current disguise of the NPC.
     * @return The current Disguise object.
     */
    public Disguise getDisguise() {
        checkDisguiseStatus();
        return DisguiseAPI.getDisguise(NPCEntity);
    }

    /**
     * Sets the custom name of the NPC.
     * @param name The new custom name.
     */
    public void setCustomName(String name) {
        this.NPCZombieEntity.setCustomName(name);
    }

    /**
     * Sets whether the NPC is in mining mode.
     * @param mining True to enable mining, false to disable.
     */
    public void setMining(boolean mining) {
        Mining = mining;
        if (Mining && Harvesting) Harvesting = false;
    }

    /**
     * Sets whether the NPC is in harvesting mode.
     * @param harvesting True to enable harvesting, false to disable.
     */
    public void setHarvesting(boolean harvesting) {
        Harvesting = harvesting;
        if (Mining && Harvesting) Mining = false;
    }

    /**
     * Checks if the NPC is in mining mode.
     * @return True if mining, false otherwise.
     */
    public boolean isMining() {
        return Mining;
    }

    /**
     * Checks if the NPC is in harvesting mode.
     * @return True if harvesting, false otherwise.
     */
    public boolean isHarvesting() {
        return Harvesting;
    }

    /**
     * Sets the NPC's display name and updates persistent data.
     * @param name The new display name.
     */
    public void setNPCName(String name) {
        this.setCustomName(name);
        this.NPCName = name;
        PersistentDataContainer container = this.getPersistentDataContainer();
        container.set(NPCHelperNameKey(), PersistentDataType.STRING, name);
        this.disguise();
    }

    /**
     * Sets the NPC's username for disguise and updates persistent data.
     * @param userName The new username.
     */
    public void setNPCUserName(String userName) {
        this.NPCUserName = userName;
        PersistentDataContainer container = this.getPersistentDataContainer();
        container.set(NPCHelperTypeKey(), PersistentDataType.STRING, userName);
        this.disguise();
    }


    /**
     * Gets the NPC's username.
     * @return The username used in the disguise.
     */
    public String getNPCUserName() {
        return NPCUserName;
    }

    /**
     * Sets the NPC's status and updates persistent data.
     * @param status The new status (e.g., "Normal", "Sitting").
     */
    private void setStatus(String status) {
        Status = status;
        PersistentDataContainer container = this.getPersistentDataContainer();
        container.set(NPCHelperStatusKey(), PersistentDataType.STRING, Status);
    }

    /**
     * Gets the NPC's display name.
     * @return The current display name.
     */
    public String getNPCName() {
        return NPCName;
    }

    /**
     * Gets the NPC's owner name.
     * @return The owner's name.
     */
    public String getOwner() {
        return Owner;
    }

    /**
     * Gets the NPC's persistent data container.
     * @return The PersistentDataContainer instance.
     */
    PersistentDataContainer getPersistentDataContainer() {
        return NPCEntity.getPersistentDataContainer();
    }

    /**
     * Attempts to heal the NPC by consuming a carrot from its inventory.
     */
    public void tryToEat() {
        if (this.getHealth() > 20) this.setHealth(20);
        if (!(this.getHealth() < 20 && !this.isDead())) return;
        int carrotIndex = -1;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == Material.CARROT) {
                carrotIndex = i;
            }
        }
        if (carrotIndex <= -1) return;
        ItemStack carrots = inventory.getItem(carrotIndex);
        if (carrots.getAmount() > 1) {
            inventory.setItem(carrotIndex, new ItemStack(Material.CARROT, carrots.getAmount() - 1));
        } else {
            inventory.setItem(carrotIndex, new ItemStack(Material.AIR, 1));
        }
        Entity entity = this.getBukkitEntity();
        entity.getWorld().playSound(entity, Sound.ENTITY_GENERIC_EAT, 0.1f, 1);
        entity.getWorld().playSound(entity, Sound.ENTITY_GENERIC_EAT, 0.1f, 1);
        entity.getWorld().spawnParticle(Particle.ITEM, entity.getLocation(), 25, 0.1, 1, 0.1, 0, carrots);
        entity.getWorld().spawnParticle(Particle.ITEM, entity.getLocation(), 25, 0.5, 1, 0.5, 0, carrots);
        double newHealth = ((LivingEntity) entity).getHealth() + 1;
        if (newHealth <= 1) newHealth = 1;
        this.setHealth(Math.min(newHealth, 20));
    }

    /**
     * Checks for nearby chests periodically and updates the last seen chest.
     */
    public void tryCheckForChests() {
        lastChestSearchTick += 1;
        if (lastChestSearchTick > lastChestSearchCoolDown) {
            Location npcLocation = this.getLocation().clone();
            npcLocation.add(0, 1.8, 0); // Use head height
            lastChestSeen = getNearestReachableChest(10);
            lastChestSearchTick = 0;
        }
    }

    /**
     * Finds the nearest reachable chest within a radius.
     * @param searchRadius The radius to search within.
     * @return The nearest chest, or null if none found.
     */
    @Nullable
    public Chest getNearestReachableChest(int searchRadius) {
        Set<Block> nearbyBlocksLimited = NPCUtils.getBlocksSurroundingLocationLowHeight(this.getLocation(), searchRadius);
        Set<Block> chestBlocks = new HashSet<>();
        for (Block block : nearbyBlocksLimited) {
            if (block.getType().isAir()) continue;
            if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                Chest chest = (Chest) block.getState();
                if (chest.getInventory().firstEmpty() != -1) {
                    chestBlocks.add(block);
                }
            }
        }
        MineBlockLocationInfo nearestChestInfo = findNearestSuitableLocation(chestBlocks);
        if (nearestChestInfo == null) return null;
        if (nearestChestInfo.block.getType() == Material.CHEST || nearestChestInfo.block.getType() == Material.TRAPPED_CHEST) {
            if (nearestChestInfo.block.getState() instanceof Chest) {
                return (Chest) nearestChestInfo.block.getState();
            }
        }
        return null;
    }

    /**
     * Gets the last chest seen by the NPC.
     * @return The last chest seen, or null if none.
     */
    public Chest getLastChestSeen() {
        return lastChestSeen;
    }

    //endregion

    //region Combat

    /**
     * Performs a ranged attack on a target entity.
     * @param entityliving The target entity to attack.
     * @param v The power of the shot.
     */
    @Override
    public void a(@NotNull EntityLiving entityliving, float v) { //net/minecraft/world/entity/monster/AbstractSkeleton.performRangedAttack
        Entity targetEntity = entityliving.getBukkitEntity();
        if (this.isEntityNearNPC(targetEntity, 4)) {
            this.pickBestMeleeWeapon();
            return;
        }

        net.minecraft.world.item.ItemStack itemStack = this.b(ProjectileHelper.a(this, Items.pa)); //net/minecraft/world/entity/LivingEntity.getWeaponItem / net/minecraft/world/item/Items.BOW
        net.minecraft.world.item.ItemStack itemStack_1 = this.d(itemStack); //net/minecraft/world/entity/monster/Monster.getProjectile
        EntityArrow entityarrow = this.getArrow(itemStack_1, v, itemStack);

        double d0 = entityliving.dA() - this.dA();
        double d1 = entityliving.e(0.3333333333333333) - entityarrow.dC();
        double d2 = entityliving.dG() - this.dG();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        if (this.inventory.contains(Material.ARROW)) {
            int arrowIndex = -1;
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() == Material.ARROW) {
                    arrowIndex = i;
                }
            }
            ItemStack arrows = inventory.getItem(arrowIndex);
            if (arrows != null) {
                if (arrows.getAmount() > 1) {
                    if (!this.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.INFINITY)) {
                        arrows.setAmount(arrows.getAmount() - 1);
                    }
                } else if (!this.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.INFINITY)) {
                    inventory.setItem(arrowIndex, new ItemStack(Material.AIR, 1));
                }
            }
            if (!this.inventory.contains(Material.ARROW)) this.pickBestMeleeWeapon();

            net.minecraft.world.level.World world = this.dV();
            if (world instanceof WorldServer worldserver) {
                EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(this, this.eZ(), null, entityarrow, EnumHand.a, 0.8F, true);
                if (event.isCancelled()) {
                    event.getProjectile().remove();
                    return;
                }
                if (event.getProjectile() == entityarrow.getBukkitEntity()) {
                    if (!this.getEquipment().getItemInMainHand().containsEnchantment(Enchantment.INFINITY)) {
                        Arrow arrow = (Arrow) entityarrow.getBukkitEntity();
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
                    }
                    IProjectile.a(entityarrow, worldserver, itemStack_1, d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float) (14 - worldserver.am().a() * 4));
                }
            }
            this.a(SoundEffects.xN, 1.0F, 1.0F / (this.dY().i() * 0.4F + 0.8F));
        } else {
            setNPCInventory();
        }
    }

    /**
     * Creates an arrow entity for ranged attacks.
     * @param itemStack The projectile item stack.
     * @param f The power of the shot.
     * @param itemStack2 The bow item stack, or null.
     * @return The created EntityArrow.
     */
    private EntityArrow getArrow(net.minecraft.world.item.ItemStack itemStack, float f, @Nullable net.minecraft.world.item.ItemStack itemStack2) {
        return ProjectileHelper.a(this, itemStack, f, itemStack2);
    }

    /**
     * Performs an attack on a target if conditions are met.
     * @param entityToAttack The entity to attack.
     */
    public void checkAndPerformAttack(LivingEntity entityToAttack) {
        if (canAttackEntity(entityToAttack)) {
            this.setTicksUntilNextAttack(this.AttackCoolDown);
            this.a(EnumHand.a);
            this.c(getNPCWorldServer(), ((CraftEntity) entityToAttack).getHandle());//net/minecraft/world/entity/monster/Zombie.doHurtTarget
            if (entityToAttack.getHealth() <= 0) this.clearNPCTarget();
        }
    }

    /**
     * Checks if the NPC can attack a target entity.
     * @param entityToAttack The entity to check.
     * @return True if attack conditions are met, false otherwise.
     */
    public boolean canAttackEntity(LivingEntity entityToAttack) {
        boolean cooldownCheck = getTicksUntilNextAttack() <= 0;
        boolean rangeCheck = NPCUtils.getEntityDistanceToEntity(this.NPCZombieEntity, entityToAttack) <= this.Reach;
        boolean isLookingCheck = this.isNPCLookingAtEntity(entityToAttack);
        return cooldownCheck && rangeCheck && isLookingCheck;
    }

    /**
     * Gets the attack cooldown duration.
     * @return The cooldown in ticks.
     */
    public int getAttackCoolDown() {
        return AttackCoolDown;
    }

    /**
     * Sets the attack cooldown duration.
     * @param coolDown The new cooldown in ticks.
     */
    public void setAttackCoolDown(int coolDown) {
        AttackCoolDown = coolDown;
    }

    /**
     * Gets the remaining ticks until the next attack is possible.
     * @return The remaining ticks.
     */
    public int getTicksUntilNextAttack() {
        return this.TicksUntilNextAttack - 1;
    }

    /**
     * Sets the ticks until the next attack is possible.
     * @param ticks The number of ticks to set.
     */
    public void setTicksUntilNextAttack(int ticks) {
        this.TicksUntilNextAttack = ticks;
    }

    /**
     * Gets the current target of the NPC.
     * @return The target LivingEntity, or null if none.
     */
    public LivingEntity getNPCTarget() {
        return NPCZombieEntity.getTarget();
    }

    /**
     * Clears the NPC's current target.
     */
    public void clearNPCTarget() {
        NPCZombieEntity.setTarget(null);
    }

    /**
     * Sets a new target for the NPC to attack.
     * @param target The target LivingEntity.
     */
    public void setNPCTarget(LivingEntity target) {
        this.tryPickBestWeapon();
        if (NPCHelper.isNPC(target)) {
            net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) target).getHandle();
            if (nmsEntity instanceof NPCHelper helper && this.getOwner().equals(helper.getOwner())) {
                return; // Do not attack NPC if owner is the same
            }
        }
        NPCZombieEntity.setTarget(target);
    }

    /**
     * Checks if an enemy is near the NPC within a radius.
     * @param radius The radius to check within.
     * @return True if an enemy is near, false otherwise.
     */
    public boolean isEnemyNear(double radius) {
        for (Entity ent : this.getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof Monster && !isNPC(ent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a specific entity is near the NPC within a radius.
     * @param entity The entity to check.
     * @param radius The radius to check within.
     * @return True if the entity is near, false otherwise.
     */
    public boolean isEntityNearNPC(Entity entity, double radius) {
        for (Entity ent : this.getBukkitEntity().getNearbyEntities(3.5D, 3.5D, 3.5D)) {
            if (ent.getUniqueId().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    //endregion

    //region Movement and Navigation

    /**
     * Plays the main hand hit animation.
     */
    public void playHitAnimation() {
        this.NPCNmsEntity.a(EnumHand.a);
    }

    /**
     * Plays the off-hand hit animation.
     */
    public void playSecondaryHitAnimation() {
        this.NPCNmsEntity.a(EnumHand.b);
    }

    /**
     * Sets the NPC's look direction to specific coordinates.
     * @param x The x-coordinate to look at.
     * @param y The y-coordinate to look at.
     * @param z The z-coordinate to look at.
     */
    public void setNPCLookAtCoordinate(double x, double y, double z) {
        this.getNPCControllerLook().a(x, y, z);
    }

    /**
     * Sets the NPC's look direction to a location.
     * @param location The location to look at.
     */
    public void setNPCLookAtLocation(@NotNull Location location) {
        this.setNPCLookAtCoordinate(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Sets the NPC's look direction to a block.
     * @param block The block to look at.
     */
    public void setNPCLookAtBlock(@NotNull Block block) {
        this.setNPCLookAtLocation(NPCUtils.getCenter(block.getLocation()));
    }

    /**
     * Sets the NPC's look direction to an entity.
     * @param entity The entity to look at.
     */
    public void setNPCLookAtEntity(@NotNull Entity entity) {
        this.getNPCControllerLook().a(((CraftEntity) entity).getHandle()); //net/minecraft/world/entity/ai/control/LookControl.setLookAt(Entity)
    }

    /**
     * Gets the NPC's horizontal speed.
     * @return The horizontal speed as a double.
     */
    public double getNPCHorizontalSpeed() {
        return getNPCDeltaMovement().j(); //net/minecraft/world/phys/Vec3.horizontalDistanceSqr()
    }

    /**
     * Gets the NPC's delta movement vector.
     * @return The Vec3D representing movement.
     */
    public Vec3D getNPCDeltaMovement() { //net/minecraft/world/entity/Entity.getDeltaMovement()
        return this.NPCNmsEntity.dy();
    }

    /**
     * Gets the NPC's look controller.
     * @return The ControllerLook instance.
     */
    public ControllerLook getNPCControllerLook() { //net/minecraft/world/entity/Mob.getLookControl()
        return this.L();
    }

    /**
     * Gets the NPC's navigation system.
     * @return The NavigationAbstract instance.
     */
    public NavigationAbstract getNPCNavigation() { //net/minecraft/world/entity/Mob.getNavigation()
        return this.P();
    }

    /**
     * Creates a path to an entity within a distance.
     * @param entity The target entity.
     * @param distance The maximum distance for the path.
     * @return The PathEntity if a path exists, null otherwise.
     */
    public PathEntity createPath(Entity entity, int distance) {
        return this.getNPCNavigation().a(((CraftEntity) entity).getHandle(), distance);
    }

    /**
     * Moves the NPC toward an entity.
     * @param entity The target entity.
     * @param movementSpeed The speed of movement.
     * @return True if a path exists and movement started, false otherwise.
     */
    public boolean moveTo(Entity entity, double movementSpeed) {
        return this.getNPCNavigation().a(((CraftEntity) entity).getHandle(), movementSpeed);
    }

    /**
     * Teleports the NPC to a new location.
     * @param newLocation The destination location.
     */
    public void teleport(Location newLocation) {
        NPCEntity.teleport(newLocation);
    }

    /**
     * Gets the NPC's velocity vector.
     * @return The current velocity.
     */
    public Vector getVelocity() {
        return NPCEntity.getVelocity();
    }

    //endregion

    //region Visibility and Sensing

    /**
     * Checks if the NPC is looking at a specific entity.
     * @param entity The entity to check.
     * @return True if the NPC has line of sight and is looking at the entity, false otherwise.
     */
    public boolean isNPCLookingAtEntity(@NotNull Entity entity) {
        return this.getNPCSenses().a(((CraftEntity) entity).getHandle()); //net/minecraft/world/entity/ai/sensing/Sensing.hasLineOfSight(Entity)
    }

    /**
     * Updates the NPC's line of sight (currently does nothing).
     */
    public void clearNPCLineOfSight() {
        this.getNPCSenses().a(); //net/minecraft/world/entity/ai/sensing/Sensing.tick()
    }

    /**
     * Gets the NPC's senses.
     * @return The EntitySenses instance.
     */
    public EntitySenses getNPCSenses() { //net/minecraft/world/entity/Mob.getSenses()
        return this.Q();
    }

    /**
     * Gets the NPC's NMS World.
     * @return The EntitySenses instance.
     */
    public net.minecraft.world.level.World getNPCWorld() { //net/minecraft/world/entity/Entity.level()
        return this.dV();
    }

    /**
     * Gets the NPC's NMS World.
     * @return The EntitySenses instance.
     */
    public WorldServer getNPCWorldServer() { //net/minecraft/world/entity/Entity.level()
        return getNPCWorld().getMinecraftWorld();
    }

    /**
     * Gets the block the NPC is targeting within a distance.
     * @param maxDistance The maximum distance to check.
     * @return The targeted block, or null if none.
     */
    public Block getTargetBlockExact(int maxDistance) {
        return NPCZombieEntity.getTargetBlockExact(maxDistance);
    }

    /**
     * Gets the block the NPC is targeting with fluid collision mode.
     * @param maxDistance The maximum distance to check.
     * @param var2 The fluid collision mode.
     * @return The targeted block, or null if none.
     */
    public Block getTargetBlockExact(int maxDistance, @NotNull FluidCollisionMode var2) {
        return NPCZombieEntity.getTargetBlockExact(maxDistance, var2);
    }

    /**
     * Gets the block the NPC is targeting with transparent materials.
     * @param transparent Set of materials to treat as transparent.
     * @param maxDistance The maximum distance to check.
     * @return The targeted block, or null if none.
     */
    public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
        return NPCZombieEntity.getTargetBlock(transparent, maxDistance);
    }

    /**
     * Gets the blocks in the NPC's line of sight.
     * @param transparent Set of materials to treat as transparent.
     * @param maxDistance The maximum distance to check.
     * @return List of blocks in the line of sight.
     */
    public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
        return NPCZombieEntity.getLineOfSight(transparent, maxDistance);
    }

    /**
     * Checks if the NPC has line of sight to an entity.
     * @param entity The entity to check.
     * @return True if line of sight exists, false otherwise.
     */
    public boolean hasNPCLineOfSight(@NotNull Entity entity) {
        return this.NPCZombieEntity.hasLineOfSight(entity);
    }

    /**
     * Checks if the NPC can reach a block within its default reach distance.
     * @param block The block to check.
     * @return True if the block is reachable, false otherwise.
     */
    public boolean canReachBlock(Block block) {
        return canCustomReachBlock(block, Reach);
    }

    /**
     * Checks if the NPC can reach a block within a custom distance.
     * @param block The block to check.
     * @param range The custom reach distance.
     * @return True if the block is reachable, false otherwise.
     */
    public boolean canCustomReachBlock(Block block, double range) {
        Location start = this.getEyeLocation();
        Location blockLocation = block.getLocation();
        double blockDistance = NPCUtils.getDistance(start, block);
        if (blockDistance > range) return false;

        double dx = blockLocation.getX() - start.getX();
        double dy = blockLocation.getY() - start.getY();
        double dz = blockLocation.getZ() - start.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double steps = Math.max(10, distance * 3);

        for (double step = 0; step <= steps; step++) {
            double factor = step / steps;
            double x = start.getX() + dx * factor;
            double y = start.getY() + dy * factor;
            double z = start.getZ() + dz * factor;
            Block rayBlock = block.getWorld().getBlockAt(new Location(block.getWorld(), x, y, z));

            if (block.equals(rayBlock)) return true;
            if (rayBlock.getType().isAir() || NPCUtils.isCrop(rayBlock) || !rayBlock.getType().isSolid() || rayBlock.isPassable() ||
                    rayBlock.getType() == Material.CHEST || rayBlock.getType() == Material.TRAPPED_CHEST) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if the NPC can see a block within a default distance of 15.
     * @param block The block to check.
     * @return True if the block is visible, false otherwise.
     */
    public boolean canSeeBlock(Block block) {
        return canCustomReachBlock(block, 15);
    }

    /**
     * Gets all blocks visible to the NPC within a radius.
     * @param center The center location to check from.
     * @param radius The radius to search within.
     * @return A set of visible blocks.
     */
    public Set<Block> getBlocksVisibleToNPC(Location center, double radius) {
        Set<Block> visibleBlocks = ConcurrentHashMap.newKeySet();
        int r = (int) Math.ceil(radius);
        IntStream.rangeClosed(-r, r).parallel().forEach(x ->
                IntStream.rangeClosed(-r, r).forEach(y ->
                        IntStream.rangeClosed(-r, r).forEach(z -> {
                            if (x * x + y * y + z * z <= radius * radius) {
                                Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                                if (canSeeBlock(block)) {
                                    visibleBlocks.add(block);
                                }
                            }
                        })
                )
        );
        return visibleBlocks;
    }

    /**
     * Finds the nearest suitable location from a set of blocks.
     * @param mineableBlocksCandidates The candidate blocks to check.
     * @return The nearest MineBlockLocationInfo, or null if none found.
     */
    public MineBlockLocationInfo findNearestSuitableLocation(Set<Block> mineableBlocksCandidates) {
        if (mineableBlocksCandidates.isEmpty() || getLocation().getWorld() == null) {
            return null;
        }
        MineBlockLocationInfo nearestStandInfo = null;
        double nearestDistance = Double.MAX_VALUE;
        Block nearestBlock = null;
        for (Block block : mineableBlocksCandidates) {
            if (canSeeBlock(block)) {
                double distance = getLocation().distance(block.getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestBlock = block;
                }
            }
        }
        if (nearestBlock != null) {
            nearestStandInfo = new MineBlockLocationInfo(nearestBlock.getLocation(), nearestBlock);
        }
        return nearestStandInfo;
    }

    //endregion

    //region State Management

    /**
     * Checks and reapplies the disguise if it is missing.
     */
    public void checkDisguiseStatus() {
        if (!DisguiseAPI.isDisguised(NPCZombieEntity)) {
            disguise();
        }
    }

    /**
     * Gets the NPC's current health.
     * @return The health value.
     */
    public double getHealth() {
        return NPCZombieEntity.getHealth();
    }

    /**
     * Sets the NPC's health.
     * @param value The new health value.
     */
    public void setHealth(double value) {
        NPCZombieEntity.setHealth(value);
    }

    /**
     * Checks if the NPC is dead.
     * @return True if dead, false otherwise.
     */
    public boolean isDead() {
        return NPCZombieEntity.isDead();
    }

    /**
     * Checks if the NPC is free to wander.
     * @return True if in "FreeToWander" status, false otherwise.
     */
    public boolean isFreeToWander() {
        return Status.equals("FreeToWander");
    }

    /**
     * Checks if the NPC is sitting.
     * @return True if in "Sitting" status, false otherwise.
     */
    public boolean isSitting() {
        return Status.equals("Sitting");
    }

    /**
     * Checks if the NPC is sleeping.
     * @return True if in "Sleeping" status, false otherwise.
     */
    public boolean isSleeping() {
        return Status.equals("Sleeping");
    }

    /**
     * Checks if the NPC is in normal mode.
     * @return True if in "Normal" status, false otherwise.
     */
    public boolean isNormal() {
        return Status.equals("Normal");
    }

    /**
     * Sets the NPC to free wandering mode.
     */
    public void setFreeToWander() {
        Disguise disguise = DisguiseAPI.getDisguise(NPCEntity);
        FlagWatcher watcher = disguise.getWatcher();
        watcher.setSneaking(false);
        setStatus("FreeToWander");
    }

    /**
     * Sets the NPC to follow its owner.
     */
    public void setFollowOwner() {
        Disguise disguise = DisguiseAPI.getDisguise(NPCEntity);
        FlagWatcher watcher = disguise.getWatcher();
        watcher.setSneaking(false);
        setStatus("Normal");
    }

    /**
     * Sets the NPC to sleeping mode.
     */
    public void setSleeping() {
        setStatus("Sleeping");
        Disguise disguise = DisguiseAPI.getDisguise(NPCEntity);
        FlagWatcher watcher = disguise.getWatcher();
        watcher.setSleeping(true);
    }

    /**
     * Sets the NPC to sitting mode.
     */
    public void setSitting() {
        setStatus("Sitting");
        Disguise disguise = DisguiseAPI.getDisguise(NPCEntity);
        FlagWatcher watcher = disguise.getWatcher();
        watcher.setSneaking(true);
    }

    /**
     * Removes the sleeping state.
     */
    public void setNotSleeping() {
        setStatus("Normal");
        Disguise disguise = DisguiseAPI.getDisguise(NPCEntity);
        FlagWatcher watcher = disguise.getWatcher();
        watcher.setSleeping(false);
    }

    /**
     * Removes the sitting state.
     */
    public void setNotSitting() {
        setStatus("Normal");
        Disguise disguise = DisguiseAPI.getDisguise(NPCEntity);
        FlagWatcher watcher = disguise.getWatcher();
        watcher.setSneaking(false);
    }

    /**
     * Checks if the NPC is in water.
     * @return True if in water, false otherwise.
     */
    public boolean isNPCInWater() {
        return this.NPCZombieEntity.isInWater();
    }

    /**
     * Gets the NPC's fall distance.
     * @return The fall distance in blocks.
     */
    public double getNPCFallDistance() {
        return this.NPCZombieEntity.getFallDistance();
    }

    //endregion

    //region Utility Methods

    /**
     * Gets an NPCHelper instance from an entity.
     * @param entity The entity to check.
     * @return The NPCHelper instance, or null if not an NPC.
     */
    @Contract("null -> null")
    public static NPCHelper GetNPCFromEntity(Entity entity) {
        if (entity == null) return null;
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        return (nmsEntity instanceof NPCHelper) ? (NPCHelper) nmsEntity : null;
    }

    /**
     * Checks if an entity is an NPC.
     * @param entity The entity to check.
     * @return True if the entity is an NPC, false otherwise.
     */
    @Contract("null -> false")
    public static boolean isNPC(Entity entity) {
        if (entity == null) return false;
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        if (nmsEntity instanceof NPCHelper) return true;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(NPCHelperTypeKey(), PersistentDataType.STRING) ||
                !container.has(NPCHelperOwnerKey(), PersistentDataType.STRING)) {
            return false;
        }
        String NPCHelperTypeKey = container.get(NPCHelperTypeKey(), PersistentDataType.STRING);
        String NPCHelperOwnerKey = container.get(NPCHelperOwnerKey(), PersistentDataType.STRING);
        String NPCHelperNameKey = container.get(NPCHelperNameKey(), PersistentDataType.STRING);
        if (NPCHelperTypeKey == null || NPCHelperTypeKey.isBlank()) return false;
        entity.remove();
        new NPCHelper(entity.getLocation(), NPCHelperOwnerKey, NPCHelperTypeKey, NPCHelperNameKey, container).getBukkitEntity();
        return true;
    }

    /**
     * Checks if an entity is an abnormal NPC (has NPC data but no disguise).
     * @param entity The entity to check.
     * @return True if abnormal, false otherwise.
     */
    @Contract("null -> false")
    public static boolean IsAbnormalNPC(Entity entity) {
        if (entity == null) return false;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(NPCHelperTypeKey(), PersistentDataType.STRING) ||
                !container.has(NPCHelperOwnerKey(), PersistentDataType.STRING)) {
            return false;
        }
        return !DisguiseAPI.isDisguised(entity);
    }

    /**
     * Gets the NPC's current location.
     * @return The Location object.
     */
    public Location getLocation() {
        return NPCEntity.getLocation();
    }

    /**
     * Gets the NPC's eye location.
     * @return The eye Location object.
     */
    public Location getEyeLocation() {
        return NPCZombieEntity.getEyeLocation();
    }

    /**
     * Gets the NPC's world.
     * @return The World object.
     */
    public World getWorld() {
        return NPCEntity.getWorld();
    }

    /**
     * Gets entities near the NPC within a bounding box.
     * @param i The x-axis radius.
     * @param i1 The y-axis radius.
     * @param i2 The z-axis radius.
     * @return An iterable of nearby entities.
     */
    public Iterable<? extends Entity> getNearbyEntities(double i, double i1, double i2) {
        return NPCEntity.getNearbyEntities(i, i1, i2);
    }

    /**
     * Gets the NPC's unique ID.
     * @return The UUID object.
     */
    public Object getUniqueId() {
        return NPCEntity.getUniqueId();
    }

    /**
     * Gets the distance squared to an entity.
     * @param entity The entity to measure distance to.
     * @return The squared distance.
     */
    public double getDistanceSquared(@NotNull Entity entity) {
        return this.getLocation().distanceSquared(entity.getLocation());
    }

    /**
     * Gets the distance squared to a location.
     * @param location The location to measure distance to.
     * @return The squared distance.
     */
    public double getDistanceSquared(@NotNull Location location) {
        return this.getLocation().distanceSquared(location);
    }

    /**
     * Gets the NPC's random source.
     * @return The RandomSource instance.
     */
    public RandomSource getNPCRandomSource() { //net/minecraft/world/entity/Entity.getRandom()
        return this.dY();
    }

    /**
     * Gets a random integer up to a maximum value.
     * @param max The maximum value (exclusive).
     * @return A random integer.
     */
    public int getRandomNextInt(int max) {
        return this.getNPCRandomSource().a(max);
    }

    /**
     * Gets a random long value.
     * @return A random long.
     */
    public long getRandomNextLong() {
        return this.getNPCRandomSource().g();
    }

    //endregion

    //region Overrides

    /**
     * Determines if the NPC is sun-sensitive.
     * @return False, overriding default zombie behavior.
     */
    @Override
    protected boolean ai_() { //net/minecraft/world/entity/monster/Zombie.isSunSensitive()
        return false;
    }

    /**
     * Determines if the NPC converts in water.
     * @return False, overriding default zombie behavior.
     */
    @Override
    protected boolean gt() { //net/minecraft/world/entity/monster/Zombie.convertsInWater()
        return false;
    }

    /**
     * Determines if the NPC should despawn in peaceful mode.
     * @return False, overriding default monster behavior.
     */
    @Override
    protected boolean ab() { //net/minecraft/world/entity/monster/Monster.shouldDespawnInPeaceful()
        return false;
    }

    /**
     * Gets the sound effect for the NPC's footsteps.
     * @return The SoundEffect based on the block below.
     */
    @Override
    public SoundEffect t() { //net/minecraft/world/entity/monster/Zombie.getStepSound()
        Location loc = NPCEntity.getLocation();
        BlockData data = loc.subtract(0, 1, 0).getBlock().getBlockData();
        Sound step = data.getSoundGroup().getStepSound();
        String soundKeyString = step.getKey().toString().replace("_", "").toLowerCase();
        MinecraftKey soundKey = MinecraftKey.a(soundKeyString);
        return SoundEffect.a(soundKey);
    }

    /**
     * Gets the death sound for the NPC.
     * @return The player hurt sound effect.
     */
    @Override
    protected SoundEffect o_() { //net/minecraft/world/entity/monster/Zombie.getDeathSound()
        return SoundEffects.uL; //entity.player.hurt
    }

    /**
     * Gets the hurt sound for the NPC based on damage source.
     * @param source The damage source.
     * @return The death sound (player hurt sound).
     */
    @Override
    protected SoundEffect e(DamageSource source) { //net/minecraft/world/entity/monster/Zombie.getHurtSound()
        return o_();
    }

    //endregion
}