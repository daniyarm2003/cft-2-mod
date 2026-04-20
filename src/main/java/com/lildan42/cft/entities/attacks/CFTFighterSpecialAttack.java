package com.lildan42.cft.entities.attacks;

import com.lildan42.cft.entities.CFTFighterEntity;
import net.minecraft.entity.LivingEntity;

public interface CFTFighterSpecialAttack {
    boolean shouldStepBack();
    double getCooldownMultiplier();
    void doSpecialAttack(CFTFighterEntity fighter, LivingEntity target, CFTFighterAttackScheduler attackScheduler);
}
