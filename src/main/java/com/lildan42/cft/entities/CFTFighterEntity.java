package com.lildan42.cft.entities;

import com.lildan42.cft.entities.attacks.CFTFighterAttackGoal;
import com.lildan42.cft.entities.attacks.CFTFighterShurikenAttack;
import com.lildan42.cft.entities.attacks.CFTFighterSpecialAttack;
import com.lildan42.cft.fighterdata.fighters.Fighter;
import com.lildan42.cft.fighterdata.fighters.FighterSkill;
import com.lildan42.cft.fights.CFTFight;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class CFTFighterEntity extends PathAwareEntity {

    public static final String ENTITY_NAME = "cft_fighter";
    private static final int BLOCK_INDICATOR_MAX_TICKS = 40;

    private static final TrackedData<Integer> BLOCK_INDICATOR_REMAINING_TICKS = DataTracker.registerData(CFTFighterEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private final Fighter fighterData;
    private CFTFight fight;

    public CFTFighterEntity(EntityType<CFTFighterEntity> entityType, World world, Fighter fighterData) {
        super(entityType, world);
        this.fighterData = fighterData;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BLOCK_INDICATOR_REMAINING_TICKS, 0);
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

    private void onBlock(ServerWorld world) {
        world.playSound(this, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK.value(), SoundCategory.HOSTILE);
        this.dataTracker.set(BLOCK_INDICATOR_REMAINING_TICKS, BLOCK_INDICATOR_MAX_TICKS);
    }

    public float getBlockOpacity() {
        return (float)this.dataTracker.get(BLOCK_INDICATOR_REMAINING_TICKS) / (float)BLOCK_INDICATOR_MAX_TICKS;
    }

    @Override
    public void tick() {
        super.tick();
        int remainingBlockTicks = this.dataTracker.get(BLOCK_INDICATOR_REMAINING_TICKS);

        if(!this.getEntityWorld().isClient() && remainingBlockTicks > 0) {
            this.dataTracker.set(BLOCK_INDICATOR_REMAINING_TICKS, remainingBlockTicks - 1);
        }
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
            this.onBlock(world);
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

    public CFTFighterSpecialAttack getSpecialAttack() {
        return new CFTFighterShurikenAttack();
    }

    public static DefaultAttributeContainer.Builder createAttributes(Fighter fighter) {
        double baseMultiplier = 0.4;

        double baseAtkDamage = 1.0;
        double atkDamage = baseAtkDamage * (1.0 + fighter.getSkillLevel(FighterSkill.SkillType.ATTACK) * baseMultiplier);

        double baseDefense = 2.0;
        double defense = baseDefense * fighter.getSkillLevel(FighterSkill.SkillType.DEFENSE) * baseMultiplier;

        double baseProjDamage = baseAtkDamage * 1.5;
        double projDamage = baseProjDamage * (1.0 + fighter.getSkillLevel(FighterSkill.SkillType.PROJECTILE_DAMAGE) * baseMultiplier);

        double criticalMultiplier = 1.0 + fighter.getSkillLevel(FighterSkill.SkillType.CRITICAL_DAMAGE) * baseMultiplier;

        double baseCriticalChance = 0.1;
        double maxCriticalChance = 0.75;
        double criticalChance = Math.min(baseCriticalChance * fighter.getSkillLevel(FighterSkill.SkillType.CRITICAL_CHANCE), maxCriticalChance);

        double baseBlockChance = 0.1;
        double maxBlockChance = 0.75;
        double blockChance = Math.min(baseBlockChance * fighter.getSkillLevel(FighterSkill.SkillType.BLOCK_CHANCE), maxBlockChance);

        double attackCooldownMultiplier = 2.0 / (1.0 + fighter.getSkillLevel(FighterSkill.SkillType.ATTACK_SPEED));

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.FOLLOW_RANGE, 50.0)
                .add(EntityAttributes.ATTACK_DAMAGE, atkDamage)
                .add(EntityAttributes.MAX_HEALTH, fighter.getHealth())
                .add(EntityAttributes.MOVEMENT_SPEED, 0.38)
                .add(EntityAttributes.ARMOR, defense)
                .add(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE, projDamage)
                .add(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_MULTIPLIER, criticalMultiplier)
                .add(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_CHANCE, criticalChance)
                .add(CFT2ModAttributes.CFT_FIGHTER_BLOCK_CHANCE, blockChance)
                .add(CFT2ModAttributes.CFT_FIGHTER_ATTACK_COOLDOWN_MULTIPLIER, attackCooldownMultiplier);
    }

}
