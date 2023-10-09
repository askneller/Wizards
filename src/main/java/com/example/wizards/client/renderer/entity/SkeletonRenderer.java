package com.example.wizards.client.renderer.entity;

import com.example.wizards.SummonedSkeleton;
//import net.minecraft.client.model.SkeletonModel;
import com.example.wizards.client.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
//import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonRenderer extends HumanoidMobRenderer<SummonedSkeleton, SkeletonModel<SummonedSkeleton>> {
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRendererProvider.Context p_174380_) {
      this(p_174380_, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public SkeletonRenderer(EntityRendererProvider.Context p_174382_, ModelLayerLocation p_174383_, ModelLayerLocation p_174384_, ModelLayerLocation p_174385_) {
      super(p_174382_, new SkeletonModel<>(p_174382_.bakeLayer(p_174383_)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(p_174382_.bakeLayer(p_174384_)), new SkeletonModel(p_174382_.bakeLayer(p_174385_)), p_174382_.getModelManager()));
   }

   public ResourceLocation getTextureLocation(SummonedSkeleton p_115941_) {
      return SKELETON_LOCATION;
   }

   protected boolean isShaking(SummonedSkeleton p_174389_) {
      return false;
   }
}
