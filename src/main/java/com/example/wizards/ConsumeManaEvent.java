package com.example.wizards;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

import java.util.Collections;
import java.util.List;

@HasResult
public class ConsumeManaEvent extends Event {

    private final int amount;
    private final ManaColor color;
    private final List<ManaColor> cost;
    private final Player player;

    public ConsumeManaEvent(int amount, ManaColor color, Player player) {
        this.amount = amount;
        this.player = player;
        this.color = color;
        this.cost = Collections.emptyList();
    }

    public ConsumeManaEvent(Player player, List<ManaColor> cost) {
        this.amount = 0;
        this.player = player;
        this.color = ManaColor.COLORLESS;
        this.cost = cost;
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

    public List<ManaColor> getCost() {
        return cost;
    }
}
