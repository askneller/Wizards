package com.example.wizards.entity;

import com.example.wizards.entity.ai.AnimatedAttackGoal;
import com.example.wizards.entity.ai.AssignedTargetGoal;
import com.example.wizards.entity.ai.ControllerHurtByTargetGoal;
import com.example.wizards.entity.ai.FollowControllerGoal;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public abstract class SummonedCreature extends PathfinderMob implements ControlledEntity {


    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(SummonedCreature.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> ATTACK_ANIMATION_DURATION = SynchedEntityData.defineId(SummonedCreature.class, EntityDataSerializers.INT);

    protected static final Logger logger = LogUtils.getLogger();

    // Ai
    protected FollowControllerGoal followControllerGoal;
    protected AssignedTargetGoal assignedTargetGoal;
    protected ControllerHurtByTargetGoal controllerHurtByTargetGoal;
    protected AnimatedAttackGoal<?> animatedAttackGoal;
    protected String controllerUuid;

    // Animation
    public final AnimationState idleAnimationState = new AnimationState();
    protected int idleAnimationTimeout;
    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout;

    public SummonedCreature(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
//        this.goalSelector.addGoal(1, new SummonedCreature.SlimeFloatGoal(this));
//        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));

//        this.goalSelector.addGoal(4, new CreatureWaterAvoidingRandomStrollGoal(this, 1.0));
        this.followControllerGoal = new FollowControllerGoal(this, 1.0D);
        this.goalSelector.addGoal(4, followControllerGoal);

        this.assignedTargetGoal = new AssignedTargetGoal(this);
        this.targetSelector.addGoal(1, this.assignedTargetGoal);

        this.controllerHurtByTargetGoal = new ControllerHurtByTargetGoal(this);

        registerLookAndAttackGoals();
    }

    protected void registerLookAndAttackGoals() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(ATTACK_ANIMATION_DURATION, 0);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public int getAttackAnimationDuration() {
//        logger.info("getAttackAnimationDuration {}", this.entityData.get(ATTACK_ANIMATION_DURATION));
        return this.entityData.get(ATTACK_ANIMATION_DURATION);
    }

    @Override
    public void tick() {
//        if (!this.level().isClientSide) {
//            logger.info("=========================\nEntity tick: T/C {}\n{}", tickCount, animatedAttackGoal);
//        }
        super.tick();

        SummonedCreatureUtil.trySetController(this, this.level());

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

//        logger.info("goal {}", this.getAttackAnimationDuration());
        if (this.getAttackAnimationDuration() != 0) {
            if (this.isAttacking() && attackAnimationTimeout <= 0) {
//            logger.info("Entity: timeout done, starting animation");
                attackAnimationState.start(this.tickCount);
                attackAnimationTimeout = this.getAttackAnimationDuration(); // Length in ticks of your animation
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
        } else {
//            logger.info("here !!!!");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getControllerUuid() != null) {
            tag.putString("ControllerUuid", getControllerUuid());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.controllerUuid = tag.getString("ControllerUuid");
    }

    @Override
    public void setController(LivingEntity controller) {
        if (this.followControllerGoal != null ) {
            this.followControllerGoal.setController(controller);
        }
        if (this.controllerHurtByTargetGoal != null) {
            this.controllerHurtByTargetGoal.setController(controller);
        }
    }

    @Override
    public LivingEntity getController() {
        return this.followControllerGoal != null ? this.followControllerGoal.getController() : null;
    }

    @Override
    public String getControllerUuid() {
        LivingEntity controller = getController();
        return controller != null ? controller.getStringUUID() : this.controllerUuid;
    }

    @Override
    public void setControllerUuid(String uuid) {
        this.controllerUuid = uuid;
    }

    @Override
    public void assignTarget(LivingEntity livingEntity) {
        if (this.assignedTargetGoal != null) {
            this.assignedTargetGoal.assignTarget(livingEntity);
        }
    }

}
