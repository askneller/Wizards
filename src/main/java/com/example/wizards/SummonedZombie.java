package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class SummonedZombie extends Zombie implements ControlledEntity {

    private static final Logger logger = LogUtils.getLogger();

    private FollowControllerGoal followControllerGoal;
    private ControllerHurtByTargetGoal controllerHurtByTargetGoal;

    public SummonedZombie(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
//        logger.info("Summoned Zombie");
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.followControllerGoal = new FollowControllerGoal(this, 1.0D);
        this.goalSelector.addGoal(4, followControllerGoal);
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        // TODO add assigned target goal
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.controllerHurtByTargetGoal = new ControllerHurtByTargetGoal(this);
        this.targetSelector.addGoal(5, controllerHurtByTargetGoal);
        // Attack other players
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                true,
                (le -> !this.getController().equals(le))));
        // Attack mobs controlled by other players
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                true,
                (le -> {
                    if (le instanceof ControlledEntity ce) {
                        return !this.getController().equals(ce.getController());
                    }
                    return false;
                })));
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    // A lot of Monsters are removed under various conditions, including being "far away"
    // Mob.removeWhenFarAway defaults to true
    // TODO may reverse this and remove summoned monsters when they get too far away
    @Override
    public boolean removeWhenFarAway(double p_27598_) {
        return false;
    }

    @Override
    public void setController(LivingEntity controller) {
        this.followControllerGoal.setController(controller);
        this.controllerHurtByTargetGoal.setController(controller);
    }

    @Override
    public LivingEntity getController() {
        return this.followControllerGoal.getController();
    }
}
