package cl.mariofinale;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.*;

import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.Items;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

class PekoSrvFun_HoloPet extends EntityZombie implements InventoryHolder, IRangedEntity {
    public Inventory inventory;
    public String Status;
    private String Owner;
    private String petType;
    private String petName;

    public PekoSrvFun_HoloPet(Location loc, String playerName, String pType, String customName, @Nullable PersistentDataContainer dataContainer){
        super(EntityTypes.bj, ((CraftWorld) loc.getWorld()).getHandle());
        double holoSpeed = 0.30D;
        this.g(loc.getX(), loc.getY(), loc.getZ());
        this.persist = true;
        this.petType = pType;
        this.Owner = playerName;
        this.Status = "Normal";


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

        this.bT.a(2, new PathfinderGoalBowShoot<>(this, 1.0D, 20, 15.0F));
        this.bT.a(3, new PathfinderGoalMeleeAttackHolo(this, 1.2D, false, 3.2D));

        this.bT.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.bT.a(5, new PathfinderGoalLookAtPlayerHolo(this, EntityHuman.class, 8.0F));
        this.bT.a(6, new PathfinderGoalRandomLookaroundHolo(this));
        this.bT.a(7, new PathfinderGoalRandomStrollHolo(this, 0.7D));

        this.bS.a(0, new PathfinderTryPreventDeathByCreeper(this, 1.3D));
        this.bS.a(0, new PathfinderGoalHurtByTarget(this, EntityPlayer.class));
        this.bS.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityCreeper.class, true));
        this.bS.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntitySpider.class, true));
        this.bS.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityCaveSpider.class, true));
        this.bS.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeleton.class, true));
        this.bS.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonWither.class, true));
        this.bS.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombie.class, true));
        this.bS.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombieVillager.class, true));
        this.bS.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityZombieHusk.class, true));
        this.bS.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityPillager.class, true));
        this.bS.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityWitch.class, true));
        this.bS.a(7, new PathfinderGoalNearestAttackableTarget<>(this, EntityEnderman.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityVindicator.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityIllagerWizard.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityEvoker.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityVindicator.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityHoglin.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityZoglin.class, true));
        this.bS.a(10, new PathfinderGoalNearestAttackableTarget<>(this, EntityRavager.class, true));
        this.bS.a(15, new PathfinderGoalNearestAttackableTarget<>(this, EntityWither.class, true));
        this.bS.a(15, new PathfinderGoalNearestAttackableTarget<>(this, EntitySilverfish.class, true));

        if (petName.equals("Suisei") || petName.equals("Rushia")){
            this.bS.a(16, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, true));
        }

        this.craftAttributes.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0D);
        this.craftAttributes.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(holoSpeed);
        this.craftAttributes.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0D);

        ((CraftWorld) loc.getWorld()).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        ((Zombie) this.getBukkitEntity()).getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        ((Zombie) this.getBukkitEntity()).setCanPickupItems(true);
        ((Zombie) this.getBukkitEntity()).setPersistent(true);
        ((Zombie) this.getBukkitEntity()).setRemoveWhenFarAway(false);

        if (petName.equals("Suisei") || petName.equals("Rushia")){
            ((Zombie) this.getBukkitEntity()).setCanBreakDoors(true);
        }else {
            ((Zombie) this.getBukkitEntity()).setCanBreakDoors(false);
        }


        if (dataContainer == null){
            PersistentDataContainer container = this.getBukkitEntity().getPersistentDataContainer();
            container.set(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING, petName);
            container.set(PekoSrvFun.holoPetOwnerKey, PersistentDataType.STRING, Owner);
            container.set(PekoSrvFun.holoPetStatusKey, PersistentDataType.STRING, Status);
            this.inventory = Bukkit.createInventory(this, 9, this.petName + "'s Inventory | HP: 20/20" );
            container.set(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING, Utils.toBase64(this.inventory));

        }else {
            PersistentDataContainer container = this.getBukkitEntity().getPersistentDataContainer();
            if (dataContainer.has(PekoSrvFun.holoPetStatusKey, PersistentDataType.STRING)){
                String lastStatus = dataContainer.get(PekoSrvFun.holoPetStatusKey, PersistentDataType.STRING) ;
                container.set(PekoSrvFun.holoPetStatusKey, PersistentDataType.STRING, lastStatus);
                this.Status = lastStatus;
            }else{
                container.set(PekoSrvFun.holoPetStatusKey, PersistentDataType.STRING, "Normal");
            }


            container.set(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING, petName);
            container.set(PekoSrvFun.holoPetOwnerKey, PersistentDataType.STRING, Owner);
            if (dataContainer.has(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING)){
                Inventory newInvent = null;
                String encodedInv = dataContainer.get(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING) ;
                try {
                    newInvent = Utils.fromBase64(encodedInv);
                } catch (IOException e) {
                    PekoSrvFun.LogError("Error loading pet inventory.");
                    PekoSrvFun.LogError(e.getMessage());
                }
                if (newInvent != null){
                    this.inventory = newInvent;
                }
                if (this.inventory != null){
                    Utils.setPetInventory(this);
                }
                container.set(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING, encodedInv);
            }else {
                this.inventory = Bukkit.createInventory(this, 9, this.petName + "'s Inventory | HP: 20/20" );
                container.set(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING, Utils.toBase64(this.inventory));
            }
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

        if (this.Status.equals("Sitting")){
            DisguiseAPI.getDisguise(this.getBukkitEntity()).getWatcher().setSneaking(true);
        }else {
            DisguiseAPI.getDisguise(this.getBukkitEntity()).getWatcher().setSneaking(false);
        }

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
        return SoundEffects.j;
    }

    public String getPetType(){
        return petType;
    }

    public String getStatus(){
        return Status;
    }

    public void setStatus(String status){
        Status = status;
        PersistentDataContainer container = this.getBukkitEntity().getPersistentDataContainer();
        container.set(PekoSrvFun.holoPetStatusKey, PersistentDataType.STRING, Status);
    }

    public String getPetName(){
        return petName;
    }
    public String getOwner(){
        return Owner;
    }

    @Override
    public void a(EntityLiving entityliving, float v) {
        net.minecraft.world.item.ItemStack itemstack = this.h(this.b(ProjectileHelper.a(this, Items.mQ)));
        EntityArrow entityarrow = this.getArrow(itemstack, v);
        double d0 = entityliving.df() - this.df();
        double d1 = entityliving.e(0.3333333333333333D) - entityarrow.dh();
        double d2 = entityliving.dl() - this.dl();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        entityarrow.c(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.s.ag().a() * 4));
        if (this.inventory.contains(Material.ARROW)){
            int arrowIndex = -1;
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) continue;
                if (item.getType() == Material.ARROW) {
                    arrowIndex = i;
                }
            }
            ItemStack arrows = inventory.getItem(arrowIndex);

            if (arrows.getAmount() > 1){
                if (!((Zombie)this.getBukkitEntity()).getEquipment().getItemInMainHand().containsEnchantment(Enchantment.ARROW_INFINITE)){
                    arrows.setAmount(arrows.getAmount() - 1);
                }
            }else if(arrows.getAmount() == 1){
                if (!((Zombie)this.getBukkitEntity()).getEquipment().getItemInMainHand().containsEnchantment(Enchantment.ARROW_INFINITE)){
                    inventory.setItem(arrowIndex, new ItemStack(Material.AIR, 1));
                }
            }
            if (!this.inventory.contains(Material.ARROW)){
                Utils.setPetInventory(this);
            }

            EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(this, this.ez(), CraftItemStack.asNMSCopy(arrows), entityarrow, EnumHand.a, 0.8F, true);
            if (event.isCancelled()) {
                event.getProjectile().remove();
            } else {
                if (event.getProjectile() == entityarrow.getBukkitEntity()) {
                    this.s.b(entityarrow);
                    if (!((Zombie)this.getBukkitEntity()).getEquipment().getItemInMainHand().containsEnchantment(Enchantment.ARROW_INFINITE)){
                        ((Arrow)(entityarrow.getBukkitEntity())).setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
                    }
                }
                this.a(SoundEffects.sT, 1.0F, 1.0F / (this.dQ().i() * 0.4F + 0.8F));
            }

        }else{
            Utils.setPetInventory(this);
        }
    }

    private EntityArrow getArrow(net.minecraft.world.item.ItemStack itemstack, float f) {
        return ProjectileHelper.a(this, itemstack, f);
    }
}
