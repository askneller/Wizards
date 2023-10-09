package com.example.wizards.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class MobRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
   public static final int LEASH_RENDER_STEPS = 24;

   public MobRenderer(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
      super(p_174304_, p_174305_, p_174306_);
   }

   protected boolean shouldShowName(T p_115506_) {
      return super.shouldShowName(p_115506_) && (p_115506_.shouldShowName() || p_115506_.hasCustomName() && p_115506_ == this.entityRenderDispatcher.crosshairPickEntity);
   }

   public boolean shouldRender(T p_115468_, Frustum p_115469_, double p_115470_, double p_115471_, double p_115472_) {
        return super.shouldRender(p_115468_, p_115469_, p_115470_, p_115471_, p_115472_);
   }

   public void render(T p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {
      super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
   }

}
