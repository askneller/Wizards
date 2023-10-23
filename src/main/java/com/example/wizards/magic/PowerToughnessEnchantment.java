package com.example.wizards.magic;

import com.example.wizards.entity.SummonedCreature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class PowerToughnessEnchantment extends SpellEffect {

    private int power;
    private int toughness;

    public PowerToughnessEnchantment(int power, int toughness) {
        this.power = power;
        this.toughness = toughness;
    }

    @Override
    public void apply(Level level, Entity entity) {
        if (entity instanceof SummonedCreature sc) {
            sc.addPowerToughness(power, toughness);
        }
    }

    @Override
    public void remove(Level level, Entity entity) {
        if (entity instanceof SummonedCreature sc) {
            sc.removePowerToughness(power, toughness);
        }
    }
}
