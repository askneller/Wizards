package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;

public class CastingSystem {

    private static final Logger logger = LogUtils.getLogger();

//    @SubscribeEvent
//    public static void onAttemptCast(AttemptCastEvent event) {
//        Player player = event.getPlayer();
//        logger.info("Player {} trying to cast spell {}", player, event.getSpell());
//        PlayerThirst playerThirst = player.getCapability(PlayerThirstProvider.PLAYER_THIRST)
//                .orElseGet(() -> new PlayerThirst(0));
//        logger.info("Player thirst {}", playerThirst);
//
//        if (playerThirst.getThirst() == 0) {
//            logger.info("PLAYER HAS NO THIRST!");
//            event.setResult(Event.Result.DENY);
//        } else {
//            Vec3 lookAngle = player.getLookAngle();
//            logger.info("look {}, mag {}", lookAngle, lookAngle.length());
//            SmallFireball smallfireball = new SmallFireball(
//                    player.level(),
//                    player,
//                    // impulse
//                    lookAngle.x, //entity.getRandom().triangle(d1, 2.297D * d4),
//                    lookAngle.y, //d2,
//                    lookAngle.z); //entity.getRandom().triangle(d3, 2.297D * d4));
//            logger.info("smf {}", smallfireball);
//            smallfireball.setPos(smallfireball.getX(), player.getY(0.5D) + 0.5D, smallfireball.getZ());
//            logger.info("smf pos {} {} {}", smallfireball.getX(), smallfireball.getY(), smallfireball.getZ());
//            player.level().addFreshEntity(smallfireball);
//            logger.info("Cost 1 thirst");
//            playerThirst.subThirst(1);
//            if (player instanceof ServerPlayer) {
//                logger.info("Sending to server player: {}", playerThirst.getThirst());
//                com.example.examplemod.PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
//                        new ThirstDataSyncS2CPacket(playerThirst.getThirst()));
//            }
//        }
//    }

    @SubscribeEvent
    public static void onManaRegenerate(ManaRegenerateEvent event) {
        LivingEntity owner = event.getOwner();
        logger.info("Entity {} regenerated mana: {}", owner, event.getAmount());
        if (owner instanceof Player player) {
            ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> new ManaPool());
            logger.info("Pool {}", pool);

            if (pool.isEmpty()) {
                logger.info("PLAYER MANA IS EMPTY");
            } else {
                logger.info("Regen {} mana", event.getAmount());
                pool.incMana();
                if (player instanceof ServerPlayer serverPlayer) {
                    logger.info("Sending to client: {}", pool);
                    PacketHandler.sendToPlayer(serverPlayer, pool);
                }
            }
        }
    }
}
