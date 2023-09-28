package com.example.wizards;

import com.example.wizards.client.ClientManaPool;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;

public class CastingSystem {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onAttemptCast(AttemptCastEvent event) {
        Player player = event.getPlayer();
        logger.info("Player {} trying to cast spell {}", player, event.getSpell());
        ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> new ManaPool());
        logger.info("Player pool {}", pool);
        if (player.level().isClientSide) {
            logger.info("ClientManaPool {}", ClientManaPool.getPlayerPool());
            logger.info("Send to server");
            PacketHandler.sendToServer(player, event.getSpell());
        }

        if (pool.isExhausted()) {
            logger.info("PLAYER HAS NO MANA!");
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

            logger.info("Cost 1 mana");

            pool.decMana();
            if (player instanceof ServerPlayer serverPlayer) {
                logger.info("Sending to client: {}", pool);
                PacketHandler.sendToPlayer(serverPlayer, pool);
            }
        }
    }

    @SubscribeEvent
    public static void onManaRegenerate(ManaRegenerateEvent event) {
        LivingEntity owner = event.getOwner();
        logger.info("Entity {} regenerated mana: {}", owner, event.getAmount());
        if (owner instanceof Player player) {
            ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> new ManaPool());
            logger.info("Pool {}", pool);

            if (pool.isEmpty()) {
                logger.info("PLAYER MANA EMPTY");
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
