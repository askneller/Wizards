package com.example.wizards;

import com.example.wizards.block.ManaTotemBlock;
import com.example.wizards.block.ModBlocksAndItems;
import com.example.wizards.magic.ManaPoolProvider;
import com.example.wizards.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.slf4j.Logger;

import static com.example.wizards.magic.ManaPoolProvider.MANA_POOL;
import static com.example.wizards.Wizards.MOD_ID;

public class ModEvents {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        logger.info("HELLO from server starting");
    }

    @SubscribeEvent // TODO Subscribing is not working, method not getting called
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        logger.info("Registering capability2");
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(MANA_POOL).isPresent()) {
                logger.info("Attaching ManaPool capability");
                event.addCapability(new ResourceLocation(MOD_ID, "properties"), new ManaPoolProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            if (event.player instanceof ServerPlayer serverPlayer) {
                Inventory inventory = serverPlayer.getInventory();
                if (inventory.isEmpty()) {
                    ItemStack stack = new ItemStack(ModBlocksAndItems.MANA_TOTEM_BLOCK_ITEM.get(), 3);
                    inventory.add(stack);
                }
            }
//            event.player.getCapability(MANA_POOL).ifPresent(pool -> {
//                if (pool.isEmpty()) {
//                    logger.info("Player ManaPool empty: {}", pool);
//                    addDefaultMana(pool);
//                    logger.info("Added source/s: pool {}", pool);
//                    if (event.player instanceof ServerPlayer) {
//                        logger.info("Sending new pool to client player: {}", pool);
//                        PacketHandler.sendToPlayer((ServerPlayer) event.player, pool);
//                    }
//                }
//                if (event.player.level().getGameTime() % 300 == 0) logger.info("Pool: {}", pool);
//            });
        }
    }

//    private static void addDefaultMana(ManaPool pool) {
//        ManaSource source = new ManaSource(0, 1, ManaColor.COLORLESS, true);
//        pool.addSource(source);
//        source = new ManaSource(0, 1, ManaColor.RED, true);
//        pool.addSource(source);
//        source = new ManaSource(0, 1, ManaColor.WHITE, true);
//        pool.addSource(source);
//    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide) {
            if (event.getEntity() instanceof ServerPlayer player) {
                logger.info("Server player join. Checking for mana: {}", player.getCapability(MANA_POOL).isPresent());
                player.getCapability(MANA_POOL).ifPresent(pool -> {
                    logger.info("OnJoinLevelEvent Sending to client player: {}", pool);
                    PacketHandler.sendToPlayer(player, pool);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof ManaTotemBlock) {
            logger.info("ManaTotemBlock BreakEvent at {}", event.getPos());

            // TODO turn into an event
            Player player = event.getPlayer();
            Level level = player.level();
            if (!level.isClientSide) {
                level.getServer().getPlayerList().getPlayers().forEach(p -> {
                    p.getCapability(MANA_POOL).ifPresent(pool -> {
                        boolean removed = pool.removeSource(event.getPos());
                        if (removed) {
                            logger.info("Source found and removed from player {}", p);
                            if (player instanceof ServerPlayer serverPlayer) {
                                PacketHandler.sendToPlayer(serverPlayer, pool);
                                logger.info("Updated pool: {}", pool);
                            }
                            sendDestroyMessage(p, event.getPlayer());
                        }
                    });
                });
            }
        }
    }

    private static void sendDestroyMessage(Player owner, Player destroyer) {
        MutableComponent message = Component.literal(destroyer.getName().getString()).withStyle(ChatFormatting.RED);
        message.append(Component.literal(" destroyed your mana totem").withStyle(ChatFormatting.WHITE));
        owner.sendSystemMessage(message);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        logger.info("LivingDamage: {} to {} from {}", event.getAmount(), event.getEntity(), event.getSource());
    }
}
