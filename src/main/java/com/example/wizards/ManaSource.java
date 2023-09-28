package com.example.wizards;

import net.minecraft.nbt.CompoundTag;

public class ManaSource {

    private int id;
    private int amount;
    private ManaColor color;

    public ManaSource() {
    }

    public ManaSource(int id, int amount, ManaColor color) {
        this.id = id;
        this.amount = amount;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isEmpty() {
        return amount == 0;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public void subtractAmount(int amount) {
        this.amount = Math.max(this.amount - amount, 0);
    }

    public ManaColor getColor() {
        return color;
    }

    public void saveNBTDate(CompoundTag nbt) {
        nbt.putInt("mana_source_amount", amount);
        nbt.putInt("mana_source_id", id);
        nbt.putInt("mana_source_color", color.ordinal());
    }

    public void loadNBTData(CompoundTag nbt) {
        amount = nbt.getInt("mana_source_amount");
        id = nbt.getInt("mana_source_id");
        color = ManaColor.values()[nbt.getInt("mana_source_color")];
    }

    @Override
    public String toString() {
        return "ManaSource{" +
                "id=" + id +
                ", amount=" + amount +
                ", color=" + color +
                '}';
    }
}
