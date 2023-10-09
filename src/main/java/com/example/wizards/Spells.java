package com.example.wizards;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.wizards.ModEntities.SUMMONED_SLIME;
import static com.example.wizards.ModEntities.SUMMONED_POLAR_BEAR;
import static com.example.wizards.ModEntities.SUMMONED_SKELETON;
import static com.example.wizards.ModEntities.SUMMONED_SKELETON_ARCHER;
import static com.example.wizards.ModEntities.SUMMONED_SPIDER;
//import static com.example.wizards.ModEntities.SUMMONED_ZOMBIE;
import static com.example.wizards.ModEntities.SUMMONED_ZOMBIE2;

public class Spells {

//    public static final Spell SUMMON_ZOMBIE = new Spell("Zombie", SummonedZombie.class, SUMMONED_ZOMBIE, ManaColor.COLORLESS);
    public static final Spell SUMMON_ZOMBIE2 = new Spell("Zombie2", SummonedCreatureZombie.class, SUMMONED_ZOMBIE2, ManaColor.COLORLESS);
    public static final Spell SUMMON_SKELETON = new Spell("Skeleton", SummonedSkeleton.class, SUMMONED_SKELETON, ManaColor.COLORLESS);
    public static final Spell SUMMON_SPIDER = new Spell("Spider", SummonedSpider.class, SUMMONED_SPIDER, ManaColor.COLORLESS);
    public static final Spell SUMMON_POLAR_BEAR = new Spell("Polar Bear", SummonedPolarBear.class, SUMMONED_POLAR_BEAR, ManaColor.COLORLESS, ManaColor.COLORLESS);
    public static final Spell SUMMON_PHANTOM = new Spell("Phantom", SummonedPhantom.class, EntityType.PHANTOM, ManaColor.COLORLESS, ManaColor.COLORLESS);
    public static final Spell SUMMON_SLIME = new Spell("Slime", SummonedCreatureSlime.class, SUMMONED_SLIME, ManaColor.COLORLESS);
//    public static final Spell SUMMON_CREATURE = new Spell("Creature", SummonedCreature.class, SUMMONED_SLIME, ManaColor.COLORLESS);
    public static final Spell SUMMON_SKELETON_ARCHER = new Spell("Skeleton Archer", SummonedSkeletonArcher.class, SUMMONED_SKELETON_ARCHER, ManaColor.COLORLESS, ManaColor.COLORLESS);
    public static final Spell SMALL_FIREBALL = new Spell.Builder("Fireball (S)").withProjectile(SmallFireball.class, EntityType.SMALL_FIREBALL).withCost(ManaColor.COLORLESS).build();
    public static final Spell LARGE_FIREBALL = new Spell.Builder("Fireball (L)").withProjectile(LargeFireball.class, EntityType.FIREBALL).withPower(2.0f).withCost(ManaColor.COLORLESS, ManaColor.COLORLESS).build();
    public static final Spell CODE_OF_ARROWS = new Spell.Builder("Cone of Arrows").withProjectile(Arrow.class, EntityType.ARROW).withQuantity(10).withPower(3.0f).withSpread(0.2f).withCost(ManaColor.COLORLESS, ManaColor.COLORLESS).build();

    private static final Map<String, Spell> spellsByName = new HashMap<>();

    static {
        spellsByName.put(SummonedCreatureZombie.key, SUMMON_ZOMBIE2);
        spellsByName.put(SummonedSkeleton.key, SUMMON_SKELETON);
        spellsByName.put(SummonedSpider.key, SUMMON_SPIDER);
        spellsByName.put(SummonedPolarBear.key, SUMMON_POLAR_BEAR);
        spellsByName.put("summonphantom", SUMMON_PHANTOM);
        spellsByName.put(SummonedCreatureSlime.key, SUMMON_SLIME);
        spellsByName.put(SummonedSkeletonArcher.key, SUMMON_SKELETON_ARCHER);
        spellsByName.put("smallfireball", SMALL_FIREBALL);
        spellsByName.put("largefireball", LARGE_FIREBALL);
        spellsByName.put("codeofarrows", CODE_OF_ARROWS);
//        spellsByName.put("summoncreature", SUMMON_CREATURE);
    }

    public static Optional<Spell> getSpellByName(String name) {
        return Optional.ofNullable(spellsByName.get(name));
    }

    public static Optional<List<ManaColor>> getSpellCostByName(String name) {
        Spell spell = spellsByName.get(name);
        return Optional.ofNullable(spell != null ? spell.getCost() : null);
    }
}
