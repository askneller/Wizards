package com.example.wizards;

import com.example.wizards.client.ClientManaPool;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class ManaPoolSyncS2CPacket {

    private static final Logger logger = LogUtils.getLogger();

    private final ManaPool pool;

    public ManaPoolSyncS2CPacket(ManaPool pool) {
        this.pool = pool;
//        logger.info("ManaPoolSyncS2CPacket pool construct. {}", this.pool);
    }

    public ManaPoolSyncS2CPacket(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
//        logger.info("Read tag. {}", tag);
        this.pool = new ManaPool();
        this.pool.loadNBTData(tag);
//        logger.info("ManaPoolSyncS2CPacket buf construct. {}", this.pool);
    }

    public void toBytes(FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        this.pool.saveNBTDate(tag);
//        logger.info("saved to tag. {}", tag);
        buf.writeNbt(tag);
    }

//    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
//        NetworkEvent.Context context = supplier.get();
//        context.enqueueWork(() -> {
//            // HERE WE ARE ON THE CLIENT
//            ClientManaPool.set(pool);
//        });
//        return true;
//    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE CLIENT
            ClientManaPool.set(pool);
//            logger.info("Set client mana pool {}", pool);
        });
        return true;
    }
}
