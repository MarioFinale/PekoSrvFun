package cl.mariofinale;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.*;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.player.EntityHuman;

import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

class PekoSrvFun_HoloPet extends EntityZombie implements InventoryHolder {
    public Inventory inventory;
    public boolean Sitting;
    private String Owner;
    private String petType;
    private String petName;

    public PekoSrvFun_HoloPet(Location loc, String playerName, String pType, String customName){
        super(EntityTypes.bj, ((CraftWorld) loc.getWorld()).getHandle());
        double holoSpeed = 0.30D;
        this.g(loc.getX(), loc.getY(), loc.getZ());
        persist = true;
        petType = pType;
        this.Owner = playerName;

        EntityInsentient nmsEntity = (EntityInsentient) ((this.getBukkitEntity()).getHandle());
        PathfinderGoalSelector goalSelector = nmsEntity.bT;
        PathfinderGoalSelector targetSelector = nmsEntity.bS;


        try {
            Field dField;
            dField = PathfinderGoalSelector.class.getDeclaredField("d");
            dField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            dField.set(targetSelector, new LinkedHashSet<>());

            Field cField;
            cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

            Field fField;
            fField = PathfinderGoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        petType = Utils.GetMinecraftUsernameByType(petType);
        petName = Utils.GetHoloPetNameByMinecraftUsername(petType);


        this.bT.a(0, new PathfinderGoalFloat(this));
        if (!(petName.equals("Suisei") || petName.equals("Rushia"))){
            this.bT.a(1, new PathfinderGoalWalkNearPlayer(this, 1.2D, Owner));
        }
        this.bT.a(3, new PathfinderGoalMeleeAttackHolo(this, 1.2D, false, 3.2D));

        this.bT.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.bT.a(5, new PathfinderGoalLookAtPlayerHolo(this, EntityHuman.class, 8.0F));
        this.bT.a(6, new PathfinderGoalRandomLookaroundHolo(this));
        this.bT.a(7, new PathfinderGoalRandomStrollHolo(this, 0.7D));

        this.bS.a(0, new PathfinderTryPreventDeathByCreeper(this, 1.3D));
        this.bS.a(0, new PathfinderGoalHurtByTarget(this, EntityPlayer.class));
        this.bS.a(1, new PathfinderGoalNearestAttackableTarget<EntityCreeper>(this, EntityCreeper.class, true));
        this.bS.a(2, new PathfinderGoalNearestAttackableTarget<EntitySpider>(this, EntitySpider.class, true));
        this.bS.a(2, new PathfinderGoalNearestAttackableTarget<EntityCaveSpider>(this, EntityCaveSpider.class, true));
        this.bS.a(3, new PathfinderGoalNearestAttackableTarget<EntitySkeleton>(this, EntitySkeleton.class, true));
        this.bS.a(3, new PathfinderGoalNearestAttackableTarget<EntitySkeletonWither>(this, EntitySkeletonWither.class, true));
        this.bS.a(3, new PathfinderGoalNearestAttackableTarget<EntityZombie>(this, EntityZombie.class, true));
        this.bS.a(4, new PathfinderGoalNearestAttackableTarget<EntityZombieVillager>(this, EntityZombieVillager.class, true));
        this.bS.a(4, new PathfinderGoalNearestAttackableTarget<EntityZombieHusk>(this, EntityZombieHusk.class, true));
        this.bS.a(5, new PathfinderGoalNearestAttackableTarget<EntityPillager>(this, EntityPillager.class, true));
        this.bS.a(5, new PathfinderGoalNearestAttackableTarget<EntityWitch>(this, EntityWitch.class, true));
        this.bS.a(7, new PathfinderGoalNearestAttackableTarget<EntityEnderman>(this, EntityEnderman.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<EntityVindicator>(this, EntityVindicator.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<EntityIllagerWizard>(this, EntityIllagerWizard.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<EntityEvoker>(this, EntityEvoker.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<EntityVindicator>(this, EntityVindicator.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<EntityHoglin>(this, EntityHoglin.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<EntityZoglin>(this, EntityZoglin.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<EntityRavager>(this, EntityRavager.class, true));
        this.bS.a(15, new PathfinderGoalNearestAttackableTarget<EntityWither>(this, EntityWither.class, true));
        this.bS.a(15, new PathfinderGoalNearestAttackableTarget<EntitySilverfish>(this, EntitySilverfish.class, true));
        if (petName.equals("Suisei") || petName.equals("Rushia")){
            this.bS.a(16, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
        }

        this.craftAttributes.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0D);
        this.craftAttributes.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(holoSpeed);
        this.craftAttributes.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0D);


        ((CraftWorld) loc.getWorld()).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        PersistentDataContainer container = this.getBukkitEntity().getPersistentDataContainer();
        container.set(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING, petName);
        container.set(PekoSrvFun.holoPetOwnerKey, PersistentDataType.STRING, Owner);

        ((Zombie) this.getBukkitEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        ((Zombie) this.getBukkitEntity()).setCanPickupItems(true);
        ((Zombie) this.getBukkitEntity()).setPersistent(true);
        ((Zombie) this.getBukkitEntity()).setRemoveWhenFarAway(false);

        if (petName.equals("Suisei") || petName.equals("Rushia")){
            ((Zombie) this.getBukkitEntity()).setCanBreakDoors(true);
        }else {
            ((Zombie) this.getBukkitEntity()).setCanBreakDoors(false);
        }

        PekoSrvFun.LogInfo("Entity ID: " + this.getBukkitEntity().getEntityId());
        PekoSrvFun.LogInfo("Location: " + this.getBukkitEntity().getLocation().toString());
        PlayerDisguise disguise = new PlayerDisguise(petType, petType);
        disguise.setReplaceSounds(true);
        FlagWatcher watcher = disguise.getWatcher();

        if (customName.isBlank() || customName.isEmpty()){
            this.getBukkitEntity().setCustomName(Owner + "'s " + petName +" clone");
            watcher.setCustomName(Owner + "'s " + petName +" clone");
            watcher.setCustomNameVisible(true);
            disguise.setDynamicName(true);
        }else {
            this.getBukkitEntity().setCustomName(customName);
            watcher.setCustomName(customName);
            watcher.setCustomNameVisible(true);
            disguise.setDynamicName(true);
        }

        DisguiseAPI.disguiseToAll(this.getBukkitEntity(), disguise);
        this.inventory = Bukkit.createInventory(this, 9, this.petName + "'s Inventory | HP: 20/20" );
        ((Zombie) this.getBukkitEntity()).setFireTicks(0);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public SoundEffect t() {
        Location loc = this.getBukkitEntity().getLocation();
        BlockData data = loc.subtract(0,1,0).getBlock().getBlockData();
        Sound step = data.getSoundGroup().getStepSound();
        loc.getWorld().playSound(loc,step,0.2f,1);
        SoundEffect soundEffect = SoundEffects.j;
        return soundEffect;
    }

    public String getPetType(){
        return petType;
    }

    public String getPetName(){
        return petName;
    }
    public String getOwner(){
        return Owner;
    }
}
