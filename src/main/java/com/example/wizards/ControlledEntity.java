package com.example.wizards;

import net.minecraft.world.entity.LivingEntity;

public interface ControlledEntity {

    void setController(LivingEntity livingEntity);

    LivingEntity getController();

    void assignTarget(LivingEntity livingEntity);
}
