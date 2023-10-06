package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;

public class CastingSystem {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onAttemptCast(AttemptCastEvent event) {
        Player player = event.getPlayer();
//        logger.info("Player {} trying to cast spell {}", player, event.getSpell());

        Optional<List<ManaColor>> cost = Spells.getSpellCostByName(event.getSpellName());
        ConsumeManaEvent consumeManaEvent = new ConsumeManaEvent(player, cost.orElse(Arrays.asList(ManaColor.COLORLESS)));
        MinecraftForge.EVENT_BUS.post(consumeManaEvent);

//        logger.info("ConsumeManaEvent result: {}", consumeManaEvent.getResult());
//        logger.info("BlockPos {}", event.getBlockPos());

        String spellCast = null;
        Optional<Spell> spellOptional = Spells.getSpellByName(event.getSpellName());
        if (consumeManaEvent.hasResult() && consumeManaEvent.getResult() == Event.Result.DENY) {
            logger.info("No!");
            event.setResult(Event.Result.DENY);
        } else if (spellOptional.isPresent() && spellOptional.get().getProjectileClass() != null) {
            Object o = spawnProjectileEntity(spellOptional.get(), player.level(), player);
            if (o == null) {
                logger.error("Returned NULL");
                return;
            }
            spellCast = spellOptional.get().getName();
        }
        else if (event.getBlockPos() != null && !event.getBlockPos().equals(BlockPos.ZERO)) {
            Vec3 spawnPos = new Vec3(
                    event.getBlockPos().getX() + 0.5,
                    event.getBlockPos().getY() + 1.0,
                    event.getBlockPos().getZ() + 0.5);

            if (event.getSpellName() != null) {
                spellOptional = Spells.getSpellByName(event.getSpellName());
                if (spellOptional.isPresent()) {
                    Object summonedEntity = spawnSummonedEntity(spellOptional.get(), player.level(), spawnPos, player);
                    spellCast = "Summon " + spellOptional.get().getName();
                    logger.info("SPECIAL!! Summoned creature successfully");
                }
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

    private static Object spawnSummonedEntity(Spell spell, Level level, Vec3 spawnPos, Player controller) {
        try {
            Class creatureClass = spell.getCreatureClass();
            Constructor<?> constructor = creatureClass.getConstructor(EntityType.class, Level.class);
            Object o = constructor.newInstance(spell.getEntityType(), level);
            logger.info("Spell {}, {}", o, o.getClass());
            if (o instanceof LivingEntity le) {
                le.setPos(spawnPos);
                logger.info("Summoned le {}", le);
                level.addFreshEntity(le);
                if (le instanceof ControlledEntity ce) {
                    logger.info("ce is controlled");
                    ce.setController(controller);
                }
            }
            return o;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Object spawnProjectileEntity(Spell spell, Level level, Player player) {
        Vec3 lookAngle = player.getLookAngle();
        for (int i = 0; i < spell.getQuantity(); i++) {

            Object o = createProjectileObject(spell, level, player, lookAngle);

            if (o == null) {
                logger.error("Failed to create projectile of class {}", spell.getProjectileClass());
                return null;
            }

            if (o instanceof AbstractHurtingProjectile ahp) {
                ahp.setPos(ahp.getX(), player.getY(0.5D) + 0.5D, ahp.getZ());
                level.addFreshEntity(ahp);
            } else if (o instanceof Projectile projectile) {
                Vec3 offset = lookAngle.offsetRandom(level.random, spell.getSpread());
                projectile.setPos(player.getX(), player.getY(0.5D) + 0.5D, player.getZ());
                projectile.shoot(offset.x, offset.y, offset.z, spell.getPower(), 1.0f);
                level.addFreshEntity(projectile);
            }
            return o;
        }
        return null;
    }

    private static Object createProjectileObject(Spell spell, Level level, Player player, Vec3 lookAngle) {
        Class<?> projectileClass = spell.getProjectileClass();
        Constructor<?> constructor;
        try {
            if (spell.getEntityType() == EntityType.SMALL_FIREBALL) {
                constructor = projectileClass.getConstructor(Level.class, LivingEntity.class, double.class, double.class, double.class);
                return constructor.newInstance(level, player, lookAngle.x, lookAngle.y, lookAngle.z);
            } else if (spell.getEntityType() == EntityType.FIREBALL) {
                constructor = projectileClass.getConstructor(Level.class, LivingEntity.class, double.class, double.class, double.class, int.class);
                return constructor.newInstance(level, player, lookAngle.x, lookAngle.y, lookAngle.z, (int) spell.getPower());
            } else if (spell.getEntityType() == EntityType.ARROW) {
                constructor = projectileClass.getConstructor(EntityType.class, Level.class);
                return constructor.newInstance(spell.getEntityType(), level);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SubscribeEvent
    public static void onManaRegenerate(ManaRegenerateEvent event) {
        LivingEntity owner = event.getOwner();
//        logger.info("Entity {} regenerated mana: {}", owner, event.getAmount());
        if (owner instanceof Player player) {
            ManaPool pool = player.getCapability(MANA_POOL).orElseGet(() -> ManaPool.EMPTY);
//            logger.info("Pool {}", pool);

            if (pool.isEmpty()) {
//                logger.info("PLAYER MANA EMPTY");
            } else {
//                logger.info("Regen {} mana", event.getAmount());
                // todo changed to new system
                pool.replenishSource(event.getBlockSource().getId());
                if (player instanceof ServerPlayer serverPlayer) {
//                    logger.info("Sending to client: {}", pool);
                    PacketHandler.sendToPlayer(serverPlayer, pool);
                }
            }
        }
    }
}
