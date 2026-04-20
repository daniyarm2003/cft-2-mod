package com.lildan42.cft.entities;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.initialization.CFT2ModDamageTypes;
import com.lildan42.cft.initialization.CFT2ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CFTShurikenEntity extends ProjectileEntity implements FlyingItemEntity {
    public static final String ENTITY_NAME = "cft_shuriken";
    public static final Identifier ENTITY_ID = CFT2Mod.createModIdentifier(ENTITY_NAME);

    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(CFTShurikenEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final float DEFAULT_DAMAGE = 10.0f;
    private static final String DAMAGE_TAG_KEY = "ProjectileDamage";

    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(CFTShurikenEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final String ITEM_TAG_KEY = "Item";

    public CFTShurikenEntity(EntityType<CFTShurikenEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(DAMAGE, DEFAULT_DAMAGE);
        builder.add(ITEM, CFT2ModItems.CFT_SHURIKEN_ITEM.getDefaultStack());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        World world = hitEntity.getEntityWorld();

        if(world.isClient() || !(this.getOwner() instanceof LivingEntity) || hitEntity.equals(this.getOwner())) {
            return;
        }

        if(hitEntity instanceof CFTFighterProjectileEntity || hitEntity instanceof CFTShurikenEntity) {
            hitEntity.remove(RemovalReason.KILLED);
            this.remove(RemovalReason.KILLED);

            return;
        }

        hitEntity.damage((ServerWorld) world, world.getDamageSources().create(CFT2ModDamageTypes.CFT_SHURIKEN_DAMAGE), this.getDamage());

        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if(!this.getEntityWorld().isClient() && (owner == null || owner.isRemoved())) {
            this.discard();
            return;
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, e -> e instanceof LivingEntity || e instanceof CFTFighterProjectileEntity || e instanceof CFTShurikenEntity, RaycastContext.ShapeType.COLLIDER);
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

    public float getDamage() {
        return this.dataTracker.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.dataTracker.set(DAMAGE, damage);
    }

    @Override
    public ItemStack getStack() {
        return this.dataTracker.get(ITEM);
    }

    private void setStack(ItemStack stack) {
        this.dataTracker.set(ITEM, stack.copyWithCount(1));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);

        view.putFloat(DAMAGE_TAG_KEY, this.getDamage());
        view.put(ITEM_TAG_KEY, ItemStack.CODEC, this.getStack());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);

        this.setDamage(view.getFloat(DAMAGE_TAG_KEY, DEFAULT_DAMAGE));
        this.setStack(view.read(ITEM_TAG_KEY, ItemStack.CODEC).orElseGet(CFT2ModItems.CFT_SHURIKEN_ITEM::getDefaultStack));
    }
}
