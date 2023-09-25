package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

public class CastingSystem {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onAttemptCast(AttemptCastEvent event) {
        Player player = event.getPlayer();
        logger.info("Player {} trying to cast spell {}", player, event.getSpell());
        PlayerThirst playerThirst = player.getCapability(PlayerThirstProvider.PLAYER_THIRST)
                .orElseGet(() -> new PlayerThirst(0));
        logger.info("Player thirst {}", playerThirst);

        if (playerThirst.getThirst() == 0) {
            logger.info("PLAYER HAS NO THIRST!");
            event.setResult(Event.Result.DENY);
        } else {
            Vec3 lookAngle = player.getLookAngle();
            logger.info("look {}, mag {}", lookAngle, lookAngle.length());
            SmallFireball smallfireball = new SmallFireball(
                    player.level(),
                    player,
                    // impulse
                    lookAngle.x, //entity.getRandom().triangle(d1, 2.297D * d4),
                    lookAngle.y, //d2,
                    lookAngle.z); //entity.getRandom().triangle(d3, 2.297D * d4));
            logger.info("smf {}", smallfireball);
            smallfireball.setPos(smallfireball.getX(), player.getY(0.5D) + 0.5D, smallfireball.getZ());
            logger.info("smf pos {} {} {}", smallfireball.getX(), smallfireball.getY(), smallfireball.getZ());
            player.level().addFreshEntity(smallfireball);
            logger.info("Cost 1 thirst");
            playerThirst.subThirst(1);
            if (player instanceof ServerPlayer) {
                logger.info("Sending to server player: {}", playerThirst.getThirst());
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new ThirstDataSyncS2CPacket(playerThirst.getThirst()));
            }
        }
    }

    @SubscribeEvent
    public static void onManaRegenerate(ManaRegenerateEvent event) {
        LivingEntity owner = event.getOwner();
        logger.info("Entity {} regenerated mana: {}", owner, event.getAmount());
        if (owner instanceof Player player) {
            PlayerThirst playerThirst = player.getCapability(PlayerThirstProvider.PLAYER_THIRST)
                    .orElseGet(() -> new PlayerThirst(0));
            logger.info("Player thirst {}", playerThirst);

//            if (playerThirst.getThirst() == 0) {
//                logger.info("PLAYER HAS NO THIRST!");
//                event.setResult(Event.Result.DENY);
//            } else {
                logger.info("Regen {} mana / thirst", event.getAmount());
                playerThirst.addThirst(event.getAmount());
                if (player instanceof ServerPlayer) {
                    logger.info("Sending to server player: {}", playerThirst.getThirst());
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new ThirstDataSyncS2CPacket(playerThirst.getThirst()));
                }
//            }
        }
    }
}
