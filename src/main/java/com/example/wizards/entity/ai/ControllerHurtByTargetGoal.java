package com.example.wizards.entity.ai;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class ControllerHurtByTargetGoal extends HurtByTargetGoal {

    private static final Logger logger = LogUtils.getLogger();

    private static final TargetingConditions HURT_BY_TARGETING =
            TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private int timestamp;
    private LivingEntity controller;

    public ControllerHurtByTargetGoal(PathfinderMob mob, Class<?>... toIgnoreDamage) {
        super(mob, toIgnoreDamage);
    }


    public boolean canUse() {
        if (this.controller == null) {
//            logger.info("No controller");
            return false;
        }
        int i = this.controller.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.controller.getLastHurtByMob();
//        if (livingentity != null)
//            logger.info("Last hurt time {}, mob {}", i, livingentity);
        if (i != this.timestamp && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER &&
                    this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
//                if (this.mob.level().getGameTime() % 40 == 0)
//                    logger.info("Here 1");
                return false;
            } else {
//                if (this.mob.level().getGameTime() % 40 == 0)
//                    logger.info("Can attack {}", this.canAttack(livingentity, HURT_BY_TARGETING));
                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        } else {
//            if (this.mob.level().getGameTime() % 40 == 0)
//                logger.info("Here 2");
            return false;
        }
    }

    public void start() {
        super.start();
//        logger.info("Starting");
        this.mob.setTarget(this.controller.getLastHurtByMob());
        this.targetMob = this.controller.getLastHurtByMob();
        this.timestamp = this.controller.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
//        logger.info("Target {}", this.targetMob);
//        if (this.alertSameType) {
//            this.alertOthers();
//        }
    }

    public void setController(LivingEntity controller) {
        this.controller = controller;
    }
}
