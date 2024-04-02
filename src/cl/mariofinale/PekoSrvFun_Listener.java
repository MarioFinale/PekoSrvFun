package cl.mariofinale;
import me.libraryaddict.disguise.*;
import me.libraryaddict.disguise.disguisetypes.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static java.lang.Math.round;

class PekoSrvFun_Listener implements Listener{


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
        PekoSrvFun_HoloPet newpet = new PekoSrvFun_HoloPet(event.getTo(), ownerName, holoPetData, pet.getCustomName(), container);
        pet.remove();
        event.setCancelled(true);
    }


    /** @noinspection unused*/
    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event){
        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (isHoloPet(target) && entity.getType().equals(EntityType.IRON_GOLEM)){
            event.setCancelled(true);
        }

        if (!DisguiseAPI.isDisguised(event.getEntity())) return;

        if (isHoloPet(entity) && isHoloPet(target)){
            event.setCancelled(true);
            return;
        }
        if (isHoloPet(entity) && target instanceof Player){
            event.setCancelled(true);
            return;
        }

        if (isHoloPet(entity)){
            PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)entity).getHandle();
            if (holoPet.getStatus().equals("Sitting")){
                event.setCancelled(true);
            }
        }

    }


    /** @noinspection unused*/
    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event){
        if (event.isCancelled() ){
            if (event.getBedEnterResult() ==  PlayerBedEnterEvent.BedEnterResult.NOT_SAFE){
                Collection<Entity> nearbyEntities = event.getBed().getLocation().getWorld().getNearbyEntities(event.getBed().getLocation(), 8,5,8);
                int petsNear = 0;
                int monstersNear = 0;

                for (Entity ent: nearbyEntities) {
                    if (ent.getPersistentDataContainer().has(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING)){
                        petsNear += 1;
                        continue;
                    }

                    if (ent.getType() == EntityType.PLAYER){
                        petsNear += 1;
                        continue;
                    }
                    if (ent instanceof  Monster){
                        monstersNear += 1;
                    }
                }

                if (nearbyEntities.size() <= petsNear){
                    event.setCancelled(false);
                    event.getPlayer().sleep(event.getBed().getLocation(), true);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Sleeping through this night."));
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

    private void RightClickedOnPet(Player player, Entity pet){
        if(((CraftEntity)pet).getHandle() instanceof PekoSrvFun_HoloPet){
            PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)pet).getHandle();
            if (!player.getName().equals(holoPet.getOwner())){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(holoPet.getPetName() + " pet owned by " + holoPet.getOwner()));
                return;
            }

            if (player.getEquipment() != null ){
                if (player.getEquipment().getItemInMainHand().getType() == Material.NAME_TAG){
                    return;
                }
            }

            if (player.isSneaking()) {
                if (!(holoPet.getPetName().equals("Suisei") || holoPet.getPetName().equals("Rushia"))){
                    if (player.getEquipment() != null ){
                        if (player.getEquipment().getItemInMainHand().getType() == Material.AIR){
                            if (holoPet.getStatus().equals("Sitting")){
                                holoPet.setStatus("Normal");
                                DisguiseAPI.getDisguise(holoPet.getBukkitEntity()).getWatcher().setSneaking(false);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(holoPet.getPetName() + " is no longer waiting."));
                            }else {
                                holoPet.setStatus("Sitting");
                                DisguiseAPI.getDisguise(holoPet.getBukkitEntity()).getWatcher().setSneaking(true);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(holoPet.getPetName() + " will wait here."));
                            }
                            return;
                        }
                    }
                }
            }

            double currentHealth = ((LivingEntity) holoPet.getBukkitEntity()).getHealth();
            double maxHealth = 20; //Let's force max health to 20. For some reason the Max health attribute changes to absurd levels sometimes
            Objects.requireNonNull(((LivingEntity) holoPet.getBukkitEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20); //Force set base health to 20
            Inventory newInventory = Bukkit.createInventory(holoPet, 9, holoPet.getPetName() + "'s Inventory | HP: " + round(currentHealth) + "/" + round(maxHealth) );
            newInventory.setContents(holoPet.inventory.getContents());
            holoPet.inventory = newInventory;
            player.openInventory(holoPet.inventory);
        }
    }


    /** @noinspection unused*/
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getLocation().getWorld() == null) return;
            if (event.getClickedBlock().getBlockData() instanceof Bed){
                Collection<Entity> nearbyEntities = event.getClickedBlock().getLocation().getWorld().getNearbyEntities(event.getClickedBlock().getLocation(),10,10,10);
                for (Entity ent: nearbyEntities) {
                    if (ent.getPersistentDataContainer().has(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING)){
                        PekoSrvFun_HoloPet pet = (PekoSrvFun_HoloPet)((CraftEntity) ent).getHandle();
                        if (pet.getPetName().equals("Suisei")){
                            event.setCancelled(true);
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You may not sleep now; Suisei is near."));
                            return;
                        }
                        if (pet.getPetName().equals("Rushia")){
                            event.setCancelled(true);
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You may not sleep now; Rushia is near."));
                            return;
                        }
                    }
                }
            }
        }
    }

    /** @noinspection unused, EmptyMethod */
    private void ClickedOnHorse(PlayerInteractEntityEvent event){
        ///TODO: Horse mechanics.
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

    private void HoloPetDeathEvent(EntityDeathEvent event){
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
        Location location = event.getEntity().getLocation();
        String locationString = "X" + location.getBlockX() + " Y" + location.getBlockY() + " Z" + location.getBlockZ();
        PekoSrvFun.LogWarn(holoPet.getPetName() + " pet died! at: " + locationString + " Owner: " + holoPet.getOwner());
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
                event.getDrops().add(newSkull);
                Random prob = new Random();
                int tp = prob.nextInt(100);
                if (tp <= 5){
                    event.getDrops().add(new ItemStack(Material.MILK_BUCKET, 1));
                }
                tp = prob.nextInt(100);
                if (tp <= 1){
                    event.getDrops().add(new ItemStack(Material.CARROT, 1));
                }
                entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.ENTITY_RABBIT_DEATH, SoundCategory.NEUTRAL,2,1 );
            }
        }
    }


    /** @noinspection unused*/
    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event ){
         Entity[] entities = event.getChunk().getEntities();
         for (Entity entity: entities){
             if (isHoloPet(entity)){
                 Entity entity1 = entity;
                 if(!(((CraftEntity)entity).getHandle() instanceof PekoSrvFun_HoloPet)){
                     PersistentDataContainer container = entity.getPersistentDataContainer();
                     String holoPetTypeKey = container.get(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING);
                     String holoPetOwnerKey = container.get(PekoSrvFun.holoPetOwnerKey, PersistentDataType.STRING);
                     PekoSrvFun_HoloPet pet = new PekoSrvFun_HoloPet(entity.getLocation(), holoPetOwnerKey, holoPetTypeKey, entity.getCustomName(), entity.getPersistentDataContainer());
                     entity.remove();
                     entity1 = pet.getBukkitEntity();
                 }
                 Location location = entity1.getLocation();
                 PekoSrvFun_HoloPet pet = (PekoSrvFun_HoloPet) ((CraftEntity)entity1).getHandle();
                 String locationString = "X" + location.getBlockX() + " Y" + location.getBlockY() + " Z" + location.getBlockZ();
                 PekoSrvFun.LogWarn(pet.getPetName() + " pet stored in unloaded chunk!: " + locationString + " Owner: " + pet.getOwner());
                 String ownerName = pet.getOwner();
                 Player player = Bukkit.getPlayer(ownerName);
                 if (player == null) continue;
                 if (player.isOnline()){
                     PekoSrvFun_Commands.SendMessageToPlayer(player,"Your " + pet.getPetName() + " pet is in a now unloaded chunk at: " + locationString);
                 }
             }
         }
    }


    private static void SetSlimePekomon(Entity entity){
        Slime slime = (Slime) entity;
        if (slime.getSize() == 1){
            Random prob = new Random();
            int tp = prob.nextInt(500);
            if (tp <= 2){
                SetPekomon(slime);
            }
        }
    }

    private static void SetPekomon(Slime slime){
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
        new PekoSrvFun_Pekomon(location, name);
    }



    private boolean isPekoMon(Entity entity){
        if (!(entity.getType() == EntityType.SLIME)) return false;
        if (!(DisguiseAPI.isDisguised(entity))) return false;
        FlagWatcher watcher = DisguiseAPI.getDisguise(entity).getWatcher();
        if (!watcher.getDisguise().isUpsideDown()) return false;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING))) return false;
        String pekomonTypeKey;
        pekomonTypeKey = container.get(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING);
        return !pekomonTypeKey.isBlank();
    }

    private boolean isHoloPet(Entity entity){
        if (entity == null) return  false;
        if (!entity.isValid()) return  false;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING))) return false;
        if (!(container.has(PekoSrvFun.holoPetOwnerKey, PersistentDataType.STRING))) return false;
        String holoPetTypeKey;
        holoPetTypeKey = container.get(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING);
        return !holoPetTypeKey.isBlank();
    }

}