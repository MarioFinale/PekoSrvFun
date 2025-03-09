package cl.mariofinale;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class Peko_Utils {


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


    public static boolean sendBarTextToPlayer(Player player, String message){
        if (!player.isOnline()) return false;
        if (player.isBanned()) return false;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(message));
        return true;
    }

    public static boolean sendMessageToPlayer(Player player, String message) {
        if (!player.isOnline()) return false;
        if (player.isBanned()) return false;
        player.sendMessage(message);
        return true;
    }

    public static void sendMessagesToPlayer(Player player, String[] messages){
        for (String message : messages){
            sendMessageToPlayer(player, message);
        }
    }

}
