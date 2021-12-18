package cl.mariofinale;
import me.libraryaddict.disguise.*;
import me.libraryaddict.disguise.disguisetypes.*;
import me.libraryaddict.disguise.disguisetypes.watchers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

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
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Entity entity = event.getRightClicked();
        if (PekoSrvFun.PekomonList.containsKey(entity.getUniqueId())){
            if (inventory.getItemInOffHand().isSimilar(new ItemStack(Material.NAME_TAG)) || inventory.getItemInMainHand().isSimilar(new ItemStack(Material.NAME_TAG))){
                event.setCancelled(true);
                return;
            }
            if (inventory.getItemInMainHand().isSimilar(new ItemStack(Material.CARROT)) || inventory.getItemInMainHand().isSimilar(new ItemStack(Material.MILK_BUCKET))){
                boolean maintype = PekoSrvFun.PekomonList.get(entity.getUniqueId()).b().endsWith("F");
                boolean nearType = maintype;
                UUID nearUUID = null;
                UUID mainUUID = entity.getUniqueId();
                boolean tired = false;
                if (PekoSrvFun.TiredPekomonList.containsKey(nearUUID)){
                    if ((new Date().getTime() - PekoSrvFun.TiredPekomonList.get(mainUUID).getTime()) < 300000){
                        tired = true;
                    }
                }
                for (Entity near : entity.getNearbyEntities(2,2,2)){
                    if (PekoSrvFun.PekomonList.containsKey(near.getUniqueId())){
                        nearType = PekoSrvFun.PekomonList.get(near.getUniqueId()).b().endsWith("F");
                        nearUUID = near.getUniqueId();
                        if (PekoSrvFun.TiredPekomonList.containsKey(nearUUID)){
                            if ((new Date().getTime() - PekoSrvFun.TiredPekomonList.get(nearUUID).getTime()) < 300000){
                                tired = true;
                            }
                        }
                    }
                }
                if ((maintype ^ nearType) && !tired){
                    inventory.removeItem(new ItemStack(Material.CARROT,1));
                    entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation(),1);
                    Slime slime = (Slime) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.SLIME);
                    slime.setSize(0);
                    SetPekomon(slime);
                    PekoSrvFun.TiredPekomonList.put(nearUUID,new Date());
                    PekoSrvFun.TiredPekomonList.put(mainUUID,new Date());
                }else {
                    entity.getWorld().spawnParticle(Particle.SMOKE_LARGE, entity.getLocation(),1);
                }
            }
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
                if (PekoSrvFun.PekomonList.containsKey(entity.getUniqueId())){
                    PekoSrvFun.PekomonList.remove(entity.getUniqueId());
                }
            }
            return;
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
            int tp = prob.nextInt(100);
            if (tp <= 15){
                SetPekomon(slime);
            }
        }
    }

    public static void SetPekomon(Slime slime){
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
        Location location = slime.getLocation();
        PekoSrvFun_Pekomon pekomon = new PekoSrvFun_Pekomon(location, name);
        slime.remove();
    }
}