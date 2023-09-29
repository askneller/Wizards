package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;

public class ManaSystem {

    private static final Logger logger = LogUtils.getLogger();
    private static final MutableInt SOURCES_ID = new MutableInt(0);

    public static int getNewSourceId() {
        return SOURCES_ID.getAndIncrement();
    }

    @SubscribeEvent
    public static void onConsumeMana(ConsumeManaEvent event) {
        Player player = event.getPlayer();
        logger.info("Player {} trying to cast spell for {} {}", player, event.getAmount(), event.getColor());
        ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> ManaPool.EMPTY);
        logger.info("Player pool {}", pool);

        if (pool.isExhausted()) {
            logger.info("PLAYER HAS NO MANA!");
            event.setResult(Event.Result.DENY);
            return;
        }

        if (!pool.has(event.getAmount(), event.getColor())) {
            logger.info("Insufficient Mana");
            event.setResult(Event.Result.DENY);
            return;
        }

        // TODO should the mana check and the consumption be separated

        logger.info("Sufficient mana to cast");
        if (pool.consume(event.getAmount(), event.getColor())) {
            if (player instanceof ServerPlayer serverPlayer) {
                logger.info("Sending updated pool to client: {}", pool);
                PacketHandler.sendToPlayer(serverPlayer, pool);
            }
        } else {
            logger.error("Failed to consume mana from {}", pool);
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onAddManaSource(AddManaSourceEvent event) {
        logger.info("AddManaSourceEvent {}", event);

        if (event.getOwner() instanceof Player player) {
            ManaTotemBlockEntity source = event.getSource();
            ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> ManaPool.EMPTY);
            logger.info("Is player and has pool {}", pool);
            pool.addSource(new ManaSource(source.getId(), source.getMana(), source.getColor(), source.isAvailable(), source));

            if (player instanceof ServerPlayer serverPlayer) {
                logger.info("Sending updated pool to client: {}", pool);
                PacketHandler.sendToPlayer(serverPlayer, pool);
            }
        }

    }
}
