package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

public class ManaTotemBlockEntity extends BlockEntity {

    private static final Logger logger = LogUtils.getLogger();

    private int COUNT_TIME = 200;
    private LivingEntity placedBy;
    private int countdown = COUNT_TIME;
    private boolean available = false;

//    public ManaTotemBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
//        super(p_155228_, p_155229_, p_155230_);
//    }

    public ManaTotemBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ExampleMod.MANA_TOTEM_BLOCK_ENTITY.get(), blockPos, blockState);
//        logger.info("Created: {}, {}", blockPos, blockState);
    }

    public ManaTotemBlockEntity(BlockPos blockPos, BlockState blockState, LivingEntity placedBy) {
        this(blockPos, blockState);
        this.placedBy = placedBy;
//        logger.info("Created: {}, {} placed by {}", blockPos, blockState, placedBy);
    }

    public boolean isAvailable() {
        return available;
    }

    public int getMana() {
        return 1;
    }

    protected LivingEntity getPlacedBy() {
        return placedBy;
    }

    protected void setPlacedBy(LivingEntity placedBy) {
        this.placedBy = placedBy;
    }

    protected void decrementCount() {
        if (this.countdown > 0) {
            this.countdown--;
            if (this.countdown == 0) {
                logger.info("Mana ready");
                this.available = true;
                ManaRegenerateEvent event = new ManaRegenerateEvent(1, this, placedBy);
                MinecraftForge.EVENT_BUS.post(event);
            }
        }
    }

    public static void serverTick(Level p_155145_, BlockPos p_155146_, BlockState p_155147_, ManaTotemBlockEntity p_155148_) {
        if (p_155145_.getGameTime() % 60 == 0) {
            logger.info("Server tick: {}\n{}\n{}\n{}", p_155145_, p_155146_, p_155147_, p_155148_);
        }
        p_155148_.decrementCount();
//        tickOccupants(p_155145_, p_155146_, p_155147_, p_155148_.stored, p_155148_.savedFlowerPos);
//        if (!p_155148_.stored.isEmpty() && p_155145_.getRandom().nextDouble() < 0.005D) {
//            double d0 = (double)p_155146_.getX() + 0.5D;
//            double d1 = (double)p_155146_.getY();
//            double d2 = (double)p_155146_.getZ() + 0.5D;
//            p_155145_.playSound((Player)null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
//        }
//
//        DebugPackets.sendHiveInfo(p_155145_, p_155146_, p_155147_, p_155148_);
    }

    @Override
    public String toString() {
        return "ManaTotemBlockEntity{" +
                "placedBy=" + placedBy +
                ", countdown=" + countdown +
                ", available=" + available +
                '}';
    }
}
