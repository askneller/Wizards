package com.example.wizards;

import com.example.examplemod.PacketHandler;
import com.example.examplemod.PlayerThirst;
import com.example.examplemod.PlayerThirstProvider;
import com.example.examplemod.ThirstDataSyncS2CPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;
import static com.example.wizards.Wizards.MOD_ID;

public class ModEvents {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event)
    {
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
            event.player.getCapability(MANA_POOL).ifPresent(pool -> {
                if (pool.getSources().isEmpty()) {
                    logger.info("Player ManaPool empty: {}", pool);
                    ManaSource source = new ManaSource(0, 1, "generic");
                    logger.info("Adding ManaSource {}", source);
                    pool.addSource(source);
                    logger.info("Added source: pool {}", pool);
//                    if (event.player instanceof ServerPlayer) {
//                        logger.info("Sending to server player: {}", pool.getThirst());
//                        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.player),
//                                new ThirstDataSyncS2CPacket(pool.getThirst()));
//                    }

                }
                if (event.player.level().getGameTime() % 300 == 0) logger.info("Pool: {}", pool);
            });
        }
    }

}
