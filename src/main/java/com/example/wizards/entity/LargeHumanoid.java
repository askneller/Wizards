package com.example.wizards.entity;

import com.example.wizards.entity.ai.AnimatedAttackGoal;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class LargeHumanoid extends SummonedCreature {

    public LargeHumanoid(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public LargeHumanoid(EntityType<? extends PathfinderMob> entityType, Level level, int power, int toughness) {
        super(entityType, level, power, toughness);
    }

    @Override
    protected void registerLookAndAttackGoals() {
        this.animatedAttackGoal = new AnimatedAttackGoal<>(this, 1.0, true, 23, 7);
        this.goalSelector.addGoal(2, this.animatedAttackGoal);

        this.entityData.set(ATTACK_ANIMATION_DURATION, this.animatedAttackGoal.getAnimationTotalTicks());
    }

}
