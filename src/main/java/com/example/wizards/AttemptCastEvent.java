package com.example.wizards;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class AttemptCastEvent extends Event {

    private int spell;
    private Player player;
    private BlockPos blockPos;

    public AttemptCastEvent(int spell, Player player) {
        this(spell, player, BlockPos.ZERO);
    }

    public AttemptCastEvent(int spell, Player player, BlockPos blockPos) {
        this.spell = spell;
        this.player = player;
        this.blockPos = blockPos;
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

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
