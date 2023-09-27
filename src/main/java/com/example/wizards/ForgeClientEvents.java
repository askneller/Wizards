package com.example.wizards;

import com.example.examplemod.PacketHandler;
import com.example.examplemod.TestPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;
import static com.example.wizards.Wizards.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event)
    {
        // Some client setup code
        if (event.getKey() == InputConstants.KEY_R && event.getAction() == InputConstants.PRESS) {
            logger.info("onKeyPress {}, {}, {}", event.getClass(), event.getKey(), event.getAction());
            LocalPlayer player = Minecraft.getInstance().player;
            logger.info("Player: {}", player);
            assert player != null;
            player.getCapability(MANA_POOL).ifPresent(pool -> {
                logger.info("Pool present: {}", pool);
            });
        }
    }
}
