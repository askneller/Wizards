package com.example.wizards;

import com.example.examplemod.ManaRegenerateEvent;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

import static com.example.wizards.ModBlocksAndItems.MANA_TOTEM_BLOCK_ENTITY;

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
        super(MANA_TOTEM_BLOCK_ENTITY.get(), blockPos, blockState);
//        logger.info("Created: {}, {}", blockPos, blockState);
    }

    public ManaTotemBlockEntity(BlockPos blockPos, BlockState blockState, LivingEntity placedBy) {
        this(blockPos, blockState);
        this.placedBy = placedBy;
        logger.info("Created ManaTotemBlockEntity: {}, {}", blockPos, blockState);
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
                this.countdown = COUNT_TIME;
            }
        }
    }

    public static void serverTick(Level p_155145_, BlockPos p_155146_, BlockState p_155147_, ManaTotemBlockEntity p_155148_) {
        if (p_155145_.getGameTime() % 60 == 0) {
            logger.info("Server tick: {}\n{}\n{}\n{}", p_155145_, p_155146_, p_155147_, p_155148_);
        }
        p_155148_.decrementCount();
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
