package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class SummonedZombie extends Zombie implements ControlledEntity {

    private static final Logger logger = LogUtils.getLogger();

    private FollowControllerGoal followControllerGoal;

    public SummonedZombie(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
        logger.info("Summoned Zombie");
    }

    protected void addBehaviourGoals() {
//        super.addBehaviourGoals();
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.followControllerGoal = new FollowControllerGoal(this, 1.0D);
        this.goalSelector.addGoal(4, followControllerGoal);
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Pillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
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

    public void setController(LivingEntity controller) {
        this.followControllerGoal.setController(controller);
    }

    @Override
    public LivingEntity getController() {
        return this.followControllerGoal.getController();
    }
}
