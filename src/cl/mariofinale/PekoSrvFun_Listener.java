package cl.mariofinale;
import me.libraryaddict.disguise.*;
import me.libraryaddict.disguise.disguisetypes.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
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
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        if(event.getHand().equals(EquipmentSlot.HAND)) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Entity entity = event.getRightClicked();
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
            entity.getWorld().spawnParticle(Particle.SMOKE, entity.getLocation(),1);
        }
    }


    /** @noinspection unused*/
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event){
        Entity entity = event.getEntity();

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
                if (meta == null) return;
                String name = meta.getItemName();
                ItemStack newSkull = switch (name) {
                    case "Flipped Happy Pekomon M" -> PekoSrvFun.PekomonLaughSkull.clone();
                    case "Flipped Cool Pekomon M" -> PekoSrvFun.PekomonCoolSkull.clone();
                    case "Flipped Winking Pekomon M" -> PekoSrvFun.PekomonWinkSkull.clone();
                    case "Flipped Derp Pekomon M" -> PekoSrvFun.PekomonDerpSkull.clone();
                    case "???? M" -> PekoSrvFun.PekomonBlankSkull.clone();
                    case "Flipped Happy Pekomon F" -> PekoSrvFun.PekomonLaughSkullF.clone();
                    case "Flipped Cool Pekomon F" -> PekoSrvFun.PekomonCoolSkullF.clone();
                    case "Flipped Winking Pekomon F" -> PekoSrvFun.PekomonWinkSkullF.clone();
                    case "Flipped Derp Pekomon F" -> PekoSrvFun.PekomonDerpSkullF.clone();
                    case "???? F" -> PekoSrvFun.PekomonBlankSkullF.clone();
                    case "Flipped Smiling Pekomon F" -> PekoSrvFun.PekomonSmileSkullF.clone();
                    default -> PekoSrvFun.PekomonSmileSkull.clone();
                };
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
        if (entity == null) return false;
        if (!(entity.getType() == EntityType.SLIME)) return false;
        if (!(DisguiseAPI.isDisguised(entity))) return false;
        FlagWatcher watcher = DisguiseAPI.getDisguise(entity).getWatcher();
        if (!watcher.getDisguise().isUpsideDown()) return false;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING))) return false;
        String pekomonTypeKey = container.get(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING);
        if (pekomonTypeKey == null) return false;
        return !pekomonTypeKey.isBlank();
    }


}