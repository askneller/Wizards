package com.example.wizards.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.level.Level;

public class SummonedPolarBear extends SummonedCreature {

    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(PolarBear.class, EntityDataSerializers.BOOLEAN);

    public static final String spell_name = "summon_polar_bear";
    public static final String entity_name = "summoned_polar_bear";

    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private int warningSoundTicks;

    public SummonedPolarBear(EntityType<? extends PathfinderMob> p_29519_, Level p_29520_) {
        super(p_29519_, p_29520_);
        logger = LogUtils.getLogger();
    }

    public SummonedPolarBear(EntityType<? extends PathfinderMob> p_29519_, Level p_29520_, int power, int toughness) {
        super(p_29519_, p_29520_, power, toughness);
        logger = LogUtils.getLogger();
    }

    @Override
    protected void registerLookAndAttackGoals() {
        super.registerLookAndAttackGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SummonedPolarBear.PolarBearMeleeAttackGoal());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }

            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            if (this.isStanding()) {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            } else {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
    }

    public float getStandingAnimationScale(float p_29570_) {
        return Mth.lerp(p_29570_, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean p_29568_) {
        this.entityData.set(DATA_STANDING_ID, p_29568_);
    }

    class PolarBearMeleeAttackGoal extends MeleeAttackGoal {
        public PolarBearMeleeAttackGoal() {
            super(SummonedPolarBear.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity p_29589_, double p_29590_) {
            double d0 = this.getAttackReachSqr(p_29589_);
            if (p_29590_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_29589_);
                SummonedPolarBear.this.setStanding(false);
            } else if (p_29590_ <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    SummonedPolarBear.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    SummonedPolarBear.this.setStanding(true);
                    SummonedPolarBear.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                SummonedPolarBear.this.setStanding(false);
            }

        }

        public void stop() {
            SummonedPolarBear.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity p_29587_) {
            return (double)(4.0F + p_29587_.getBbWidth());
        }
    }

}
