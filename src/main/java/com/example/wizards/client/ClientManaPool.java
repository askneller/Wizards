package com.example.wizards.client;

import com.example.wizards.magic.ManaPool;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class ClientManaPool {

    private static final Logger logger = LogUtils.getLogger();

    private static ManaPool pool = new ManaPool();

    public static void set(ManaPool pool) {
        logger.info("Setting here {}", pool);
        ClientManaPool.pool = pool;
    }

    public static ManaPool getPlayerPool() {
        return ClientManaPool.pool;
    }

}
