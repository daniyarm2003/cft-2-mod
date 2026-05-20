package com.lildan42.cft.entities;

import com.lildan42.cft.CFT2Mod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CFTFighterProjectileEntity extends ProjectileEntity {
    public static final String ENTITY_NAME = "cft_fighter_entity_projectile";
    public static final Identifier ENTITY_ID = CFT2Mod.createModIdentifier(ENTITY_NAME);

    private static final int EFFECT_TICKS = 80, EFFECT_AMPLIFIER = 2;

    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(CFTFighterProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final float DEFAULT_DAMAGE = 10.0F;
    private static final String DAMAGE_TAG_KEY = "ProjectileDamage";

    private static final TrackedData<Integer> PROJECTILE_COLOR = DataTracker.registerData(CFTFighterProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int DEFAULT_PROJECTILE_COLOR = ColorHelper.getArgb(0, 128, 255);
    private static final String PROJECTILE_COLOR_TAG_KEY = "ProjectileColor";

    public CFTFighterProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DAMAGE, DEFAULT_DAMAGE);
        builder.add(PROJECTILE_COLOR, DEFAULT_PROJECTILE_COLOR);
    }

    public float getProjectileDamage() {
        return this.getDataTracker().get(DAMAGE);
    }

    public void setProjectileDamage(float damage) {
        this.getDataTracker().set(DAMAGE, damage);
    }

    public int getProjectileColor() {
        return this.getDataTracker().get(PROJECTILE_COLOR);
    }

    public void setProjectileColor(int projectileColor) {
        this.getDataTracker().set(PROJECTILE_COLOR, projectileColor);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        World world = hitEntity.getEntityWorld();

        if(world.isClient() || !(this.getOwner() instanceof LivingEntity)) {
            return;
        }

        if(hitEntity instanceof CFTFighterProjectileEntity) {
            hitEntity.remove(RemovalReason.KILLED);
            this.remove(RemovalReason.KILLED);

            return;
        }

        hitEntity.damage((ServerWorld) world, world.getDamageSources().magic(), this.getProjectileDamage());

        if(hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, EFFECT_TICKS, EFFECT_AMPLIFIER));
        }

        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if(!this.getEntityWorld().isClient() && (owner == null || owner.isRemoved())) {
            this.discard();
            return;
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, e -> e instanceof LivingEntity || e instanceof CFTFighterProjectileEntity, RaycastContext.ShapeType.COLLIDER);
        Vec3d pos = hitResult.getType() != HitResult.Type.MISS ? hitResult.getPos() : this.getEntityPos().add(this.getVelocity());

        this.setPosition(pos);
        this.tickBlockCollision();
        super.tick();

        if(hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitOrDeflect(hitResult);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);

        this.setProjectileDamage(view.getFloat(DAMAGE_TAG_KEY, DEFAULT_DAMAGE));
        this.setProjectileColor(view.getInt(PROJECTILE_COLOR_TAG_KEY, DEFAULT_PROJECTILE_COLOR));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);

        view.putFloat(DAMAGE_TAG_KEY, this.getProjectileDamage());
        view.putInt(PROJECTILE_COLOR_TAG_KEY, this.getProjectileColor());
    }
}
