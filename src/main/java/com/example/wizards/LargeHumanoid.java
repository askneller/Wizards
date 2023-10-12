package com.example.wizards;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class LargeHumanoid extends SummonedCreature {

    public LargeHumanoid(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new AttackGoal(this, 1.0, true));

        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0f));

        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
//        if (!this.level().isClientSide) logger.info("=========================\nEntity tick: T/C {}", tickCount);
        super.tick();

        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    protected void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
//            logger.info("Entity: timeout done, starting animation");
            attackAnimationState.start(this.tickCount);
            attackAnimationTimeout = AttackGoal.TIME_TO_ATTACK + AttackGoal.TIME_AFTER_ATTACK; // Length in ticks of your animation
//            logger.info("Entity: attackAnimationState {}", attackAnimationState.getAccumulatedTime());
//            logger.info("Entity: attackAnimationTimeout reset {}", attackAnimationTimeout);
        } else {
            --this.attackAnimationTimeout;
//            logger.info("Entity: countdown animation {}", this.attackAnimationTimeout);
        }

        if (!this.isAttacking()) {
//            if (attackAnimationState.isStarted()) logger.info("Stopping anim");
            attackAnimationState.stop();
        }
    }

    @Override
    protected void updateWalkAnimation(float partialTick) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f = Math.min(partialTick * 6f, 1f);
        } else {
            f = 0;
        }

        this.walkAnimation.update(f, 0.2f);
    }


    @Override
    public boolean doHurtTarget(Entity p_21372_) {
//        logger.info("Entity: Doing hurt ({})", this.attackAnimationState.getAccumulatedTime());
        return super.doHurtTarget(p_21372_);
    }

    public class AttackGoal extends MeleeAttackGoal {

        protected final LargeHumanoid entity;

        // total 30 ticks, 1.5 seconds
        public static final int TIME_TO_ATTACK = 23;
        public static final int TIME_AFTER_ATTACK = 7;

        // Ticks from start of animation to "attack" part
        protected int attackDelay = TIME_TO_ATTACK;

        // Ticks from "attack" part to end of animation
        protected int ticksUntilNextAttack = TIME_AFTER_ATTACK;
        protected boolean shouldCountTillNextAttack = false;

        public AttackGoal(PathfinderMob p_25552_, double p_25553_, boolean p_25554_) {
            super(p_25552_, p_25553_, p_25554_);
            entity = (LargeHumanoid) p_25552_;
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


    }
}
