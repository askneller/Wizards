package com.example.wizards;

import com.example.wizards.client.ClientManaPool;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class CastResultS2CPacket {

    private static final Logger logger = LogUtils.getLogger();

    public int spell;
    public boolean result;
    public BlockPos blockPos;

    public CastResultS2CPacket(int spell, boolean result, BlockPos blockPos) {
        this.spell = spell;
        this.result = result;
        this.blockPos = blockPos;
    }

    public CastResultS2CPacket(FriendlyByteBuf buf) {
        this.spell = buf.readInt();
        this.result = buf.readBoolean();
        this.blockPos = buf.readBlockPos();
        logger.info("CastResultS2CPacket buf construct. {}", this);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(spell);
        buf.writeBoolean(result);
        buf.writeBlockPos(blockPos);
        logger.info("CastResultS2CPacket toBytes {}", this);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE CLIENT
            ClientManaPool.particles(blockPos);
        });
        return true;
    }

    @Override
    public String toString() {
        return "CastResultS2CPacket{" +
                "spell=" + spell +
                ", result=" + result +
                ", blockPos=" + blockPos +
                '}';
    }
}
