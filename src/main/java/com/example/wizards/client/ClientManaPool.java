package com.example.wizards.client;

import com.example.wizards.ManaPool;

public class ClientManaPool {

    private static ManaPool pool = new ManaPool();

    public static void set(ManaPool pool) {
        ClientManaPool.pool = pool;
    }

    public static ManaPool getPlayerPool() {
        return ClientManaPool.pool;
    }
}
