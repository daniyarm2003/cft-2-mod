package com.lildan42.cft.entities.renderers;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.entities.models.CFTFighterEntityModel;
import com.lildan42.cft.entities.renderstates.CFTFighterEntityRenderState;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CFTFighterEntityRenderer extends MobEntityRenderer<CFTFighterEntity, CFTFighterEntityRenderState, CFTFighterEntityModel> {

    private static final float MODEL_SCALE = 0.9375f;

    public CFTFighterEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CFTFighterEntityModel(context.getPart(CFTFighterEntityModel.MODEL_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(CFTFighterEntityRenderState state) {
        return DefaultSkinHelper.getTexture();
    }

    @Override
    public CFTFighterEntityRenderState createRenderState() {
        return new CFTFighterEntityRenderState();
    }

    @Override
    public void updateRenderState(CFTFighterEntity fighter, CFTFighterEntityRenderState renderState, float tickProgress) {
        super.updateRenderState(fighter, renderState, tickProgress);
        BipedEntityRenderer.updateBipedRenderState(fighter, renderState, tickProgress, this.itemModelResolver);
    }

    @Override
    protected void scale(CFTFighterEntityRenderState state, MatrixStack matrices) {
        matrices.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }
}
