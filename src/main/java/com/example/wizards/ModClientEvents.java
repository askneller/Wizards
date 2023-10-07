package com.example.wizards;

import com.example.wizards.client.ManaOverlay;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import static com.example.wizards.ModEntities.SUMMONED_POLAR_BEAR;
import static com.example.wizards.ModEntities.SUMMONED_SKELETON;
import static com.example.wizards.ModEntities.SUMMONED_SKELETON_ARCHER;
import static com.example.wizards.ModEntities.SUMMONED_SPIDER;
import static com.example.wizards.ModEntities.SUMMONED_ZOMBIE;
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
        event.registerEntityRenderer(SUMMONED_ZOMBIE, ZombieRenderer::new);
        event.registerEntityRenderer(SUMMONED_SKELETON, SkeletonRenderer::new);
        event.registerEntityRenderer(SUMMONED_SKELETON_ARCHER, SkeletonRenderer::new);
        event.registerEntityRenderer(SUMMONED_SPIDER, SpiderRenderer::new);
        event.registerEntityRenderer(SUMMONED_POLAR_BEAR, PolarBearRenderer::new);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("mana", ManaOverlay.GUI_OVERLAY);
    }
}
