package com.example.wizards;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class AddManaSourceEvent extends Event {

    private LivingEntity owner;
    private ManaTotemBlockEntity source;

    public AddManaSourceEvent(LivingEntity owner, ManaTotemBlockEntity source) {
        this.owner = owner;
        this.source = source;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public ManaTotemBlockEntity getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "AddManaSourceEvent{" +
                "owner=" + owner +
                ", source=" + source +
                '}';
    }
}
