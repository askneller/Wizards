package com.example.wizards.network;

import com.example.wizards.event.AttemptCastEvent;
import com.example.wizards.client.ClientSpellList;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class AttemptCastC2SPacket {

    private static final Logger logger = LogUtils.getLogger();

    private final int spell;
    private final int playerId;
    private final BlockPos blockPos;
    private final int targetEntityId;

    public AttemptCastC2SPacket(int spell, int playerId) {
        this(spell, playerId, BlockPos.ZERO, -1);
    }

    public AttemptCastC2SPacket(int spell, int playerId, BlockPos blockPos) {
        this(spell, playerId, blockPos, -1);
    }

    public AttemptCastC2SPacket(int spell, int playerId, BlockPos blockPos, int targetEntityId) {
        this.spell = spell;
        this.playerId = playerId;
        this.blockPos = blockPos;
        this.targetEntityId = targetEntityId;
    }

    public AttemptCastC2SPacket(int spell, int playerId, int targetEntityId) {
        this(spell, playerId, BlockPos.ZERO, targetEntityId);
    }

    public AttemptCastC2SPacket(FriendlyByteBuf buf) {
        this.spell = buf.readInt();
        this.playerId = buf.readInt();
        this.blockPos = buf.readBlockPos();
        this.targetEntityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.spell);
        buf.writeInt(this.playerId);
        buf.writeBlockPos(this.blockPos);
        buf.writeInt(this.targetEntityId);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            // ON THE SERVER?
            ServerPlayer sender = context.getSender();
            AttemptCastEvent event = new AttemptCastEvent(spell, sender, blockPos, targetEntityId);
            // TODO will not work on network players, move to client-side packet creation
            event.setSpellName(ClientSpellList.getKey(spell));
            MinecraftForge.EVENT_BUS.post(event);
        });
        return true;
    }
}
