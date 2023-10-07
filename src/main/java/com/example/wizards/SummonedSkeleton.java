package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class SummonedSkeleton extends Skeleton implements ControlledEntity {

    private static final Logger logger = LogUtils.getLogger();

    public static final String key = "summonedskeleton";

    private FollowControllerGoal followControllerGoal;
    private AssignedTargetGoal assignedTargetGoal;
    private ControllerHurtByTargetGoal controllerHurtByTargetGoal;
    private String controllerUuid;

    public SummonedSkeleton(EntityType<? extends Skeleton> p_33570_, Level p_33571_) {
        super(p_33570_, p_33571_);
    }

    protected void registerGoals() {
        // Attack goals (bow and melee) added in AbstractSkeleton.reassessWeaponGoal
        this.followControllerGoal = new FollowControllerGoal(this, 1.0D);
        this.goalSelector.addGoal(5, followControllerGoal);
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.assignedTargetGoal = new AssignedTargetGoal(this);
        this.targetSelector.addGoal(1, this.assignedTargetGoal);
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.controllerHurtByTargetGoal = new ControllerHurtByTargetGoal(this);
        this.targetSelector.addGoal(5, controllerHurtByTargetGoal);
        // Attack other players
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                true,
                (le -> this.getController() != null && !le.equals(this.getController()))));
        // Attack mobs controlled by other players
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                true,
                (le -> {
                    if (le instanceof ControlledEntity ce) {
                        return this.getController() != null && ce.getController() != null &&
                                !this.getController().equals(ce.getController());
                    }
                    return false;
                })));
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("ControllerUuid", getControllerUuid());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.controllerUuid = tag.getString("ControllerUuid");
    }

    public void tick() {
        super.tick();
        SummonedCreatureUtil.trySetController(this, this.level());
    }

    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void setController(LivingEntity controller) {
        this.followControllerGoal.setController(controller);
        this.controllerHurtByTargetGoal.setController(controller);
    }

    @Override
    public LivingEntity getController() {
        return this.followControllerGoal.getController();
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
        this.assignedTargetGoal.assignTarget(livingEntity);
    }
}
