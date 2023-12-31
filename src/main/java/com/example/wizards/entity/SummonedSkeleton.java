package com.example.wizards.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SummonedSkeleton extends SummonedCreature implements RangedAttackMob {

    public static final String spell_name = "summon_skeleton";
    public static final String entity_name = "summoned_skeleton";

    protected RangedBowAttackGoal<SummonedSkeleton> bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
    protected final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false) {
        public void stop() {
            super.stop();
            SummonedSkeleton.this.setAggressive(false);
        }

        public void start() {
            super.start();
            SummonedSkeleton.this.setAggressive(true);
        }
    };

    public SummonedSkeleton(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        logger = LogUtils.getLogger();
    }

    public SummonedSkeleton(EntityType<? extends PathfinderMob> entityType, Level level, int power, int toughness) {
        super(entityType, level, power, toughness);
        logger = LogUtils.getLogger();

        this.reassessWeaponGoal();
    }

    protected void registerLookAndAttackGoals() {
    }

    public void reassessWeaponGoal() {
        if (this.level() != null && !this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem));
            if (itemstack.is(Items.BOW)) {
                int i = 20;
                if (this.level().getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }

                this.bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {

    }
}
