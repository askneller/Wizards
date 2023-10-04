package com.example.wizards;

import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class Spell {

    private String name;
    private List<ManaColor> cost;
    private Class<?> creatureClass;
    private EntityType<?> entityType;

    public Spell(String name, Class<?> creatureClass, EntityType<?> entityType, ManaColor ...cost) {
        this.name = name;
        this.creatureClass = creatureClass;
        this.entityType = entityType;
        this.cost = Arrays.asList(cost);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ManaColor> getCost() {
        return cost;
    }

    public void setCost(List<ManaColor> cost) {
        this.cost = cost;
    }

    public Class<?> getCreatureClass() {
        return creatureClass;
    }

    public void setCreatureClass(Class<?> creatureClass) {
        this.creatureClass = creatureClass;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }
}
