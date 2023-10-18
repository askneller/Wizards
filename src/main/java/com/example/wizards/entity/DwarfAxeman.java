package com.example.wizards.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class DwarfAxeman extends BaseDwarf {

//    protected static final Logger logger = LogUtils.getLogger();

    public static final String spell_name = "summon_dwarf_axeman";

    public DwarfAxeman(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        logger = LogUtils.getLogger();
    }

    public DwarfAxeman(EntityType<? extends PathfinderMob> entityType, Level level, int power, int toughness) {
        super(entityType, level, power, toughness);
        logger = LogUtils.getLogger();
    }

    @Override
    protected void registerLookAndAttackGoals() {
        super.registerLookAndAttackGoals();

        populateDefaultEquipmentSlots(this.getRandom(), this.level().getCurrentDifficultyAt(this.blockPosition()));
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance instance) {
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.IRON_AXE));
    }

}
