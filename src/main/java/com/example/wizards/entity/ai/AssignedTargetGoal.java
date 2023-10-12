package com.example.wizards.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class AssignedTargetGoal extends TargetGoal {

    private LivingEntity selectedTarget;

    public AssignedTargetGoal(Mob mob) {
        super(mob, false);
    }

    @Override
    public boolean canUse() {
        return this.canAttack(this.selectedTarget, TargetingConditions.DEFAULT);
    }

    public void start() {
        super.start();
        this.mob.setTarget(this.selectedTarget);
        this.targetMob = this.selectedTarget;
    }

    // TODO ability to clear assigned target
    public void assignTarget(LivingEntity target) {
        this.selectedTarget = target;
    }
}
