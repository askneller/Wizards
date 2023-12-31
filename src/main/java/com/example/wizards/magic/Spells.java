package com.example.wizards.magic;

import com.example.wizards.entity.DwarfAxeman;
import com.example.wizards.entity.Orc;
import com.example.wizards.entity.SummonedPhantom;
import com.example.wizards.entity.SummonedPolarBear;
import com.example.wizards.entity.SummonedSkeleton;
import com.example.wizards.entity.SummonedSkeletonArcher;
import com.example.wizards.entity.SummonedSlime;
import com.example.wizards.entity.SummonedSpider;
import com.example.wizards.entity.SummonedZombie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.wizards.entity.ModEntities.DWARF_AXEMAN;
import static com.example.wizards.entity.ModEntities.ORC;
import static com.example.wizards.entity.ModEntities.SUMMONED_SKELETON_ARCHER;
import static com.example.wizards.entity.ModEntities.SUMMONED_SLIME;
import static com.example.wizards.entity.ModEntities.SUMMONED_POLAR_BEAR;
import static com.example.wizards.entity.ModEntities.SUMMONED_SKELETON;
import static com.example.wizards.entity.ModEntities.SUMMONED_SPIDER;
import static com.example.wizards.entity.ModEntities.SUMMONED_ZOMBIE;

public class Spells {

    public static final Spell SUMMON_ZOMBIE = new Spell.Builder("Zombie")
            .withCreature(SummonedZombie.class, SUMMONED_ZOMBIE)
            .withPowerToughness(1, 1)
            .withCost(ManaColor.COLORLESS)
            .build();
    public static final Spell SUMMON_SKELETON = new Spell.Builder("Skeleton")
            .withCreature(SummonedSkeleton.class, SUMMONED_SKELETON)
            .withPowerToughness(1, 1)
            .withCost(ManaColor.COLORLESS)
            .build();
    public static final Spell SUMMON_SPIDER = new Spell.Builder("Spider")
            .withCreature(SummonedSpider.class, SUMMONED_SPIDER)
            .withPowerToughness(1, 1)
            .withCost(ManaColor.COLORLESS)
            .build();
    public static final Spell SUMMON_POLAR_BEAR = new Spell.Builder("Polar Bear")
            .withCreature(SummonedPolarBear.class, SUMMONED_POLAR_BEAR)
            .withPowerToughness(2, 2)
            .withCost(ManaColor.COLORLESS, ManaColor.COLORLESS)
            .build();
    public static final Spell SUMMON_PHANTOM = new Spell("Phantom", SummonedPhantom.class, EntityType.PHANTOM, ManaColor.COLORLESS, ManaColor.COLORLESS);
    public static final Spell SUMMON_SLIME = new Spell.Builder("Slime")
            .withCreature(SummonedSlime.class, SUMMONED_SLIME)
            .withPowerToughness(2, 2)
            .withCost(ManaColor.COLORLESS)
            .build();
    public static final Spell SUMMON_SKELETON_ARCHER = new Spell.Builder("Skeleton Archer")
            .withCreature(SummonedSkeletonArcher.class, SUMMONED_SKELETON_ARCHER)
            .withPowerToughness(1, 1)
            .withCost(ManaColor.COLORLESS, ManaColor.COLORLESS)
            .build();
    public static final Spell SMALL_FIREBALL = new Spell.Builder("Fireball (S)")
            .withProjectile(SmallFireball.class, EntityType.SMALL_FIREBALL)
            .withCost(ManaColor.COLORLESS)
            .build();
    public static final Spell LARGE_FIREBALL = new Spell.Builder("Fireball (L)")
            .withProjectile(LargeFireball.class, EntityType.FIREBALL)
            .withForce(2.0f)
            .withCost(ManaColor.COLORLESS, ManaColor.COLORLESS)
            .build();
    public static final Spell CODE_OF_ARROWS = new Spell.Builder("Cone of Arrows")
            .withProjectile(Arrow.class, EntityType.ARROW)
            .withQuantity(10)
            .withForce(3.0f)
            .withSpread(0.2f)
            .withCost(ManaColor.COLORLESS, ManaColor.COLORLESS)
            .build();
    public static final Spell SUMMON_ORC = new Spell.Builder("Orc")
            .withCreature(Orc.class, ORC)
            .withPowerToughness(2, 1)
            .withCost(ManaColor.COLORLESS)
            .build();
    public static final Spell SUMMON_DWARF_AXEMAN = new Spell.Builder("Dwarf Axeman")
            .withCreature(DwarfAxeman.class, DWARF_AXEMAN)
            .withPowerToughness(1, 1)
            .withCost(ManaColor.COLORLESS)
            .build();

    public static final Spell MINOR_STRENGTH = new Spell.Builder("Minor Strength")
            .withEffect(new PowerToughnessEnchantment(1, 1))
            .withCost(ManaColor.COLORLESS)
            .build();

    private static final Map<String, Spell> spellsByName = new HashMap<>();

    static {
        spellsByName.put(SummonedZombie.spell_name, SUMMON_ZOMBIE);
        spellsByName.put(SummonedSkeleton.spell_name, SUMMON_SKELETON);
        spellsByName.put(SummonedSpider.spell_name, SUMMON_SPIDER);
        spellsByName.put(SummonedPolarBear.spell_name, SUMMON_POLAR_BEAR);
        spellsByName.put("summonphantom", SUMMON_PHANTOM);
        spellsByName.put(SummonedSlime.spell_name, SUMMON_SLIME);
        spellsByName.put(SummonedSkeletonArcher.spell_name, SUMMON_SKELETON_ARCHER);
        spellsByName.put("smallfireball", SMALL_FIREBALL);
        spellsByName.put("largefireball", LARGE_FIREBALL);
        spellsByName.put("codeofarrows", CODE_OF_ARROWS);
        spellsByName.put(Orc.spell_name, SUMMON_ORC);
        spellsByName.put(DwarfAxeman.spell_name, SUMMON_DWARF_AXEMAN);
        spellsByName.put("minor_strength", MINOR_STRENGTH);
    }

    public static Optional<Spell> getSpellByName(String name) {
        return Optional.ofNullable(spellsByName.get(name));
    }

    public static Optional<List<ManaColor>> getSpellCostByName(String name) {
        Spell spell = spellsByName.get(name);
        return Optional.ofNullable(spell != null ? spell.getCost() : null);
    }
}
