package com.example.wizards.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class AttemptCastEvent extends Event {

    private int spell;
    private String spellName;
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

    public AttemptCastEvent(int spell, Player player, BlockPos blockPos, String name) {
        this.spell = spell;
        this.player = player;
        this.blockPos = blockPos;
        this.spellName = name;
    }

    public int getSpell() {
        return spell;
    }

    public void setSpell(int spell) {
        this.spell = spell;
    }

    public String getSpellName() {
        return spellName;
    }

    public void setSpellName(String spellName) {
        this.spellName = spellName;
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

    @Override
    public String toString() {
        return "AttemptCastEvent{" +
                "spell=" + spell +
                ", spellName='" + spellName + '\'' +
                ", player=" + player +
                ", blockPos=" + blockPos +
                '}';
    }
}
