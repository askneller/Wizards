package com.example.wizards;

import com.example.wizards.client.ClientManaPool;
import com.example.wizards.client.ClientSpellList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
    public static void onMouseWheel(InputEvent.MouseScrollingEvent event) {
//        logger.info("scrolling {}", event.getScrollDelta());
        // delta either -1 or 1
//        logger.info("{}", ClientSpellList.keyDown);
        if (ClientSpellList.keyDown) {
            if (event.getScrollDelta() > 0) {
                ClientSpellList.inc();
            } else if (event.getScrollDelta() < 0) {
                ClientSpellList.dec();
            }
//            logger.info("{} cancelling", ClientSpellList.keyDown);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == InputConstants.KEY_LALT &&
                (event.getAction() == InputConstants.REPEAT || event.getAction() == InputConstants.PRESS)) {
//            logger.info("Down LALT");
            ClientSpellList.keyDown = true;
        }
        if (event.getKey() == InputConstants.KEY_LALT && event.getAction() == InputConstants.RELEASE) {
//            logger.info("Release LALT");
            ClientSpellList.keyDown = false;
        }
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

                int spellNumber = ClientSpellList.getSelected();
                if (finalPos == null && spellNumber > 2) {
                    player.sendSystemMessage(Component.literal("Cannot cast spell: No target position").withStyle(ChatFormatting.RED));
                } else {
                    if (finalPos == null) {
                        PacketHandler.sendToServer(player, spellNumber);
                    } else {
                        PacketHandler.sendToServer(player, spellNumber, finalPos);
                    }
                }
            });
        }
    }

    public static void makeParticles(Level level, Player player, BlockPos blockPos) {
//        logger.info("====== Particles at {}", blockPos);
        RandomSource randomsource = level.getRandom();

        // TODO change color or particles (something purple-y)
        // TODO try to find Zombie death particle spawn code
        for (int i = 0; i < 20; i++) {
            double rx = (randomsource.nextDouble() * 1.5 - 0.75);
            double rz = (randomsource.nextDouble() * 1.5 - 0.75);
            level.addParticle(ParticleTypes.EFFECT, false,
                    // position
                    blockPos.getX() + 0.5 + rx,
                    blockPos.getY() + 1.0,
                    blockPos.getZ() + 0.5 + rz,
                    // impulse
                    0.0,
                    0.5,
                    0.0);
        }
    }
}
