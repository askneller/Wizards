package com.example.wizards.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

import static com.example.wizards.Wizards.MOD_ID;

public class ModEntities {

    private static final Logger logger = LogUtils.getLogger();

    public static EntityType<? extends SummonedZombie> SUMMONED_ZOMBIE;
    public static EntityType<? extends SummonedSkeleton> SUMMONED_SKELETON;
    public static EntityType<? extends SummonedSkeleton> SUMMONED_SKELETON_ARCHER;
    public static EntityType<? extends Spider> SUMMONED_SPIDER;
    public static EntityType<? extends PolarBear> SUMMONED_POLAR_BEAR;
    public static EntityType<? extends SummonedSlime> SUMMONED_SLIME;
    public static EntityType<? extends BaseHuman> HUMAN;
    public static EntityType<? extends LargerHumanoid> LARGER_HUMANOID;
    public static EntityType<? extends LargeHumanoid> LARGE_HUMANOID;
    public static EntityType<? extends Orc> ORC;

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEntityEvents {

        @SubscribeEvent
        public static void setupEntities(RegisterEvent event) {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ENTITY_TYPES)) {
                logger.info("Setting up mod entities");
                SUMMONED_ZOMBIE = build(event.getForgeRegistry(), SummonedZombie.spell_name,
                        EntityType.Builder.<SummonedZombie>of(SummonedZombie::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(0.6F, 1.95F)
                                .clientTrackingRange(8)
                );

                SUMMONED_SKELETON = build(event.getForgeRegistry(), SummonedSkeleton.spell_name,
                        EntityType.Builder.<SummonedSkeleton>of(SummonedSkeleton::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(0.6F, 1.99F)
                                .clientTrackingRange(8)
                );

                SUMMONED_SKELETON_ARCHER = build(event.getForgeRegistry(), SummonedSkeletonArcher.spell_name,
                        EntityType.Builder.<SummonedSkeleton>of(SummonedSkeletonArcher::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(0.6F, 1.99F)
                                .clientTrackingRange(8)
                );

                SUMMONED_SPIDER = build(event.getForgeRegistry(), SummonedSpider.spell_name,
                        EntityType.Builder.<Spider>of(SummonedSpider::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(1.4F, 0.9F)
                                .clientTrackingRange(8)
                );

                SUMMONED_POLAR_BEAR = build(event.getForgeRegistry(), SummonedPolarBear.spell_name,
                        EntityType.Builder.<PolarBear>of(SummonedPolarBear::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .immuneTo(Blocks.POWDER_SNOW)
                                .sized(1.4F, 1.4F)
                                .clientTrackingRange(10)
                );

                SUMMONED_SLIME = build(event.getForgeRegistry(), SummonedSlime.spell_name,
                        EntityType.Builder.<SummonedSlime>of(SummonedSlime::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(2.0F, 2.0F)
                                .clientTrackingRange(10)
                );

                HUMAN = build(event.getForgeRegistry(), "human",
                        EntityType.Builder.<BaseHuman>of(BaseHuman::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(0.6F, 1.9F)
                                .clientTrackingRange(10)
                );

                LARGER_HUMANOID = build(event.getForgeRegistry(), "larger_humanoid",
                        EntityType.Builder.<LargerHumanoid>of(LargerHumanoid::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(0.7F, 2.1F)
                                .clientTrackingRange(10)
                );

                LARGE_HUMANOID = build(event.getForgeRegistry(), "large_humanoid",
                        EntityType.Builder.<LargeHumanoid>of(LargeHumanoid::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(0.6F, 1.95F)
                                .clientTrackingRange(10)
                );

                ORC = build(event.getForgeRegistry(), Orc.spell_name,
                        EntityType.Builder.<Orc>of(Orc::new, MobCategory.CREATURE) // MobCategory.MONSTER)
                                .sized(0.6F, 1.95F)
                                .clientTrackingRange(10)
                );

            }
        }

        private static <T extends Entity> EntityType<T> build(IForgeRegistry<EntityType> registry,
                                                              final String key,
                                                              final EntityType.Builder<T> builder) {
            EntityType<T> entity = builder.build(getKey(key));
            registry.register(new ResourceLocation(key), entity);
            return entity;
        }

        private static String getKey(String name) {
            return MOD_ID + ":" + name;
        }

        // Create attributes
        @SubscribeEvent
        public static void createEntityAttribute(final EntityAttributeCreationEvent event) {
            logger.info("Creating default attributes");
            event.put(SUMMONED_ZOMBIE, SummonedZombie.createAttributes().build());
            event.put(SUMMONED_SKELETON, SummonedSkeleton.createAttributes().build());
            event.put(SUMMONED_SKELETON_ARCHER, SummonedSkeletonArcher.createAttributes().build());
            event.put(SUMMONED_SPIDER, SummonedSpider.createAttributes().build());
            event.put(SUMMONED_POLAR_BEAR, SummonedPolarBear.createAttributes().build());
            event.put(SUMMONED_SLIME, SummonedSlime.createAttributes().build());
            event.put(HUMAN, BaseHuman.createAttributes().build());
            event.put(LARGER_HUMANOID, LargerHumanoid.createAttributes().build());
            event.put(LARGE_HUMANOID, LargeHumanoid.createAttributes().build());
            event.put(ORC, Orc.createAttributes().build());
        }

    }
}
