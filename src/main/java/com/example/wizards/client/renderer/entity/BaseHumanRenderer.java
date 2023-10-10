package com.example.wizards.client.renderer.entity;

import com.example.wizards.BaseHuman;
import com.example.wizards.client.BaseHumanModel;
import com.example.wizards.client.ModModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.example.wizards.Wizards.MOD_ID;

public class BaseHumanRenderer extends MobRenderer<BaseHuman, BaseHumanModel<BaseHuman>> {

    public BaseHumanRenderer(EntityRendererProvider.Context context) {
        super(context, new BaseHumanModel<>(context.bakeLayer(ModModelLayers.BASE_HUMAN_LAYER)), 2f);
    }

    @Override
    public ResourceLocation getTextureLocation(BaseHuman p_114482_) {
        return new ResourceLocation(MOD_ID, "textures/entity/base_human.png");
    }

}
