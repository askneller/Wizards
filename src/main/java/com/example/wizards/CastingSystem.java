package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
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

        String spellCast = null;
        if (consumeManaEvent.hasResult() && consumeManaEvent.getResult() == Event.Result.DENY) {
            logger.info("No!");
            event.setResult(Event.Result.DENY);
        } else if (event.getSpell() == 1) {
            Vec3 lookAngle = player.getLookAngle();
            SmallFireball smallfireball = new SmallFireball(
                    player.level(),
                    player,
                    // impulse
                    lookAngle.x,
                    lookAngle.y,
                    lookAngle.z);
            smallfireball.setPos(smallfireball.getX(), player.getY(0.5D) + 0.5D, smallfireball.getZ());
            player.level().addFreshEntity(smallfireball);
            spellCast = "Small Fireball";
        }  else if (event.getSpell() == 2) {
            Vec3 lookAngle = player.getLookAngle();
            LargeFireball fireball =
                    new LargeFireball(player.level(), player, lookAngle.x, lookAngle.y, lookAngle.z,
                            /* explosive power */ 2);
            fireball.setPos(fireball.getX(), player.getY(0.5D) + 0.5D, fireball.getZ());
            player.level().addFreshEntity(fireball);
            spellCast = "Large Fireball";
        } else if (event.getBlockPos() != null && !event.getBlockPos().equals(BlockPos.ZERO)) {
            Vec3 spawnPos = new Vec3(
                    event.getBlockPos().getX() + 0.5,
                    event.getBlockPos().getY() + 1.0,
                    event.getBlockPos().getZ() + 0.5);
            if (event.getSpell() == 3) {
                SummonedZombie zombie = new SummonedZombie(EntityType.ZOMBIE, player.level());
                zombie.setPos(spawnPos);
                logger.info("SummonedZombie {}", zombie);
                player.level().addFreshEntity(zombie);
                spellCast = "Summon Zombie";
            } else if (event.getSpell() == 4) {
                SummonedSkeleton monster = new SummonedSkeleton(EntityType.SKELETON, player.level());
                monster.setPos(spawnPos);
                logger.info("SummonedSkeleton {}", monster);
                player.level().addFreshEntity(monster);
                spellCast = "Summon Skeleton";
            } else if (event.getSpell() == 5) {
                SummonedSpider monster = new SummonedSpider(EntityType.SPIDER, player.level());
                monster.setPos(spawnPos);
                logger.info("SummonedSpider {}", monster);
                player.level().addFreshEntity(monster);
                spellCast = "Summon Spider";
            } else if (event.getSpell() == 6) {
                SummonedPolarBear monster = new SummonedPolarBear(EntityType.POLAR_BEAR, player.level());
                monster.setPos(spawnPos);
                logger.info("SummonedPolarBear {}", monster);
                player.level().addFreshEntity(monster);
                spellCast = "Summon Polar Bear";
            } else if (event.getSpell() == 7) {
                SummonedPhantom monster = new SummonedPhantom(EntityType.PHANTOM, player.level());
                monster.setPos(spawnPos);
                logger.info("SummonedPhantom {}", monster);
                player.level().addFreshEntity(monster);
                spellCast = "Summon Phantom";
            } else if (event.getSpell() == 8) {
                SummonedSlime monster = new SummonedSlime(EntityType.SLIME, player.level());
                monster.setPos(spawnPos);
                logger.info("SummonedSlime {}", monster);
                player.level().addFreshEntity(monster);
                spellCast = "Summon Slime";
            } else if (event.getSpell() == 9) {
                SummonedSkeletonArcher monster = new SummonedSkeletonArcher(EntityType.SKELETON, player.level());
                monster.setPos(spawnPos);
                logger.info("SummonedSkeletonArcher {}", monster);
                player.level().addFreshEntity(monster);
                spellCast = "Summon Slime";
            }
            if (player instanceof ServerPlayer serverPlayer) {
                // TODO change the BlockPos to Vec3 spawnPos above
                // Spawn particles at summon position
                PacketHandler.sendToPlayer(serverPlayer, event.getSpell(), true, event.getBlockPos());
            } else {
                logger.error("Not a ServerPlayer");
            }
        }
        if (spellCast != null) {
            MutableComponent message = Component.literal(player.getName().getString()).withStyle(ChatFormatting.GREEN);
            message.append(Component.literal(" cast ").withStyle(ChatFormatting.WHITE));
            message.append(Component.literal(spellCast).withStyle(ChatFormatting.AQUA));
            player.sendSystemMessage(message);
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
