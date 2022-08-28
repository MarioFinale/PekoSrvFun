package cl.mariofinale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/** @noinspection unused*/
public class PekoSrvFun_Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        if (!player.isValid()) return true;
        if ((args == null || args.length < 2)) {
            return false;
        }
        switch (args[0].toUpperCase()){
            case "LISTWORLD":
                return AddWorldToList(player, args[1]);
            case "UNLISTWORLD":
                return RemoveWorldFromList(player, args[1]);
            case "PEKOMON":
                SpawnPekomon(player,args[1]);
                return true;
            case "HOLOPET":
                if(!SpawnHoloPet(player, args[1])){
                    SendMessageToPlayer(player,"Insufficient resources! You Need 1 Pekomon Head, and 1 Deepslate Emerald Block or 10 Netherite cubes.");
                }
                return true;
            default:
                return false;
        }
    }

    static boolean SpawnHoloPet(Player player, String type){
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
            if (netherite.getAmount() > 10){
                netherite.setAmount(netherite.getAmount()-10);
            }else if(netherite.getAmount() == 10){
                inventory.setItem(NetheriteIndex, new ItemStack(Material.AIR, 1));
            }else{
                if (EmeraldIndex <= -1) return false;
            }
        }
        ItemStack skull = inventory.getItem(SkullIndex);
        if (skull.getAmount() > 1){
            skull.setAmount(skull.getAmount() - 1);
        }else if(skull.getAmount() == 1){
            inventory.setItem(SkullIndex, new ItemStack(Material.AIR, 1));
        }else{
            return false;
        }
        if (EmeraldIndex > -1){
            ItemStack emeralds = inventory.getItem(EmeraldIndex);
            if (emeralds.getAmount() > 1){
                emeralds.setAmount(emeralds.getAmount()-1);
            }else{
                inventory.setItem(EmeraldIndex, new ItemStack(Material.AIR, 1));
            }
        }

        PekoSrvFun_HoloPet pet = new PekoSrvFun_HoloPet(player.getLocation(), player.getName(), type, "");
        SendMessageToPlayer(player,"Your " + pet.getPetName() + " HoloPet has been Invoked!" );
        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1,1);
        return true;
    }

    /** @noinspection SameReturnValue*/
    private static boolean AddWorldToList(Player player, String worldName) {
        ArrayList<String> worldList = new ArrayList<>();
        for (World world : Bukkit.getWorlds()){
            worldList.add(world.getName());
        }
        if (worldList.contains(worldName)){
            if (!PekoSrvFun.WorldsList.contains(worldName)){
                PekoSrvFun.WorldsList.add(worldName);
                SendMessageToPlayer(player, "The World '" + worldName + "' was added to the list.");
            }else{
                SendMessageToPlayer(player, "The World '" + worldName + "' already is on the list.");
            }
        }else {
            SendMessageToPlayer(player, "The World '" + worldName + "' does not exist.");
            StringBuilder availableWorlds = new StringBuilder();
            for (String wN : worldList){
                availableWorlds.append(wN).append(" - ");
            }
            availableWorlds = new StringBuilder(availableWorlds.substring(0, availableWorlds.length() - 3).trim());
            SendMessageToPlayer(player, "Available Worlds: " + availableWorlds + "");
        }
        return true;
    }

    /** @noinspection SameReturnValue*/
    private static boolean RemoveWorldFromList(Player player, String worldName) {
        if (PekoSrvFun.WorldsList.contains(worldName)){
            PekoSrvFun.WorldsList.remove(worldName);
            SendMessageToPlayer(player, "The World '" + worldName + "' was removed from the list.");
        }else {
            SendMessageToPlayer(player, "The World '" + worldName + "' is not on the list.");
        }
        return true;
    }


    private static void SendMessageToPlayer(Player player, String message) {
        if (!player.isValid()) return;
        if (player.isBanned()) return;
        if (!player.isOnline()) return;
        String resultingMessage = PekoSrvFun_PluginVars.PluginPrefix + " " + message;
        player.sendMessage(resultingMessage);
    }

    private static void SpawnPekomon(Player player, String type){
        ItemStack skull;
        String name;
        switch (type){
            case "Blank":
            case "BlankF":
            case "Smile":
            case "SmileF":
            case "Wink":
            case "WinkF":
            case "Happy":
            case "HappyF":
            case "Derp":
            case "DerpF":
            case "Cool":
            case "CoolF":
                break;
            default:
                player.sendMessage(type + " does not exist!");
                player.sendMessage("Try Blank/Smile/Wink/Happy/Cool or BlankF/SmileF/WinkF/HappyF/CoolF");
                return;
        }
        PekoSrvFun_Pekomon pekomon =  new PekoSrvFun_Pekomon(player.getLocation(),type);
    }
}
