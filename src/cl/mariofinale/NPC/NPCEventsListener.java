package cl.mariofinale.NPC;

import cl.mariofinale.Peko_Utils;
import me.libraryaddict.disguise.DisguiseAPI;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class NPCEventsListener implements Listener {

    /** @noinspection unused*/
    @EventHandler
    public void onEntityTeleportEvent(EntityTeleportEvent event){
        if(event.isCancelled()) return;
        if(event.getTo() == null) return;
        Entity pet = event.getEntity();
        if (!pet.isValid())return;
        if (!NPCHelper.isNPC(event.getEntity())) return;
        NPCHelper npc = NPCHelper.GetNPCFromEntity(pet);
        if (npc == null) return;
        npc.checkDisguiseStatus();
    }
    /** @noinspection unused*/
    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event){
        if (!event.getPlayer().isValid()) return;
        Player player = event.getPlayer();
        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();
        if (toLocation == null) return;
        World fromWorld = fromLocation.getWorld();
        World toWorld = toLocation.getWorld();
        if (fromWorld == null) return;
        if (toWorld == null) return;
        if (NPCUtils.isMobSpawnDenied(toLocation)) return;
        String toWorldName = toWorld.getName().toLowerCase();
        String fromWorldName = fromWorld.getName().toLowerCase();
        if (toWorldName.contains("creativo")) return;
        if (toWorldName.contains("factions") || fromWorldName.contains("factions")) {
            if (toWorldName.contains("factions") && !fromWorldName.contains("factions")) return;
            if (fromWorldName.contains("factions") && !toWorldName.contains("factions")) return;
        }
        Collection<Entity> entities = fromWorld.getNearbyEntities(fromLocation,40,40,40);
        for (Entity entity : entities){
            if (!NPCHelper.isNPC(entity)) continue;
            NPCHelper helper = NPCHelper.GetNPCFromEntity(entity);
            if (helper == null) continue;
            if (!helper.getOwner().equals(player.getName())) continue;
            if (helper.isFreeToWander()) continue;
            if (helper.isSitting()) continue;
            entity.teleport(toLocation);
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if (!(e.getEntity() instanceof LivingEntity hitter)) return;
        if (e.getEntity() instanceof Player hittedPlayer) {
            Set<NPCHelper> hitPlayerNCPList = NPCUtils.GetPlayerNPCs(hittedPlayer);
            for (NPCHelper helper : hitPlayerNCPList) {
                if (NPCUtils.getDistance(helper.getBukkitEntity(), hittedPlayer) <= 20) {
                    helper.setNPCTarget(hitter);
                }
            }
        }
        if (!(e.getDamager() instanceof Player player)) return;
        Set<NPCHelper> NPCList = NPCUtils.GetPlayerNPCs(player);
        for (NPCHelper helper : NPCList){
            if (NPCUtils.getDistance(helper.getBukkitEntity(), player) <= 20){
                helper.setNPCTarget((LivingEntity) e.getEntity());
            }
        }
        if (!(e.getEntity() instanceof Zombie)) return;
        if (!NPCHelper.isNPC(e.getEntity())) return;
        NPCHelper npcHelper = (NPCHelper) ((CraftEntity) e.getEntity()).getHandle();
        String npcOwner = npcHelper.getOwner();
        World world = player.getWorld();
        if (!player.getName().equals(npcOwner)) {
            if (world.getPVP()){
                npcHelper.setNPCTarget(player);
                return; //allow hit on pvp world
            }
            e.setCancelled(true);
            Peko_Utils.sendBarTextToPlayer(player,"You can't damage " + npcHelper.getNPCName() + "!");
            for (NPCHelper helper : NPCList){
                if (NPCUtils.getDistance(helper.getBukkitEntity(), player) <= 20){
                    helper.clearNPCTarget();
                }
            }
            return;
        }
        if (!player.isSneaking()) return;
        e.setCancelled(true);
        if (npcHelper.isFreeToWander()){
            Peko_Utils.sendBarTextToPlayer(player,npcHelper.getNPCName() + " will follow you.");
            npcHelper.setFollowOwner();
            npcHelper.clearNPCTarget();
        }else{
            Peko_Utils.sendBarTextToPlayer(player,npcHelper.getNPCName() + " will roam around freely.");
            npcHelper.setFreeToWander();
            npcHelper.clearNPCTarget();
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntitiesUnloadEvent(EntitiesUnloadEvent event){
        List<Entity> entities = event.getEntities();
        for (Entity entity: entities){
            if (NPCHelper.isNPC(entity)){
                if(!(((CraftEntity)entity).getHandle() instanceof NPCHelper)){
                    PersistentDataContainer container = entity.getPersistentDataContainer();
                    String NPCHelperTypeKey = container.get(NPCHelper.NPCHelperTypeKey(), PersistentDataType.STRING);
                    String NPCHelperNameKey = container.get(NPCHelper.NPCHelperNameKey(), PersistentDataType.STRING);
                    String NPCHelperOwnerKey = container.get(NPCHelper.NPCHelperOwnerKey(), PersistentDataType.STRING);
                    NPCHelper pet = new NPCHelper(entity.getLocation(), NPCHelperOwnerKey, NPCHelperTypeKey, NPCHelperNameKey, entity.getPersistentDataContainer());
                    entity.remove();
                }
                event.getChunk().setForceLoaded(true);
                event.getChunk().load();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> event.getChunk().setForceLoaded(false), 20L);
            }
        }
    }


    /** @noinspection unused*/
    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event){
        Entity[] entities = event.getChunk().getEntities();
        for (Entity entity: entities){
            if (NPCHelper.isNPC(entity)){
                if(!(((CraftEntity)entity).getHandle() instanceof NPCHelper)){
                    PersistentDataContainer container = entity.getPersistentDataContainer();
                    String NPCHelperTypeKey = container.get(NPCHelper.NPCHelperTypeKey(), PersistentDataType.STRING);
                    String NPCHelperNameKey = container.get(NPCHelper.NPCHelperNameKey(), PersistentDataType.STRING);
                    String NPCHelperOwnerKey = container.get(NPCHelper.NPCHelperOwnerKey(), PersistentDataType.STRING);
                    NPCHelper pet = new NPCHelper(entity.getLocation(), NPCHelperOwnerKey, NPCHelperTypeKey, NPCHelperNameKey, entity.getPersistentDataContainer());
                    entity.remove();
                }
                event.getChunk().setForceLoaded(true);
                event.getChunk().load();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(NPCHelper.plugin(), () -> event.getChunk().setForceLoaded(false), 20L);
            }
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityPortalEvent(EntityPortalEvent event){
        if (!NPCHelper.isNPC(event.getEntity())) return;
        event.setCancelled(true);
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event){
        Entity entity = event.getEntity();
        if (NPCHelper.isNPC(event.getEntity())) NPCHelperDeathEvent(event);
    }

    private void NPCHelperDeathEvent(EntityDeathEvent event){
        Entity pet = event.getEntity();
        if(!(((CraftEntity)pet).getHandle() instanceof NPCHelper helper)) return;
        helper.dropInventory();
        event.getDrops().clear();
        Location location = event.getEntity().getLocation();
        String locationString = "X" + location.getBlockX() + " Y" + location.getBlockY() + " Z" + location.getBlockZ();
        NPCHelper.plugin().getLogger().warning("[NPC] " + helper.getNPCName() + " NPC died! at: " + locationString + " Owner: " + helper.getOwner());
        Player player = Bukkit.getPlayer(helper.getOwner());
        if (player == null) return;
        if (player.isOnline()) Peko_Utils.sendMessageToPlayer(player,"Your NPC " + helper.getNPCName() + " died! at: " + locationString);
    }

    /** @noinspection unused*/
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event){
        InventoryHolder ent = event.getInventory().getHolder();
        if (ent == null) return;
        if(ent instanceof NPCHelper helper) helper.setNPCInventory();
    }

    /** @noinspection unused*/
    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        if(event.getHand().equals(EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.HORSE || entity.getType() == EntityType.SKELETON_HORSE || entity.getType() == EntityType.ZOMBIE_HORSE ) clickedOnHorse(event);
        if (NPCHelper.isNPC(entity)){
            clickedOnPet(player, entity);
            event.setCancelled(true);
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityDismountEvent(EntityDismountEvent event){
        Entity dismounted = event.getDismounted();
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!player.isValid()) return;
        if (!(dismounted instanceof Arrow arrow)) return;
        arrow.remove();
    }

    /** @noinspection unused, EmptyMethod */
    private void clickedOnHorse(PlayerInteractEntityEvent event){
        //TODO: Horse mechanics.
    }

    private void clickedOnPet(Player player, Entity pet){
        if(!NPCHelper.isNPC(pet)) return;
        NPCHelper npcHelper = NPCHelper.GetNPCFromEntity(pet);
        if (npcHelper == null) return;
        if (!player.getName().equals(npcHelper.getOwner())){
            Peko_Utils.sendBarTextToPlayer(player,npcHelper.getNPCUserName() + " npc owned by " + npcHelper.getOwner());
            return;
        }
        if (player.getEquipment() != null ){
            if (player.getEquipment().getItemInMainHand().getType() == Material.NAME_TAG){
                ItemMeta meta = player.getEquipment().getItemInMainHand().getItemMeta();
                if (meta == null) return;
                String tagName = meta.getDisplayName();
                if (tagName.toUpperCase().startsWith("DISGUISE.")|| tagName.toUpperCase().startsWith("SKIN.") ){
                    tagName = tagName.replaceFirst("[Dd][Ii][Ss][Gg][Uu][Ii][Ss][Ee]\\.","").trim();
                    tagName = tagName.replaceFirst("[Ss][Kk][Ii][Nn]\\.","").trim();
                    npcHelper.setNPCUserName(tagName);
                }else {
                    npcHelper.setNPCName(tagName);
                }
                return;
            }
        }
        if (player.isSneaking()){
            if (player.getEquipment() == null) return;
            if (!(player.getEquipment().getItemInMainHand().getType() == Material.AIR)) return;
            if (npcHelper.isSitting()){
                npcHelper.setNotSitting();
                Peko_Utils.sendBarTextToPlayer(player,npcHelper.getNPCName() + " is no longer waiting.");
            }else {
                npcHelper.setSitting();
                Peko_Utils.sendBarTextToPlayer(player,npcHelper.getNPCName() + " will wait here.");
            }
        }else{
            player.openInventory(npcHelper.getInventoryForPlayerGUI());
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityResurrectEvent( EntityResurrectEvent event ){
        if(event.isCancelled()) return;
        if (!NPCHelper.isNPC(event.getEntity())) return;
        if (((CraftEntity) event.getEntity()).getHandle() instanceof NPCHelper helper){
            helper.removeItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
            helper.setNPCInventory();
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        NPCWatchdog.RefreshNPCHelpers();
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityDropItemEvent(EntityDropItemEvent event){
        if(event.isCancelled()) return;
        if (!NPCHelper.isNPC(event.getEntity())) return;
        Entity pet = event.getEntity();
        if(((CraftEntity)pet).getHandle() instanceof NPCHelper helper){
            helper.removeItem(event.getItemDrop().getItemStack());
            helper.setNPCInventory();
        }
    }

    /** @noinspection unused*/
    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event ){
        if(event.isCancelled()) return;
        if (!NPCHelper.isNPC(event.getEntity())) return;
        Entity pet = event.getEntity();
        if(((CraftEntity)pet).getHandle() instanceof NPCHelper helper){
            helper.addItem(event.getItem().getItemStack());
            helper.setNPCInventory();
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
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event){
        if (event.isCancelled() ){
            if (event.getBedEnterResult() ==  PlayerBedEnterEvent.BedEnterResult.NOT_SAFE){
                World world = event.getBed().getLocation().getWorld();
                if (world == null) return;
                Collection<Entity> nearbyEntities = world.getNearbyEntities(event.getBed().getLocation(), 8,5,8);
                int petsNear = 0;
                int monstersNear = 0;

                for (Entity ent: nearbyEntities) {
                    if (ent.getPersistentDataContainer().has(NPCHelper.NPCHelperTypeKey(), PersistentDataType.STRING)){
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
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Sleeping through this night."));
                }
            }
        }
    }


    /** @noinspection unused*/
    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event){
        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        if (target == null) return;
        if (!entity.isValid()) return;
        if (!target.isValid()) return;

        if (NPCHelper.isNPC(target) && entity.getType().equals(EntityType.IRON_GOLEM)){
            event.setCancelled(true);
        }
        if (DisguiseAPI.isDisguised(entity) && DisguiseAPI.isDisguised(target)){
            event.setCancelled(true);
            return;
        }


        if (!DisguiseAPI.isDisguised(event.getEntity())) return;

        if (NPCHelper.isNPC(entity) && NPCHelper.isNPC(target)){
            event.setCancelled(true);
            return;
        }
        if (NPCHelper.isNPC(entity) && target instanceof Player){
            event.setCancelled(true);
            return;
        }

        if (NPCHelper.isNPC(entity)){
            NPCHelper holoPet = (NPCHelper) ((CraftEntity)entity).getHandle();
            if (holoPet.isSitting()){
                event.setCancelled(true);
            }
        }

    }
    
}

