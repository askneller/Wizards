package com.example.wizards;

import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class Spell {

    private String name;
    private List<ManaColor> cost;
    private EntityType<?> entityType;

    // Summoned creature
    private Class<?> creatureClass;

    // Projectile
    private Class<?> projectileClass;
    private int quantity = 1;
    private float power = 0.0f;
    private float spread = 0.0f;

    public Spell(String name) {
        this.name = name;
    }

    public Spell(String name, Class<?> creatureClass, EntityType<?> entityType, ManaColor ...cost) {
        this.name = name;
        this.creatureClass = creatureClass;
        this.entityType = entityType;
        this.cost = Arrays.asList(cost);
    }

    public String getName() {
        return name;
    }

    public List<ManaColor> getCost() {
        return cost;
    }

    public Class<?> getCreatureClass() {
        return creatureClass;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public Class<?> getProjectileClass() {
        return projectileClass;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPower() {
        return power;
    }

    public float getSpread() {
        return spread;
    }



    public static class Builder {
        private Spell spell;

        public Builder(String name) {
            this.spell = new Spell(name);
        }

        public Builder withCreature(Class<?> creatureClass, EntityType<?> entityType) {
            this.spell.creatureClass = creatureClass;
            this.spell.entityType = entityType;
            return this;
        }

        public Builder withProjectile(Class<?> projectile, EntityType<?> entityType) {
            this.spell.projectileClass = projectile;
            this.spell.entityType = entityType;
            return this;
        }

        public Builder withQuantity(int quantity) {
            this.spell.quantity = quantity;
            return this;
        }

        public Builder withPower(float power) {
            this.spell.power = power;
            return this;
        }

        public Builder withSpread(float spread) {
            this.spell.spread = spread;
            return this;
        }

        public Builder withCost(ManaColor ...cost) {
            this.spell.cost = Arrays.asList(cost);
            return this;
        }

        public Spell build() {
            return spell;
        }
    }
}
