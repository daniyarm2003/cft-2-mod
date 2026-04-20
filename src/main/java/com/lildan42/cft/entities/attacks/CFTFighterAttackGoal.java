package com.lildan42.cft.entities.attacks;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.impl.launch.MappingConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;

public class CFTFighterAttackGoal extends MeleeAttackGoal {
    private static final double STEP_BACK_VELOCITY_Y = 0.15;

    private static final int PROJECTILE_LAUNCH_DELAY = 10;

    private final CFTFighterEntity fighter;

    private final int stepBackWaitTicks;
    private final int stepBackCooldown;

    private int stepBackTimer = 0;
    private int stepBackCooldownTimer = 0;
    private int projectileLaunchDelayTimer = PROJECTILE_LAUNCH_DELAY;

    private boolean hasProjectile = false;

    private final double stepBackChance;
    private final double stepBackVelocity;

    private final Field cooldownField;

    private final CFTFighterAttackScheduler attackScheduler = new CFTFighterAttackScheduler();

    public CFTFighterAttackGoal(CFTFighterEntity fighter, int stepBackWaitTicks, int stepBackCooldown, double stepBackChance, double stepBackVelocity) {
        super(fighter, 1.0, true);

        this.fighter = fighter;
        this.stepBackWaitTicks = (int) ((double) stepBackWaitTicks * this.fighter.getSpecialAttack().getCooldownMultiplier());
        this.stepBackCooldown = stepBackCooldown;
        this.stepBackChance = stepBackChance;
        this.stepBackVelocity = stepBackVelocity;

        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();

        String cooldownFieldName = mappingResolver.mapFieldName(MappingConfiguration.INTERMEDIARY_NAMESPACE,
                mappingResolver.unmapClassName(MappingConfiguration.INTERMEDIARY_NAMESPACE, MeleeAttackGoal.class.getName()),
                "field_24667",
                "I");

        try {
            Field cooldownField = MeleeAttackGoal.class.getDeclaredField(cooldownFieldName);
            cooldownField.setAccessible(true);

            this.cooldownField = cooldownField;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int getMaxCooldown() {
        return (int) (20.0 * this.fighter.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_ATTACK_COOLDOWN_MULTIPLIER));
    }

    @Override
    protected void resetCooldown() {
        try {
            this.cooldownField.set(this, this.getTickCount(this.getMaxCooldown()));
        } catch (IllegalAccessException ignored) {
            super.resetCooldown();
        }
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.fighter.getTarget();

        if (target == null) {
            return;
        }

        this.attackScheduler.update();

        if (this.hasProjectile) {
            if (this.projectileLaunchDelayTimer > 0) {
                this.projectileLaunchDelayTimer--;
            } else {
                this.hasProjectile = false;

                if (!this.fighter.getEntityWorld().isClient()) {
                    this.fighter.getSpecialAttack().doSpecialAttack(this.fighter, target, this.attackScheduler);
                }
            }
        }

        if (this.stepBackTimer < this.stepBackWaitTicks) {
            this.stepBackTimer++;
        } else {
            this.stepBackTimer = 0;

            if (this.stepBackCooldownTimer > 0) {
                this.stepBackCooldownTimer--;
            } else if (this.fighter.getRandom().nextDouble() < this.stepBackChance) {
                this.stepBackCooldownTimer = this.stepBackCooldown;

                if (this.fighter.getSpecialAttack().shouldStepBack()) {
                    Vec3d targetDir = target.getEyePos().subtract(this.fighter.getEyePos());
                    targetDir = targetDir.subtract(0.0, targetDir.getY(), 0.0).normalize();

                    Vec3d newVel = targetDir.multiply(-stepBackVelocity).add(Vec3d.Y.multiply(STEP_BACK_VELOCITY_Y));

                    this.fighter.setVelocity(newVel);
                    this.fighter.velocityDirty = true;
                }

                this.hasProjectile = true;
                this.projectileLaunchDelayTimer = PROJECTILE_LAUNCH_DELAY;
            }
        }
    }
}
