package com.example.wizards.client.renderer.entity;

import com.example.wizards.Orc;
import com.example.wizards.client.LargeHumanoidModel;
import com.example.wizards.client.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.example.wizards.Wizards.MOD_ID;

public class OrcRenderer extends MobRenderer<Orc, LargeHumanoidModel<Orc>> {

    public OrcRenderer(EntityRendererProvider.Context context) {
        super(context, new LargeHumanoidModel<>(context.bakeLayer(ModModelLayers.ORC_LAYER)), 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(Orc p_114482_) {
        return new ResourceLocation(MOD_ID, "textures/entity/orc.png");
    }

}
