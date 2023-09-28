package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

import java.util.List;

import static com.example.wizards.ModBlocksAndItems.MANA_TOTEM_BLOCK_ENTITY;

public class ManaTotemBlockEntity extends BlockEntity {

    private static final Logger logger = LogUtils.getLogger();

    public static double CHECK_DISTANCE = 500.0;

    private int COUNT_TIME = 200;
    private LivingEntity placedBy;
    private String placedByUuid = null;
    private int loadedTries = 100;
    private int countdown = COUNT_TIME;
    private boolean available = false;
    private ManaColor color = null;

    public ManaTotemBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MANA_TOTEM_BLOCK_ENTITY.get(), blockPos, blockState);
//        logger.info("Created: {}, {}", blockPos, blockState);
    }

    public ManaTotemBlockEntity(BlockPos blockPos, BlockState blockState, LivingEntity placedBy) {
        this(blockPos, blockState);
        this.placedBy = placedBy;
        logger.info("Created ManaTotemBlockEntity: {}, {}", blockPos, blockState);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        logger.info("load {}", tag);
        if (tag.contains("placed_by_uuid")) {
            placedByUuid = tag.getString("placed_by_uuid");
        }
        logger.info("this.placedByUuid {}", this.placedByUuid);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (placedBy != null) {
            tag.putString("placed_by_uuid", placedBy.getStringUUID());
            logger.info("saveAdditional {}", tag);
        }
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

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ManaTotemBlockEntity entity) {
        if (entity.color == null) {
            Holder<Biome> biome = level.getBiome(entity.getBlockPos());
            logger.info("Biome {}", getBiomeStr(biome));
            entity.color = getColor(getBiomeStr(biome));
            logger.info("Color {}", entity.color);
        }
        if (entity.placedByUuid != null && entity.placedBy == null && entity.loadedTries > 0) {
            logger.info("placedBy currently null {}", entity.placedByUuid);

            // TODO find better way to save and load entity over sessions
            Vec3 vec = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            List<Entity> entities =
                    level.getEntities(null, AABB.ofSize(vec, CHECK_DISTANCE, CHECK_DISTANCE, CHECK_DISTANCE));
            Entity entity1 = entities.stream()
                    .filter(e -> e.getStringUUID().equals(entity.placedByUuid))
                    .findFirst()
                    .orElse(null);
            logger.info("entity1 {}", entity1);
            if (entity1 instanceof LivingEntity livingEntity) {
                entity.placedBy = livingEntity;
                logger.info("Set placedBy to {}", entity.placedBy);
                logger.info("So entity is {}", entity);
                entity.loadedTries = 0;
            }

            entity.loadedTries--;
        }
//        if (level.getGameTime() % 60 == 0) {
//            logger.info("Server tick: {}\n{}\n{}\n{}", level, blockPos, blockState, entity);
//        }
        entity.decrementCount();
    }

    private static String getBiomeStr(Holder<Biome> p_205375_) {
        return p_205375_.unwrap().map((p_205377_) -> {
            return p_205377_.location().toString();
        }, (p_205367_) -> {
            return "[unregistered " + p_205367_ + "]";
        });
    }

    private static ManaColor getColor(String biome) {
        if (biome.contains("savanna") || biome.contains("plain")) {
            return ManaColor.WHITE;
        }

        return ManaColor.COLORLESS;
    }

    @Override
    public String toString() {
        return "ManaTotemBlockEntity{" +
                "placedBy=" + placedBy +
                ", countdown=" + countdown +
                ", available=" + available +
                ", color=" + color +
                '}';
    }
}
