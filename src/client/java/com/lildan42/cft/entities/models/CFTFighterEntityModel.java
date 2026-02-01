package com.lildan42.cft.entities.models;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.entities.renderstates.CFTFighterEntityRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

public class CFTFighterEntityModel extends BipedEntityModel<CFTFighterEntityRenderState> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(CFT2Mod.createModIdentifier(CFTFighterEntity.ENTITY_NAME), "main");

    private static final String LEFT_SLEEVE = "left_sleeve";
    private static final String RIGHT_SLEEVE = "right_sleeve";

    private static final String LEFT_PANTS = "left_pants";
    private static final String RIGHT_PANTS = "right_pants";

    private final ModelPart leftSleeve, rightSleeve, leftPants, rightPants, jacket;

    public CFTFighterEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayers::entityTranslucent);

        this.leftSleeve = this.leftArm.getChild(LEFT_SLEEVE);
        this.rightSleeve = this.rightArm.getChild(RIGHT_SLEEVE);
        this.leftPants = this.leftLeg.getChild(LEFT_PANTS);
        this.rightPants = this.rightLeg.getChild(RIGHT_PANTS);
        this.jacket = this.body.getChild(EntityModelPartNames.JACKET);
    }

    public static TexturedModelData getTexturedModelData() {
        Dilation dilation = Dilation.NONE;

        ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0f);
        ModelPartData modelRoot = modelData.getRoot();

        ModelPartData leftArm = modelRoot.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create()
                .uv(32, 48)
                .cuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation),
                ModelTransform.origin(5.0f, 2.0f, 0.0f));

        ModelPartData rightArm = modelRoot.getChild(EntityModelPartNames.RIGHT_ARM);

        leftArm.addChild(LEFT_SLEEVE, ModelPartBuilder.create()
                .uv(48, 48)
                .cuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.add(0.25f)),
                ModelTransform.NONE);

        rightArm.addChild(RIGHT_SLEEVE, ModelPartBuilder.create()
                .uv(40, 32)
                .cuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.add(0.25f)),
                ModelTransform.NONE);

        ModelPartData leftLeg = modelRoot.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create()
                .uv(16, 48)
                .cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation),
                ModelTransform.origin(1.9f, 12.0f, 0.0f));

        ModelPartData rightLeg = modelRoot.getChild(EntityModelPartNames.RIGHT_LEG);

        leftLeg.addChild(LEFT_PANTS, ModelPartBuilder.create()
                .uv(0, 48)
                .cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)),
                ModelTransform.NONE);

        rightLeg.addChild(RIGHT_PANTS, ModelPartBuilder.create()
                .uv(0, 32)
                .cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)),
                ModelTransform.NONE);

        ModelPartData body = modelRoot.getChild(EntityModelPartNames.BODY);

        body.addChild(EntityModelPartNames.JACKET, ModelPartBuilder.create()
                .uv(16, 32)
                .cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation.add(0.25f)),
                ModelTransform.NONE);

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }
}
