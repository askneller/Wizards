package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.example.wizards.ModBlocksAndItems.BLOCKS;
import static com.example.wizards.ModBlocksAndItems.BLOCK_ENTITY_TYPES;
import static com.example.wizards.ModBlocksAndItems.ITEMS;

@Mod(Wizards.MOD_ID)
public class Wizards {

    public static final String MOD_ID = "wizards";
    public static final String MOD_VERSION = "${version}";
    private static final Logger logger = LogUtils.getLogger();


    public Wizards()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(CastingSystem.class);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);

        // Register the item to a creative tab
//        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        PacketHandler.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        logger.info("HELLO FROM COMMON SETUP");
    }

}
