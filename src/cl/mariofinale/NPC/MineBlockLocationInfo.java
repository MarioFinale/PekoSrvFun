package cl.mariofinale.NPC;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class MineBlockLocationInfo {
    public final Location location;
    public final Block block;

    public MineBlockLocationInfo(Location location, Block block) {
        this.location = location;
        this.block = block;
    }
}
