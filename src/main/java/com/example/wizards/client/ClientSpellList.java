package com.example.wizards.client;

import com.example.wizards.Spell;
import com.example.wizards.Spells;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class ClientSpellList {

    private static final Logger logger = LogUtils.getLogger();

    private static int index = 0;
    public static int[] spells = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static Spell[] spells2 = {
            Spells.SUMMON_ZOMBIE,
            Spells.SUMMON_SKELETON
    };

    public static void inc() {
        ++index;
        if (index >= spells.length)
            index = 0;
    }

    public static void dec() {
        --index;
        if (index < 0)
            index = spells.length - 1;
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
                return "Polar Bear";
            case 7:
                return "Phantom";
            case 8:
                return "Slime";
            case 9:
                return "Skeleton Archer";
            default:
                return "None";
        }
    }

    public static String getKey(int spellNum) {
        switch (spellNum) {
            case 1:
                return null;
            case 2:
                return null;
            case 3:
                return "summonzombie";
            case 4:
                return "summonskeleton";
            case 5:
                return "summonspider";
            case 6:
                return "summonpolarbear";
            case 7:
                return "summonphantom";
            case 8:
                return "summonslime";
            case 9:
                return "summonskeletonarcher";
            default:
                return null;
        }
    }
}
