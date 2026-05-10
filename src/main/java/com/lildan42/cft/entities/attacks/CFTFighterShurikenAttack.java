package com.lildan42.cft.entities.attacks;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.entities.CFTShurikenEntity;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import com.lildan42.cft.initialization.CFT2ModEntities;
import com.lildan42.cft.items.CFTShurikenItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CFTFighterShurikenAttack implements CFTFighterSpecialAttack {
    private static final float DAMAGE_MULTIPLER = 0.09F;
    private static final float PROJECTILE_DIVERGENCE = 1.0F;

    @Override
    public boolean shouldStepBack() {
        return true;
    }

    @Override
    public double getCooldownMultiplier() {
        return 0.25;
    }

    private void throwShuriken(CFTFighterEntity fighter, LivingEntity target) {
        if(!target.isAlive()) {
            return;
        }

        Vec3d dir = target.getEyePos().subtract(fighter.getEyePos()).normalize();
        World world = fighter.getEntityWorld();

        CFTShurikenEntity shuriken = CFT2ModEntities.CFT_SHURIKEN.create(world, SpawnReason.MOB_SUMMONED);

        if(shuriken == null) {
            return;
        }

        shuriken.setOwner(fighter);
        shuriken.setPosition(fighter.getEyePos().add(dir));
        shuriken.setVelocity(dir.getX(), dir.getY(), dir.getZ(), (float) CFTShurikenItem.SHURIKEN_LAUNCH_SPEED, PROJECTILE_DIVERGENCE);
        shuriken.setDamage((float)fighter.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE) * DAMAGE_MULTIPLER);

        world.spawnEntity(shuriken);
    }

    @Override
    public void doSpecialAttack(CFTFighterEntity fighter, LivingEntity target, CFTFighterAttackScheduler attackScheduler) {
        attackScheduler.scheduleAttack(() -> this.throwShuriken(fighter, target), 0);
        attackScheduler.scheduleAttack(() -> this.throwShuriken(fighter, target), 5);
        attackScheduler.scheduleAttack(() -> this.throwShuriken(fighter, target), 10);
    }
}
