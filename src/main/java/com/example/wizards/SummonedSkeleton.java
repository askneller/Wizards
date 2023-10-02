package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class SummonedSkeleton extends Skeleton implements ControlledEntity {

    private static final Logger logger = LogUtils.getLogger();

    private FollowControllerGoal followControllerGoal;

    public SummonedSkeleton(EntityType<? extends Skeleton> p_33570_, Level p_33571_) {
        super(p_33570_, p_33571_);
        logger.info("SummonedSkeleton here");
    }

    protected void registerGoals() {
//        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
//        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.followControllerGoal = new FollowControllerGoal(this, 1.0D);
        this.goalSelector.addGoal(5, followControllerGoal);
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                (le -> {
                    if (le instanceof ControlledEntity ce) {
                        return !this.getController().equals(ce.getController());
                    }
                    // TODO implement interface so that players will be targeted
                    return false; // !this.getController().equals(le);
                })));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void setController(LivingEntity livingEntity) {
        followControllerGoal.setController(livingEntity);
    }

    @Override
    public LivingEntity getController() {
        return followControllerGoal.getController();
    }
}
