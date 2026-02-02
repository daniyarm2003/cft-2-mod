package com.lildan42.cft.initialization;

import com.lildan42.cft.entities.models.CFTFighterEntityModel;
import com.lildan42.cft.entities.models.CFTFighterProjectileEntityModel;
import com.lildan42.cft.entities.renderers.CFTFighterEntityRenderer;
import com.lildan42.cft.entities.renderers.CFTFighterProjectileEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import org.slf4j.Logger;

import java.util.Iterator;

public class CFT2ModEntitiesClient implements CFT2Initializer {
    @Override
    public String getInitializationStageName() {
        return "Entity client registration";
    }

    @Override
    public void initialize(Logger logger) {

        for (Iterator<FighterEntityRegistryContext> it = CFT2ModEntities.getFighterRegistryIterator(); it.hasNext(); ) {
            FighterEntityRegistryContext context = it.next();
            EntityRendererRegistryImpl.register(context.entityType(), CFTFighterEntityRenderer::new);
        }

        EntityRendererRegistryImpl.register(CFT2ModEntities.CFT_FIGHTER_PROJECTILE, CFTFighterProjectileEntityRenderer::new);

        logger.info("Entity renderers were registered successfully");

        EntityModelLayerRegistry.registerModelLayer(CFTFighterEntityModel.MODEL_LAYER, CFTFighterEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(CFTFighterProjectileEntityModel.MODEL_LAYER, CFTFighterProjectileEntityModel::getTexturedModelData);

        logger.info("Entity models were registered successfully");
    }
}
