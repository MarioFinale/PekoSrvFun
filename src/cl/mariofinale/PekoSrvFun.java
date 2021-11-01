package cl.mariofinale;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieWatcher;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

import static me.libraryaddict.disguise.DisguiseConfig.loadConfig;

/** @noinspection unused*/
public class PekoSrvFun extends JavaPlugin {
    static ArrayList<String> WorldsList = new ArrayList<>();
    static HashMap<UUID, Date> TiredPekomonList = new HashMap<>();
    static HashMap<UUID, Pair<Location, String>> PekomonList = new HashMap<>();
    private static Plugin plugin;

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

    @Override
    public void onEnable() {
        plugin = this;
        LogInfo("Registering listener.");
        getServer().getPluginManager().registerEvents(new PekoSrvFun_Listener(), this);
        LogInfo("Listener registered.");
        LogInfo("Loading Worlds list.");
        LoadWorldsList();
        LogInfo("Registering commands.");
        PekoSrvFun_Commands pekoSrvFun_commands = new PekoSrvFun_Commands();
        this.getCommand("peko").setExecutor(pekoSrvFun_commands);
        LogInfo("Commands registered.");
        LogInfo("Creating skulls...");
        CreateSkulls();
        LogInfo("Delaying load of Pekomons.");
        new BukkitRunnable() {
            @Override
            public void run() {
                LogInfo("Loading Pekomons...");
                LoadPekomons();
            }
        }.runTaskLater(this.plugin, 20);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                RefreshPekomons();
            }
        }, 20, 20);
        LogInfo("PekoSrvFun loaded!");
    }

    @Override
    public void onDisable() {
        LogInfo("Saving config.");
        plugin.saveDefaultConfig();
        SaveWorldsList();
        SavePekomons();
        LogInfo("PekoSrvFun disabled!");
    }

    public void CreateSkulls(){
        PekomonSmileSkull = SetSkull("b7663111cc5f5c97c7c6c9e3c500edcac5179587be82f5b299b7e197d8efe503", "Smiling Pekomon M");

        FlippedPekomonSmileSkull = SetSkull ("991de5578eef55db6fed22af00d5bf0d425b927cbfeeaa72871d2c0c29fc2378","Flipped Smiling Pekomon M");

        PekomonLaughSkull = SetSkull("6644db503b23a879dd80d050cdb19d01e24cb5931b3160c4239c5da3ec63d7ea", "Happy Pekomon M");

        FlippedPekomonLaughSkull = SetSkull("be534411b12b93ccba2c4786de880a171ab39b50993b07ad4c1eb43392752b0f","Flipped Happy Pekomon M");

        PekomonCoolSkull = SetSkull("167befffc1b45a591d47f8e0c0b65a1441ed2b61b268d80ce3a64b4bdc710bbe", "Cool Pekomon M");

        FlippedPekomonCoolSkull = SetSkull("ecefd7812356ffbbf259e618b8bdc6687523079f9c32258ec2b6c98ad8365ab", "Flipped Cool Pekomon M");

        PekomonBlankSkull = SetSkull("4b97c85f0c1dcf3f19a190294dbebb5e469fff11bfa3aad04a2eafa49c94a80a", ChatColor.MAGIC + "--------- M");

        FlippedPekomonBlankSkull = SetSkull("73418b338072c5b965e19d4dd91ee3d35262ec5b2fd530c1b94753b9b46997a5", "???? M");

        PekomonDerpSkull = SetSkull("d0bf0f2c2d1245413a6c2a016dc4034e1b0dfd58e032720d84ee8ff49d632021","Derp Pekomon M");

        FlippedPekomonDerpSkull = SetSkull("c74d6123257bddb85c5082b7ed8f73b362d7c0b9f107c8d6275253cf79c681c9","Flipped Derp Pekomon M");

        PekomonWinkSkull = SetSkull("33a81542669e3805613d00e82020dfd8587efe280278e0bfb8e7c19eb2388206","Winking Pekomon M");

        FlippedPekomonWinkSkull = SetSkull("333e8ad6b369b5cd0a56e34e7c6fec64709893bfa347fcf872ad3a2b820b1638","Flipped Winking Pekomon M");


        PekomonSmileSkullF = SetSkull("c1f5fcffdd2b2fdf3509ebd553dfc10eeb14b9e243efa3e08d3c291afc1c1909", "Smiling Pekomon F");

        FlippedPekomonSmileSkullF = SetSkull("119e9173f75617e11036afbbacae3ffb2b3fa5d80a9e882ebac690c3de2f71b7", "Flipped Smiling Pekomon F");

        PekomonLaughSkullF = SetSkull("b9c9418b7fe8e5102fdef53a0e6a6f3d16f4e9b9b837afda6e1460678e570d16", "Happy Pekomon F");

        FlippedPekomonLaughSkullF = SetSkull("5548989e1e4de91401d1d685b9a6d79fb20ac0f0236682aa116438f4c7537146","Flipped Happy Pekomon F");

        PekomonCoolSkullF = SetSkull("e1c32055055bcff0aad71c0d06f91225ae908f2acb27cc436ad36c65c3ef286e", "Cool Pekomon F");

        FlippedPekomonCoolSkullF = SetSkull("ace5726397d0615ba1d6088e5d05417342a7bba7bd9e2abae63b9e0fb4dfb210", "Flipped Cool Pekomon F");

        PekomonBlankSkullF = SetSkull("b3af6f76be380e24b49345d3b5b24dd38c2be7d2d865686c6799fc8e8f1557af", ChatColor.MAGIC + "--------- F");

        FlippedPekomonBlankSkullF = SetSkull("5edf7063635c5ee8d26b15d45f3efa5a1a23fb16861b42639e880afc65cc32", "???? F");

        PekomonDerpSkullF = SetSkull("fc12b15ebede9ffbe976add294897506bd0a44417a140c88a4e067f6aa6da8b9","Derp Pekomon F");

        FlippedPekomonDerpSkullF = SetSkull("c6ad2d5f75a276a0f8cc4ff1119632568384770e9df14ee3a643348554c0678d","Flipped Derp Pekomon F");

        PekomonWinkSkullF = SetSkull("ce08126f8eb55b31af9523536a41895ef3bb658bfa742cf3ab4819c7cabd32ea","Winking Pekomon F");

        FlippedPekomonWinkSkullF = SetSkull("33a065b541b6587c13fbfb7efb5b039fb6649cb4253e77df36152646517e045b","Flipped Winking Pekomon F");

        LogInfo("Finished creating skulls!");
    }
    private void LogInfo(String line) {
        plugin.getLogger().log(Level.INFO,line);
    }
    private void LogWarn(String line) {
        plugin.getLogger().log(Level.WARNING,line);
    }
    private void LogError(String line) {
        plugin.getLogger().log(Level.SEVERE,line);
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

    private void LoadPekomons() {
        File Pekomons1 = new File(getDataFolder().getAbsolutePath(), "Pekomon-1.yml");
        File Pekomons2 = new File(getDataFolder().getAbsolutePath(), "Pekomon-2.yml");
        File Pekomons3 = new File(getDataFolder().getAbsolutePath(), "Pekomon-3.yml");
        ArrayList<String> uuid;
        ArrayList<String> type;
        ArrayList<String> location;
        try {
            Pekomons1.createNewFile();
            FileConfiguration pekomonfile1 = YamlConfiguration.loadConfiguration(Pekomons1);
            uuid = (ArrayList<String>) pekomonfile1.get("List");
            Pekomons1.createNewFile();
            FileConfiguration pekomonfile2 = YamlConfiguration.loadConfiguration(Pekomons2);
            type = (ArrayList<String>) pekomonfile2.get("List");
            FileConfiguration pekomonfile3 = YamlConfiguration.loadConfiguration(Pekomons3);
            location = (ArrayList<String>) pekomonfile3.get("List");

            for (int i = 0; i < uuid.size(); i++ ){
                String[] locs = location.get(i).split(" ");
                Location tloc = new Location(Bukkit.getWorld(locs[0]), Double.parseDouble(locs[1]),Double.parseDouble(locs[2]),Double.parseDouble(locs[3]),Float.parseFloat(locs[4]),Float.parseFloat(locs[5]));
                PekoSrvFun_Pekomon pekomon = new PekoSrvFun_Pekomon(tloc,type.get(i));
            }

        } catch (Exception ex) {
            LogWarn("Pekomon list files have not been loaded: " + ex.getMessage());
            LogWarn("If this is the first time running the plugin the files will be created on server stop.");
            return;
        }
        LogInfo("Pekomons loaded.");
    }

    private void RefreshPekomons(){
        for(Player player: Bukkit.getOnlinePlayers()){
            for (Entity entity : player.getNearbyEntities(32,32,32)){
                if (PekoSrvFun.PekomonList.keySet().contains(entity.getUniqueId())){
                    ItemStack skull;
                    switch (PekomonList.get(entity.getUniqueId()).getB()){
                        case "FlippedPekomonBlankSkull":
                            skull =  PekoSrvFun.FlippedPekomonBlankSkull.clone();
                            break;
                        case "FlippedPekomonBlankSkullF":
                            skull =  PekoSrvFun.FlippedPekomonBlankSkullF.clone();
                            break;
                        case "FlippedPekomonSmileSkullF":
                            skull =  PekoSrvFun.FlippedPekomonSmileSkullF.clone();
                            break;
                        case "FlippedPekomonWinkSkull":
                            skull =  PekoSrvFun.FlippedPekomonWinkSkull.clone();
                            break;
                        case "FlippedPekomonWinkSkullF":
                            skull =  PekoSrvFun.FlippedPekomonWinkSkullF.clone();
                            break;
                        case "FlippedPekomonLaughSkull":
                            skull =  PekoSrvFun.FlippedPekomonLaughSkull.clone();
                            break;
                        case "FlippedPekomonLaughSkullF":
                            skull =  PekoSrvFun.FlippedPekomonLaughSkullF.clone();
                            break;
                        case "FlippedPekomonDerpSkull":
                            skull =  PekoSrvFun.FlippedPekomonDerpSkull.clone();
                            break;
                        case "FlippedPekomonDerpSkullF":
                            skull =  PekoSrvFun.FlippedPekomonDerpSkullF.clone();
                            break;
                        case "FlippedPekomonCoolSkull":
                            skull =  PekoSrvFun.FlippedPekomonCoolSkull.clone();
                            break;
                        case "FlippedPekomonCoolSkullF":
                            skull =  PekoSrvFun.FlippedPekomonCoolSkullF.clone();
                            break;
                        default:
                            skull =  PekoSrvFun.FlippedPekomonSmileSkull.clone();
                    }
                    if (!DisguiseAPI.isDisguised(entity)){
                        MobDisguise disguise = new MobDisguise(DisguiseType.ZOMBIE);
                        ZombieWatcher watcher = (ZombieWatcher) disguise.getWatcher();
                        watcher.setSneaking(true);
                        watcher.setInvisible(true);
                        //watcher.setBaby(true);
                        watcher.setCustomName("Wild PekoMon");
                        watcher.setCustomNameVisible(true);
                        watcher.setArmor(new ItemStack[]{null, null, null, skull});
                        watcher.setUpsideDown(true);
                        DisguiseAPI.disguiseToAll(entity, disguise);
                        entity.setPersistent(true);
                    }

                }
            }
        }
    }

    private void SavePekomons() {
        LogInfo("Saving Pekomons list.");
        File Pekomons1 = new File(getDataFolder().getAbsolutePath(), "Pekomon-1.yml");
        File Pekomons2 = new File(getDataFolder().getAbsolutePath(), "Pekomon-2.yml");
        File Pekomons3 = new File(getDataFolder().getAbsolutePath(), "Pekomon-3.yml");
        try {
            Pekomons1.delete();
            Pekomons1.createNewFile();
            Pekomons2.delete();
            Pekomons2.createNewFile();
            Pekomons3.delete();
            Pekomons3.createNewFile();
            if (PekomonList == null) {
                PekomonList = new HashMap<>();
            }
        } catch (Exception ex) {
            LogError("Error saving Pekomons list: " + ex.getMessage());
            return;
        }
        ArrayList<String> uuids = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> locations = new ArrayList<>();

        for (Map.Entry<UUID, Pair<Location, String>> entry: PekomonList.entrySet()) {
            uuids.add(entry.getKey().toString());
            Location loc = entry.getValue().getA();
            types.add(entry.getValue().getB());
            String locstr = loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " + loc.getYaw() + " " + loc.getPitch();
            locations.add(locstr);
        }

        FileConfiguration pekomonfile1 = YamlConfiguration.loadConfiguration(Pekomons1);
        pekomonfile1.set("List", uuids);
        FileConfiguration pekomonfile2 = YamlConfiguration.loadConfiguration(Pekomons2);
        pekomonfile2.set("List", types);
        FileConfiguration pekomonfile3 = YamlConfiguration.loadConfiguration(Pekomons3);
        pekomonfile3.set("List", locations);
        try {
            pekomonfile1.save(Pekomons1);
            pekomonfile2.save(Pekomons2);
            pekomonfile3.save(Pekomons3);
        } catch (Exception ex) {
            LogError("Error saving Pekomons list: " + ex.getMessage());
            return;
        }
        LogInfo("Pekomons list saved.");
        for (Map.Entry<UUID, Pair<Location, String>> entry: PekomonList.entrySet()) {
            Bukkit.getEntity(entry.getKey()).remove();
        }
    }


    public ItemStack SetSkull(String textureID, String name){
        ItemStack skull =  new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);;
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        OfflinePlayer offlinePlayer2 = Bukkit.getOfflinePlayer("dummy_pekomon");
        meta.setOwningPlayer(offlinePlayer2);
        skull.setItemMeta(meta);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/" + textureID ).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        meta.setDisplayName("Pekomon head");
        meta.setLore(Arrays.asList(name, "How terrible!"));
        skull.setItemMeta(meta);
        return skull;
    }
}
