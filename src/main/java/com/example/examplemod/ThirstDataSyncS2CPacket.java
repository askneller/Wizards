//package com.example.examplemod;
//
//import com.example.examplemod.client.ClientThirstData;
//import com.mojang.logging.LogUtils;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.network.NetworkEvent;
//import org.slf4j.Logger;
//
//import java.util.function.Supplier;
//
//public class ThirstDataSyncS2CPacket {
//
//    private static final Logger logger = LogUtils.getLogger();
//
//    private final int thirst;
//
//    public ThirstDataSyncS2CPacket(int thirst) {
//        this.thirst = thirst;
//    }
//
//    public ThirstDataSyncS2CPacket(FriendlyByteBuf buf) {
//        this.thirst = buf.readInt();
//    }
//
//    public void toBytes(FriendlyByteBuf buf) {
//        buf.writeInt(thirst);
//    }
//
//    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
//        NetworkEvent.Context context = supplier.get();
//        context.enqueueWork(() -> {
//            // HERE WE ARE ON THE CLIENT
//            ClientThirstData.set(thirst);
//        });
//        return true;
//    }
//
//    public boolean handle(NetworkEvent.Context context) {
//        context.enqueueWork(() -> {
//            // HERE WE ARE ON THE CLIENT
//            ClientThirstData.set(thirst);
//            logger.info("Set client thirst {}", thirst);
//        });
//        return true;
//    }
//}
