package com.lildan42.cft.entities;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fighterdata.fighters.Fighter;
import com.lildan42.cft.fighterdata.fighters.FighterSkill;
import com.lildan42.cft.fights.CFTFight;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import com.lildan42.cft.initialization.CFT2ModEntities;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.impl.launch.MappingConfiguration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class CFTFighterEntity extends PathAwareEntity {

    public static final String ENTITY_NAME = "cft_fighter";

    private final Fighter fighterData;
    private CFTFight fight;

    public CFTFighterEntity(EntityType<CFTFighterEntity> entityType, World world, Fighter fighterData) {
        super(entityType, world);
        this.fighterData = fighterData;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new CFTFighterAttackGoal(this, 20, 2, 0.25, 2.0));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, CFTFighterEntity.class, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SheepEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, false));
    }

    @Override
    public double getAttributeValue(RegistryEntry<EntityAttribute> attribute) {
        if(attribute == EntityAttributes.ATTACK_DAMAGE) {
            double baseDamage = super.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);

            double criticalMultiplier = this.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_MULTIPLIER);
            double criticalChance = this.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_CHANCE);

            if(this.random.nextDouble() < criticalChance) {
                baseDamage *= criticalMultiplier;
            }

            return baseDamage;
        }

        return super.getAttributeValue(attribute);
    }

    public Fighter getFighterData() {
        return this.fighterData;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        double blockChance = this.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_BLOCK_CHANCE);

        if(this.random.nextDouble() < blockChance && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.isOf(DamageTypes.MAGIC)) {
            return false;
        }

        return super.damage(world, source, amount);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean shouldRenderName() {
        return true;
    }

    public CFTFight getFight() {
        return this.fight;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    public void setFight(CFTFight fight) {
        this.fight = fight;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if(!this.getEntityWorld().isClient() && this.getFight() != null) {
            this.getFight().onFighterDefeat(this);
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes(Fighter fighter) {
        double baseMultiplier = 0.4;

        double baseAtkDamage = 5.0;
        double atkDamage = baseAtkDamage * (1.0 + fighter.getSkillLevel(FighterSkill.SkillType.ATTACK) * baseMultiplier);

        double baseDefense = 2.0;
        double defense = baseDefense * fighter.getSkillLevel(FighterSkill.SkillType.DEFENSE) * baseMultiplier;

        double baseProjDamage = baseAtkDamage * 1.5;
        double projDamage = baseProjDamage * (1.0 + fighter.getSkillLevel(FighterSkill.SkillType.PROJECTILE_DAMAGE) * baseMultiplier);

        double criticalMultiplier = 1.0 + fighter.getSkillLevel(FighterSkill.SkillType.CRITICAL_DAMAGE) * baseMultiplier;

        double baseCriticalChance = 0.15;
        double maxCriticalChance = 0.75;
        double criticalChance = Math.min(baseCriticalChance * fighter.getSkillLevel(FighterSkill.SkillType.CRITICAL_CHANCE) * baseMultiplier, maxCriticalChance);

        double baseBlockChance = 0.15;
        double maxBlockChance = 0.75;
        double blockChance = Math.min(baseBlockChance * fighter.getSkillLevel(FighterSkill.SkillType.BLOCK_CHANCE) * baseMultiplier, maxBlockChance);

        double attackCooldownMultiplier = 2.0 / (1.0 + fighter.getSkillLevel(FighterSkill.SkillType.ATTACK_SPEED) * baseMultiplier);

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.FOLLOW_RANGE, 50.0)
                .add(EntityAttributes.ATTACK_DAMAGE, atkDamage)
                .add(EntityAttributes.MAX_HEALTH, 250.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.38)
                .add(EntityAttributes.ARMOR, defense)
                .add(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE, projDamage)
                .add(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_MULTIPLIER, criticalMultiplier)
                .add(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_CHANCE, criticalChance)
                .add(CFT2ModAttributes.CFT_FIGHTER_BLOCK_CHANCE, blockChance)
                .add(CFT2ModAttributes.CFT_FIGHTER_ATTACK_COOLDOWN_MULTIPLIER, attackCooldownMultiplier);
    }

    private static class CFTFighterAttackGoal extends MeleeAttackGoal {
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

        public CFTFighterAttackGoal(CFTFighterEntity fighter, int stepBackWaitTicks, int stepBackCooldown, double stepBackChance, double stepBackVelocity) {
            super(fighter, 1.0, true);

            this.fighter = fighter;
            this.stepBackWaitTicks = stepBackWaitTicks;
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
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected int getMaxCooldown() {
            return (int)(20.0 * this.fighter.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_ATTACK_COOLDOWN_MULTIPLIER));
        }

        @Override
        protected void resetCooldown() {
            try {
                this.cooldownField.set(this, this.getTickCount(this.getMaxCooldown()));
            }
            catch (IllegalAccessException ignored) {
                super.resetCooldown();
            }
        }

        private void launchProjectile(LivingEntity target) {
            Vec3d dir = target.getEyePos().subtract(this.fighter.getEyePos()).normalize();
            World world = this.fighter.getEntityWorld();

            if(world.isClient()) {
                return;
            }

            CFTFighterProjectileEntity projectile = CFT2ModEntities.CFT_FIGHTER_PROJECTILE.create(world, SpawnReason.MOB_SUMMONED);

            if(projectile == null) {
                return;
            }

            projectile.setPosition(this.fighter.getEyePos().add(dir));
            projectile.setOwner(this.fighter);
            projectile.setVelocity(dir);
            projectile.setProjectileDamage((float)this.fighter.getAttributeValue(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE));

            world.spawnEntity(projectile);
        }

        @Override
        public void tick() {
            super.tick();

            LivingEntity target = this.fighter.getTarget();

            if(target == null) {
                return;
            }

            if(this.hasProjectile) {
                if (this.projectileLaunchDelayTimer > 0) {
                    this.projectileLaunchDelayTimer--;
                }
                else {
                    this.hasProjectile = false;
                    this.launchProjectile(target);
                }
            }

            if(this.stepBackTimer < this.stepBackWaitTicks) {
                this.stepBackTimer++;
            }
            else {
                this.stepBackTimer = 0;

                if(this.stepBackCooldownTimer > 0) {
                    this.stepBackCooldownTimer--;
                }
                else if(this.fighter.getRandom().nextDouble() < this.stepBackChance) {
                    this.stepBackCooldownTimer = this.stepBackCooldown;

                    Vec3d targetDir = target.getEyePos().subtract(this.fighter.getEyePos());
                    targetDir = targetDir.subtract(0.0, targetDir.getY(), 0.0).normalize();

                    Vec3d newVel = targetDir.multiply(-stepBackVelocity).add(Vec3d.Y.multiply(STEP_BACK_VELOCITY_Y));

                    this.fighter.setVelocity(newVel);
                    this.fighter.velocityDirty = true;

                    this.hasProjectile = true;
                    this.projectileLaunchDelayTimer = PROJECTILE_LAUNCH_DELAY;
                }
            }
        }
    }
}
