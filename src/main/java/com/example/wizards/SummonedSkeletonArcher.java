package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class SummonedSkeletonArcher extends SummonedSkeleton {

    private RangedBowAttackGoal<SummonedSkeletonArcher> bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);

    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false) {
        public void stop() {
            super.stop();
            SummonedSkeletonArcher.this.setAggressive(false);
        }

        public void start() {
            super.start();
            SummonedSkeletonArcher.this.setAggressive(true);
        }
    };

    private static final Logger logger = LogUtils.getLogger();

    public SummonedSkeletonArcher(EntityType<? extends Skeleton> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();

        if (this.bowGoal == null) {
            bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
        }

        populateDefaultEquipmentSlots(this.random, this.level().getCurrentDifficultyAt(this.blockPosition()));
    }

    protected boolean isSunBurnTick() {
        return false;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance instance) {
        super.populateDefaultEquipmentSlots(randomSource, instance);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public void reassessWeaponGoal() {
        if (!this.level().isClientSide) {
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
//                logger.info("Added");
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }

        }
    }

}
