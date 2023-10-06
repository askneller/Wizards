package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FollowControllerGoal extends Goal {

    private static final Logger logger = LogUtils.getLogger();
    private static final double DISTANCE = 5.0;
    private static final double DISTANCE_SQ = DISTANCE * DISTANCE;
    private static final double MAX_DISTANCE = 20.0;
    private static final double MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;

    private final PathfinderMob entity;

    @Nullable
    private LivingEntity parent;
    @Nullable
    private LivingEntity controller;
    private final double speedModifier;
    private int timeToRecalcPath;

    public FollowControllerGoal(PathfinderMob entity, double speedMod) {
        this.entity = entity;
        this.speedModifier = speedMod;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        this.parent = this.controller;
        return this.parent != null && this.entity.distanceToSqr(this.parent) >= DISTANCE_SQ;
    }

    public boolean canContinueToUse() {
        if (!this.parent.isAlive()) {
            return false;
        } else {
            double d0 = this.entity.distanceToSqr(this.parent);
            // TODO change distances
            return !(d0 < DISTANCE_SQ) && !(d0 > MAX_DISTANCE_SQ);
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.parent = null;
    }

    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.entity.getNavigation().moveTo(this.parent, this.speedModifier);
        }
    }

    public void setController(@Nullable LivingEntity controller) {
        this.controller = controller;
    }

    public @Nullable LivingEntity getController() {
        return this.controller;
    }

}
