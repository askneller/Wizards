package com.example.wizards.client;

import com.example.wizards.ForgeClientEvents;
import com.example.wizards.ManaPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public class ClientManaPool {

    private static ManaPool pool = new ManaPool();

    public static void set(ManaPool pool) {
        ClientManaPool.pool = pool;
    }

    public static ManaPool getPlayerPool() {
        return ClientManaPool.pool;
    }

    public static void particles(BlockPos blockPos) {
        // TODO move this somewhere else
        ClientLevel level = Minecraft.getInstance().level;
        ForgeClientEvents.makeParticles(level, Minecraft.getInstance().player, blockPos);
    }
}
