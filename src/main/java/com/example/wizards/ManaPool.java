package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoRegisterCapability
public class ManaPool {

    private static final Logger logger = LogUtils.getLogger();

    public static final ManaPool EMPTY = new ManaPool();

    private List<ManaSource> sources = new ArrayList<>();

    public void addSource(ManaSource source) {
        this.sources.add(source);
    }

    public boolean isEmpty() {
        return this.sources == null || this.sources.isEmpty();
    }

    public boolean isExhausted() {
        return isEmpty() || this.sources.stream().allMatch(ManaSource::isEmpty);
    }

    public boolean has(int amount, ManaColor type) {
        Map<ManaColor, Integer> colorIntegerMap = getTotalMap();
//        logger.info("Sources\n{}", colorIntegerMap);
        if (type != ManaColor.COLORLESS) {
            Integer typeAmount = colorIntegerMap.getOrDefault(type, 0);
//            logger.info("{} {}", type, typeAmount);
            return typeAmount >= amount;
        } else {
            Integer total = colorIntegerMap.values().stream().reduce(0, Integer::sum);
//            logger.info("total {}", total);
            return total >= amount;
        }
    }

    public Map<ManaColor, Integer> getTotalMap() {
        return this.sources.stream()
                .collect(
                        Collectors.toMap(
                                ManaSource::getColor,
                                source -> source.isAvailable() ? source.getAmount() : 0,
                                Integer::sum));
    }

    public Map<ManaColor, List<ManaSource>> getMap() {
        return this.sources.stream().collect(Collectors.groupingBy(ManaSource::getColor));
    }

    public void incMana() {
        if (!isEmpty()) {
            this.sources.get(0).addAmount(1);
        }
    }

    public void replenishSource(int id) {
        if (!isEmpty()) {
            this.sources.stream()
                    .filter(source -> source.getId() == id)
                    .findFirst()
                    .ifPresent(ManaSource::replenish);
        }
    }

    public void decMana() {
        if (!isEmpty()) {
            this.sources.get(0).subtractAmount(1);
        }
    }

    public boolean consume(int amount, ManaColor type) {
        Map<ManaColor, List<ManaSource>> map = getMap();
        if (type != ManaColor.COLORLESS) {
            List<ManaSource> list = map.get(type);
            list = list.stream()
                    .filter(ManaSource::isAvailable)
                    .sorted(Comparator.comparing(ManaSource::getAmount))
                    .collect(Collectors.toList());
//            logger.info("consume {} {}", type, list);
//            int acc = 0;
            return false;
        } else {
            sources.sort(Comparator.comparing(ManaSource::getAmount));
            int acc = 0;
            for (int i = 0; i < sources.size(); i++) {
                ManaSource source = sources.get(i);
                acc += source.spend();
                if (acc >= amount) {
//                    logger.info("consume COLORLESS {}", acc);
                    return true;
                }
            }
            return false;
        }
    }

    public void saveNBTDate(CompoundTag nbt) {
        ListTag sourcesList = new ListTag();
        for (ManaSource source : sources) {
            CompoundTag tag = new CompoundTag();
            source.saveNBTDate(tag);
            sourcesList.add(tag); // CompoundTag is type 10
        }
        if (!sourcesList.isEmpty()) {
            nbt.put("mana_sources", sourcesList);
        }
    }

    public void loadNBTData(CompoundTag nbt) {
        Tag manaSources = nbt.get("mana_sources");
        if (manaSources instanceof ListTag listTag) {
            for (Tag tag : listTag) {
                if (tag instanceof CompoundTag cpdTag) {
                    ManaSource source = new ManaSource();
                    source.loadNBTData(cpdTag);
                    sources.add(source);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ManaPool{" +
                "sources=" + sources +
                '}';
    }
}
