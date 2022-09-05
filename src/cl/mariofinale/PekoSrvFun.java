package cl.mariofinale;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieWatcher;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @noinspection unused
 */
public class PekoSrvFun extends JavaPlugin {
    static ArrayList<String> WorldsList = new ArrayList<>();
    static ItemStack PekomonSmileSkull;
    static ItemStack FlippedPekomonSmileSkull;
    static ItemStack PekomonLaughSkull;
    static ItemStack FlippedPekomonLaughSkull;
    static ItemStack PekomonCoolSkull;
    static ItemStack FlippedPekomonCoolSkull;
    static ItemStack PekomonBlankSkull;
    static ItemStack FlippedPekomonBlankSkull;
    static ItemStack PekomonDerpSkull;
    static ItemStack FlippedPekomonDerpSkull;
    static ItemStack PekomonWinkSkull;
    static ItemStack FlippedPekomonWinkSkull;
    static ItemStack PekomonSmileSkullF;
    static ItemStack FlippedPekomonSmileSkullF;
    static ItemStack PekomonLaughSkullF;
    static ItemStack FlippedPekomonLaughSkullF;
    static ItemStack PekomonCoolSkullF;
    static ItemStack FlippedPekomonCoolSkullF;
    static ItemStack PekomonBlankSkullF;
    static ItemStack FlippedPekomonBlankSkullF;
    static ItemStack PekomonDerpSkullF;
    static ItemStack FlippedPekomonDerpSkullF;
    static ItemStack PekomonWinkSkullF;
    static ItemStack FlippedPekomonWinkSkullF;
    static NamespacedKey holoPetTypeKey;
    static NamespacedKey holoPetStatusKey;
    static NamespacedKey holoPetOwnerKey;
    static NamespacedKey holoPetInventoryKey;
    static NamespacedKey pekomonTypeKey;
    static NamespacedKey pekomonLastBreedKey;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        LogInfo("Registering listener...");
        getServer().getPluginManager().registerEvents(new PekoSrvFun_Listener(), this);
        LogInfo("Listener registered.");
        LogInfo("Loading Worlds list...");
        LoadWorldsList();
        LogInfo("Worlds list loaded.");
        LogInfo("Registering commands...");
        PekoSrvFun_Commands pekoSrvFun_commands = new PekoSrvFun_Commands();
        this.getCommand("peko").setExecutor(pekoSrvFun_commands);
        LogInfo("Commands registered.");
        LogInfo("Creating skulls...");
        CreateSkulls();
        LogInfo("Skulls created.");
        LogInfo("Creating Namespaced Keys...");
        pekomonTypeKey = new NamespacedKey(this, "PekoMonType");
        pekomonLastBreedKey = new NamespacedKey(this, "PekoMonLastBreed");
        holoPetTypeKey = new NamespacedKey(this, "holoPetTypeKey");
        holoPetOwnerKey = new NamespacedKey(this, "holoPetOwnerKey");
        holoPetInventoryKey = new NamespacedKey(this, "holoPetInventoryKey");
        holoPetStatusKey = new NamespacedKey(this, "holoPetStatusKey");
        LogInfo("Namespaced Keys created.");
        LogInfo("Creating slow refreshing task...");
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            RefreshPekomons();
            RefreshPekos();
        }, 30, 60);
        LogInfo("Refreshing task created.");
        LogInfo("Creating fast refreshing task...");
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, PekoSrvFun::HoloPetFastWatcher, 0, 2);
        LogInfo("Fast refreshing task created.");
        LogInfo("PekoSrvFun loaded!");
    }

    @Override
    public void onDisable() {
        LogInfo("Saving config.");
        plugin.saveDefaultConfig();
        SaveWorldsList();
        LogInfo("PekoSrvFun disabled!");
    }

    public void CreateSkulls() {
        PekomonSmileSkull = SetSkull("b7663111cc5f5c97c7c6c9e3c500edcac5179587be82f5b299b7e197d8efe503", "Smiling Pekomon M");

        FlippedPekomonSmileSkull = SetSkull("991de5578eef55db6fed22af00d5bf0d425b927cbfeeaa72871d2c0c29fc2378", "Flipped Smiling Pekomon M");

        PekomonLaughSkull = SetSkull("6644db503b23a879dd80d050cdb19d01e24cb5931b3160c4239c5da3ec63d7ea", "Happy Pekomon M");

        FlippedPekomonLaughSkull = SetSkull("be534411b12b93ccba2c4786de880a171ab39b50993b07ad4c1eb43392752b0f", "Flipped Happy Pekomon M");

        PekomonCoolSkull = SetSkull("167befffc1b45a591d47f8e0c0b65a1441ed2b61b268d80ce3a64b4bdc710bbe", "Cool Pekomon M");

        FlippedPekomonCoolSkull = SetSkull("ecefd7812356ffbbf259e618b8bdc6687523079f9c32258ec2b6c98ad8365ab", "Flipped Cool Pekomon M");

        PekomonBlankSkull = SetSkull("4b97c85f0c1dcf3f19a190294dbebb5e469fff11bfa3aad04a2eafa49c94a80a", ChatColor.MAGIC + "--------- M");

        FlippedPekomonBlankSkull = SetSkull("73418b338072c5b965e19d4dd91ee3d35262ec5b2fd530c1b94753b9b46997a5", "???? M");

        PekomonDerpSkull = SetSkull("d0bf0f2c2d1245413a6c2a016dc4034e1b0dfd58e032720d84ee8ff49d632021", "Derp Pekomon M");

        FlippedPekomonDerpSkull = SetSkull("c74d6123257bddb85c5082b7ed8f73b362d7c0b9f107c8d6275253cf79c681c9", "Flipped Derp Pekomon M");

        PekomonWinkSkull = SetSkull("33a81542669e3805613d00e82020dfd8587efe280278e0bfb8e7c19eb2388206", "Winking Pekomon M");

        FlippedPekomonWinkSkull = SetSkull("333e8ad6b369b5cd0a56e34e7c6fec64709893bfa347fcf872ad3a2b820b1638", "Flipped Winking Pekomon M");


        PekomonSmileSkullF = SetSkull("c1f5fcffdd2b2fdf3509ebd553dfc10eeb14b9e243efa3e08d3c291afc1c1909", "Smiling Pekomon F");

        FlippedPekomonSmileSkullF = SetSkull("119e9173f75617e11036afbbacae3ffb2b3fa5d80a9e882ebac690c3de2f71b7", "Flipped Smiling Pekomon F");

        PekomonLaughSkullF = SetSkull("b9c9418b7fe8e5102fdef53a0e6a6f3d16f4e9b9b837afda6e1460678e570d16", "Happy Pekomon F");

        FlippedPekomonLaughSkullF = SetSkull("5548989e1e4de91401d1d685b9a6d79fb20ac0f0236682aa116438f4c7537146", "Flipped Happy Pekomon F");

        PekomonCoolSkullF = SetSkull("e1c32055055bcff0aad71c0d06f91225ae908f2acb27cc436ad36c65c3ef286e", "Cool Pekomon F");

        FlippedPekomonCoolSkullF = SetSkull("ace5726397d0615ba1d6088e5d05417342a7bba7bd9e2abae63b9e0fb4dfb210", "Flipped Cool Pekomon F");

        PekomonBlankSkullF = SetSkull("b3af6f76be380e24b49345d3b5b24dd38c2be7d2d865686c6799fc8e8f1557af", ChatColor.MAGIC + "--------- F");

        FlippedPekomonBlankSkullF = SetSkull("5edf7063635c5ee8d26b15d45f3efa5a1a23fb16861b42639e880afc65cc32", "???? F");

        PekomonDerpSkullF = SetSkull("fc12b15ebede9ffbe976add294897506bd0a44417a140c88a4e067f6aa6da8b9", "Derp Pekomon F");

        FlippedPekomonDerpSkullF = SetSkull("c6ad2d5f75a276a0f8cc4ff1119632568384770e9df14ee3a643348554c0678d", "Flipped Derp Pekomon F");

        PekomonWinkSkullF = SetSkull("ce08126f8eb55b31af9523536a41895ef3bb658bfa742cf3ab4819c7cabd32ea", "Winking Pekomon F");

        FlippedPekomonWinkSkullF = SetSkull("33a065b541b6587c13fbfb7efb5b039fb6649cb4253e77df36152646517e045b", "Flipped Winking Pekomon F");

        LogInfo("Finished creating skulls!");
    }

    static void LogInfo(String line) {
        plugin.getLogger().log(Level.INFO, line);
    }

    static void LogWarn(String line) {
        plugin.getLogger().log(Level.WARNING, line);
    }

    static void LogError(String line) {
        plugin.getLogger().log(Level.SEVERE, line);
    }

    private void SaveWorldsList() {
        LogInfo("Saving Worlds list.");
        File WorldsListFile = new File(getDataFolder().getAbsolutePath(), "WorldsList.yml");
        try {
            WorldsListFile.delete();
            WorldsListFile.createNewFile();
            if (WorldsList == null) {
                WorldsList = new ArrayList<>();
            }
        } catch (Exception ex) {
            LogError("Error saving Worlds list: " + ex.getMessage());
            return;
        }
        FileConfiguration playerPointsConfig = YamlConfiguration.loadConfiguration(WorldsListFile);
        playerPointsConfig.set("List", WorldsList);

        try {
            playerPointsConfig.save(WorldsListFile);
        } catch (Exception ex) {
            LogError("Error saving Worlds list: " + ex.getMessage());
            return;
        }
        LogInfo("Worlds list saved.");
    }

    private void LoadWorldsList() {
        File WorldsListFile = new File(getDataFolder().getAbsolutePath(), "WorldsList.yml");
        try {
            WorldsListFile.createNewFile();
            FileConfiguration listConfigFile = YamlConfiguration.loadConfiguration(WorldsListFile);
            WorldsList = (ArrayList<String>) listConfigFile.get("List");
        } catch (Exception ex) {
            LogWarn("Worlds list file has not been loaded: " + ex.getMessage());
            LogWarn("If this is the first time running the plugin the file will be created on server stop.");
            return;
        }
        if (WorldsList == null) {
            WorldsList = new ArrayList<>();
        }
        LogInfo("Worlds list loaded.");
    }

    private void RefreshPekomons() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
                String pekomonData = getPekomonData(entity);
                if (!pekomonData.equals("")) {
                    ItemStack skull;
                    switch (pekomonData) {
                        case "Blank":
                        case "FlippedPekomonBlankSkull":
                            skull = PekoSrvFun.FlippedPekomonBlankSkull.clone();
                            break;
                        case "BlankF":
                        case "FlippedPekomonBlankSkullF":
                            skull = PekoSrvFun.FlippedPekomonBlankSkullF.clone();
                            break;
                        case "SmileF":
                        case "FlippedPekomonSmileSkullF":
                            skull = PekoSrvFun.FlippedPekomonSmileSkullF.clone();
                            break;
                        case "Wink":
                        case "FlippedPekomonWinkSkull":
                            skull = PekoSrvFun.FlippedPekomonWinkSkull.clone();
                            break;
                        case "WinkF":
                        case "FlippedPekomonWinkSkullF":
                            skull = PekoSrvFun.FlippedPekomonWinkSkullF.clone();
                            break;
                        case "Happy":
                        case "FlippedPekomonLaughSkull":
                            skull = PekoSrvFun.FlippedPekomonLaughSkull.clone();
                            break;
                        case "HappyF":
                        case "FlippedPekomonLaughSkullF":
                            skull = PekoSrvFun.FlippedPekomonLaughSkullF.clone();
                            break;
                        case "Derp":
                        case "FlippedPekomonDerpSkull":
                            skull = PekoSrvFun.FlippedPekomonDerpSkull.clone();
                            break;
                        case "DerpF":
                        case "FlippedPekomonDerpSkullF":
                            skull = PekoSrvFun.FlippedPekomonDerpSkullF.clone();
                            break;
                        case "Cool":
                        case "FlippedPekomonCoolSkull":
                            skull = PekoSrvFun.FlippedPekomonCoolSkull.clone();
                            break;
                        case "CoolF":
                        case "FlippedPekomonCoolSkullF":
                            skull = PekoSrvFun.FlippedPekomonCoolSkullF.clone();
                            break;
                        default:
                            skull = PekoSrvFun.FlippedPekomonSmileSkull.clone();
                            break;
                    }
                    if (!DisguiseAPI.isDisguised(entity)) {
                        MobDisguise disguise = new MobDisguise(DisguiseType.ZOMBIE);
                        ZombieWatcher watcher = (ZombieWatcher) disguise.getWatcher();
                        watcher.setSneaking(true);
                        watcher.setInvisible(true);
                        watcher.setCustomNameVisible(true);
                        watcher.setArmor(new ItemStack[]{null, null, null, skull});
                        watcher.setUpsideDown(true);
                        DisguiseAPI.disguiseToAll(entity, disguise);
                        entity.setPersistent(true);
                        Slime slime = (Slime) entity;
                        slime.setRemoveWhenFarAway(false);
                    }
                } else {
                    if (entity.getType().equals(EntityType.SLIME)) {
                        Slime slime = (Slime) entity;
                        if (slime.getSize() <= 1) {
                            String name = slime.getCustomName();
                            if (!(name == null)) {
                                if (name.equals("Dinnerbone")) {
                                    slime.damage(999);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void HoloPetFastWatcher(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                String holoPetData = getHoloPetData(entity);
                if (holoPetData.isBlank()) continue;
                if (!DisguiseAPI.isDisguised(entity)){
                    String ownerName = getHoloPetOwner(entity);
                    if (ownerName.isBlank()) continue;
                    PersistentDataContainer container = entity.getPersistentDataContainer();
                    Inventory newInvent = null;
                    PekoSrvFun_HoloPet pet = new PekoSrvFun_HoloPet(entity.getLocation(), ownerName, holoPetData, entity.getCustomName(), container);
                    entity.remove();
                }

                Zombie zombie = (Zombie) entity;
                EntityEquipment equipment = zombie.getEquipment();
                if (equipment == null) return;
                if(equipment.getChestplate().getType() == Material.ELYTRA){
                    if(!entity.isInWater()){
                        if (entity.getFallDistance() > 0.2D){
                            ((Zombie) entity).setGliding(true);
                        }
                    }
                }else{
                    ((Zombie) entity).setGliding(false);
                }
            }
        }
    }

    public static void RefreshPekos() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
                String holoPetData = getHoloPetData(entity);
                if (!holoPetData.isBlank()){
                    if (DisguiseAPI.isDisguised(entity)){
                        if (!(((CraftEntity)entity).getHandle() instanceof PekoSrvFun_HoloPet)){
                            String ownerName = getHoloPetOwner(entity);
                            if (ownerName.isBlank()) continue;
                            PersistentDataContainer container = entity.getPersistentDataContainer();
                            PekoSrvFun_HoloPet pet = new PekoSrvFun_HoloPet(entity.getLocation(), ownerName, holoPetData, entity.getCustomName(), container);
                            entity.remove();
                        }else {
                            if (((LivingEntity) entity).getHealth() <= 0){
                                entity.remove();
                                return;
                            }
                            if (entity.isDead()){
                                entity.remove();
                            }
                            if (entity.isInvulnerable()){
                                entity.setInvulnerable(false);
                            }

                            if (((LivingEntity)entity).getHealth() < ((LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() ){
                                PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ((CraftEntity)entity).getHandle();
                                Inventory inventory = holoPet.inventory;
                                int carrotIndex = -1;
                                for (int i = 0; i < inventory.getSize(); i++) {
                                    ItemStack item = inventory.getItem(i);
                                    if (item == null) continue;
                                    if (item.getType() == Material.CARROT) {
                                        carrotIndex = i;
                                    }
                                }
                                if (carrotIndex > -1){
                                    ItemStack carrots = inventory.getItem(carrotIndex);
                                    if (carrots.getAmount() > 1){
                                        inventory.setItem(carrotIndex, new ItemStack(Material.CARROT,carrots.getAmount() - 1 ));
                                    }else {
                                        inventory.setItem(carrotIndex, new ItemStack(Material.AIR,1 ));
                                    }
                                    entity.getWorld().playSound(entity, Sound.ENTITY_GENERIC_EAT,0.3f,1);
                                    entity.getWorld().playSound(entity, Sound.ENTITY_GENERIC_EAT,0.3f,1);
                                    entity.getWorld().spawnParticle(Particle.ITEM_CRACK, entity.getLocation(), 25, 0.1,1,0.1,0,carrots);
                                    entity.getWorld().spawnParticle(Particle.ITEM_CRACK, entity.getLocation(), 25, 0.5,1,0.5,0,carrots);
                                    double newHealth = ((LivingEntity) entity).getHealth() + 1;
                                    double maxHealth = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                                    ((Zombie) holoPet.getBukkitEntity()).setHealth(Math.min(newHealth, maxHealth));
                                }
                            }
                            PekoSrvFun_HoloPet pet = (PekoSrvFun_HoloPet) ((CraftEntity)entity).getHandle();
                            String ownerName = entity.getCustomName().split(" ")[0].split("'")[0];
                        }
                    }else {
                        String ownerName = getHoloPetOwner(entity);
                        if (ownerName.isBlank()) continue;
                        PersistentDataContainer container = entity.getPersistentDataContainer();
                        PekoSrvFun_HoloPet pet = new PekoSrvFun_HoloPet(entity.getLocation(), ownerName, holoPetData, entity.getCustomName(), container);
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

    public ItemStack SetSkull(String textureID, String name) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        OfflinePlayer offlinePlayer2 = Bukkit.getOfflinePlayer("dummy_pekomon");
        assert meta != null;
        meta.setOwningPlayer(offlinePlayer2);
        skull.setItemMeta(meta);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/" + textureID).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        meta.setDisplayName("Pekomon head");
        meta.setLore(Arrays.asList(name, "It died! How terrible!"));
        skull.setItemMeta(meta);
        return skull;
    }


    String getPekomonData(Entity entity) {
        if (!(entity.getType() == EntityType.SLIME)) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING))) return "";
        String pekomonTypeKey;
        pekomonTypeKey = container.get(PekoSrvFun.pekomonTypeKey, PersistentDataType.STRING);
        if (pekomonTypeKey.isBlank()) return "";
        return pekomonTypeKey;
    }

    static String getHoloPetData(Entity entity) {
        if (!(entity.getType() == EntityType.ZOMBIFIED_PIGLIN || entity.getType() == EntityType.ZOMBIE )) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING))) return "";
        String petTypeKey;
        petTypeKey = container.get(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING);
        if (petTypeKey.isBlank()) return "";
        return petTypeKey;
    }

    static String getHoloPetOwner(Entity entity) {
        if (!(entity.getType() == EntityType.ZOMBIFIED_PIGLIN || entity.getType() == EntityType.ZOMBIE )) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(PekoSrvFun.holoPetTypeKey, PersistentDataType.STRING))) return "";
        if ((container.has(PekoSrvFun.holoPetOwnerKey, PersistentDataType.STRING))){
            String owner = container.get(PekoSrvFun.holoPetOwnerKey, PersistentDataType.STRING);
            if (!owner.isBlank()) return owner;
        }else {
            if (entity.getCustomName().matches(".+?'s [A-Z].+? clone")){
                return entity.getCustomName().split(" ")[0].split("'")[0];
            }
        }
        return "";
    }
}
