package com.example.wizards;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;

public class SummonedCreatureZombie extends SummonedCreature {

    public static final String key = "summonedzombie2";

    public SummonedCreatureZombie(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerLookAndAttackGoals() {
        this.goalSelector.addGoal(2, new SummonedCreatureZombie.ZombieAttackGoal(this, 1.0D, false));
    }

    // ====================================================================================== //
    public class ZombieAttackGoal extends MeleeAttackGoal {
        private final SummonedCreatureZombie zombie;
        private int raiseArmTicks;

        public ZombieAttackGoal(SummonedCreatureZombie p_26019_, double p_26020_, boolean p_26021_) {
            super(p_26019_, p_26020_, p_26021_);
            this.zombie = p_26019_;
        }

        public void start() {
            super.start();
            this.raiseArmTicks = 0;
        }

        public void stop() {
            super.stop();
            this.zombie.setAggressive(false);
        }

        public void tick() {
            super.tick();
            ++this.raiseArmTicks;
            if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
                this.zombie.setAggressive(true);
            } else {
                this.zombie.setAggressive(false);
            }

        }
    }
}