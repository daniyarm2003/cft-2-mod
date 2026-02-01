package com.lildan42.cft.entities.models;

import com.lildan42.cft.entities.CFTFighterProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class CFTFighterProjectileEntityModel extends EntityModel<EntityRenderState> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(CFTFighterProjectileEntity.ENTITY_ID, "main");

    private final ModelPart projectile_base;

    public CFTFighterProjectileEntityModel(ModelPart root) {
        super(root);
        this.projectile_base = root.getChild("projectile_base");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("projectile_base", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(25, 17).cuboid(4.0F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, new Dilation(0.0F))
                .uv(25, 30).cuboid(-5.0F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 17).cuboid(-3.0F, -5.0F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 25).cuboid(-3.0F, 4.0F, -3.0F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 33).cuboid(-3.0F, -3.0F, 4.0F, 6.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(33, 0).cuboid(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 4.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(EntityRenderState state) {
        super.setAngles(state);
    }
}