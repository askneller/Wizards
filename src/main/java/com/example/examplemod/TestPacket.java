//package com.example.examplemod;
//
//import com.mojang.logging.LogUtils;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.Entity;
//import net.minecraftforge.network.NetworkEvent;
//import org.slf4j.Logger;
//
//import java.nio.charset.Charset;
//
//public class TestPacket {
//
//    private static final Logger logger = LogUtils.getLogger();
//
//    private final String message;
//
//    public TestPacket(String message) {
//        this.message = message;
//    }
//
//    TestPacket(FriendlyByteBuf buf) {
//        this.message = buf.readCharSequence(1, Charset.defaultCharset()).toString();
//    }
//
//    void encode(FriendlyByteBuf buffer)
//    {
//        buffer.writeCharSequence(message, Charset.defaultCharset());
//    }
//
//    void handle(NetworkEvent.Context context)
//    {
//        logger.info("Handling message: {}", message);
//        context.enqueueWork(() -> {
//            final ServerPlayer sender = context.getSender();
//            if (sender != null) {
////                TestClass.logMessage("TestPacket: " + message, sender);
//            }
//        });
//    }
//
//}
