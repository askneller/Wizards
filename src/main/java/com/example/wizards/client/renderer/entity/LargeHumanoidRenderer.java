package com.example.wizards.client.renderer.entity;

import com.example.wizards.LargeHumanoid;
import com.example.wizards.client.LargeHumanoidModel;
import com.example.wizards.client.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.example.wizards.Wizards.MOD_ID;

public class LargeHumanoidRenderer extends MobRenderer<LargeHumanoid, LargeHumanoidModel<LargeHumanoid>> {

    public LargeHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context, new LargeHumanoidModel<>(context.bakeLayer(ModModelLayers.LARGE_HUMANOID_LAYER)), 2f);
    }

    @Override
    public ResourceLocation getTextureLocation(LargeHumanoid p_114482_) {
        return new ResourceLocation(MOD_ID, "textures/entity/large_humanoid.png");
    }

}
