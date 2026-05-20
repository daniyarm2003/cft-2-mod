package com.lildan42.cft.entities.renderers;

import com.lildan42.cft.entities.CFTSmallProjectileEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class CFTSmallProjectileEntityRenderer extends FlyingItemEntityRenderer<CFTSmallProjectileEntity> {

    public CFTSmallProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}
