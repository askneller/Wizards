package com.example.wizards;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.example.wizards.Wizards.MOD_ID;

public class ModBlocksAndItems {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<Block> MANA_TOTEM_BLOCK = BLOCKS.register("mana_totem", () -> new ManaTotemBlock(
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel((p_50755_) -> { return 14; })
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY),
            ParticleTypes.FLAME));

    public static final RegistryObject<Item> MANA_TOTEM_BLOCK_ITEM = ITEMS.register("mana_totem", () ->
            new StandingAndWallBlockItem(MANA_TOTEM_BLOCK.get(), Blocks.WALL_TORCH, new Item.Properties(), Direction.DOWN));

    public static final RegistryObject<BlockEntityType<ManaTotemBlockEntity>> MANA_TOTEM_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("mana_totem_entity",
            () -> BlockEntityType.Builder.of(ManaTotemBlockEntity::new, MANA_TOTEM_BLOCK.get()).build(null));

}
