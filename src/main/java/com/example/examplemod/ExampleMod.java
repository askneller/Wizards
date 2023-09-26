package com.example.examplemod;

import com.example.examplemod.client.ManaOverlay;
import com.example.examplemod.client.renderer.entity.ManaTotemRenderer;
import com.example.wizards.ManaTotemBlock;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
//@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "examplemod";
    public static final String MOD_VERSION = "${version}";
    // Directly reference a slf4j logger
    private static final Logger logger = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> MANA_TOTEM_BLOCK = BLOCKS.register("mana_totem", () -> new ManaTotemBlock(
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel((p_50755_) -> { return 14; })
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY),
            ParticleTypes.FLAME));

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> MANA_TOTEM_BLOCK_ITEM = ITEMS.register("mana_totem", () -> new StandingAndWallBlockItem(MANA_TOTEM_BLOCK.get(), Blocks.WALL_TORCH, new Item.Properties(), Direction.DOWN));

    public static final RegistryObject<BlockEntityType<ManaTotemBlockEntity>> MANA_TOTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("mana_totem_entity",
            () -> BlockEntityType.Builder.of(ManaTotemBlockEntity::new, MANA_TOTEM_BLOCK.get()).build(null));

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    public ExampleMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(CastingSystem.class);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        PacketHandler.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        logger.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            logger.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        logger.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> logger.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        logger.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player) {
            LivingEntity entity = event.getEntity();
            Item item = event.getItem().getItem();
            logger.info("LivingEntityUseItemEvent.Start: item {}, duration {}",
                    event.getItem().getItem(), event.getDuration());
            if (item instanceof TridentItem) {
//                Vec3 lookAngle = entity.getLookAngle();
//                logger.info("look {}, mag {}", lookAngle, lookAngle.length());
//                SmallFireball smallfireball = new SmallFireball(
//                        entity.level(),
//                        entity,
//                        // impulse
//                        lookAngle.x, //entity.getRandom().triangle(d1, 2.297D * d4),
//                        lookAngle.y, //d2,
//                        lookAngle.z); //entity.getRandom().triangle(d3, 2.297D * d4));
//                logger.info("smf {}", smallfireball);
//                smallfireball.setPos(smallfireball.getX(), entity.getY(0.5D) + 0.5D, smallfireball.getZ());
//                logger.info("smf pos {} {} {}", smallfireball.getX(), smallfireball.getY(), smallfireball.getZ());
//                entity.level().addFreshEntity(smallfireball);

                HitResult hitResult = entity.pick(20.0, 0.0f, false);
                logger.info("Hit, type {}, location {}", hitResult.getType(), hitResult.getLocation());
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    BlockState blockState = entity.level().getBlockState(blockPos);
                    logger.info("State at {}: {}", blockPos, blockState);
                    Zombie zombie = new Zombie(EntityType.ZOMBIE, entity.level());
                    zombie.setPos(blockPos.getX(), blockPos.getY() + 2, blockPos.getZ());
                    logger.info("Adding zombie: {}", zombie);
                    entity.level().addFreshEntity(zombie);
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerThirstProvider.PLAYER_THIRST).isPresent()) {
                logger.info("Attaching capability");
                event.addCapability(new ResourceLocation(ExampleMod.MODID, "properties"), new PlayerThirstProvider());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        logger.info("Registering capability");
        event.register(PlayerThirst.class);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
//            logger.info("Checking for thirst: {}", event.player.getCapability(PlayerThirstProvider.PLAYER_THIRST).isPresent());
            event.player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                if (thirst.getThirst() > 0) {
                    if (event.player.getRandom().nextFloat() < 0.00005f) {
                        logger.info("Has thirst: {}, subtracting",
                                event.player.getCapability(PlayerThirstProvider.PLAYER_THIRST).orElse(new PlayerThirst()));
                        thirst.subThirst(1);
//                        event.player.sendSystemMessage(Component.literal("Subtracted Thirst"));
                        if (event.player instanceof ServerPlayer) {
                            logger.info("Sending to server player: {}", thirst.getThirst());
                            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.player),
                                    new ThirstDataSyncS2CPacket(thirst.getThirst()));
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) {
//            logger.info("Checking for thirst: {}", event.player.getCapability(PlayerThirstProvider.PLAYER_THIRST).isPresent());
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                    logger.info("OnJoinLevelEvent Sending to server player: {}", thirst.getThirst());
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new ThirstDataSyncS2CPacket(thirst.getThirst()));
                });
            }
        }
    }

    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().is(MANA_TOTEM_BLOCK.get())) {
            logger.info("Placed {}", event.getPlacedBlock());
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            System.out.println("\n\nTrace");
            int len = 15;
            if (stackTrace.length < len)
                len = stackTrace.length;
            for (int i = 0; i < len; i++) {
                System.out.println(stackTrace[i]);
            }
            System.out.println("\n\n");
        }
    }



    // ======================================================================================
    // ======================================================================================
    // ======================================================================================
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            logger.info("HELLO FROM CLIENT SETUP");
            logger.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
        {
            // Some client setup code
            logger.info("RegisterKeyMappingsEvent");
            KeyMapping mapping = new KeyMapping("Wizards cast", InputConstants.Type.KEYSYM, InputConstants.KEY_R, KeyMapping.CATEGORY_GAMEPLAY);
            logger.info("Registering {}", mapping);
            event.register(mapping);
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            logger.info("Registering renderers");
            event.registerBlockEntityRenderer(MANA_TOTEM_BLOCK_ENTITY.get(), ManaTotemRenderer::new);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("mana", ManaOverlay.GUI_OVERLAY);
        }
    }
    // ======================================================================================
    // ======================================================================================
    // ======================================================================================



    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeModEvents {

        // ScreenEvent is called when a screen is active, like inventory
        /*
        @SubscribeEvent
        public static void onKeyPressedPost(ScreenEvent.KeyPressed event)
        {
            // Some client setup code
            logger.info("onKeyPressedPost {}, {}", event.getClass(), event.getKeyCode());
            if (event instanceof ScreenEvent.KeyPressed.Post) {
                logger.info("Is Post!");
            }
        }

        @SubscribeEvent
        public static void onKeyPressedPre(ScreenEvent.KeyPressed.Pre event)
        {
            // Some client setup code
            logger.info("onKeyPressedPre {}, {}", event.getClass(), event.getKeyCode());
        }

        @SubscribeEvent
        public static void onKeyPressedPost(ScreenEvent.KeyPressed.Post event)
        {
            // Some client setup code
            logger.info("onKeyPressedPost2 {}, {}", event.getClass(), event.getKeyCode());
        }
        */

        @SubscribeEvent
        public static void onKeyPress(InputEvent.Key event)
        {
            // Some client setup code
            if (event.getKey() == InputConstants.KEY_R && event.getAction() == InputConstants.PRESS) {
                logger.info("onKeyPress {}, {}, {}", event.getClass(), event.getKey(), event.getAction());
                PacketHandler.INSTANCE.sendToServer(new TestPacket("R"));
            }
        }
    }
}
