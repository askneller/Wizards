package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

// TODO will become and interface on ManaTotemBlockEntity
public class ManaSource {

    private static final Logger logger = LogUtils.getLogger();

    private int id;
    private int amount;
    private ManaColor color;
    private boolean available = true;
    private ManaTotemBlockEntity entity;

    public ManaSource() {
//        logger.info("Create empty");
    }

    public ManaSource(int id, int amount, ManaColor color, boolean available, ManaTotemBlockEntity source) {
        this.id = id;
        this.amount = amount;
        this.color = color;
        this.available = available;
        this.entity = source;
//        logger.info("Create params");
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

    public boolean isAvailable() {
        return available;
    }

    public int spend() {
        if (isAvailable()) {
            this.available = false;
            if (this.entity != null) {
                this.entity.spent(this);
            } else {
                logger.error("Cannot reset source {} as entity is null", this);
            }
            return amount;
        }
        return 0;
    }

    public void replenish() {
        if (!isAvailable()) {
            this.available = true;
//            logger.info("replenish {}", id);
        }
    }

    // ManaSources are not saved as they will be reloaded with their corresponding ManaTotemBlockEntity
    // and sent to the user via an AddManaSourceEvent
    public void saveNBTDate(CompoundTag nbt) {
        nbt.putInt("mana_source_amount", amount);
        nbt.putInt("mana_source_id", id);
        nbt.putInt("mana_source_color", color.ordinal());
        nbt.putBoolean("mana_source_available", available);
    }

    public void loadNBTData(CompoundTag nbt) {
        amount = nbt.getInt("mana_source_amount");
        id = nbt.getInt("mana_source_id");
        color = ManaColor.values()[nbt.getInt("mana_source_color")];
        available = nbt.getBoolean("mana_source_available");
    }

    @Override
    public String toString() {
        return "ManaSource{" +
                "id=" + id +
                ", amount=" + amount +
                ", color=" + color +
                ", available=" + available +
                '}';
    }
}
