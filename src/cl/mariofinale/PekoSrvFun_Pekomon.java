package cl.mariofinale;


import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieWatcher;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityRabbit;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.material.Material;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import oshi.util.tuples.Pair;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;

public class PekoSrvFun_Pekomon extends EntitySlime {

    public PekoSrvFun_Pekomon(Location loc, String type){
        super(EntityTypes.aD, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
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
        this.setPersistent();
        this.setSilent(true);
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Entity entity = this.getBukkitEntity();
        Slime slime = (Slime) entity;
        slime.setSize(0);

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
        //watcher.setBaby(true);
        watcher.setCustomName("Wild PekoMon");
        watcher.setCustomNameVisible(true);
        watcher.setArmor(new ItemStack[]{null, null, null, skull});
        watcher.setUpsideDown(true);

        DisguiseAPI.disguiseToAll(entity, disguise);
        PekoSrvFun.PekomonList.put(entity.getUniqueId(), new Pair<>(entity.getLocation(),name));
    }
}

