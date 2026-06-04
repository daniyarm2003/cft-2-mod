package com.lildan42.cft.entities.attacks;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.entities.CFTSmallProjectileEntity;
import com.lildan42.cft.fighterdata.attacks.SmallProjectileAttack;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import com.lildan42.cft.initialization.CFT2ModDataComponentTypes;
import com.lildan42.cft.initialization.CFT2ModEntities;
import com.lildan42.cft.items.CFTSmallProjectileItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CFTFighterSmallProjectileAttack implements CFTFighterSpecialAttack {
    private static final float DAMAGE_MULTIPLIER = 0.1F;
    private static final float PROJECTILE_DIVERGENCE = 1.0F;

    private final SmallProjectileAttack smallProjectileAttack;

    public CFTFighterSmallProjectileAttack(SmallProjectileAttack smallProjectileAttack) {
        this.smallProjectileAttack = smallProjectileAttack;
    }

    @Override
    public boolean shouldStepBack() {
        return true;
    }

    @Override
    public double getCooldownMultiplier() {
        return 0.25;
    }

    private void throwSmallProjectile(CFTFighterEntity fighter, LivingEntity target) {
        if(!target.isAlive()) {
            return;
        }

        Vec3d dir = target.getEyePos().subtract(fighter.getEyePos()).normalize();
        World world = fighter.getEntityWorld();

        CFTSmallProjectileEntity projectile = CFT2ModEntities.CFT_SMALL_PROJECTILE.create(world, SpawnReason.MOB_SUMMONED);

        if(projectile == null) {
            return;
        }

        projectile.setOwner(fighter);
        projectile.setPosition(fighter.getEyePos().add(dir));
        projectile.setVelocity(dir.getX(), dir.getY(), dir.getZ(), (float) CFTSmallProjectileItem.SHURIKEN_LAUNCH_SPEED, PROJECTILE_DIVERGENCE);
        projectile.setDamage((float)fighter.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE) * DAMAGE_MULTIPLIER);

        ItemStack projItem = projectile.getStack();
        projItem.set(CFT2ModDataComponentTypes.CFT_SMALL_PROJECTILE_TYPE, this.smallProjectileAttack.getProjectileType());
        projectile.setItemStack(projItem);

        world.spawnEntity(projectile);
        fighter.incrementAttackCount();
    }

    @Override
    public void doSpecialAttack(CFTFighterEntity fighter, LivingEntity target, CFTFighterAttackScheduler attackScheduler) {
        attackScheduler.scheduleAttack(() -> this.throwSmallProjectile(fighter, target), 0);
        attackScheduler.scheduleAttack(() -> this.throwSmallProjectile(fighter, target), 5);
        attackScheduler.scheduleAttack(() -> this.throwSmallProjectile(fighter, target), 10);
    }
}
