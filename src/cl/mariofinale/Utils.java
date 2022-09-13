package cl.mariofinale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

class Utils {


    public static boolean getCustomBedOcuppied(Block b) {
        List<MetadataValue> metadataValueList = b.getMetadata("Occupied");
        for (MetadataValue metadataValue : metadataValueList) {
            return metadataValue.asBoolean();
        }
        return false;
    }

    public static void setCustomBedOcuppied(Block bed, boolean occupied) {
        bed.setMetadata("Occupied", new FixedMetadataValue(PekoSrvFun.plugin, occupied));
    }

    public static String GetMinecraftUsernameByType(String holoType) {
        String name;
        holoType = holoType.toLowerCase();
        switch (holoType) {
            case "botan":
                name = "botaaan";
                break;
            case "gura":
                name = "GawrGura";
                break;
            case "towa":
            case "towasama":
                name = "Towasama";
                break;
            case "aqua":
            case "baqua":
                name = "minatoaqua";
                break;
            case "nene":
            case "nenechi":
            case "supernene":
            case "supernenechi":
                name = "supernenechii";
                break;
            case "watame":
            case "watamelon":
                name = "Tsunomakiwatame";
                break;
            case "mio":
                name = "ookamimio";
                break;
            case "matsuri":
                name = "natsuiromatsuri";
                break;
            case "korone":
            case "korosan":
                name = "inugamikorone";
                break;
            case "okayu":
                name = "nekomata_okayu";
                break;
            case "kiara":
                name = "Kiara_HOLOEN";
                break;
            case "ina":
                name = "ninoina";
                break;
            case "noel":
                name = "shiroganenoel";
                break;
            case "flare":
                name = "shiranuiflare";
                break;
            case "ame":
                name = "amwatson";
                break;
            case "haato":
            case "haachama":
                name = "AkaiHaato";
                break;
            case "sora":
                name = "tokinosorach";
                break;
            case "iofi":
                name = "IOFI15";
                break;
            case "moona":
                name = "itsmoona";
                break;
            case "risu":
                name = "Ayunda_Risu";
                break;
            case "subaru":
            case "duck":
                name = "oozorasubaru";
                break;
            case "rushia":
            case "f":
                name = "uruharushia";
                break;
            case "fubuki":
                name = "shirakamifubuki";
                break;
            case "miko":
                name = "sakuramiko35";
                break;
            case "luna":
            case "baby":
                name = "HimemoriLuna";
                break;
            case "aki":
                name = "akirosenthal";
                break;
            case "mel":
                name = "yozoramel";
                break;
            case "achan":
                name = "achan_UGA";
                break;
            case "polka":
            case "omapol":
            case "pol":
            case "omapolka":
                name = "omapol";
                break;
            case "ayame":
                name = "nakiriayame";
                break;
            case "lamy":
                name = "Lamy_Yukihara";
                break;
            case "suisei":
                name = "SUISEI_HOSIMATI";
                break;
            case "shion":
                name = "murasakishion";
                break;
            case "marine":
            case "horni":
                name = "houshou_marine";
                break;
            case "kanata":
                name = "Amane_Kanata";
                break;
            case "coco":
                name = "kiryucoco";
                break;
            case "roboco":
                name = "robocosan";
                break;
            case "baelz":
            case "bae":
            case "rat":
                name = "whatabae";
                break;
            case "kaela":
            case "kael":
                name = "kaelanalysis";
                break;
            case "laplus":
                name = "laplus_sama";
                break;
            case "fauna":
                name = "faunaceres";
                break;
            case "mumei":
                name = "nana_mumei";
                break;
            case "pekora":
            case "peko":
            case "pek":
            default:
                name = "usadapekora";
                break;
        }
        return name;
    }

    public static String GetHoloPetNameByMinecraftUsername(String holoName) {
        String holoType;
        switch (holoName) {
            case "botaaan":
                holoType = "Botan";
                break;
            case "GawrGura":
                holoType = "Gura";
                break;
            case "Towasama":
                holoType = "Towa";
                break;
            case "minatoaqua":
                holoType = "Aqua";
                break;
            case "supernenechii":
                holoType = "Nene";
                break;
            case "Tsunomakiwatame":
                holoType = "Watame";
                break;
            case "ookamimio":
                holoType = "Mio";
                break;
            case "natsuiromatsuri":
                holoType = "Matsuri";
                break;
            case "inugamikorone":
                holoType = "Korone";
                break;
            case "nekomata_okayu":
                holoType = "Okayu";
                break;
            case "Kiara_HOLOEN":
                holoType = "Kiara";
                break;
            case "ninoina":
                holoType = "Ina";
                break;
            case "shiroganenoel":
                holoType = "Noel";
                break;
            case "shiranuiflare":
                holoType = "Flare";
                break;
            case "amwatson":
                holoType = "Amelia";
                break;
            case "AkaiHaato":
                holoType = "Haato";
                break;
            case "tokinosorach":
                holoType = "Sora";
                break;
            case "IOFI15":
                holoType = "Iofi";
                break;
            case "itsmoona":
                holoType = "Moona";
                break;
            case "Ayunda_Risu":
                holoType = "Risu";
                break;
            case "oozorasubaru":
                holoType = "Subaru";
                break;
            case "uruharushia":
                holoType = "Rushia";
                break;
            case "shirakamifubuki":
                holoType = "Fubuki";
                break;
            case "sakuramiko35":
                holoType = "Miko";
                break;
            case "HimemoriLuna":
                holoType = "Luna";
                break;
            case "akirosenthal":
                holoType = "Aki";
                break;
            case "yozoramel":
                holoType = "Mel";
                break;
            case "achan_UGA":
                holoType = "A-Chan";
                break;
            case "omapol":
                holoType = "Polka";
                break;
            case "nakiriayame":
                holoType = "Ayame";
                break;
            case "Lamy_Yukihara":
                holoType = "Lamy";
                break;
            case "SUISEI_HOSIMATI":
                holoType = "Suisei";
                break;
            case "murasakishion":
                holoType = "Shion";
                break;
            case "houshou_marine":
                holoType = "Marine";
                break;
            case "Amane_Kanata":
                holoType = "Kanata";
                break;
            case "kiryucoco":
                holoType = "Coco";
                break;
            case "robocosan":
                holoType = "Roboco";
                break;
            case "whatabae":
                holoType = "Baelz";
                break;
            case "kaelanalysis":
                holoType = "Kaela";
                break;
            case "laplus_sama":
                holoType = "Laplus";
                break;
            case "faunaceres":
                holoType = "Fauna";
                break;
            case "nana_mumei":
                holoType = "Mumei";
                break;
            case "usadapekora":
            default:
                holoType = "Pekora";
                break;
        }
        return  holoType;
    }

    public static boolean isOre(Block block){
        return isOre(block.getType());
    }

    public static boolean isOre(Material material){
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

    public static void setPetInventory(InventoryHolder ent){
        PekoSrvFun_HoloPet holoPet = (PekoSrvFun_HoloPet) ent;
        Inventory inventory = holoPet.inventory;
        Zombie zombie = (Zombie) holoPet.getBukkitEntity();
        EntityEquipment equipment = zombie.getEquipment();
        ItemStack helmet = equipment.getHelmet();
        ItemStack chestPlate = equipment.getChestplate();
        ItemStack leggings = equipment.getLeggings();
        ItemStack boots = equipment.getBoots();
        ItemStack mainHand = equipment.getItemInMainHand();
        ItemStack offHand = equipment.getItemInOffHand();

        if (!(helmet == null)){
            if (!inventory.contains(helmet.getType())){
                equipment.setHelmet(new ItemStack(Material.AIR,1));
            }
        }
        if (!(chestPlate == null)){
            if (!inventory.contains(chestPlate.getType())){
                equipment.setChestplate(new ItemStack(Material.AIR,1));
            }
        }
        if (!(leggings == null)) {
            if (!inventory.contains(leggings.getType())) {
                equipment.setLeggings(new ItemStack(Material.AIR, 1));
            }
        }
        if (!(boots == null)) {
            if (!inventory.contains(boots.getType())) {
                equipment.setBoots(new ItemStack(Material.AIR, 1));
            }
        }
        if (!inventory.contains(mainHand.getType())) {
            equipment.setItemInMainHand(new ItemStack(Material.AIR, 1));
        }
        if (!inventory.contains(offHand.getType())){
            equipment.setItemInOffHand(new ItemStack(Material.AIR,1));
        }
        if (inventory.contains(Material.TOTEM_OF_UNDYING)){
            if(equipment.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING){
                equipment.setItemInMainHand(new ItemStack(Material.AIR,1));
            }
            equipment.setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING,1));
        }
            if (inventory.contains(Material.LEATHER_HELMET)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.LEATHER_HELMET) {
                        equipment.setHelmet(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.CHAINMAIL_HELMET)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.CHAINMAIL_HELMET) {
                        equipment.setHelmet(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.IRON_HELMET)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.IRON_HELMET) {
                        equipment.setHelmet(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.DIAMOND_HELMET)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.DIAMOND_HELMET) {
                        equipment.setHelmet(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.NETHERITE_HELMET)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.NETHERITE_HELMET) {
                        equipment.setHelmet(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.LEATHER_CHESTPLATE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.LEATHER_CHESTPLATE) {
                        equipment.setChestplate(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.CHAINMAIL_CHESTPLATE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.CHAINMAIL_CHESTPLATE) {
                        equipment.setChestplate(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.IRON_CHESTPLATE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.IRON_CHESTPLATE) {
                        equipment.setChestplate(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.DIAMOND_CHESTPLATE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.DIAMOND_CHESTPLATE) {
                        equipment.setChestplate(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.NETHERITE_CHESTPLATE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.NETHERITE_CHESTPLATE) {
                        equipment.setChestplate(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.ELYTRA)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.ELYTRA) {
                        equipment.setChestplate(stack);
                        break;
                    }
                }
            }

            if (inventory.contains(Material.LEATHER_LEGGINGS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.LEATHER_LEGGINGS) {
                        equipment.setLeggings(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.CHAINMAIL_LEGGINGS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.CHAINMAIL_LEGGINGS) {
                        equipment.setLeggings(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.IRON_LEGGINGS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.IRON_LEGGINGS) {
                        equipment.setLeggings(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.DIAMOND_LEGGINGS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.DIAMOND_LEGGINGS) {
                        equipment.setLeggings(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.NETHERITE_LEGGINGS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.NETHERITE_LEGGINGS) {
                        equipment.setLeggings(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.LEATHER_BOOTS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.LEATHER_BOOTS) {
                        equipment.setBoots(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.CHAINMAIL_BOOTS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.CHAINMAIL_BOOTS) {
                        equipment.setBoots(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.IRON_BOOTS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.IRON_BOOTS) {
                        equipment.setBoots(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.DIAMOND_BOOTS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.DIAMOND_BOOTS) {
                        equipment.setBoots(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.NETHERITE_BOOTS)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.NETHERITE_BOOTS) {
                        equipment.setBoots(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.WOODEN_SWORD)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.WOODEN_SWORD) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.WOODEN_AXE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.WOODEN_AXE) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.STONE_SWORD)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.STONE_SWORD) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.STONE_AXE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.STONE_AXE) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.IRON_SWORD)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.IRON_SWORD) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.IRON_AXE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.IRON_AXE) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.DIAMOND_SWORD)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.DIAMOND_SWORD) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.DIAMOND_AXE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.DIAMOND_AXE) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.NETHERITE_SWORD)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.NETHERITE_SWORD) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.NETHERITE_AXE)) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.NETHERITE_AXE) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
            if (inventory.contains(Material.BOW) && inventory.contains(Material.ARROW) ) {
                ItemStack[] contents = inventory.getStorageContents();
                for (ItemStack stack : contents) {
                    if (stack == null) continue;
                    if (stack.getType() == Material.BOW) {
                        equipment.setItemInMainHand(stack);
                        break;
                    }
                }
            }
        PersistentDataContainer container =  holoPet.getBukkitEntity().getPersistentDataContainer();
        container.set(PekoSrvFun.holoPetInventoryKey, PersistentDataType.STRING, Utils.toBase64(inventory));
    }

}
