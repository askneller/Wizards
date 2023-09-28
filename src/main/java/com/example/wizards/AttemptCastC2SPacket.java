package com.example.wizards;

import com.example.wizards.client.ClientManaPool;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class AttemptCastC2SPacket {

    private static final Logger logger = LogUtils.getLogger();

    private final int spell;
    private final int playerId;

    public AttemptCastC2SPacket(int spell, int playerId) {
        this.spell = spell;
        this.playerId = playerId;
        logger.info("ManaPoolSyncS2CPacket pool construct. {} {}", this.spell, this.playerId);
    }

    public AttemptCastC2SPacket(FriendlyByteBuf buf) {
        this.spell = buf.readInt();
        this.playerId = buf.readInt();
        logger.info("ManaPoolSyncS2CPacket buf construct. spell {} playerId {}", this.spell, this.playerId);
    }

    public void toBytes(FriendlyByteBuf buf) {
        logger.info("toBytes s {} p {}", spell, playerId);
        buf.writeInt(this.spell);
        buf.writeInt(this.playerId);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            // ON THE SERVER?
            ServerPlayer sender = context.getSender();
            logger.info("AttemptCast received {} {}", spell, playerId);
            logger.info("From {}", sender);
            MinecraftForge.EVENT_BUS.post(new AttemptCastEvent(spell, sender));
        });
        return true;
    }
}
