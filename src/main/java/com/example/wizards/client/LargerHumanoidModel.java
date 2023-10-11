package com.example.wizards.client;
// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class LargerHumanoidModel<T extends Entity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
//	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "baseorc"), "main");

	private final ModelPart orc;
	private final ModelPart head;

	public LargerHumanoidModel(ModelPart root) {
		this.orc = root.getChild("root");
		this.head = this.orc.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition orc = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition body = orc.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20).addBox(-5.5F, -8.0F, -2.0F, 11.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition head = orc.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -32.0F, 0.5F));

		PartDefinition left_arm = orc.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 43).addBox(-1.5F, -1.0F, -2.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -31.0F, 0.5F));

		PartDefinition right_arm = orc.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(20, 43).addBox(-3.5F, -1.0F, -2.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, -31.0F, 0.5F));

		PartDefinition left_leg = orc.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 20).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, 0.0F));

		PartDefinition right_leg = orc.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 43).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);

		this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

//		this.animateWalk(BaseHumanAnimations.BASE_HUMAN_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
//		this.animate(entity.idleAnimationState, BaseHumanAnimations.BASE_HUMAN_IDLE, ageInTicks, 1.0f);
	}

	private void applyHeadRotation(float netHeadYaw, float headPitch, float ageInTicks) {
		netHeadYaw = Mth.clamp(netHeadYaw, -30.0f, 30.0f);
		headPitch = Mth.clamp(headPitch, -25.0f, 45.0f);

		this.head.yRot = netHeadYaw * ((float) Math.PI / 180f);
		this.head.xRot = headPitch * ((float) Math.PI / 180f);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		orc.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.orc;
	}
}