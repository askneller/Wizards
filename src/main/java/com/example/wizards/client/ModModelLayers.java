package com.example.wizards.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static com.example.wizards.Wizards.MOD_ID;

public class ModModelLayers {

    public static final ModelLayerLocation BASE_HUMAN_LAYER = new ModelLayerLocation(
            new ResourceLocation(MOD_ID, "base_human_layer"), "main");

    public static final ModelLayerLocation LARGER_HUMANOID_LAYER = new ModelLayerLocation(
            new ResourceLocation(MOD_ID, "larger_humanoid_layer"), "main");

    public static final ModelLayerLocation LARGE_HUMANOID_LAYER = new ModelLayerLocation(
            new ResourceLocation(MOD_ID, "large_humanoid_layer"), "main");

    public static final ModelLayerLocation ORC_LAYER = new ModelLayerLocation(
            new ResourceLocation(MOD_ID, "orc_layer"), "main");

    public static final ModelLayerLocation BASE_DWARF_LAYER = new ModelLayerLocation(
            new ResourceLocation(MOD_ID, "base_dwarf_layer"), "main");

}
