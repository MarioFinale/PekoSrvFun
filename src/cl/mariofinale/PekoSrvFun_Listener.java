package cl.mariofinale;
import me.libraryaddict.disguise.*;
import me.libraryaddict.disguise.disguisetypes.*;
import me.libraryaddict.disguise.disguisetypes.watchers.*;
import net.milkbowl.vault.economy.plugins.Economy_TAEcon;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.item.Items;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.*;

import static java.lang.Math.round;

public class PekoSrvFun_Listener implements Listener{


    /** @noinspection unused*/
    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event){
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SALMON ||entity.getType() == EntityType.COD ){
            //SetFishPekomon(entity);  //Disabled for now,
            return;
        }
        if (entity.getType() == EntityType.SLIME){
            SetSlimePekomon(entity);
            return;
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityTeleportEvent(EntityTeleportEvent event){
        if(event.isCancelled()) return;
        if(event.getTo() == null) return;
        Entity pet = event.getEntity();
        if (!pet.isValid())return;
        if (!isHoloPet(event.getEntity())) return;
        if (DisguiseAPI.isDisguised(pet)) return;

        String holoPetData = PekoSrvFun.getHoloPetData(pet);
        String ownerName = pet.getCustomName().split(" ")[0].split("'")[0];
        PersistentDataContainer container = pet.getPersistentDataContainer();
        Inventory newInvent = null;
        PekoSrvFun_HoloPet newpet = new PekoSrvFun_HoloPet(event.getTo(), ownerName, holoPetData, pet.getCustomName());
        pet.remove();
        if (container.has(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING)){
            String encodedInv = container.get(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING) ;
            try {
                newInvent = Utils.fromBase64(encodedInv);
            } catch (IOException e) {
                PekoSrvFun.LogError("Error loading pet inventory.");
                PekoSrvFun.LogError(e.getMessage());
            }
            if (newInvent != null){
                newpet.inventory = newInvent;
            }
            if (newpet.inventory != null){
                Utils.setPetInventory(newpet);
            }
        }
        event.setCancelled(true);
    }


    /** @noinspection unused*/
    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event){
        if (DisguiseAPI.isDisguised(event.getEntity()) && DisguiseAPI.isDisguised(event.getTarget())){
            event.setCancelled(true);
        }
        if (DisguiseAPI.isDisguised(event.getTarget()) && event.getEntityType().equals(EntityType.IRON_GOLEM)){
            event.setCancelled(true);
        }
    }


    /** @noinspection unused*/
    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event){
        if (event.isCancelled() ){
            if (event.getBedEnterResult() ==  PlayerBedEnterEvent.BedEnterResult.NOT_SAFE){
                Collection<Entity> nearbyEntities = event.getBed().getLocation().getWorld().getNearbyEntities(event.getBed().getLocation(), 8,5,8);
                int petsNear = 0;
                for (Entity ent: nearbyEntities) {
                    if (ent.getPersistentDataContainer().has(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING)){
                        petsNear += 1;
                    }
                    if (ent.getType() == EntityType.PLAYER){
                        petsNear += 1;
                    }
                }
                if (nearbyEntities.size() <= petsNear){
                    event.setCancelled(false);
                    event.getPlayer().sleep(event.getBed().getLocation(), true);
                }
            }
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityCombustEvent(EntityCombustEvent event){
        if (event instanceof EntityCombustByBlockEvent || event instanceof EntityCombustByEntityEvent) return;
        if (!DisguiseAPI.isDisguised(event.getEntity())) return;
        if (!(event.getEntity().getLocation().getBlock().getLightFromSky() > 11)) return;
        Block block = event.getEntity().getLocation().subtract(0,0,0).getBlock();
        Block blockUnder = event.getEntity().getLocation().subtract(0,1,0).getBlock();
        Material blockMaterial = block.getType();
        Material blockUnderMaterial = blockUnder.getType();

        if (blockMaterial == Material.FIRE) return;
        if (blockMaterial == Material.SOUL_FIRE) return;
        if (blockUnderMaterial == Material.LAVA) return;
        if (blockMaterial == Material.LAVA) return;

        event.setCancelled(true);

    }

    /** @noinspection unused*/
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        PekoSrvFun.RefreshPekos();
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityDropItemEvent(EntityDropItemEvent event ){
        if(event.isCancelled()) return;
        if (!isHoloPet(event.getEntity())) return;
        Entity pet = event.getEntity();
        if(((CraftEntity)pet).getHandle() instanceof PekoSrvFun_HoloPet){
            PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)pet).getHandle();
            holoPet.inventory.remove(event.getItemDrop().getItemStack());
            Utils.setPetInventory(holoPet);
        }

    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event ){
        if(event.isCancelled()) return;
        if (!isHoloPet(event.getEntity())) return;
        Entity pet = event.getEntity();
        if(((CraftEntity)pet).getHandle() instanceof PekoSrvFun_HoloPet){
            PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)pet).getHandle();
            holoPet.inventory.addItem(event.getItem().getItemStack());
            Utils.setPetInventory(holoPet);
        }
    }


    /** @noinspection unused*/
    @EventHandler
    public void onEntityResurrectEvent( EntityResurrectEvent event ){
        if(event.isCancelled()) return;
        if (!isHoloPet(event.getEntity())) return;
        Entity pet = event.getEntity();
        if(((CraftEntity)pet).getHandle() instanceof PekoSrvFun_HoloPet){
            PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)pet).getHandle();
            Inventory inventory = holoPet.inventory;
            EntityEquipment equipment = ((Zombie) holoPet.getBukkitEntity()).getEquipment();
            int totemIndex = -1;
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) continue;
                if (item.getType() == Material.TOTEM_OF_UNDYING) {
                    totemIndex = i;
                }
            }
            if (totemIndex > -1){
                inventory.setItem(totemIndex, new ItemStack(Material.AIR, 1));
            }
            if (inventory.contains(Material.TOTEM_OF_UNDYING)){
                equipment.setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
            }
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event ){
            InventoryHolder ent = event.getInventory().getHolder();
            if (ent == null) return;
            if( ent.getClass() == PekoSrvFun_HoloPet.class) Utils.setPetInventory(ent);
    }

    void RightClickedOnPet(Player player, Entity pet){
        if(((CraftEntity)pet).getHandle() instanceof PekoSrvFun_HoloPet){
            if (player.getEquipment() != null ){
                if (player.getEquipment().getItemInMainHand().getType() == Material.NAME_TAG){
                    return;
                }
            }
            PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)pet).getHandle();
            if (player.isSneaking()) {
                if (!(holoPet.getPetName().equals("Suisei") || holoPet.getPetName().equals("Rushia"))){
                    if (player.getEquipment() != null ){
                        if (player.getEquipment().getItemInMainHand().getType() == Material.AIR){
                            if (holoPet.Sitting){
                                holoPet.Sitting = false;
                                DisguiseAPI.getDisguise(holoPet.getBukkitEntity()).getWatcher().setSneaking(false);
                            }else {
                                holoPet.Sitting = true;
                                DisguiseAPI.getDisguise(holoPet.getBukkitEntity()).getWatcher().setSneaking(true);
                            }
                            return;
                        }
                    }
                }
            }

            double currentHealth = ((LivingEntity) holoPet.getBukkitEntity()).getHealth();
            double maxHealth = ((LivingEntity) holoPet.getBukkitEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            Inventory newInventory = Bukkit.createInventory(holoPet, 9, holoPet.getPetName() + "'s Inventory | HP: " + round(currentHealth) + "/" + round(maxHealth) );
            newInventory.setContents(holoPet.inventory.getContents());
            holoPet.inventory = newInventory;
            player.openInventory(holoPet.inventory);
        }
    }


    public void ClickedOnHorse(PlayerInteractEntityEvent event){
        //not yet.
    }

    /** @noinspection unused*/
    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        if(event.getHand().equals(EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.HORSE || entity.getType() == EntityType.SKELETON_HORSE || entity.getType() == EntityType.ZOMBIE_HORSE ) ClickedOnHorse(event);
        if (isHoloPet(entity)) RightClickedOnPet(player, entity);
        if (!isPekoMon(entity)) return;

        if (inventory.getItemInOffHand().isSimilar(new ItemStack(Material.NAME_TAG)) || inventory.getItemInMainHand().isSimilar(new ItemStack(Material.NAME_TAG))){
            event.setCancelled(true);
            return;
        }

        if (inventory.getItemInMainHand().isSimilar(new ItemStack(Material.CARROT)) || inventory.getItemInMainHand().isSimilar(new ItemStack(Material.MILK_BUCKET))){
            PersistentDataContainer mainPekomonContainer = entity.getPersistentDataContainer();
            String mainPekomonTypeKey = mainPekomonContainer.get(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING);
            boolean mainFemale = mainPekomonTypeKey.endsWith("F");
            boolean mainTired;

            if (mainPekomonContainer.has(PekoSrvFun.pekomonLastBreedKey, PersistentDataType.LONG)){
                long lastBreedTime = mainPekomonContainer.get(PekoSrvFun.pekomonLastBreedKey, PersistentDataType.LONG);
                long currentTime = new Date().getTime();
                mainTired = ((currentTime - lastBreedTime) < 300000);
            }else {
                mainTired = false;
            }

            for (Entity near : entity.getNearbyEntities(2,2,2)){
                if (isPekoMon(near)){
                    boolean secondaryTired;
                    boolean secondaryFemale = near.getPersistentDataContainer().get(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING).endsWith("F");
                    if (near.getPersistentDataContainer().has(PekoSrvFun.pekomonLastBreedKey, PersistentDataType.LONG)){
                        long lastBreedTime = near.getPersistentDataContainer().get(PekoSrvFun.pekomonLastBreedKey, PersistentDataType.LONG);
                        long currentTime = new Date().getTime();
                        secondaryTired = ((currentTime - lastBreedTime) < 300000);
                    }else {
                        secondaryTired = false;
                    }
                    if (!(mainTired || secondaryTired)){
                        if(mainFemale ^ secondaryFemale){
                            entity.getPersistentDataContainer().set(PekoSrvFun.pekomonLastBreedKey, PersistentDataType.LONG, new Date().getTime());
                            near.getPersistentDataContainer().set(PekoSrvFun.pekomonLastBreedKey, PersistentDataType.LONG, new Date().getTime());
                            inventory.removeItem(new ItemStack(inventory.getItemInMainHand().getType(),1));
                            entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(),1);
                            Slime slime = (Slime) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.SLIME);
                            slime.setSize(0);
                            SetPekomon(slime);
                            return;
                        }
                    }
                }
            }
            entity.getWorld().spawnParticle(Particle.SMOKE_LARGE, entity.getLocation(),1);
        }
    }

    void HoloPetDeathEvent(EntityDeathEvent event){
        Entity pet = event.getEntity();
        if(!(((CraftEntity)pet).getHandle() instanceof PekoSrvFun_HoloPet)) return;
        PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)pet).getHandle();
        Inventory inventory = holoPet.inventory;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            pet.getWorld().dropItem(pet.getLocation(), item);
        }
        event.getDrops().clear();
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event){
        Entity entity = event.getEntity();
        if (isHoloPet(event.getEntity())) HoloPetDeathEvent(event);

        if (entity.getType() == EntityType.SALMON ||entity.getType() == EntityType.COD ){
            if (DisguiseAPI.isDisguised(entity)){
                Disguise dis = DisguiseAPI.getDisguise(entity);
                ItemStack skull = dis.getWatcher().getHelmet().clone();
                skull.setAmount(1);
                entity.getWorld().dropItem(entity.getLocation(), skull);
            }
            return;
        }
        if (entity.getType() == EntityType.SLIME){
            if (DisguiseAPI.isDisguised(entity)){
                Disguise dis = DisguiseAPI.getDisguise(entity);
                ItemStack skull = dis.getWatcher().getHelmet().clone();
                ItemMeta meta = skull.getItemMeta();
                String lore = meta.getLore().get(0);
                ItemStack newSkull;
                switch (lore){
                    case "Flipped Happy Pekomon M":
                        newSkull =  PekoSrvFun.PekomonLaughSkull.clone();
                        break;
                    case "Flipped Cool Pekomon M":
                        newSkull =  PekoSrvFun.PekomonCoolSkull.clone();
                        break;
                    case "Flipped Winking Pekomon M":
                        newSkull =  PekoSrvFun.PekomonWinkSkull.clone();
                        break;
                    case "Flipped Derp Pekomon M":
                        newSkull =  PekoSrvFun.PekomonDerpSkull.clone();
                        break;
                    case "???? M":
                        newSkull =  PekoSrvFun.PekomonBlankSkull.clone();
                        break;
                    case "Flipped Happy Pekomon F":
                        newSkull =  PekoSrvFun.PekomonLaughSkullF.clone();
                        break;
                    case "Flipped Cool Pekomon F":
                        newSkull =  PekoSrvFun.PekomonCoolSkullF.clone();
                        break;
                    case "Flipped Winking Pekomon F":
                        newSkull =  PekoSrvFun.PekomonWinkSkullF.clone();
                        break;
                    case "Flipped Derp Pekomon F":
                        newSkull =  PekoSrvFun.PekomonDerpSkullF.clone();
                        break;
                    case "???? F":
                        newSkull =  PekoSrvFun.PekomonBlankSkullF.clone();
                        break;
                    case "Flipped Smiling Pekomon F":
                        newSkull =  PekoSrvFun.PekomonSmileSkullF.clone();
                        break;
                    default:
                        newSkull =  PekoSrvFun.PekomonSmileSkull.clone();
                }
                newSkull.setAmount(1);
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.SLIME_BALL, 1));
                Random prob = new Random();
                int tp = prob.nextInt(100);
                if (tp <= 5){
                    event.getDrops().add(new ItemStack(Material.MILK_BUCKET, 1));
                }
                tp = prob.nextInt(100);
                if (tp <= 1){
                    event.getDrops().add(new ItemStack(Material.CARROT, 1));
                }
                event.getDrops().add(newSkull);
                entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.ENTITY_RABBIT_DEATH, SoundCategory.NEUTRAL,2,1 );
            }
        }
    }


    public void SetFishPekomon(Entity entity){
        Random prob = new Random();
        int tp = prob.nextInt(100);
        if (tp <= 1){
            Random r = new Random();
            int res = r.nextInt(2);
            ItemStack skull;
            if (res == 0){
                skull =  PekoSrvFun.PekomonSmileSkull;
            }else{
                skull =  PekoSrvFun.PekomonLaughSkull;
            }
            MobDisguise disguise = new MobDisguise(DisguiseType.ZOMBIE);
            ZombieWatcher watcher = (ZombieWatcher) disguise.getWatcher();
            watcher.setInvisible(true);
            watcher.setBaby(true);
            watcher.setCustomName("Wild PekoMon");
            watcher.setCustomNameVisible(true);
            watcher.setArmor(new ItemStack[]{null, null, null, skull});
            DisguiseAPI.disguiseToAll(entity, disguise);
        }
    }

    public static void SetSlimePekomon(Entity entity){
        Slime slime = (Slime) entity;
        if (slime.getSize() == 1){
            Random prob = new Random();
            int tp = prob.nextInt(500);
            if (tp <= 2){
                SetPekomon(slime);
            }
        }
    }

    public static void SetPekomon(Slime slime){
        Location location = slime.getLocation();
        slime.remove();
        Random r = new Random();
        int res = r.nextInt(100);
        Random flip = new Random();
        int sex = flip.nextInt(999) + 1; //Fun Fact: 93,1% of nousagis are male.
        String name;
        if (res == 0){ //1 in 101
            if (sex <= 931){
                name = "Blank";
            }else{
                name = "BlankF";
            }
        }else if( res < 33){
            if (sex <= 931){
                name = "Smile";
            }else{
                name = "SmileF";
            }
        }else if (res < 45) {
            if (sex <= 931){
                name = "Wink";
            }else{
                name = "WinkF";
            }
        }else if (res < 85) {
            if (sex <= 931){
                name = "Happy";
            }else{
                name = "HappyF";
            }
        }else if (res < 90) {
            if (sex <= 931){
                name = "Derp";
            }else{
                name = "DerpD";
            }
        }else{
            if (sex <= 931){
                name = "Cool";
            }else{
                name = "CoolF";
            }
        }
        PekoSrvFun_Pekomon pekomon = new PekoSrvFun_Pekomon(location, name);
    }

    boolean isPekoMon(Entity entity){
        if (!(entity.getType() == EntityType.SLIME)) return false;
        if (!(DisguiseAPI.isDisguised(entity))) return false;
        FlagWatcher watcher = DisguiseAPI.getDisguise(entity).getWatcher();
        if (!watcher.getDisguise().isUpsideDown()) return false;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING))) return false;
        String pekomonTypeKey;
        pekomonTypeKey = container.get(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING);
        if (pekomonTypeKey.isBlank()) return false;
        return true;
    }

    boolean isHoloPet(Entity entity){
        if (!(DisguiseAPI.isDisguised(entity))) return false;
        FlagWatcher watcher = DisguiseAPI.getDisguise(entity).getWatcher();
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING))) return false;
        String holoPetTypeKey;
        holoPetTypeKey = container.get(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING);
        if (holoPetTypeKey.isBlank()) return false;
        return true;
    }
}