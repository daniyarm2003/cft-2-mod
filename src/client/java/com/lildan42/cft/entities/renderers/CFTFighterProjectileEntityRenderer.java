package com.lildan42.cft.entities.renderers;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterProjectileEntity;
import com.lildan42.cft.entities.models.CFTFighterProjectileEntityModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CFTFighterProjectileEntityRenderer extends EntityRenderer<CFTFighterProjectileEntity, EntityRenderState> {

    private static final Identifier TEXTURE_IDENTIFIER = CFT2Mod.createModIdentifier("textures/entity/cft_fighter_projectile.png");

    private final CFTFighterProjectileEntityModel model;

    public CFTFighterProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new CFTFighterProjectileEntityModel(context.getPart(CFTFighterProjectileEntityModel.MODEL_LAYER));
    }

    @Override
    public void render(EntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        queue.submitModel(this.model, renderState, matrices, RenderLayers.entitySolid(TEXTURE_IDENTIFIER), renderState.light, OverlayTexture.DEFAULT_UV, renderState.outlineColor, null);
        super.render(renderState, matrices, queue, cameraState);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
