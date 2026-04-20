package com.lildan42.cft.entities.attacks;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class CFTFighterLightningAttack implements CFTFighterSpecialAttack {
    private static final double DAMAGE_MULTIPLIER = 3.25;

    @Override
    public boolean shouldStepBack() {
        return true;
    }

    @Override
    public double getCooldownMultiplier() {
        return 3.0;
    }

    @Override
    public void doSpecialAttack(CFTFighterEntity fighter, LivingEntity target, CFTFighterAttackScheduler attackScheduler) {
        World world = fighter.getEntityWorld();

        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.MOB_SUMMONED);

        if(lightning == null) {
            return;
        }

        lightning.setCosmetic(true);
        lightning.setPosition(target.getEntityPos());

        world.spawnEntity(lightning);

        target.damage((ServerWorld) world, target.getDamageSources().lightningBolt(), (float)(DAMAGE_MULTIPLIER * fighter.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE)));
    }
}
