package com.dayofpi.lucys_sloths.client;
// Made with Blockbench 4.2.4

import com.dayofpi.lucys_sloths.common.SlothEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SlothModel<T extends SlothEntity> extends AnimalModel<T> {
	private final ModelPart BODY;
	private final ModelPart HEAD;
	private final ModelPart SNOUT;
	private final ModelPart LEFT_ARM;
	private final ModelPart RIGHT_ARM;
	private final ModelPart LEFT_LEG;
	private final ModelPart RIGHT_LEG;

	public SlothModel(ModelPart modelPart) {
		super(true, 15.0F, 2.0F);
		ModelPart ROOT = modelPart.getChild("root");
		ModelPart BODY_ROOT = ROOT.getChild("body_root");
		this.BODY = BODY_ROOT.getChild("body");
		this.HEAD = BODY_ROOT.getChild("head");
		this.SNOUT = HEAD.getChild("snout");
		this.LEFT_ARM = BODY_ROOT.getChild("left_arm");
		this.RIGHT_ARM = BODY_ROOT.getChild("right_arm");
		this.LEFT_LEG = ROOT.getChild("left_leg");
		this.RIGHT_LEG = ROOT.getChild("right_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData left_leg = root.addChild("left_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-3.5F, -2.0F, 2.5F, 5.0F, 4.0F, 6.0F), ModelTransform.pivot(3.5F, -2.0F, 5.5F));
		left_leg.addChild("left_toes", ModelPartBuilder.create().uv(0, 5).cuboid(13.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F), ModelTransform.of(-3.5F, 0.0F, -5.5F, -1.5708F, -1.5708F, 0.0F));

		ModelPartData right_leg = root.addChild("right_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-3.5F, -2.0F, 2.5F, 5.0F, 4.0F, 6.0F), ModelTransform.pivot(-1.5F, -2.0F, 5.5F));
		right_leg.addChild("right_toes", ModelPartBuilder.create().uv(0, 5).cuboid(13.0F, -5.0F, -2.0F, 3.0F, 5.0F, 0.0F), ModelTransform.of(1.5F, 2.0F, -5.5F, -1.5708F, -1.5708F, 0.0F));

		ModelPartData body_root = root.addChild("body_root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -3.0F, 7.0F));

		body_root.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -3.0F, -11.0F, 10.0F, 6.0F, 12.0F), ModelTransform.pivot(0.0F, -3.0F, 7.0F));

		ModelPartData right_arm = body_root.addChild("right_arm", ModelPartBuilder.create().uv(30, 23).mirrored().cuboid(-7.75F, -2.0F, -2.5F, 10.0F, 4.0F, 5.0F).mirrored(false), ModelTransform.pivot(-7.25F, -2.0F, -2.5F));
		right_arm.addChild("right_digits", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F).mirrored(false), ModelTransform.of(-9.25F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		ModelPartData left_arm = body_root.addChild("left_arm", ModelPartBuilder.create().uv(30, 23).cuboid(-2.25F, -2.0F, -2.5F, 10.0F, 4.0F, 5.0F), ModelTransform.pivot(7.25F, -2.0F, -2.5F));
		left_arm.addChild("left_digits", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F), ModelTransform.of(9.25F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		ModelPartData head = body_root.addChild("head", ModelPartBuilder.create().uv(0, 18).cuboid(-4.0F, -4.5F, -6.5F, 8.0F, 7.0F, 7.0F), ModelTransform.pivot(0.0F, -2.5F, -4.5F));
		head.addChild("snout", ModelPartBuilder.create().uv(54, 0).cuboid(-2.0F, 0.5F, -7.5F, 4.0F, 2.0F, 1.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
		this.SNOUT.visible = entity.getSlothType().equals(SlothEntity.TWO_TOED);
		this.HEAD.pitch = headPitch * 0.017453292F;
		this.HEAD.yaw = headYaw * 0.017453292F;
		float limbAngle  = 0.0F;
		if (entity.isRidingPlayer()) {
			limbAngle = -1.7452F;
		}
		this.RIGHT_ARM.roll = limbAngle;
		this.LEFT_ARM.roll = -limbAngle;
		this.RIGHT_ARM.pitch = limbAngle / 2;
		this.LEFT_ARM.pitch = limbAngle / 2;
		this.RIGHT_LEG.pitch = limbAngle / 2;
		this.LEFT_LEG.pitch = limbAngle / 2;
		this.RIGHT_ARM.yaw = (MathHelper.cos(limbSwing * -0.6662F) * limbSwingAmount * 7F) - 0.8726F;
		this.LEFT_ARM.yaw = (MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * 7F) + 0.8726F;
		this.RIGHT_LEG.yaw = (MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount) - 0.4363F;
		this.LEFT_LEG.yaw = (MathHelper.cos(limbSwing * 0.6662F + MathHelper.PI) * limbSwingAmount) + 0.4363F;
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.of(this.HEAD);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.of(this.BODY, this.LEFT_LEG, this.RIGHT_LEG, this.LEFT_ARM, this.RIGHT_ARM);
	}

	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (this.child)
			matrices.translate(0, 0.75D, 0);
		else matrices.translate(0.0D, 1.5D, 0.0D);
		super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}