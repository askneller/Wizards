package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
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
        int manaAmount = event.getSpell() == 1 ? 1 : 2;
        ConsumeManaEvent consumeManaEvent = new ConsumeManaEvent(manaAmount, ManaColor.COLORLESS, player);
        MinecraftForge.EVENT_BUS.post(consumeManaEvent);

        logger.info("ConsumeManaEvent result: {}", consumeManaEvent.getResult());
        logger.info("BlockPos {}", event.getBlockPos());
        if (consumeManaEvent.hasResult() && consumeManaEvent.getResult() == Event.Result.DENY) {
            logger.info("No!");
            event.setResult(Event.Result.DENY);
        } else if (event.getSpell() == 1) {
            Vec3 lookAngle = player.getLookAngle();
//            logger.info("look {}, mag {}", lookAngle, lookAngle.length());
            SmallFireball smallfireball = new SmallFireball(
                    player.level(),
                    player,
                    // impulse
                    lookAngle.x, //entity.getRandom().triangle(d1, 2.297D * d4),
                    lookAngle.y, //d2,
                    lookAngle.z); //entity.getRandom().triangle(d3, 2.297D * d4));
//            logger.info("smf {}", smallfireball);
            smallfireball.setPos(smallfireball.getX(), player.getY(0.5D) + 0.5D, smallfireball.getZ());
//            logger.info("smf pos {} {} {}", smallfireball.getX(), smallfireball.getY(), smallfireball.getZ());
            player.level().addFreshEntity(smallfireball);
        } else if (event.getBlockPos() != null && !event.getBlockPos().equals(BlockPos.ZERO)) {
            if (event.getSpell() == 2) {
                Zombie zombie = new Zombie(EntityType.ZOMBIE, player.level());
                zombie.setPos(event.getBlockPos().getX(), event.getBlockPos().getY() + 1, event.getBlockPos().getZ());
                logger.info("Zombie {}", zombie);
                player.level().addFreshEntity(zombie);
            } else if (event.getSpell() == 3) {
                Skeleton monster = new Skeleton(EntityType.SKELETON, player.level());
                monster.setPos(event.getBlockPos().getX(), event.getBlockPos().getY() + 1, event.getBlockPos().getZ());
                logger.info("Skeleton {}", monster);
                player.level().addFreshEntity(monster);
            } else if (event.getSpell() == 4) {
                Spider monster = new Spider(EntityType.SPIDER, player.level());
                monster.setPos(event.getBlockPos().getX(), event.getBlockPos().getY() + 1, event.getBlockPos().getZ());
                logger.info("Spider {}", monster);
                player.level().addFreshEntity(monster);
            } else if (event.getSpell() == 5) {
                Piglin monster = new Piglin(EntityType.PIGLIN, player.level());
                monster.setPos(event.getBlockPos().getX(), event.getBlockPos().getY() + 1, event.getBlockPos().getZ());
                logger.info("Piglin {}", monster);
                player.level().addFreshEntity(monster);
            } else if (event.getSpell() == 6) {
                Witch monster = new Witch(EntityType.WITCH, player.level());
                monster.setPos(event.getBlockPos().getX(), event.getBlockPos().getY() + 1, event.getBlockPos().getZ());
                logger.info("Witch {}", monster);
                player.level().addFreshEntity(monster);
            }
        }
    }

    @SubscribeEvent
    public static void onManaRegenerate(ManaRegenerateEvent event) {
        LivingEntity owner = event.getOwner();
        logger.info("Entity {} regenerated mana: {}", owner, event.getAmount());
        if (owner instanceof Player player) {
            ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> ManaPool.EMPTY);
            logger.info("Pool {}", pool);

            if (pool.isEmpty()) {
                logger.info("PLAYER MANA EMPTY");
            } else {
                logger.info("Regen {} mana", event.getAmount());
                // todo changed to new system
                pool.replenishSource(event.getBlockSource().getId());
                if (player instanceof ServerPlayer serverPlayer) {
                    logger.info("Sending to client: {}", pool);
                    PacketHandler.sendToPlayer(serverPlayer, pool);
                }
            }
        }
    }
}
