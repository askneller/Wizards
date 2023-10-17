package com.example.wizards.entity;

import com.example.wizards.entity.ai.AnimatedAttackGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class BaseDwarf extends SummonedCreature {

    public BaseDwarf(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerLookAndAttackGoals() {
        this.animatedAttackGoal = new AnimatedAttackGoal<>(this, 1.0, true, 14, 4);
        this.goalSelector.addGoal(2, this.animatedAttackGoal);

        this.entityData.set(ATTACK_ANIMATION_DURATION, this.animatedAttackGoal.getAnimationTotalTicks());
    }

}
