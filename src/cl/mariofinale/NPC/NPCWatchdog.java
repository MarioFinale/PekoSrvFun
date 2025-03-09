package cl.mariofinale.NPC;

import me.libraryaddict.disguise.DisguiseAPI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;


import static cl.mariofinale.NPC.NPCUtils.*;

public class NPCWatchdog implements Runnable {

    @Override
    public void run() {
        RefreshNPCHelpers();
        NPCHelperFastWatcher();
    }


    public static void NPCHelperFastWatcher(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (!NPCHelper.isNPC(entity)) continue;
                NPCHelper helper = NPCHelper.GetNPCFromEntity(entity);
                if (helper == null) continue;
                if (helper.getEquipment() == null) return;

                helper.getChestPlate().ifPresent(chestPlate -> {
                    if(chestPlate.getType() == Material.ELYTRA){
                        if(!helper.isNPCInWater()){
                            if (helper.getNPCFallDistance() > 0.2D){
                                helper.setGliding(true);
                            }
                        }
                    }else{
                        helper.setGliding(false);
                    }
                });
                if (NPCHelper.IsAbnormalNPC(entity)) helper.disguise();
            }
        }
    }

    public static void RefreshNPCHelpers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
                String npcHelperType = getNPCHelperType(entity);
                if (!npcHelperType.isBlank()){
                    if (((CraftEntity)entity).getHandle() instanceof NPCHelper){
                       NPCHelper npcHelper = NPCHelper.GetNPCFromEntity(entity);
                       if (npcHelper == null) continue;
                       npcHelper.tryToEat();
                       npcHelper.checkDisguiseStatus();
                    }else {
                        String npcHelperName = getNPCHelperName(entity);
                        String ownerName = getNPCHelperOwner(entity);
                        if (ownerName.isBlank()) continue;
                        PersistentDataContainer container = entity.getPersistentDataContainer();
                        new NPCHelper(entity.getLocation(), ownerName, npcHelperType, npcHelperName, container);
                        entity.remove();
                    }
                }else{
                    if ((entity.getType() == EntityType.ZOMBIFIED_PIGLIN || entity.getType() == EntityType.ZOMBIE )) {
                        String entityName = entity.getCustomName();
                        if (entityName != null){
                            if (entityName.contains("clone")) {
                                if (!DisguiseAPI.isDisguised(entity)){
                                    entity.remove();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
