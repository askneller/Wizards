package com.example.wizards;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;

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
    public static final Spell SMALL_FIREBALL = new Spell.Builder("Fireball (S)").withProjectile(SmallFireball.class, EntityType.SMALL_FIREBALL).withCost(ManaColor.COLORLESS).build();
    public static final Spell LARGE_FIREBALL = new Spell.Builder("Fireball (L)").withProjectile(LargeFireball.class, EntityType.FIREBALL).withPower(2.0f).withCost(ManaColor.COLORLESS, ManaColor.COLORLESS).build();
    public static final Spell CODE_OF_ARROWS = new Spell.Builder("Cone of Arrows").withProjectile(Arrow.class, EntityType.ARROW).withQuantity(10).withPower(3.0f).withSpread(0.2f).withCost(ManaColor.COLORLESS, ManaColor.COLORLESS).build();

    private static final Map<String, Spell> spellsByName = new HashMap<>();

    static {
        spellsByName.put("summonzombie", SUMMON_ZOMBIE);
        spellsByName.put("summonskeleton", SUMMON_SKELETON);
        spellsByName.put("summonspider", SUMMON_SPIDER);
        spellsByName.put("summonpolarbear", SUMMON_POLAR_BEAR);
        spellsByName.put("summonphantom", SUMMON_PHANTOM);
        spellsByName.put("summonslime", SUMMON_SLIME);
        spellsByName.put("summonskeletonarcher", SUMMON_SKELETON_ARCHER);
        spellsByName.put("smallfireball", SMALL_FIREBALL);
        spellsByName.put("largefireball", LARGE_FIREBALL);
        spellsByName.put("codeofarrows", CODE_OF_ARROWS);
    }

    public static Optional<Spell> getSpellByName(String name) {
        return Optional.ofNullable(spellsByName.get(name));
    }

    public static Optional<List<ManaColor>> getSpellCostByName(String name) {
        Spell spell = spellsByName.get(name);
        return Optional.ofNullable(spell != null ? spell.getCost() : null);
    }
}
