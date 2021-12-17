package cl.mariofinale;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieWatcher;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntitySlime;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;


public class PekoSrvFun_Pekomon extends EntitySlime {
    public PekoSrvFun_Pekomon(Location loc, String type){
        super(EntityTypes.aD, ((CraftWorld) loc.getWorld()).getHandle());
        this.g(loc.getX(), loc.getY(), loc.getZ());
        persist = true;

        /* // Disabled for now, Slimes don't have a PathfinderGoalTempt or PathfinderGoalRandomStroll. A custom one needs to be created.
        EntityInsentient nmsEntity = (EntityInsentient) ((this.getBukkitEntity()).getHandle());
        PathfinderGoalSelector goalSelector = nmsEntity.bQ;
        PathfinderGoalSelector targetSelector = nmsEntity.bP;

        try {
            Field dField;
            dField = PathfinderGoalSelector.class.getDeclaredField("d");
            dField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            dField.set(targetSelector, new LinkedHashSet<>());

            Field cField;
            cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

            Field fField;
            fField = PathfinderGoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.bQ.a(0, new PathfinderGoalFloat(this));
        this.bQ.a(1, new PathfinderGoalTempt(this,2, RecipeItemStack.a(new IMaterial[] { (IMaterial) Items.rl }),false));
        this.bQ.a(1, new PathfinderGoalTempt(this,2, RecipeItemStack.a(new IMaterial[] { (IMaterial) Items.oc }),false));
        this.bQ.a(2, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.bQ.a(3, new PathfinderGoalRandomStroll(this, 1.0D));
        this.bQ.a(4, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.bQ.a(5, new PathfinderGoalRandomLookaround(this));
        */
        this.getBukkitEntity().setCustomName(type);
        this.getBukkitEntity().setPersistent(true);
        this.getBukkitEntity().setSilent(true);
        ((CraftWorld) loc.getWorld()).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Entity entity = this.getBukkitEntity();
        Slime slime = (Slime) entity;
        slime.setSize(0);
        slime.setCustomName("Dinnerbone");
        slime.setPersistent(true);
        slime.setRemoveWhenFarAway(false);

        //LivingEntity livingEntity = (LivingEntity) entity;
        //livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE,1));

        ItemStack skull;
        String name;
        switch (type){
            case "Blank":
            case "FlippedPekomonBlankSkull":
                skull =  PekoSrvFun.FlippedPekomonBlankSkull.clone();
                name = "FlippedPekomonBlankSkull";
                break;
            case "BlankF":
            case "FlippedPekomonBlankSkullF":
                skull =  PekoSrvFun.FlippedPekomonBlankSkullF.clone();
                name = "FlippedPekomonBlankSkullF";
                break;
            case "SmileF":
            case "FlippedPekomonSmileSkullF":
                skull =  PekoSrvFun.FlippedPekomonSmileSkullF.clone();
                name = "FlippedPekomonSmileSkullF";
                break;
            case "Wink":
            case "FlippedPekomonWinkSkull":
                skull =  PekoSrvFun.FlippedPekomonWinkSkull.clone();
                name = "FlippedPekomonWinkSkull";
                break;
            case "WinkF":
            case "FlippedPekomonWinkSkullF":
                skull =  PekoSrvFun.FlippedPekomonWinkSkullF.clone();
                name = "FlippedPekomonWinkSkullF";
                break;
            case "Happy":
            case "FlippedPekomonLaughSkull":
                skull =  PekoSrvFun.FlippedPekomonLaughSkull.clone();
                name = "FlippedPekomonLaughSkull";
                break;
            case "HappyF":
            case "FlippedPekomonLaughSkullF":
                skull =  PekoSrvFun.FlippedPekomonLaughSkullF.clone();
                name = "FlippedPekomonLaughSkullF";
                break;
            case "Derp":
            case "FlippedPekomonDerpSkull":
                skull =  PekoSrvFun.FlippedPekomonDerpSkull.clone();
                name = "FlippedPekomonDerpSkull";
                break;
            case "DerpF":
            case "FlippedPekomonDerpSkullF":
                skull =  PekoSrvFun.FlippedPekomonDerpSkullF.clone();
                name = "FlippedPekomonDerpSkullF";
                break;
            case "Cool":
            case "FlippedPekomonCoolSkull":
                skull =  PekoSrvFun.FlippedPekomonCoolSkull.clone();
                name = "FlippedPekomonCoolSkull";
                break;
            case "CoolF":
            case "FlippedPekomonCoolSkullF":
                skull =  PekoSrvFun.FlippedPekomonCoolSkullF.clone();
                name = "FlippedPekomonCoolSkullF";
                break;
            default:
                skull =  PekoSrvFun.FlippedPekomonSmileSkull.clone();
                name = "FlippedPekomonSmileSkull";
                break;
        }

        MobDisguise disguise = new MobDisguise(DisguiseType.ZOMBIE);
        ZombieWatcher watcher = (ZombieWatcher) disguise.getWatcher();
        watcher.setSneaking(true);
        watcher.setInvisible(true);
        watcher.setCustomNameVisible(true);
        watcher.setCustomName("Dinnerbone");
        watcher.setUpsideDown(true);
        watcher.setArmor(new ItemStack[]{null, null, null, skull});
        DisguiseAPI.disguiseToAll(entity, disguise);

        watcher.setInternalUpsideDown(true);
        watcher.setCustomName("Dinnerbone");
        PekoSrvFun.PekomonList.put(entity.getUniqueId(), new Tuple<>(entity.getLocation(),name));
    }
}

