package com.example.wizards;

import com.example.wizards.client.ClientManaPool;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
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


            HitResult hitResult = Minecraft.getInstance().hitResult;
            logger.info("Hit {} {}", hitResult.getType(), hitResult.getType() != HitResult.Type.MISS ? hitResult.getLocation() : null);
            BlockPos pos = null;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult result = (BlockHitResult) hitResult;
                logger.info("Block {} {}", result.getBlockPos(), result.getDirection());
                pos = result.getBlockPos();
            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult result = (EntityHitResult) hitResult;
                logger.info("Entity {}", result.getEntity());
            }

            BlockPos finalPos = pos;
            player.getCapability(MANA_POOL).ifPresent(pool -> {
                logger.info("Client Pool cap present: {}", pool);
                logger.info("ClientManaPool: {}", ClientManaPool.getPlayerPool());

                if (finalPos == null) {
                    PacketHandler.sendToServer(player, 1);
                } else {
                    PacketHandler.sendToServer(player, 3, finalPos);
                }
            });
        }
    }
}
