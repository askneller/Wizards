package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AutoRegisterCapability
public class ManaPool {

    private static final Logger logger = LogUtils.getLogger();

    public static final ManaPool EMPTY = new ManaPool();

    private List<ManaSource> sources = new ArrayList<>();

    public List<ManaSource> getSources() {
        return sources;
    }

    public void setSources(List<ManaSource> sources) {
        this.sources = sources;
    }

    public void addSource(ManaSource source) {
        this.sources.add(source);
    }

    public boolean isEmpty() {
        return this.sources == null || this.sources.isEmpty();
    }

    public boolean isExhausted() {
        return isEmpty() || this.sources.stream().allMatch(ManaSource::isEmpty);
    }

    public boolean has(int amount, String type) {
        Map<String, List<ManaSource>> collect = this.sources.stream().collect(Collectors.groupingBy(ManaSource::getType));
        logger.info("Sources\n{}", collect);
        List<ManaSource> typeSources = collect.get(type);
        Integer typeAmount = typeSources.stream().map(ManaSource::getAmount).reduce(Integer::sum).orElse(0);
        logger.info("{} {}", type, typeAmount);
        return typeAmount >= amount;
    }

    public void incMana() {
        if (!isEmpty()) {
            this.sources.get(0).addAmount(1);
        }
    }

    public void decMana() {
        if (!isEmpty()) {
            this.sources.get(0).subtractAmount(1);
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
