package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ManaPoolProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private static final Logger logger = LogUtils.getLogger();

    public static Capability<ManaPool> MANA_POOL = CapabilityManager.get(new CapabilityToken<ManaPool>() { });

    private ManaPool manaPool = null;
    private final LazyOptional<ManaPool> optional = LazyOptional.of(this::createManaPool);

    private ManaPool createManaPool() {
        if (this.manaPool == null) {
            this.manaPool = new ManaPool();
        }

        return this.manaPool;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == MANA_POOL) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createManaPool().saveNBTDate(nbt);
        logger.info("Serialized NBT data: {}", nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        logger.info("Deserializing NBT from: {}", nbt);
        createManaPool().loadNBTData(nbt);
    }
}
