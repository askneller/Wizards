package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import org.slf4j.Logger;

public class TestClass {

    private static final Logger logger = LogUtils.getLogger();

    public static void logMessage(String message, Player player) {
        logger.info("Received message: {}", message);
        logger.info("From: {} (is client {})", player, player.level().isClientSide);
        AttemptCastEvent event = new AttemptCastEvent(1, player);
        MinecraftForge.EVENT_BUS.post(event);
        logger.info("AttemptCastEvent result: {}", event.getResult());
    }
}
