package com.example.wizards;

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

    protected static final Logger logger = LogUtils.getLogger();

    // Ai
    protected FollowControllerGoal followControllerGoal;
    protected AssignedTargetGoal assignedTargetGoal;
    protected ControllerHurtByTargetGoal controllerHurtByTargetGoal;
    protected String controllerUuid;

    // Animation
    public final AnimationState idleAnimationState = new AnimationState();
    protected int idleAnimationTimeout;
    public final AnimationState attackAnimationState = new AnimationState();
    protected int attackAnimationTimeout;

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
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
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
    public void tick() {
        super.tick();
        SummonedCreatureUtil.trySetController(this, this.level());
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
