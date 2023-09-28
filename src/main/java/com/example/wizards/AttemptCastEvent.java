package com.example.wizards;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class AttemptCastEvent extends Event {

    private int spell;
    private Player player;

    public AttemptCastEvent(int spell, Player player) {
        this.spell = spell;
        this.player = player;
    }

    public int getSpell() {
        return spell;
    }

    public void setSpell(int spell) {
        this.spell = spell;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
