package com.example.wizards.client.renderer.entity;

import com.example.wizards.LargerHumanoid;
import com.example.wizards.client.LargerHumanoidModel;
import com.example.wizards.client.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.example.wizards.Wizards.MOD_ID;

public class LargerHumanoidRenderer extends MobRenderer<LargerHumanoid, LargerHumanoidModel<LargerHumanoid>> {

    public LargerHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context, new LargerHumanoidModel<>(context.bakeLayer(ModModelLayers.LARGER_HUMANOID_LAYER)), 2f);
    }

    @Override
    public ResourceLocation getTextureLocation(LargerHumanoid p_114482_) {
        return new ResourceLocation(MOD_ID, "textures/entity/larger_humanoid.png");
    }

}
