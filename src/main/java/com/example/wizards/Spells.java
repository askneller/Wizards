package com.example.wizards;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Spells {

    public static final Spell SUMMON_ZOMBIE = new Spell("Zombie", SummonedZombie.class, EntityType.ZOMBIE, ManaColor.COLORLESS);
    public static final Spell SUMMON_SKELETON = new Spell("Skeleton", SummonedSkeleton.class, EntityType.SKELETON, ManaColor.COLORLESS);
    public static final Spell SUMMON_SPIDER = new Spell("Spider", SummonedSpider.class, EntityType.SPIDER, ManaColor.COLORLESS);
    public static final Spell SUMMON_POLAR_BEAR = new Spell("Polar Bear", SummonedPolarBear.class, EntityType.POLAR_BEAR, ManaColor.COLORLESS, ManaColor.COLORLESS);
    public static final Spell SUMMON_PHANTOM = new Spell("Phantom", SummonedPhantom.class, EntityType.PHANTOM, ManaColor.COLORLESS, ManaColor.COLORLESS);
    public static final Spell SUMMON_SLIME = new Spell("Slime", SummonedSlime.class, EntityType.SLIME, ManaColor.COLORLESS, ManaColor.COLORLESS);
    public static final Spell SUMMON_SKELETON_ARCHER = new Spell("Skeleton Archer", SummonedSkeletonArcher.class, EntityType.SKELETON, ManaColor.COLORLESS, ManaColor.COLORLESS);

    private static final Map<String, Spell> spellsByName = new HashMap<>();

    static {
        spellsByName.put("summonzombie", SUMMON_ZOMBIE);
        spellsByName.put("summonskeleton", SUMMON_SKELETON);
        spellsByName.put("summonspider", SUMMON_SPIDER);
        spellsByName.put("summonpolarbear", SUMMON_POLAR_BEAR);
        spellsByName.put("summonphantom", SUMMON_PHANTOM);
        spellsByName.put("summonslime", SUMMON_SLIME);
        spellsByName.put("summonskeletonarcher", SUMMON_SKELETON_ARCHER);
    }

    public static Optional<Spell> getSpellByName(String name) {
        return Optional.ofNullable(spellsByName.get(name));
    }

    public static Optional<List<ManaColor>> getSpellCostByName(String name) {
        Spell spell = spellsByName.get(name);
        return Optional.ofNullable(spell != null ? spell.getCost() : null);
    }
}
