package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

public class ModEvents {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        logger.info("Registering capability2");
        event.register(PlayerThirst.class);
    }

}
