package com.example.wizards.network;

import com.example.wizards.event.PlayerSelectedEntityEvent;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

public class EntitySelectedC2SPacket {

    private static final Logger logger = LogUtils.getLogger();

    private final int entityId;

    public EntitySelectedC2SPacket(int entityId) {
        this.entityId = entityId;
    }

    public EntitySelectedC2SPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            // ON THE SERVER?
            ServerPlayer sender = context.getSender();
            PlayerSelectedEntityEvent event = new PlayerSelectedEntityEvent(entityId, sender);
            MinecraftForge.EVENT_BUS.post(event);
        });
        return true;
    }
}
