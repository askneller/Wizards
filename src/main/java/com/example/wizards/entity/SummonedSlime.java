package com.example.wizards.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SummonedSlime extends SummonedCreature {

    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(SummonedSlime.class, EntityDataSerializers.INT);

    public static final String spell_name = "summon_slime";

    public float squish;
    public float oSquish;

    public SummonedSlime(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);

        this.moveControl = new SummonedSlime.SlimeMoveControl(this);


        EntityDimensions dimensions = getDimensions(Pose.STANDING);
        this.setSize((int) dimensions.width, true);


        AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);
        logger.info("\nHealth {}", attribute.getBaseValue());
        attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        logger.info("\nMOVEMENT_SPEED {}", attribute.getBaseValue());
        logger.info("\nSpeed {}", this.getSpeed());
        attribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        logger.info("\nATTACK_DAMAGE {}", attribute.getBaseValue());
        logger.info("\nStanding Dimensions {}", getDimensions(Pose.STANDING));
        logger.info("\nBB {}", getBoundingBox());

    }

    @Override
    protected void registerLookAndAttackGoals() {
        this.goalSelector.addGoal(2, new SummonedSlime.SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SummonedSlime.CreatureSlimeRandomDirectionGoal(this));
    }

    // Slime specific
    @VisibleForTesting
    public void setSize(int p_33594_, boolean p_33595_) {
        int i = Mth.clamp(p_33594_, 1, 127);
        this.entityData.set(ID_SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(i * i));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)i);
        if (p_33595_) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = i;
    }


    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 1); // Slime specific
    }

    //===========================================================================
    // Slime specific
    public void push(Entity entity) {
        super.push(entity);
        if (/*entity instanceof IronGolem &&*/ this.isDealsDamage()) {
            if (this.getController() != null) {
                if (this.getController().equals(entity)) {
//                logger.info("Not hurting controller, push");
                    return;
                }
                if (entity instanceof ControlledEntity ce) {
                    if (this.getController().equals(ce.getController())) {
                        logger.info("Not hurting entity with same controller, push");
                        return;
                    }
                }
            }
            this.dealDamage((LivingEntity)entity);
        }

    }

    public void playerTouch(Player player) {
//        logger.info("Player touch {}", player);
        if (this.isDealsDamage()) {
            if (this.getController() != null && this.getController().equals(player)) {
//                logger.info("Not hurting controller, playerTouch");
                return;
            }
            logger.info("Deal damage");
            this.dealDamage(player);
        }
    }

    public int getSize() {
        return this.entityData.get(ID_SIZE);
    }

    protected void dealDamage(LivingEntity living) {
        if (this.isAlive()) {
            logger.info("Alive");
            int i = this.getSize();
            logger.info("Size {}", i);
            double d = 0.6D * (double) i * 0.6D * (double) i;
            double dist = this.distanceToSqr(living);
            boolean b = dist < d + 1;
            logger.info("d {}, dist {}, b {}", d, dist, b);
            if (this.distanceToSqr(living) < d + 1  /*0.6D * (double)i * 0.6D * (double)i*/ &&
                    this.hasLineOfSight(living) &&
                    living.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, living);
            }
        }
    }

    protected float getStandingEyeHeight(Pose p_33614_, EntityDimensions p_33615_) {
        return 0.625F * p_33615_.height;
    }

    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    // ====================================================================================== //
    static class SlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final SummonedSlime slime;
        private boolean isAggressive;

        public SlimeMoveControl(SummonedSlime p_33668_) {
            super(p_33668_);
            this.slime = p_33668_;
            this.yRot = 180.0F * p_33668_.getYRot() / (float)Math.PI;
        }

        public void setDirection(float p_33673_, boolean p_33674_) {
            this.yRot = p_33673_;
            this.isAggressive = p_33674_;
        }

        public void setWantedMovement(double p_33671_) {
            this.speedModifier = p_33671_;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
//                        logger.info("jump - {} | {}", this.jumpDelay, this.slime.position());
//                        if (this.slime.getTarget() != null) logger.info("target | {}", this.slime.getTarget().position());
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    protected boolean doPlayJumpSound() {
        return true; //this.getSize() > 0;
    }

    protected void jumpFromGround() {
//        logger.info("jumpFromGround");
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, (double)this.getJumpPower(), vec3.z);
//        logger.info("jump delta {}", this.getJumpPower());
        this.hasImpulse = true;
    }

    float getSoundPitch() {
        float f = /*this.isTiny() ? 1.4F :*/ 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEvent getJumpSound() {
        return /*this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL :*/ SoundEvents.SLIME_JUMP;
    }

    // ====================================================================================== //
    static class SlimeAttackGoal extends Goal {
        private final SummonedCreature slime;
        private int growTiredTimer;

        public SlimeAttackGoal(SummonedCreature p_33648_) {
            this.slime = p_33648_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return this.slime.canAttack(livingentity) && this.slime.getMoveControl() instanceof SlimeMoveControl;
            }
        }

        public void start() {
//            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!this.slime.canAttack(livingentity)) {
                return false;
            } else {
                return true; // --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity != null) {
                this.slime.lookAt(livingentity, 10.0F, 10.0F);
            }

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SummonedSlime.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.slime.getYRot(), true);
            }

        }
    }

    // ====================================================================================== //
    static class CreatureSlimeRandomDirectionGoal extends Goal {
        private final SummonedCreature slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public CreatureSlimeRandomDirectionGoal(SummonedCreature p_33679_) {
            this.slime = p_33679_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return /*this.slime.getTarget() == null &&*/ this.slime.getController() != null &&
                    (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) &&
                    this.slime.getMoveControl() instanceof SummonedSlime.SlimeMoveControl;
        }

        public boolean canContinueToUse() {
            return this.slime.getController() != null;
        }


        public void tick() {
            LivingEntity livingentity = this.slime.getController();
            if (livingentity != null) {
                this.slime.lookAt(livingentity, 10.0F, 10.0F);
            }

            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof SummonedSlime.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.slime.getYRot(), false);
            }

        }

        public void start() {
            super.start();
//            LivingEntity controller = this.slime.getController();
//            Vec3 contPos = controller.position();
//            Vec3 thisPos = this.slime.position();
            logger.info("Start look");
//            logger.info("contPos {}", contPos);
//            logger.info("thisPos {}", thisPos);
//            logger.info("vectorTo {}", thisPos.vectorTo(contPos));
        }

        public void stop() {
            super.stop();
            logger.info("Stop look");
        }

    }

    // ====================================================================================== //
    public class CreatureWaterAvoidingRandomStrollGoal extends RandomStrollGoal {
        public static final float PROBABILITY = 0.001F;
        protected final float probability;

        public CreatureWaterAvoidingRandomStrollGoal(PathfinderMob p_25987_, double p_25988_) {
            this(p_25987_, p_25988_, 0.001F);
        }

        public CreatureWaterAvoidingRandomStrollGoal(PathfinderMob p_25990_, double p_25991_, float p_25992_) {
            super(p_25990_, p_25991_);
            this.probability = p_25992_;
        }

        @Nullable
        protected Vec3 getPosition() {
            if (this.mob.isInWaterOrBubble()) {
                Vec3 vec3 = LandRandomPos.getPos(this.mob, 15, 7);
                return vec3 == null ? super.getPosition() : vec3;
            } else {
                return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
            }
        }

        public boolean canUse() {
            boolean canUse = super.canUse();
//            logger.info("XXX canUse {}", canUse);
            if (canUse) {
                logger.info("current mob pos {}", this.mob.blockPosition());
                logger.info("wanted {} {} {}", wantedX, wantedY, wantedZ);

            }
            return canUse;
        }

        public boolean canContinueToUse() {
            boolean continueToUse = super.canContinueToUse();
            if (!continueToUse) {
                logger.info("continueToUse nav done {}", this.mob.getNavigation().isDone());
//                logger.info("wanted {} {} {}", wantedX, wantedY, wantedZ);
            }
            return continueToUse;
        }

        public void start() {
            super.start();
            logger.info("start target {}", this.mob.getNavigation().getTargetPos());
            logger.info("start pos {}", this.mob.blockPosition());
            logger.info("path {} idx {}", this.mob.getNavigation().getPath(), this.mob.getNavigation().getPath().getNextNodeIndex());
        }

        public void stop() {
//            logger.info("stop path {} idx {}", this.mob.getNavigation().getPath(), this.mob.getNavigation().getPath().getNextNodeIndex());
            super.stop();
            logger.info("stop pos {}", this.mob.blockPosition());
            logger.info("stop target {}", this.mob.getNavigation().getTargetPos());
        }


    }

//    static class SlimeFloatGoal extends Goal {
//        private final SummonedCreature slime;
//
//        public SlimeFloatGoal(SummonedCreature p_33655_) {
//            this.slime = p_33655_;
//            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
//            p_33655_.getNavigation().setCanFloat(true);
//        }
//
//        public boolean canUse() {
//            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SummonedCreature.SlimeMoveControl;
//        }
//
//        public boolean requiresUpdateEveryTick() {
//            return true;
//        }
//
//        public void tick() {
//            if (this.slime.getRandom().nextFloat() < 0.8F) {
//                this.slime.getJumpControl().jump();
//            }
//
//            MoveControl movecontrol = this.slime.getMoveControl();
//            if (movecontrol instanceof SummonedCreature.SlimeMoveControl slime$slimemovecontrol) {
//                slime$slimemovecontrol.setWantedMovement(1.2D);
//            }
//
//        }
//    }

}
