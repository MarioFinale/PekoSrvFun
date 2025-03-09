package cl.mariofinale.NPC;

import cl.mariofinale.PekoSrvFun;
import cl.mariofinale.Peko_Utils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class NPCUtils {


    public static Set<NPCHelper> GetPlayerNPCs(@NotNull Player player){
        Set<NPCHelper> NPCs = new HashSet<>();
        for (World world : Bukkit.getWorlds()){
            for (Entity entity : world.getLivingEntities()){
                if (((CraftEntity) entity).getHandle() instanceof NPCHelper helper){
                    NPCs.add(helper);
                }
            }
        }
        Set<NPCHelper> OwnedNPCs = new HashSet<>();
        for (NPCHelper helper : NPCs){
            String ownerName = helper.getOwner();
            if (player.getName().equals(ownerName)){
                OwnedNPCs.add(helper);
            }
        }
        return OwnedNPCs;
    }

   public static boolean SpawnNPCHelper(Player player, String NPCUserName){
        Inventory inventory = player.getInventory();
        int SkullIndex = -1;
        int EmeraldIndex = -1;
        int NetheriteIndex = -1;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            if (item.getType() == Material.PLAYER_HEAD) {
                SkullIndex = i;
            }
            if (item.getType() == Material.DEEPSLATE_EMERALD_ORE) {
                EmeraldIndex = i;
            }
            if (item.getType() == Material.NETHERITE_BLOCK) {
                NetheriteIndex = i;
            }
        }
        if (SkullIndex <= -1) return false;
        if (EmeraldIndex <= -1 && NetheriteIndex <= -1) return false;
        if (NetheriteIndex > -1){
            ItemStack netherite = inventory.getItem(NetheriteIndex);
            if (netherite != null && netherite.getAmount() > 10){
                netherite.setAmount(netherite.getAmount()-10);
            }else if(netherite != null && netherite.getAmount() == 10){
                inventory.setItem(NetheriteIndex, new ItemStack(Material.AIR, 1));
            }else{
                if (EmeraldIndex <= -1) return false;
            }
        }
        ItemStack skull = inventory.getItem(SkullIndex);
        if (skull != null && skull.getAmount() > 1){
            skull.setAmount(skull.getAmount() - 1);
        }else if(skull != null && skull.getAmount() == 1){
            inventory.setItem(SkullIndex, new ItemStack(Material.AIR, 1));
        }else{
            return false;
        }
        if (EmeraldIndex > -1){
            ItemStack emeralds = inventory.getItem(EmeraldIndex);
            if (emeralds != null && emeralds.getAmount() > 1){
                emeralds.setAmount(emeralds.getAmount()-1);
            }else{
                inventory.setItem(EmeraldIndex, new ItemStack(Material.AIR, 1));
            }
        }

        NPCHelper pet = new NPCHelper(player.getLocation().clone(), player.getName(), NPCUserName, NPCUserName, null);
        Peko_Utils.sendMessageToPlayer(player,"Your \"" + pet.getNPCUserName() + "\" NPC has been summoned!" );
        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1,1);
        return true;
    }

    static ItemStack pickBestFromMaterialArray(Inventory inventory, Material[] materials){
        ItemStack bestItem = null;
        ItemStack[] inventoryStorageContents = inventory.getStorageContents();
        for (Material material : materials){
            if (inventory.contains(material)) {
                for (ItemStack stack : inventoryStorageContents) {
                    if (stack == null) continue;
                    if (stack.getType() == material) {
                        bestItem = stack;
                        break;
                    }
                }
            }
        }
       return bestItem;
    }


    static String getNPCHelperType(Entity entity) {
        if (!(entity.getType() == EntityType.ZOMBIFIED_PIGLIN || entity.getType() == EntityType.ZOMBIE )) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(NPCHelper.NPCHelperTypeKey(), PersistentDataType.STRING))) return "";
        String petTypeKey;
        petTypeKey = container.get(NPCHelper.NPCHelperTypeKey(), PersistentDataType.STRING);
        if (petTypeKey != null && petTypeKey.isBlank()) return "";
        return petTypeKey;
    }

    static String getNPCHelperName(Entity entity) {
        if (!(entity.getType() == EntityType.ZOMBIFIED_PIGLIN || entity.getType() == EntityType.ZOMBIE )) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();
        String npcName;
        if (container.has(NPCHelper.NPCHelperNameKey(), PersistentDataType.STRING)){
            npcName = container.get(NPCHelper.NPCHelperNameKey(), PersistentDataType.STRING);
            container.set(NPCHelper.NPCHelperNameKey(), PersistentDataType.STRING, npcName);
        }else {
            npcName = entity.getCustomName();
        }
        if (npcName != null && npcName.isBlank()) return "";
        return npcName;
    }

    static String getNPCHelperOwner(Entity entity) {
        if (!(entity.getType() == EntityType.ZOMBIFIED_PIGLIN || entity.getType() == EntityType.ZOMBIE )) return "";
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!(container.has(NPCHelper.NPCHelperTypeKey(), PersistentDataType.STRING))) return "";
        if ((container.has(NPCHelper.NPCHelperOwnerKey(), PersistentDataType.STRING))){
            String owner = container.get(NPCHelper.NPCHelperOwnerKey(), PersistentDataType.STRING);
            if (owner != null && !owner.isBlank()) return owner;
        }else {
            if (entity.getCustomName() != null && entity.getCustomName().matches(".+?'s [A-Z].+? clone")){
                return entity.getCustomName().split(" ")[0].split("'")[0];
            }
        }
        return "";
    }

    public Set<Chunk> getChunksAroundLocation(Location location) {
        int[] offset = {-1,0,1};
        World world = location.getWorld();
        int baseX = location.getChunk().getX();
        int baseZ = location.getChunk().getZ();
        Set<Chunk> chunksAroundLocation = new HashSet<>();
        if (world == null) return chunksAroundLocation;
        for(int x : offset) {
            for(int z : offset) {
                Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
                chunksAroundLocation.add(chunk);
            }
        } return chunksAroundLocation;
    }


    public static boolean getCustomBedOcuppied(Block b) {
        List<MetadataValue> metadataValueList = b.getMetadata("Occupied");
        for (MetadataValue metadataValue : metadataValueList) {
            return metadataValue.asBoolean();
        }
        return false;
    }

    public static void setCustomBedOcuppied(Block bed, boolean occupied) {
        bed.setMetadata("Occupied", new FixedMetadataValue(NPCHelper.plugin(), occupied));
    }

    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }


    public static double getEntityDistanceToEntity(@NotNull Entity entity,@NotNull Entity entity2){
        return entity.getLocation().distance(entity2.getLocation());
    }
    public static double getEntityDistanceToEntity(@NotNull Entity entity,@NotNull LivingEntity entity2){
        return entity.getLocation().distance(entity2.getLocation());
    }
    public static double getEntityDistanceToEntity(@NotNull LivingEntity entity,@NotNull LivingEntity entity2){
        return entity.getLocation().distance(entity2.getLocation());
    }
    public static double getEntityDistanceToLocation(@NotNull Entity entity,@NotNull Location location){
        return entity.getLocation().distance(location);
    }
    public static double getEntityDistanceToLocation(@NotNull Entity entity,double X, double Y, double Z){
        return entity.getLocation().distance(new Location(entity.getWorld(), X, Y, Z));
    }


    public static double getDistance(Location loc1, Location loc2){
        float X1 = loc1.getBlockX();
        float Y1 = loc1.getBlockY();
        float Z1 = loc1.getBlockZ();
        float X2 = loc2.getBlockX();
        float Y2 = loc2.getBlockY();
        float Z2 = loc2.getBlockZ();
        return Math.sqrt(Math.pow(X2 - X1, 2)  + Math.pow(Y2 - Y1, 2) + Math.pow(Z2 - Z1, 2));
    }

    public static double getDistance(Location loc1, Block block){
        float X1 = loc1.getBlockX();
        float Y1 = loc1.getBlockY();
        float Z1 = loc1.getBlockZ();
        Location loc2 = block.getLocation();
        float X2 = loc2.getBlockX();
        float Y2 = loc2.getBlockY();
        float Z2 = loc2.getBlockZ();
        return Math.sqrt(Math.pow(X2 - X1, 2)  + Math.pow(Y2 - Y1, 2) + Math.pow(Z2 - Z1, 2));
    }


    public static double getDistance(Entity entity1, Player entity2){
        Location loc1 = entity1.getLocation();
        float X1 = loc1.getBlockX();
        float Y1 = loc1.getBlockY();
        float Z1 = loc1.getBlockZ();
        Location loc2 = entity2.getLocation();
        float X2 = loc2.getBlockX();
        float Y2 = loc2.getBlockY();
        float Z2 = loc2.getBlockZ();
        return Math.sqrt(Math.pow(X2 - X1, 2)  + Math.pow(Y2 - Y1, 2) + Math.pow(Z2 - Z1, 2));
    }

    public static double getDistance(Entity entity, Location loc){

        float X1 = loc.getBlockX();
        float Y1 = loc.getBlockY();
        float Z1 = loc.getBlockZ();

        Location loc2 = entity.getLocation();
        float X2 = loc2.getBlockX();
        float Y2 = loc2.getBlockY();
        float Z2 = loc2.getBlockZ();
        return Math.sqrt(Math.pow(X2 - X1, 2)  + Math.pow(Y2 - Y1, 2) + Math.pow(Z2 - Z1, 2));
    }

    public static double getDistance(Block block1, Block block2){
        Location loc1 = block1.getLocation();
        float X1 = loc1.getBlockX();
        float Y1 = loc1.getBlockY();
        float Z1 = loc1.getBlockZ();
        Location loc2 = block2.getLocation();
        float X2 = loc2.getBlockX();
        float Y2 = loc2.getBlockY();
        float Z2 = loc2.getBlockZ();
        return Math.sqrt(Math.pow(X2 - X1, 2)  + Math.pow(Y2 - Y1, 2) + Math.pow(Z2 - Z1, 2));
    }

    public static double getDistance(Entity entity, Block block){
        Location loc1 = entity.getLocation();
        float X1 = loc1.getBlockX();
        float Y1 = loc1.getBlockY();
        float Z1 = loc1.getBlockZ();
        Location loc2 = block.getLocation();
        float X2 = loc2.getBlockX();
        float Y2 = loc2.getBlockY();
        float Z2 = loc2.getBlockZ();
        return Math.sqrt(Math.pow(X2 - X1, 2)  + Math.pow(Y2 - Y1, 2) + Math.pow(Z2 - Z1, 2));
    }


    public static boolean isCrop(Block block){return isCrop(block.getType());}

    private static boolean isCrop(Material material){
        if (material == Material.AIR) return false;
        if (material == Material.GLASS) return false;
        switch (material){
            case WHEAT:
            case POTATOES:
            case CARROTS:
            case BEETROOTS:
            case MELON:
            case PUMPKIN:
            case COCOA:
            case NETHER_WART:
            case CAVE_VINES_PLANT:
            case SWEET_BERRY_BUSH:
            case SUGAR_CANE:
            case CACTUS:
            case BAMBOO:
            case BAMBOO_SAPLING:
            case BEEHIVE:
            case BEE_NEST:
                return true;
            default:
                return false;
        }
    }

    @Nullable
    public static Material getReplantSeedType(@NotNull Block crop){return getReplantSeedType(crop.getType());}

    @Nullable
    @Contract(pure = true)
    public static Material getReplantSeedType(Material cropType) {
        switch (cropType) {
            case WHEAT:
                return Material.WHEAT_SEEDS;
            case CARROTS:
                return Material.CARROT;
            case POTATOES:
                return Material.POTATO;
            case NETHER_WART:
                return Material.NETHER_WART;
            case BEETROOTS:
                return Material.BEETROOT_SEEDS;
            default:
                return null; // Handle other crop types or return appropriate seed type
        }
    }

    public static Material getCropTypeFromReplantSeedType(Material seedType) {
        switch (seedType) {
            case WHEAT_SEEDS:
                return Material.WHEAT;
            case CARROT:
                return Material.CARROTS;
            case POTATO:
                return Material.POTATOES;
            case NETHER_WART:
                return Material.NETHER_WART;
            case BEETROOT_SEEDS:
                return Material.BEETROOTS;
            default:
                return null; // Handle other seed types or return null for unknown types
        }
    }

    public static boolean isCropSuitableToHarvest(Block block){
        BlockData blockData = block.getBlockData();
        World world = block.getWorld();

        if (blockData instanceof CaveVinesPlant){
            CaveVinesPlant plant = (CaveVinesPlant) blockData;
            if (plant.isBerries()) return true;
        }

        switch (block.getType()){
            case BEE_NEST:
            case BEEHIVE:
                Beehive beehive = (Beehive) block.getState().getBlockData();
                boolean isFull = beehive.getHoneyLevel() == beehive.getMaximumHoneyLevel();
                return isFull;
        }

        switch (block.getType()){
            case MELON:
            case PUMPKIN:
                return true;
        }

        switch (block.getType()){
            case SUGAR_CANE:
            case BAMBOO:
            case CACTUS:
                Block blockUnder = world.getBlockAt(block.getLocation().subtract(0, 1, 0));
                boolean isOverBlock = blockUnder.getType() == block.getType();
                return isOverBlock;
        }

        if (blockData instanceof org.bukkit.block.data.Ageable){
            org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable)blockData;
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return false;
    }

    public static NPCHelper.ItemValidator.ValidationResult isCropDrop(ItemStack stack){
        return new NPCHelper.ItemValidator.ValidationResult(isCropDrop(stack.getType()), stack);
    }

    private static boolean isCropDrop(Material material){
        switch (material){
            case POTATO:
            case POISONOUS_POTATO:
            case CARROT:
            case WHEAT:
            case WHEAT_SEEDS:
            case BEETROOT:
            case BEETROOT_SEEDS:
            case COCOA_BEANS:
            case PUMPKIN:
            case MELON:
            case MELON_SLICE:
            case BAMBOO:
            case SUGAR_CANE:
            case NETHER_WART:
            case GLOW_BERRIES:
            case SWEET_BERRIES:
            case HONEY_BLOCK:
                return true;
            default:
                return false;
        }
    }

    public static Boolean isMobSpawnDenied(Location location) {
        if (!PekoSrvFun.WorldGuardPresent) return false;
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());
        BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(vector);
        for (ProtectedRegion cur : regions){
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            if (registry == null) continue;
            Flag<?> flag = registry.get("mob-spawning");
            if (flag == null) continue;
            StateFlag.State state = cur.getFlag(Flags.MOB_SPAWNING);
            if (state == null) continue;
            if (state == StateFlag.State.DENY) return true;
        }
        return false;
    }

    public static Boolean isPS(Block block) {
        if (!PekoSrvFun.WorldGuardPresent) return false;
        Location location = block.getLocation();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());
        BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(vector);
        for (ProtectedRegion cur : regions){
            String x = String.valueOf(location.getBlockX());
            String y = String.valueOf(location.getBlockY());
            String z = String.valueOf(location.getBlockZ());
            String psName = x + "x" + y + "y" + z + "z";
            if (cur.getId().toLowerCase().contains(psName)){
                return true;
            }
        }
        return false;
    }


    public static boolean isPositiveInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
            if (d < 0 ) return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }



    public static boolean blockCanBeSeenFromLocation(Location location, Block block){
        Vector direction = block.getLocation().toVector().subtract(location.toVector()).normalize();
        RayTraceResult result = location.getWorld().rayTraceBlocks(location, direction, 50);
        if (result != null) {
            Block hitBlock = result.getHitBlock();
            return hitBlock != null && hitBlock.equals(block);
        } else {
            return false;
        }
    }

    public static Location getCenter(Location loc) {

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        double newX = 0;
        double newY = 0;
        double newZ = 0;

        if (x < 0){
            newX = x - 0.5;
        }else {
            newX = x + 0.5;
        }

        if (y < 0){
            newY = y - 0.5;
        }else {
            newY = y + 0.5;
        }

        if (z < 0){
            newZ = z - 0.5;
        }else {
            newZ = z + 0.5;
        }
        return new Location(loc.getWorld(),newX, newY, newZ);
    }

    public static Set<Block> getBlocksSurroundingLocation(Location location, int radius) {
        Set<Block> blocks = new HashSet<>();
        int r = (int) Math.ceil(radius);
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    int bx = location.getBlockX() + x;
                    int by = location.getBlockY() + y;
                    int bz = location.getBlockZ() + z;
                    Block block = location.getWorld().getBlockAt(bx, by, bz);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static Set<Block> getBlocksSurroundingLocationLowHeight(Location location, int radius) {
        Set<Block> blocks = new HashSet<>();
        World world = location.getWorld();
        if (world == null) return blocks;

        int r = (int) (double) radius;
        // Limit the Y dimension to 5 blocks max above and below the location
        int minY = Math.max(location.getWorld().getMinHeight(), location.getBlockY() - 5);  // Ensure we're not going below world height
        int maxY = Math.min(location.getBlockY() + 5, location.getWorld().getMaxHeight() - 1);

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                // Check if the y coordinate is within our limited range
                int by = location.getBlockY() + y;
                if(by < minY || by > maxY) continue;  // Skip this iteration if y is out of range

                for (int z = -r; z <= r; z++) {
                    int bx = location.getBlockX() + x;
                    int bz = location.getBlockZ() + z;
                    Block block = location.getWorld().getBlockAt(bx, by, bz);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static boolean isRayVisible(Location start, Location end, double range) {
        if (start == null) return  false;
        if (end == null) return false;

        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double dz = end.getZ() - start.getZ();
        double steps = 10; // Assuming 10 steps are enough for a simple cube visibility check

        Location rayLoc = start.clone();
        World world = end.getWorld();
        if (world == null) return false;

        for (double step = 1; step <= steps; step++) {
            double factor = step / steps;
            rayLoc.setX(start.getX() + dx * factor);
            rayLoc.setY(start.getY() + dy * factor);
            rayLoc.setZ(start.getZ() + dz * factor);

            Block rayBlock = world.getBlockAt(rayLoc);

            if (start.distance(rayLoc) > range) return false; // Out of range
            if (end.equals(rayLoc)) return true; // Target point is visible

            // Check for solid blocks or exceptions
            if (!rayBlock.getType().isAir() &&
                    (rayBlock.getType().isSolid() ||
                            !NPCUtils.isCrop(rayBlock) &&
                                    !(rayBlock.getState() instanceof Sign))) {
                return false; // Blocked
            }
        }

        return false; // Default case for not reaching the end point
    }



    public static boolean isOre(Block block){
        return isOre(block.getType());
    }

    public static boolean isOReOrStones(Block block){
        return isOre(block.getType()) || block.getType() == Material.STONE || block.getType() == Material.COBBLESTONE;
    }

    private static boolean isOre(Material material){
        switch (material){
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DEEPSLATE_IRON_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case NETHER_GOLD_ORE:
            case NETHER_QUARTZ_ORE:
            case REDSTONE_ORE:
                return true;
            default:
                return false;
        }
    }


    public static boolean isOreDrop(ItemStack stack){
        return isOreDrop(stack.getType());
    }

    public static boolean isOreDrop(Block block){
        return isOreDrop(block.getType());
    }

    private static boolean isOreDrop(Material material){
        switch (material){
            case COAL:
            case COPPER_INGOT:
            case RAW_COPPER:
            case GOLD_INGOT:
            case GOLD_NUGGET:
            case RAW_GOLD:
            case IRON_INGOT:
            case RAW_IRON:
            case IRON_NUGGET:
            case DIAMOND:
            case EMERALD:
            case LAPIS_LAZULI:
            case QUARTZ:
            case REDSTONE:
                return true;
            default:
                return false;
        }
    }


}
