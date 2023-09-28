package com.example.wizards;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

@HasResult
public class ConsumeManaEvent extends Event {

    private final int amount;
    private final ManaColor color;
    private final Player player;

    public ConsumeManaEvent(int amount, ManaColor color, Player player) {
        this.amount = amount;
        this.player = player;
        this.color = color;
    }

    public int getAmount() {
        return amount;
    }

    public ManaColor getColor() {
        return color;
    }

    public Player getPlayer() {
        return player;
    }
}
