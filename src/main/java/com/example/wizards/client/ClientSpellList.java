package com.example.wizards.client;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class ClientSpellList {

    private static final Logger logger = LogUtils.getLogger();

    private static int index = 0;

    public static int[] spells = {1, 2, 3, 4, 5, 6, 7, 8};
    public static boolean keyDown = false;

    public static void inc() {
        ++index;
        if (index >= spells.length)
            index = 0;
//        logger.info("New index {}, spell {}", index, spells[index]);
    }

    public static void dec() {
        --index;
        if (index < 0)
            index = spells.length - 1;
//        logger.info("New index {}, spell {}", index, spells[index]);
    }

    public static int getSelected() {
        return spells[index];
    }

    public static String getSelectedName() {
        switch (spells[index]) {
            case 1:
                return "SmallFireball";
            case 2:
                return "LargeFireball";
            case 3:
                return "Zombie";
            case 4:
                return "Skeleton";
            case 5:
                return "Spider";
            case 6:
                return "Piglin";
            case 7:
                return "Witch";
            case 8:
                return "Slime";
            default:
                return "None";
        }
    }
}
