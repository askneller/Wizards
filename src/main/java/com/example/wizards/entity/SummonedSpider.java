package com.example.wizards.entity;

import com.example.wizards.entity.ai.AssignedTargetGoal;
import com.example.wizards.entity.ai.ControllerHurtByTargetGoal;
import com.example.wizards.entity.ai.FollowControllerGoal;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class SummonedSpider extends Spider implements ControlledEntity {

    private static final Logger logger = LogUtils.getLogger();

    public static final String spell_name = "summon_spider";

    private FollowControllerGoal followControllerGoal;
    private AssignedTargetGoal assignedTargetGoal;
    private ControllerHurtByTargetGoal controllerHurtByTargetGoal;
    private String controllerUuid;

    public SummonedSpider(EntityType<? extends Spider> p_33786_, Level p_33787_) {
        super(p_33786_, p_33787_);
        logger.info("Summoned Spider");
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new SummonedSpider.SpiderAttackGoal(this));
        this.followControllerGoal = new FollowControllerGoal(this, 1.0);
        this.goalSelector.addGoal(5, followControllerGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.assignedTargetGoal = new AssignedTargetGoal(this);
        this.targetSelector.addGoal(1, this.assignedTargetGoal);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.controllerHurtByTargetGoal = new ControllerHurtByTargetGoal(this);
        this.targetSelector.addGoal(3, controllerHurtByTargetGoal);
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

    static class SpiderAttackGoal extends MeleeAttackGoal {
        public SpiderAttackGoal(Spider p_33822_) {
            super(p_33822_, 1.0D, true);
        }

        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        protected double getAttackReachSqr(LivingEntity p_33825_) {
            return (double)(4.0F + p_33825_.getBbWidth());
        }
    }
}