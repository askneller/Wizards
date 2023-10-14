package com.example.wizards.client.renderer.entity;

import com.example.wizards.client.BaseDwarfModel;
import com.example.wizards.client.ModModelLayers;
import com.example.wizards.entity.BaseDwarf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import static com.example.wizards.Wizards.MOD_ID;

public class BaseDwarfRenderer extends MobRenderer<BaseDwarf, BaseDwarfModel<BaseDwarf>> {

    public BaseDwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new BaseDwarfModel<>(context.bakeLayer(ModModelLayers.BASE_DWARF_LAYER)), 0.7f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(BaseDwarf p_114482_) {
        return new ResourceLocation(MOD_ID, "textures/entity/dwarf_axeman.png");
    }

}
