package com.example.wizards.network;

import com.example.wizards.magic.ManaPool;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.example.wizards.Wizards.MOD_ID;
import static com.example.wizards.Wizards.MOD_VERSION;

public class PacketHandler {

    private static final Logger logger = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = MOD_VERSION;
    private static final MutableInt ID = new MutableInt(0);
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        logger.info("Init PacketHandler");
        register(ManaPoolSyncS2CPacket.class, ManaPoolSyncS2CPacket::toBytes, ManaPoolSyncS2CPacket::new,
                ManaPoolSyncS2CPacket::handle, NetworkDirection.PLAY_TO_CLIENT);

        register(AttemptCastC2SPacket.class, AttemptCastC2SPacket::toBytes, AttemptCastC2SPacket::new,
                AttemptCastC2SPacket::handle, NetworkDirection.PLAY_TO_SERVER);

        register(CastResultS2CPacket.class, CastResultS2CPacket::toBytes, CastResultS2CPacket::new,
                CastResultS2CPacket::handle, NetworkDirection.PLAY_TO_CLIENT);

        register(EntitySelectedC2SPacket.class, EntitySelectedC2SPacket::toBytes, EntitySelectedC2SPacket::new,
                EntitySelectedC2SPacket::handle, NetworkDirection.PLAY_TO_SERVER);

    }

    private static <T> void register(Class<T> cls,
                                     BiConsumer<T, FriendlyByteBuf> encoder,
                                     Function<FriendlyByteBuf, T> decoder,
                                     BiConsumer<T, NetworkEvent.Context> handler) {
        INSTANCE.registerMessage(ID.getAndIncrement(), cls, encoder, decoder, (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }

    private static <T> void register(Class<T> cls,
                                     BiConsumer<T, FriendlyByteBuf> encoder,
                                     Function<FriendlyByteBuf, T> decoder,
                                     BiConsumer<T, NetworkEvent.Context> handler,
                                     NetworkDirection direction) {
        INSTANCE.registerMessage(ID.getAndIncrement(), cls, encoder, decoder, (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        }, Optional.of(direction));
    }

    public static void sendToPlayer(ServerPlayer player, ManaPool pool) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new ManaPoolSyncS2CPacket(pool));
    }

    public static void sendToPlayer(ServerPlayer player, int spell, boolean success, BlockPos pos) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new CastResultS2CPacket(spell, success, pos));
    }

    public static void sendToServer(Player player, int spellId) {
        INSTANCE.sendToServer(new AttemptCastC2SPacket(spellId, player.getId()));
    }

    public static void sendToServer(Player player, int spellId, BlockPos blockPos) {
        INSTANCE.sendToServer(new AttemptCastC2SPacket(spellId, player.getId(), blockPos));
    }

    public static void sendToServer(int entityId) {
        INSTANCE.sendToServer(new EntitySelectedC2SPacket(entityId));
    }
}
