package com.example.wizards.entity.ai;

import com.example.wizards.entity.SummonedCreature;
import com.mojang.logging.LogUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.slf4j.Logger;

public class AnimatedAttackGoal<T extends SummonedCreature> extends MeleeAttackGoal {

    private static final Logger logger = LogUtils.getLogger();

    protected final T entity;

    // total 30 ticks, 1.5 seconds
    protected final int TIME_TO_ATTACK;
    protected final int TIME_AFTER_ATTACK;

    // Ticks from start of animation to "attack" part
    protected int attackDelay;

    // Ticks from "attack" part to end of animation
    protected int ticksUntilNextAttack;
    protected boolean shouldCountTillNextAttack = false;

    public AnimatedAttackGoal(T entity, double speedModifier, boolean followingTargetEvenIfNotSeen,
                              int ticksToAttack, int ticksAfterAttack) {
        super(entity, speedModifier, followingTargetEvenIfNotSeen);
        this.TIME_TO_ATTACK = ticksToAttack;
        this.TIME_AFTER_ATTACK = ticksAfterAttack;
        this.entity = entity;
        this.attackDelay = this.TIME_TO_ATTACK;
        this.ticksUntilNextAttack = this.TIME_AFTER_ATTACK;
    }

    public int getAnimationTotalTicks() {
        return TIME_TO_ATTACK + TIME_AFTER_ATTACK;
    }

    @Override
    public void tick() {
//            logger.info("Goal: tick pre super {}", ticksUntilNextAttack);
        super.tick();
//            logger.info("Goal: tick post super {}", ticksUntilNextAttack);
//            if (shouldCountTillNextAttack) {
//                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
//            }
//            logger.info("Goal: tick end {}", ticksUntilNextAttack);
        // timing discrepancy is because (i think): in the server tick that the animation is started there is no decrement of the countdown:
        //        if (this.isAttacking() && attackAnimationTimeout <= 0) {
        //            attackAnimationState.start(this.tickCount);
        //            attackAnimationTimeout = 20; // Length in ticks of your animation
        //        } else {
        //            --this.attackAnimationTimeout;
        //        }
        // the countdown is reset OR ticked over
        // but in the goal, in the tick that an attack is performed and the count is reset, the countdown is still
        // decremented (above):
        //        super.tick(); // <<== ticksUntilNextAttack reset here on attack
        //        if (shouldCountTillNextAttack) { // <<== still counted down in same server tick
        //            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        //        }
        // so that goal tick count is one ahead of the animation tick count

    }

    @Override
    public void start() {
        super.start();
        attackDelay = TIME_TO_ATTACK;
        ticksUntilNextAttack = TIME_AFTER_ATTACK;
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }

    // resets ticksUntilNextAttack
    // if within distance, starts count down
    // counts down every tick
    // until it reaches attackDelay, then starts the animation
    // when ticksUntilNextAttack reaches zero, performs attack, resets ticksUntilNextAttack
    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
//            logger.info("Goal: CaP {}", ticksUntilNextAttack);
        if (isEnemyWithinAttackDistance(pEnemy, pDistToEnemySqr)) {
            shouldCountTillNextAttack = true;
//                logger.info("Goal: T/C {}", this.entity.tickCount);
//                logger.info("Goal: within distance, {}", ticksUntilNextAttack);

            if(isTimeToStartAttackAnimation()) {
//                    logger.info("Goal: setAttacking(true): {}", ticksUntilNextAttack);
                entity.setAttacking(true);
            }

            if(isTimeToAttack()) {
//                    logger.info("Goal: time to attack: {} (p t/o {}, anim {})",
//                            ticksUntilNextAttack, entity.attackAnimationTimeout, entity.attackAnimationState.getAccumulatedTime());
                this.mob.getLookControl().setLookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performAttack(pEnemy);
            } else {
                if (shouldCountTillNextAttack) {
                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                }
            }
            // maybe put ticksUntilNextAttack decrement here as an else branch
        } else {
            resetAttackCooldown();
            shouldCountTillNextAttack = false;
            entity.setAttacking(false);
//                logger.info("Goal: entity.attackAnimationTimeout was {}", entity.attackAnimationTimeout);
            entity.attackAnimationTimeout = 0;
//                logger.info("Goal: entity.attackAnimationTimeout {}", entity.attackAnimationTimeout);
        }
    }

    protected boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
//            logger.info("isEnemyWithinAttackDistance pDistToEnemySqr {}", pDistToEnemySqr);
        return pDistToEnemySqr <= this.getAttackReachSqr(pEnemy);
    }

    @Override
    protected double getAttackReachSqr(LivingEntity p_25556_) {
        double attackReachSqr = super.getAttackReachSqr(p_25556_);
//            logger.info("attackReachSqr {}", attackReachSqr);
        return attackReachSqr;
    }

    protected void resetAttackCooldown() {
        // Based on kaupenjoe tutorial, attackDelay is 40 ticks from start of animation to "attack" part,
        // then 40 ticks to end of animation. So from "attack" animation part to "attack" part is 80 ticks
        // i.e. attackDelay * 2
        this.ticksUntilNextAttack = this.adjustedTickDelay(TIME_TO_ATTACK + TIME_AFTER_ATTACK);
//            logger.info("Goal: reset ticksUntilNextAttack {}", ticksUntilNextAttack);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }


    protected void performAttack(LivingEntity pEnemy) {
//            logger.info("Goal: perform attack {}, parent {}", ticksUntilNextAttack, entity.attackAnimationTimeout);
        this.resetAttackCooldown();
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(pEnemy);
    }

    @Override
    public String toString() {
        return "AnimatedAttackGoal{" +
                "entity=" + entity +
                ", attackDelay=" + attackDelay +
                ", ticksUntilNextAttack=" + ticksUntilNextAttack +
                ", shouldCountTillNextAttack=" + shouldCountTillNextAttack +
                ", mob=" + mob +
                '}';
    }
}
