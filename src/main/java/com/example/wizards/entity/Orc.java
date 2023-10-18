package com.example.wizards.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class Orc extends LargeHumanoid {

//    protected static final Logger logger = LogUtils.getLogger();

    public static final String spell_name = "summon_orc";
    public static final String entity_name = "orc";

    public Orc(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        logger = LogUtils.getLogger();
    }

    public Orc(EntityType<? extends PathfinderMob> entityType, Level level, int power, int toughness) {
        super(entityType, level, power, toughness);
        logger = LogUtils.getLogger();
    }

}
