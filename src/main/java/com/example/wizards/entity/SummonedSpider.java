package com.example.wizards.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;

public class SummonedSpider extends SummonedCreature {

    public static final String spell_name = "summon_spider";
    public static final String entity_name = "summoned_spider";

    public SummonedSpider(EntityType<? extends PathfinderMob> p_33786_, Level p_33787_) {
        super(p_33786_, p_33787_);
        logger = LogUtils.getLogger();
    }

    public SummonedSpider(EntityType<? extends PathfinderMob> p_33786_, Level p_33787_, int power, int toughness) {
        super(p_33786_, p_33787_, power, toughness);
        logger = LogUtils.getLogger();
    }

    @Override
    protected void registerLookAndAttackGoals() {
        super.registerLookAndAttackGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new SummonedSpider.SpiderAttackGoal(this));
    }

    static class SpiderAttackGoal extends MeleeAttackGoal {
        public SpiderAttackGoal(SummonedSpider p_33822_) {
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
