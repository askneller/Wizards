package com.example.wizards;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

@HasResult
public class ConsumeManaEvent extends Event {

    private final int amount;
    private final String type;
    private final Player player;

    public ConsumeManaEvent(int amount, String type, Player player) {
        this.amount = amount;
        this.type = type;
        this.player = player;
    }

    public int getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }
}
