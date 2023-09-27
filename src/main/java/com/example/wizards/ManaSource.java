package com.example.wizards;

import net.minecraft.nbt.CompoundTag;

public class ManaSource {

    private int id;
    private int amount;
    private String type = "generic"; // todo generic for now,  will become color

    public ManaSource() {
    }

    public ManaSource(int id, int amount, String type) {
        this.id = id;
        this.amount = amount;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void saveNBTDate(CompoundTag nbt) {
        nbt.putString("mana_source_type", type);
        nbt.putInt("mana_source_amount", amount);
        nbt.putInt("mana_source_id", id);
    }

    public void loadNBTData(CompoundTag nbt) {
        type = nbt.getString("mana_source_type");
        amount = nbt.getInt("mana_source_amount");
        id = nbt.getInt("mana_source_id");
    }

    @Override
    public String toString() {
        return "ManaSource{" +
                "id=" + id +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                '}';
    }
}
