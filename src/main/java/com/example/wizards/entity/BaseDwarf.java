package com.example.wizards.entity;

import com.example.wizards.entity.ai.AnimatedAttackGoal;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BaseDwarf extends SummonedCreature {

    public BaseDwarf(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0f));

        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.registerLookAndAttackGoals();

        populateDefaultEquipmentSlots(this.getRandom(), this.level().getCurrentDifficultyAt(this.blockPosition()));
    }

    @Override
    protected void registerLookAndAttackGoals() {
        this.animatedAttackGoal = new AnimatedAttackGoal<>(this, 1.0, true, 14, 4);
        this.goalSelector.addGoal(2, this.animatedAttackGoal);

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.entityData.set(ATTACK_ANIMATION_DURATION, this.animatedAttackGoal.getAnimationTotalTicks());
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance instance) {
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.IRON_AXE));
    }

}
