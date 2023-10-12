package com.example.wizards;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class Orc extends LargeHumanoid {

    public static final String spell_name = "summon_orc";

    public Orc(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

}
