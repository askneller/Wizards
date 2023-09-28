package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;

public class ManaSystem {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onConsumeMana(ConsumeManaEvent event) {
        Player player = event.getPlayer();
        logger.info("Player {} trying to cast spell for {} {}", player, event.getAmount(), event.getType());
        ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> ManaPool.EMPTY);
        logger.info("Player pool {}", pool);

        if (pool.isExhausted()) {
            logger.info("PLAYER HAS NO MANA!");
            event.setResult(Event.Result.DENY);
            return;
        }

        if (!pool.has(event.getAmount(), event.getType())) {
            logger.info("Insufficient Mana");
            event.setResult(Event.Result.DENY);
            return;
        }

        // TODO should the mana check and the consumption be separated

        logger.info("Sufficient mana to cast");
        pool.decMana();
        if (player instanceof ServerPlayer serverPlayer) {
            logger.info("Sending updated pool to client: {}", pool);
            PacketHandler.sendToPlayer(serverPlayer, pool);
        }
    }
}
