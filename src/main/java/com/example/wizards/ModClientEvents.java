package com.example.wizards;

import com.example.wizards.client.BaseHumanModel;
import com.example.wizards.client.LargerHumanoidModel;
import com.example.wizards.client.LargeHumanoidModel;
import com.example.wizards.client.ManaOverlay;
import com.example.wizards.client.ModModelLayers;
import com.example.wizards.client.renderer.entity.BaseHumanRenderer;
import com.example.wizards.client.renderer.entity.LargerHumanoidRenderer;
import com.example.wizards.client.renderer.entity.LargeHumanoidRenderer;
import com.example.wizards.client.renderer.entity.OrcRenderer;
import com.example.wizards.client.renderer.entity.SkeletonRenderer;
import com.example.wizards.client.renderer.entity.SummonedSlimeRenderer;
import com.example.wizards.client.renderer.entity.SummonedZombieRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import static com.example.wizards.entity.ModEntities.LARGER_HUMANOID;
import static com.example.wizards.entity.ModEntities.HUMAN;
import static com.example.wizards.entity.ModEntities.LARGE_HUMANOID;
import static com.example.wizards.entity.ModEntities.ORC;
import static com.example.wizards.entity.ModEntities.SUMMONED_SKELETON_ARCHER;
import static com.example.wizards.entity.ModEntities.SUMMONED_SLIME;
import static com.example.wizards.entity.ModEntities.SUMMONED_POLAR_BEAR;
import static com.example.wizards.entity.ModEntities.SUMMONED_SKELETON;
import static com.example.wizards.entity.ModEntities.SUMMONED_SPIDER;
import static com.example.wizards.entity.ModEntities.SUMMONED_ZOMBIE;
import static com.example.wizards.Wizards.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        // Some client setup code
        logger.info("HELLO FROM CLIENT SETUP");
        logger.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        logger.info("Registering renderers");
        event.registerEntityRenderer(SUMMONED_ZOMBIE, SummonedZombieRenderer::new);
        event.registerEntityRenderer(SUMMONED_SKELETON, SkeletonRenderer::new);
        event.registerEntityRenderer(SUMMONED_SKELETON_ARCHER, SkeletonRenderer::new);
        event.registerEntityRenderer(SUMMONED_SPIDER, SpiderRenderer::new);
        event.registerEntityRenderer(SUMMONED_POLAR_BEAR, PolarBearRenderer::new);
        event.registerEntityRenderer(SUMMONED_SLIME, SummonedSlimeRenderer::new);
        event.registerEntityRenderer(HUMAN, BaseHumanRenderer::new);
        event.registerEntityRenderer(LARGER_HUMANOID, LargerHumanoidRenderer::new);
        event.registerEntityRenderer(LARGE_HUMANOID, LargeHumanoidRenderer::new);
        event.registerEntityRenderer(ORC, OrcRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.BASE_HUMAN_LAYER, BaseHumanModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.LARGER_HUMANOID_LAYER, LargerHumanoidModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.LARGE_HUMANOID_LAYER, LargeHumanoidModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ORC_LAYER, LargeHumanoidModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("mana", ManaOverlay.GUI_OVERLAY);
    }
}
