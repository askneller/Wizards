package com.example.wizards;

import com.example.examplemod.ManaTotemBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static com.example.examplemod.ExampleMod.MANA_TOTEM_BLOCK_ENTITY;

// Only the entity will be unique for every mana totem placed. The block is only instantiated once
public class ManaTotemBlock extends TorchBlock { //} BaseEntityBlock {

    private static final Logger logger = LogUtils.getLogger();

    private LivingEntity placedBy;
    private ManaTotemBlockEntity entity;

    public ManaTotemBlock(Properties p_57491_, ParticleOptions p_57492_) {
        super(p_57491_, p_57492_);
    }

    // this block is a blueprint created once
//    public ManaTotemBlock(Properties p_57491_, ParticleOptions p_57492_) {
//        super(p_57491_);
//        this.flameParticle = p_57492_;
////        logger.info("Created ManaTotemBlock");
//    }

//    @Nullable
//    @Override
//    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
////        logger.info("newBlockEntity: {}, {}", blockPos, blockState);
////        if (entity != null) {
////            logger.warn("Creating new entity from {}, existing {}", this, entity);
////        }
//        return null; // new ManaTotemBlockEntity(blockPos, blockState, null);
//    }

//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
////        logger.info("getTicker called");
//        return p_153212_.isClientSide ? null : createTickerHelper(p_153214_, MANA_TOTEM_BLOCK_ENTITY.get(), ManaTotemBlockEntity::serverTick);
//    }

//    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
//        super.setPlacedBy(level, pos, state, entity, stack);
//        logger.info("setPlacedBy {} at {}, state {}", entity, pos, state);
////        logger.info("(this.entity is {})", this.entity);
//        BlockEntity blockEntity = level.getBlockEntity(pos);
//        logger.info("blockEntity at place {} is {}", pos, blockEntity);
////        if (blockEntity instanceof ManaTotemBlockEntity mtbe) {
////            if (!level.isClientSide() && entity instanceof ServerPlayer) {
////                logger.info("setting placed by");
////                mtbe.setPlacedBy(entity);
////                logger.info("mtbe {}", mtbe);
////            }
////        }
////        this.placedBy = entity;
//    }

//    @Override
//    public String toString() {
//        return "ManaTotemBlock{" +
//                "placedBy=" + placedBy +
//                '}';
//    }

    // ================================================================================
    // ================================================================================
    // From TorchBlock
//    protected static final int AABB_STANDING_OFFSET = 2;
//    protected static final VoxelShape AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);
//    protected final ParticleOptions flameParticle;
//
//
//    public VoxelShape getShape(BlockState p_57510_, BlockGetter p_57511_, BlockPos p_57512_, CollisionContext p_57513_) {
//        return AABB;
//    }
//
//    public BlockState updateShape(BlockState p_57503_, Direction p_57504_, BlockState p_57505_, LevelAccessor p_57506_, BlockPos p_57507_, BlockPos p_57508_) {
//        return p_57504_ == Direction.DOWN && !this.canSurvive(p_57503_, p_57506_, p_57507_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_57503_, p_57504_, p_57505_, p_57506_, p_57507_, p_57508_);
//    }
//
//    public boolean canSurvive(BlockState p_57499_, LevelReader p_57500_, BlockPos p_57501_) {
//        return canSupportCenter(p_57500_, p_57501_.below(), Direction.UP);
//    }
//
//    public void animateTick(BlockState p_222593_, Level p_222594_, BlockPos p_222595_, RandomSource p_222596_) {
//        double d0 = (double)p_222595_.getX() + 0.5D;
//        double d1 = (double)p_222595_.getY() + 0.7D;
//        double d2 = (double)p_222595_.getZ() + 0.5D;
//        p_222594_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
//        p_222594_.addParticle(this.flameParticle, d0, d1, d2, 0.0D, 0.0D, 0.0D);
//    }
    // ================================================================================
    // ================================================================================
}
