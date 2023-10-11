package com.example.wizards;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LargerHumanoid extends SummonedCreature {

//    public final AnimationState idleAnimationState = new AnimationState();
//    private int idleAnimationTimeout;

    public LargerHumanoid(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
//        this.goalSelector.addGoal(1, new FloatGoal(this));

//        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0f));

        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

//    @Override
//    public void tick() {
//        super.tick();
//
//        if (this.level().isClientSide()) {
//            setupAnimationStates();
//        }
//    }

//    private void setupAnimationStates() {
//        if (this.idleAnimationTimeout <= 0) {
//            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
//            this.idleAnimationState.start(this.tickCount);
//        } else {
//            --this.idleAnimationTimeout;
//        }
//    }

//    @Override
//    protected void updateWalkAnimation(float partialTick) {
//        float f;
//        if (this.getPose() == Pose.STANDING) {
//            f = Math.min(partialTick * 6f, 1f);
//        } else {
//            f = 0;
//        }
//
//        this.walkAnimation.update(f, 0.2f);
//    }

}
