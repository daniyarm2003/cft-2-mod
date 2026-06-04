package com.lildan42.cft.entities.attacks;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.entities.CFTFighterProjectileEntity;
import com.lildan42.cft.fighterdata.attacks.BallAttack;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import com.lildan42.cft.initialization.CFT2ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CFTFighterBallAttack implements CFTFighterSpecialAttack {

    private static final float DAMAGE_MULTIPLIER = 0.85F;

    private final BallAttack ballAttack;

    public CFTFighterBallAttack(BallAttack ballAttack) {
        this.ballAttack = ballAttack;
    }

    @Override
    public boolean shouldStepBack() {
        return true;
    }

    @Override
    public double getCooldownMultiplier() {
        return 1.0;
    }

    @Override
    public void doSpecialAttack(CFTFighterEntity fighter, LivingEntity target, CFTFighterAttackScheduler attackScheduler) {
        Vec3d dir = target.getEyePos().subtract(fighter.getEyePos()).normalize();
        World world = fighter.getEntityWorld();

        CFTFighterProjectileEntity projectile = CFT2ModEntities.CFT_FIGHTER_PROJECTILE.create(world, SpawnReason.MOB_SUMMONED);

        if(projectile == null) {
            return;
        }

        projectile.setPosition(fighter.getEyePos().add(dir));
        projectile.setOwner(fighter);
        projectile.setVelocity(dir);
        projectile.setProjectileDamage(DAMAGE_MULTIPLIER * (float)fighter.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE));
        projectile.setProjectileColor(this.ballAttack.getColor());

        world.spawnEntity(projectile);
        fighter.incrementAttackCount();
    }
}
