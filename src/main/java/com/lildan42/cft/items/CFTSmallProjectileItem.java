package com.lildan42.cft.items;

import com.lildan42.cft.entities.CFTSmallProjectileEntity;
import com.lildan42.cft.initialization.CFT2ModEntities;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CFTSmallProjectileItem extends Item {
    public static final double SHURIKEN_LAUNCH_SPEED = 2.0;

    public CFTSmallProjectileItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if(!world.isClient()) {
            Vec3d launchVel = user.getRotationVector().multiply(SHURIKEN_LAUNCH_SPEED);
            CFTSmallProjectileEntity shuriken = CFT2ModEntities.CFT_SMALL_PROJECTILE.create(world, SpawnReason.MOB_SUMMONED);

            if(shuriken == null) {
                return ActionResult.PASS;
            }

            shuriken.setPosition(user.getEyePos());
            shuriken.setVelocity(launchVel);
            shuriken.setOwner(user);
            shuriken.setItemStack(stackInHand);

            world.spawnEntity(shuriken);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        stackInHand.decrementUnlessCreative(1, user);

        return ActionResult.SUCCESS;
    }
}
