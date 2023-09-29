package com.example.wizards;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.Event;

public class ManaRegenerateEvent extends Event {

    private int amount;
    private ManaTotemBlockEntity blockSource;
    private LivingEntity owner;

    public ManaRegenerateEvent(int amount, ManaTotemBlockEntity blockSource, LivingEntity owner) {
        super();
        this.amount = amount;
        this.blockSource = blockSource;
        this.owner = owner;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ManaTotemBlockEntity getBlockSource() {
        return blockSource;
    }

    public void setBlockSource(ManaTotemBlockEntity blockSource) {
        this.blockSource = blockSource;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }
}
