package com.lildan42.cft.initialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.entities.CFTFighterProjectileEntity;
import com.lildan42.cft.fighterdata.fighters.Fighter;
import com.lildan42.cft.fighterdata.state.CFTSaveContextSerializer;
import com.lildan42.cft.fighterdata.state.CFTState;
import com.lildan42.cft.fighterdata.state.FileCFTStateSaver;
import com.lildan42.cft.fighterdata.state.GzipJsonCFTSaveContextSerializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CFT2ModEntities implements CFT2Initializer {

    public static final TagKey<EntityType<?>> CFT_FIGHTER_ENTITY_TAG = createEntityTag("cft_fighters");

    private static final List<FighterEntityRegistryContext> CFT_FIGHTER_REGISTRY_CONTEXTS = new ArrayList<>();

    public static final EntityType<CFTFighterProjectileEntity> CFT_FIGHTER_PROJECTILE =
            registerEntity(CFTFighterProjectileEntity.ENTITY_ID, EntityType.Builder.create(CFTFighterProjectileEntity::new, SpawnGroup.MISC)
                    .dropsNothing().dimensions(0.5F, 0.5F).eyeHeight(0.0F).maxTrackingRange(16));

    static {
        List<Fighter> fighters = CFT2Mod.CFT_STATE.getActiveFighters();
        
        for(Fighter fighter : fighters) {
            EntityType.Builder<CFTFighterEntity> builder = EntityType.Builder.create((EntityType<CFTFighterEntity> entityType, World world) ->
                            new CFTFighterEntity(entityType, world, fighter), SpawnGroup.MISC)
                    .dropsNothing().dimensions(0.6f, 1.8f).eyeHeight(1.65f).maxTrackingRange(16);

            EntityType<CFTFighterEntity> entityType = registerFighterEntity(fighter, builder);

            CFT_FIGHTER_REGISTRY_CONTEXTS.add(new FighterEntityRegistryContext(entityType, fighter));

            CFT2Mod.LOGGER.info("Registered CFT fighter entity: {}", fighter.getEntityId());
        }
    }

    private static TagKey<EntityType<?>> createEntityTag(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, CFT2Mod.createModIdentifier(id));
    }
    
    private static EntityType<CFTFighterEntity> registerFighterEntity(Fighter fighter, EntityType.Builder<CFTFighterEntity> builder) {
        return registerEntity(fighter.getEntityId(), builder);
    }

    private static <T extends Entity> EntityType<T> registerEntity(Identifier id, EntityType.Builder<T> builder) {
        RegistryKey<EntityType<?>> registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
        EntityType<T> entityType = builder.build(registryKey);

        return Registry.register(Registries.ENTITY_TYPE, registryKey, entityType);
    }

    public static Iterator<FighterEntityRegistryContext> getFighterRegistryIterator() {
        return CFT_FIGHTER_REGISTRY_CONTEXTS.iterator();
    }

    @Override
    public String getInitializationStageName() {
        return "Entity registration";
    }

    @Override
    public void initialize(Logger logger) {
        for(FighterEntityRegistryContext context : CFT_FIGHTER_REGISTRY_CONTEXTS) {
            FabricDefaultAttributeRegistry.register(context.entityType(), CFTFighterEntity.createAttributes(context.fighterData()));
        }

        logger.info("Mob attributes were registered successfully");
    }
}
