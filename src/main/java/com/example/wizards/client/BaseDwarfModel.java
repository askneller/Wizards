package com.example.wizards.client;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.example.wizards.entity.BaseDwarf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class BaseDwarfModel<T extends BaseDwarf> extends HierarchicalModel<T> implements ArmedModel {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightArm;

	public BaseDwarfModel(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("body").getChild("head");
		this.leftArm = this.root.getChild("body").getChild("left_arm");
		this.rightArm = this.root.getChild("body").getChild("right_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.0F, -2.5F, 10.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.5F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(46, 29).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 4.5F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -6.0F, 0.0F));

		PartDefinition left_arm_lower = left_arm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(45, 49).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 3.5F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(12, 50).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 4.5F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -6.0F, 0.0F));

		PartDefinition right_arm_lower = right_arm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create(), PartPose.offset(-1.0F, 3.5F, 0.0F));

		PartDefinition right_arm_lower_r1 = right_arm_lower.addOrReplaceChild("right_arm_lower_r1", CubeListBuilder.create().texOffs(34, 17).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition shield = right_arm_lower.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(45, 0).addBox(-4.0F, -6.5F, -5.0F, 8.0F, 11.0F, 0.25F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 33).addBox(-1.5F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -9.0F, 0.5F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(2, 33).addBox(-2.5F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -9.0F, 0.5F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

		this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

		this.animateWalk(BaseDwarfAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);

		this.animate(entity.idleAnimationState, BaseDwarfAnimations.IDLE, ageInTicks, 1.0f);
		this.animate(entity.attackAnimationState, BaseDwarfAnimations.LEFT_ARM_SWING, ageInTicks, 1.0f);
	}

	private void applyHeadRotation(float netHeadYaw, float headPitch, float ageInTicks) {
		netHeadYaw = Mth.clamp(netHeadYaw, -30.0f, 30.0f);
		headPitch = Mth.clamp(headPitch, -25.0f, 45.0f);

		this.head.yRot = netHeadYaw * ((float) Math.PI / 180f);
		this.head.xRot = headPitch * ((float) Math.PI / 180f);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return root;
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
		ModelPart modelPart = this.getArm(arm);
		if (modelPart.equals(rightArm)) {
			modelPart.translateAndRotate(poseStack);
		} else {
			modelPart.y += 12.0f;
			modelPart.translateAndRotate(poseStack);
			modelPart.y -= 12.0f;
		}
	}

	protected ModelPart getArm(HumanoidArm arm) {
		return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}

}
