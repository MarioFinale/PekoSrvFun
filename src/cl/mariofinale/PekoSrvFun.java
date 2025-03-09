package cl.mariofinale;

import cl.mariofinale.NPC.NPCCommands;
import cl.mariofinale.NPC.NPCEventsListener;
import cl.mariofinale.NPC.NPCWatchdog;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieWatcher;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * Main plugin class for PekoSrvFun, handling various server functionalities including
 * custom entities, skulls, and world management.
 */
public class PekoSrvFun extends JavaPlugin {
    // Static fields
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
    public static BukkitScheduler scheduler;
    public static NamespacedKey holoPetTypeKey;
    public static NamespacedKey holoPetNameKey;
    public static NamespacedKey holoPetStatusKey;
    public static NamespacedKey holoPetOwnerKey;
    public static NamespacedKey holoPetInventoryKey;
    static NamespacedKey pekomonTypeKey;
    static NamespacedKey pekomonLastBreedKey;
    public static Plugin plugin;
    public static CoreProtectAPI CoreProtectApiInstance;
    public static boolean WorldEditPresent;
    public static boolean WorldGuardPresent;
    public static boolean VaultPresent;
    public static boolean CoreProtectPresent;

    // === Plugin Lifecycle Methods ===

    /**
     * Called when the plugin is enabled. Initializes plugin components,
     * registers listeners and commands, and sets up scheduled tasks.
     */
    @Override
    public void onEnable() {
        plugin = this;

        // Check for dependent plugins
        checkDependentPlugins();

        // Register listeners
        LogInfo("Registering listeners...");
        getServer().getPluginManager().registerEvents(new PekoSrvFun_Listener(), this);
        getServer().getPluginManager().registerEvents(new NPCEventsListener(), this);
        LogInfo("Listeners registered.");

        // Load worlds
        LogInfo("Loading Worlds list...");
        LoadWorldsList();
        LogInfo("Worlds list loaded.");

        // Register commands
        LogInfo("Registering commands...");
        PekoSrvFun_Commands pekoSrvFun_commands = new PekoSrvFun_Commands();
        this.getCommand("peko").setExecutor(pekoSrvFun_commands);
        NPCCommands npcCommands = new NPCCommands();
        this.getCommand("npc").setExecutor(npcCommands);
        LogInfo("Commands registered.");

        // Initialize skulls
        LogInfo("Creating skulls...");
        CreateSkulls();
        LogInfo("Skulls created.");

        // Initialize NamespacedKeys
        LogInfo("Creating Namespaced Keys...");
        initializeNamespacedKeys();
        LogInfo("Namespaced Keys created.");

        // Setup scheduler
        LogInfo("Registering Scheduler");
        scheduler = getServer().getScheduler();
        LogInfo("Scheduler Registered");

        // Schedule tasks
        scheduleTasks();

        LogInfo("PekoSrvFun loaded!");
    }

    /**
     * Called when the plugin is disabled. Saves configuration and worlds list.
     */
    @Override
    public void onDisable() {
        LogInfo("Saving config.");
        plugin.saveDefaultConfig();
        SaveWorldsList();
        LogInfo("PekoSrvFun disabled!");
    }

    // === Plugin Initialization Helpers ===

    /**
     * Checks for the presence of dependent plugins and sets corresponding flags.
     */
    private void checkDependentPlugins() {
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            getLogger().info("WorldGuard Detected!");
            WorldGuardPresent = true;
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            getLogger().info("WorldEdit Detected!");
            WorldEditPresent = true;
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            getLogger().info("Vault Detected!");
            VaultPresent = true;
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("CoreProtect") != null) {
            getLogger().info("CoreProtect Detected!");
            CoreProtectPresent = true;
            CoreProtectApiInstance = getCoreProtect();
        }
    }

    /**
     * Initializes all NamespacedKeys used by the plugin.
     */
    private void initializeNamespacedKeys() {
        pekomonTypeKey = new NamespacedKey(this, "PekoMonType");
        pekomonLastBreedKey = new NamespacedKey(this, "PekoMonLastBreed");
        holoPetTypeKey = new NamespacedKey(this, "holoPetTypeKey");
        holoPetNameKey = new NamespacedKey(this, "holoPetNameKey");
        holoPetOwnerKey = new NamespacedKey(this, "holoPetOwnerKey");
        holoPetInventoryKey = new NamespacedKey(this, "holoPetInventoryKey");
        holoPetStatusKey = new NamespacedKey(this, "holoPetStatusKey");
    }

    /**
     * Schedules recurring tasks for the plugin.
     */
    private void scheduleTasks() {
        LogInfo("Scheduling 'RefreshPekomons' task");
        scheduler.runTaskTimer(this, this::RefreshPekomons, 30L, 60L);

        LogInfo("Scheduling 'NPCWatchdog' task");
        scheduler.runTaskTimer(this, new NPCWatchdog(), 0L, 20L);
    }

    // === Skull Management ===

    /**
     * Creates all Pekomon skull items used by the plugin.
     */
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

    /**
     * Creates a custom skull ItemStack with the specified texture and name.
     *
     * @param textureID The texture ID for the skull
     * @param name The display name for the skull
     * @return The configured ItemStack representing the skull
     */
    public ItemStack SetSkull(String textureID, String name) {
        PlayerProfile profile = getProfile("https://textures.minecraft.net/texture/" + textureID);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null){
            LogError("Error getting skull meta. It was null.");
            return skull;
        }
        meta.setItemName(name);
        meta.setOwnerProfile(profile);
        skull.setItemMeta(meta);
        return skull;
    }

    private static PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID()); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            LogError("Error getting PlayerProfile. Invalid URL.");
            LogError("A random profile will be returned.");
            return profile;
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

    // === World Management ===

    /**
     * Saves the current worlds list to a YAML file.
     */
    private void SaveWorldsList() {
        LogInfo("Saving Worlds list.");
        File worldsListFile = new File(getDataFolder(), "WorldsList.yml");
        try {
            if (worldsListFile.exists()) {
                worldsListFile.delete();
            }
            worldsListFile.createNewFile();
            if (WorldsList == null) {
                WorldsList = new ArrayList<>();
            }
        } catch (Exception ex) {
            LogError("Error saving Worlds list: " + ex.getMessage());
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(worldsListFile);
        config.set("List", WorldsList);

        try {
            config.save(worldsListFile);
        } catch (Exception ex) {
            LogError("Error saving Worlds list: " + ex.getMessage());
            return;
        }
        LogInfo("Worlds list saved.");
    }

    /**
     * Loads the worlds list from a YAML file.
     */
    private void LoadWorldsList() {
        File worldsListFile = new File(getDataFolder(), "WorldsList.yml");
        try {
            worldsListFile.createNewFile();
            FileConfiguration listConfigFile = YamlConfiguration.loadConfiguration(worldsListFile);
            Object list = listConfigFile.get("List");
            WorldsList = (list instanceof List) ? new ArrayList<>((List<String>) list) : new ArrayList<>();
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

    // === Entity Management ===

    /**
     * Refreshes Pekomon entities near online players, updating their disguises.
     */
    private void RefreshPekomons() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
                String pekomonData = getPekomonData(entity);
                if (!pekomonData.isEmpty()) {
                    ItemStack skull = selectPekomonSkull(pekomonData);
                    if (!DisguiseAPI.isDisguised(entity)) {
                        applyPekomonDisguise(entity, skull);
                        configurePekomonEntity(entity);
                    }
                } else if (entity.getType() == EntityType.SLIME) {
                    handleUnmarkedSlime((Slime) entity);
                }
            }
        }
    }

    /**
     * Selects the appropriate skull ItemStack based on Pekomon data.
     *
     * @param pekomonData The Pekomon type identifier
     * @return The corresponding skull ItemStack
     */
    private ItemStack selectPekomonSkull(String pekomonData) {
        switch (pekomonData) {
            case "Blank":
            case "FlippedPekomonBlankSkull":
                return FlippedPekomonBlankSkull.clone();
            case "BlankF":
            case "FlippedPekomonBlankSkullF":
                return FlippedPekomonBlankSkullF.clone();
            case "SmileF":
            case "FlippedPekomonSmileSkullF":
                return FlippedPekomonSmileSkullF.clone();
            case "Wink":
            case "FlippedPekomonWinkSkull":
                return FlippedPekomonWinkSkull.clone();
            case "WinkF":
            case "FlippedPekomonWinkSkullF":
                return FlippedPekomonWinkSkullF.clone();
            case "Happy":
            case "FlippedPekomonLaughSkull":
                return FlippedPekomonLaughSkull.clone();
            case "HappyF":
            case "FlippedPekomonLaughSkullF":
                return FlippedPekomonLaughSkullF.clone();
            case "Derp":
            case "FlippedPekomonDerpSkull":
                return FlippedPekomonDerpSkull.clone();
            case "DerpF":
            case "FlippedPekomonDerpSkullF":
                return FlippedPekomonDerpSkullF.clone();
            case "Cool":
            case "FlippedPekomonCoolSkull":
                return FlippedPekomonCoolSkull.clone();
            case "CoolF":
            case "FlippedPekomonCoolSkullF":
                return FlippedPekomonCoolSkullF.clone();
            default:
                return FlippedPekomonSmileSkull.clone();
        }
    }

    /**
     * Applies a Pekomon disguise to an entity.
     *
     * @param entity The entity to disguise
     * @param skull The skull ItemStack to use as head
     */
    private void applyPekomonDisguise(Entity entity, ItemStack skull) {
        MobDisguise disguise = new MobDisguise(DisguiseType.ZOMBIE);
        ZombieWatcher watcher = (ZombieWatcher) disguise.getWatcher();
        watcher.setSneaking(true);
        watcher.setInvisible(true);
        watcher.setCustomNameVisible(true);
        watcher.setArmor(new ItemStack[]{null, null, null, skull});
        watcher.setUpsideDown(true);
        DisguiseAPI.disguiseToAll(entity, disguise);
    }

    /**
     * Configures a Pekomon entity's properties.
     *
     * @param entity The entity to configure
     */
    private void configurePekomonEntity(Entity entity) {
        entity.setPersistent(true);
        Slime slime = (Slime) entity;
        slime.setRemoveWhenFarAway(false);
    }

    /**
     * Handles unmarked slime entities, removing Dinnerbone-named ones.
     *
     * @param slime The slime entity to handle
     */
    private void handleUnmarkedSlime(Slime slime) {
        if (slime.getSize() <= 1) {
            String name = slime.getCustomName();
            if ("Dinnerbone".equals(name)) {
                slime.damage(999);
            }
        }
    }

    /**
     * Gets Pekomon data from an entity's persistent data container.
     *
     * @param entity The entity to check
     * @return The Pekomon type string, or empty string if not a Pekomon
     */
    String getPekomonData(Entity entity) {
        if (entity.getType() != EntityType.SLIME) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(pekomonTypeKey, PersistentDataType.STRING)) return "";
        String pekomonType = container.get(pekomonTypeKey, PersistentDataType.STRING);
        return (pekomonType != null && !pekomonType.isBlank()) ? pekomonType : "";
    }

    // === External API Integration ===

    /**
     * Gets the CoreProtect API instance if available and compatible.
     *
     * @return CoreProtectAPI instance or null if unavailable/incompatible
     */
    private @Nullable CoreProtectAPI getCoreProtect() {
        if (!CoreProtectPresent) return null;
        Plugin coreProtectPlugin = getServer().getPluginManager().getPlugin("CoreProtect");
        if (coreProtectPlugin == null) return null;

        CoreProtectAPI coreProtect = ((CoreProtect) coreProtectPlugin).getAPI();
        if (!coreProtect.isEnabled()) {
            return null;
        }
        if (coreProtect.APIVersion() < 9) {
            LogError("The installed version of CoreProtect is too old!");
            return null;
        }
        return coreProtect;
    }

    // === Logging Utilities ===

    /**
     * Logs an info message using the plugin's logger.
     *
     * @param line The message to log
     */
    static void LogInfo(String line) {
        plugin.getLogger().log(Level.INFO, line);
    }

    /**
     * Logs a warning message using the plugin's logger.
     *
     * @param line The message to log
     */
    static void LogWarn(String line) {
        plugin.getLogger().log(Level.WARNING, line);
    }

    /**
     * Logs an error message using the plugin's logger.
     *
     * @param line The message to log
     */
    static void LogError(String line) {
        plugin.getLogger().log(Level.SEVERE, line);
    }
}