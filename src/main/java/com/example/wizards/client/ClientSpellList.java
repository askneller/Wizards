package com.example.wizards.client;

import com.example.wizards.entity.DwarfAxeman;
import com.example.wizards.magic.ManaColor;
import com.example.wizards.entity.Orc;
import com.example.wizards.magic.Spell;
import com.example.wizards.magic.Spells;
import com.example.wizards.entity.SummonedSlime;
import com.example.wizards.entity.SummonedZombie;
import com.example.wizards.entity.SummonedPolarBear;
import com.example.wizards.entity.SummonedSkeleton;
import com.example.wizards.entity.SummonedSkeletonArcher;
import com.example.wizards.entity.SummonedSpider;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientSpellList {

    private static final Logger logger = LogUtils.getLogger();

    private static int index = 0;
    public static int[] spells = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

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

    public static int getSelectedNumber() {
        return spells[index];
    }

    public static String getSelectedName() {
        Optional<Spell> selected = getSelected();
        return selected
                .map(s -> s.getName() + (s.getCreatureClass() != null ? " " + s.getPower() + "/" + s.getToughness() : ""))
                .orElse("None");
    }

    public static String getKey(int spellNum) {
        switch (spellNum) {
            case 1:
                return "smallfireball";
            case 2:
                return "largefireball";
            case 3:
                return SummonedZombie.spell_name;
            case 4:
                return SummonedSkeleton.spell_name;
            case 5:
                return SummonedSpider.spell_name;
            case 6:
                return SummonedPolarBear.spell_name;
            case 7:
                return "summonphantom";
            case 8:
                return SummonedSlime.spell_name;
            case 9:
                return SummonedSkeletonArcher.spell_name;
            case 10:
                return "codeofarrows";
            case 11:
                return Orc.spell_name;
            case 12:
                return DwarfAxeman.spell_name;
            case 13:
                return "minor_strength";
            default:
                return null;
        }
    }

    public static Optional<Spell> getSelected() {
        String key = getKey(getSelectedNumber());
        return Spells.getSpellByName(key);
    }

    public static Component getSelectedCostString() {
        Optional<Spell> spellOptional = getSelected();
        MutableComponent costStr = Component.literal("");
        if (spellOptional.isPresent()) {
            Map<ManaColor, Integer> totalMap = getCostMap(spellOptional.get().getCost());

            for (ManaColor color : ManaColor.values()) {
                Integer integer = totalMap.get(color);
                if (integer != null && integer > 0) {
                    Style style = Style.EMPTY;
                    if (color == ManaColor.COLORLESS) {
                        style = style.withColor(color.toInt());
                        costStr.append(Component.literal(integer.toString()).withStyle(style));
                    } else {
                        style = style.withColor(color.toInt());
                        for (int i = 0; i < integer; i++) {
                            costStr.append(Component.literal(color.getChar()).withStyle(style));
                        }
                    }
                }
            }
        }
        return costStr;
    }

    public static Map<ManaColor, Integer> getCostMap(List<ManaColor> costs) {
        return costs.stream()
                .collect(
                        Collectors.toMap(color -> color,
                                color -> 1,
                                Integer::sum));
    }

}
