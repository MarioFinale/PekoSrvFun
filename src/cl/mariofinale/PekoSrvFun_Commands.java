package cl.mariofinale;
import cl.mariofinale.NPC.NPCHelper;
import cl.mariofinale.NPC.NPCUtils;
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
                if (!NPCUtils.SpawnNPCHelper(player, args[1])) {
                    Peko_Utils.sendMessageToPlayer(player, "Insufficient Resources!");
                    Peko_Utils.sendMessageToPlayer(player, "You need a Pekomon head and 10 Netherithe blocks or one Deepslate emerald ore.");
                }
                return true;
            default:
                return false;
        }
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
                sendMessageToPlayer(player, "The World '" + worldName + "' was added to the list.");
            }else{
                sendMessageToPlayer(player, "The World '" + worldName + "' already is on the list.");
            }
        }else {
            sendMessageToPlayer(player, "The World '" + worldName + "' does not exist.");
            StringBuilder availableWorlds = new StringBuilder();
            for (String wN : worldList){
                availableWorlds.append(wN).append(" - ");
            }
            availableWorlds = new StringBuilder(availableWorlds.substring(0, availableWorlds.length() - 3).trim());
            sendMessageToPlayer(player, "Available Worlds: " + availableWorlds + "");
        }
        return true;
    }

    /** @noinspection SameReturnValue*/
    private static boolean RemoveWorldFromList(Player player, String worldName) {
        if (PekoSrvFun.WorldsList.contains(worldName)){
            PekoSrvFun.WorldsList.remove(worldName);
            sendMessageToPlayer(player, "The World '" + worldName + "' was removed from the list.");
        }else {
            sendMessageToPlayer(player, "The World '" + worldName + "' is not on the list.");
        }
        return true;
    }


    public static void sendMessageToPlayer(Player player, String message) {
        if (!player.isValid()) return;
        if (!player.isOnline()) return;
        String resultingMessage = PekoSrvFun_PluginVars.PluginPrefix + " " + message;
        player.sendMessage(resultingMessage);
    }

    public static void sendMessagesToPlayer(Player player, String[] messages){
        for (String message : messages){
            sendMessageToPlayer(player, message);
        }
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
