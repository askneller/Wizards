package com.example.wizards;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.Event;

public class ManaRegenerateEvent extends Event {

    private int amount;
    private BlockEntity blockSource;
    private LivingEntity owner;

    public ManaRegenerateEvent(int amount, BlockEntity blockSource, LivingEntity owner) {
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

    public BlockEntity getBlockSource() {
        return blockSource;
    }

    public void setBlockSource(BlockEntity blockSource) {
        this.blockSource = blockSource;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }
}
