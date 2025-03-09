package cl.mariofinale.NPC;

import cl.mariofinale.Peko_Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class NPCCommands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = ((Player) sender).getPlayer();
        if (player == null) return false;
        if (args.length == 0) return false;
        String com = args[0].toUpperCase();
        switch (com) {
            case "CREATE":
            case "CREAR":
            case "CR":
                if (args.length < 2) {
                    Peko_Utils.sendMessageToPlayer(player, "Usage: \"/NPC Create\" {Name}");
                    return true;
                }
                if (!NPCUtils.SpawnNPCHelper(player, args[1])) {
                    Peko_Utils.sendMessageToPlayer(player, "Insufficient Resources!");
                    Peko_Utils.sendMessageToPlayer(player, "You need a Pekomon head and 10 Netherithe blocks or one Deepslate emerald ore.");
                }
                break;
            case "TEST":
                NPCTest(player);
                return true;
            case "LIST":
            case "LISTA":
            case "L":
                ProcessOwnedNPCLocationsCommand(player);
                return true;
            case "CALL":
            case "LLAMAR":
            case "TELEPORTAR":
            case "TP":
            case "TPHERE":
            case "TPACA":
            case "C":
                if (args.length < 2) {
                    Peko_Utils.sendMessageToPlayer(player, "Usage: \"/NPC TP\" {Name}");
                    return true;
                }
                ProcessTPNPCCommand(player, args[1]);
                return true;
            default: return false;
        }
        return true;
    }

    public String[] GenerateOwnedNPCLocationsList(Player player){
        Set<NPCHelper> NPCs = NPCUtils.GetPlayerNPCs(player);
        if (NPCs.isEmpty()) return new String[]{"You don't have any NPC!"};
        List<String> lines = new ArrayList<>();
        lines.add("You have " + NPCs.size() + " NPCs:");
        for (NPCHelper helper : NPCs){
            Location location = helper.getLocation();
            World world = location.getWorld();
            if (world == null) continue;
            String worldName = world.getName();
            String helperName = helper.getNPCName();
            int x = location.getBlockX();
            int y = location.getBlockY();;
            int z = location.getBlockZ();
            lines.add("NPC \"" + helperName+ "\" in world \"" + worldName + "\". Location: [X" + x + " Y" + y + " Z" + z + "]");
        }
        return lines.toArray(new String[0]);
    }

    public void ProcessOwnedNPCLocationsCommand(Player player){
        Peko_Utils.sendMessagesToPlayer(player,GenerateOwnedNPCLocationsList(player));
    }

    public void ProcessTPNPCCommand(Player player, String name){
        Set<NPCHelper> NPCs = NPCUtils.GetPlayerNPCs(player);
        if (NPCs.isEmpty()){
            Peko_Utils.sendMessageToPlayer( player, "You don't have any NPC!");
            return;
        }
        for (NPCHelper helper : NPCs){
            String helperName = helper.getNPCName();
            if (helperName.equals(name)){
                helper.teleport(player.getLocation());
                Peko_Utils.sendMessageToPlayer( player, "NPC \"" + helperName + "\" has been teleported!");
            }
        }

    }

    public void NPCTest(Player player){

    }

}
