package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketHandler {

    private static final Logger logger = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = ExampleMod.MOD_VERSION;
    private static final MutableInt ID = new MutableInt(0);
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExampleMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        logger.info("Init");
        register(TestPacket.class, TestPacket::encode, TestPacket::new, TestPacket::handle);
        register(ThirstDataSyncS2CPacket.class, ThirstDataSyncS2CPacket::toBytes, ThirstDataSyncS2CPacket::new, ThirstDataSyncS2CPacket::handle, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <T> void register(Class<T> cls, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, NetworkEvent.Context> handler)
    {
        INSTANCE.registerMessage(ID.getAndIncrement(), cls, encoder, decoder, (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }

    private static <T> void register(Class<T> cls, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, NetworkEvent.Context> handler, NetworkDirection direction)
    {
        INSTANCE.registerMessage(ID.getAndIncrement(), cls, encoder, decoder, (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        }, Optional.of(direction));
    }
}
