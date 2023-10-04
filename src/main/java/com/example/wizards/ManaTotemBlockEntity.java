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

// TODO this class will become the ManaSource, turn it into an interface and implement here

// TODO source not removed from player when block is destroyed
public class ManaTotemBlockEntity extends BlockEntity {

    private static final Logger logger = LogUtils.getLogger();

    public static double CHECK_DISTANCE = 500.0;

    private int COUNT_TIME = 200;
    private final int id;
    private LivingEntity placedBy;
    private String placedByUuid = null;
    private int loadedTries = 100;
    private int countdown = 0;
    private boolean available = true;
    private ManaColor color = null;

    public ManaTotemBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MANA_TOTEM_BLOCK_ENTITY.get(), blockPos, blockState);
        this.id = ManaSystem.getNewSourceId();
        // this.level is null at this point
//        if (color == null) {
//            Holder<Biome> biome = level.getBiome(blockPos);
//            logger.info("Biome {}", getBiomeStr(biome));
//            color = getColorForBiome(getBiomeStr(biome));
//            logger.info("Color {}", color);
//        }
        logger.info("Created pos and state: {}, {}", blockPos, blockState);
    }

    public ManaTotemBlockEntity(BlockPos blockPos, BlockState blockState, LivingEntity placedBy) {
        // this.level is null at this point
        this(blockPos, blockState);
        this.placedBy = placedBy;
//        logger.info("Created ManaTotemBlockEntity w/placedBy: {}, {}, {}", blockPos, blockState, placedBy);
    }

    // id, countdown, available, and color are not saved
    // id is generated freshly on every reload
    // available is true and countdown is zero when the source enters the game
    // color is inferred from the location's biome
    public void load(CompoundTag tag) {
        super.load(tag);
//        logger.info("load {}", tag);
        if (tag.contains("placed_by_uuid")) {
            placedByUuid = tag.getString("placed_by_uuid");
        }
//        logger.info("this.placedByUuid {}", this.placedByUuid);

//        logger.info("setting changed");
//        this.setChanged();
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // Don't save id as a new one can be assigned on construct
        if (placedBy != null) {
            tag.putString("placed_by_uuid", placedBy.getStringUUID());
        }
//        logger.info("saveAdditional {}", tag);
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getMana() {
        return 1;
    }

    public ManaColor getColor() {
        return color;
    }

    public LivingEntity getPlacedBy() {
        return placedBy;
    }

    protected void setPlacedBy(LivingEntity placedBy) {
        this.placedBy = placedBy;
    }

    public void setColor(ManaColor color) {
        this.color = color;
    }

    public void spent(ManaSource fromSource) {
//        logger.info("Resetting source entity. From {} (this {})", fromSource.getId(), this.id);
        if (this.id == fromSource.getId()) {
//            logger.info("Set available false, resetting count");
            this.available = false;
            this.countdown = COUNT_TIME;
        } else {
            logger.error("Error in reset: id mismatch. From {}, this {}", fromSource.getId(), this.id);
        }
    }

    protected void decrementCount() {
        if (this.countdown > 0) {
            this.countdown--;
            if (this.countdown == 0) {
//                logger.info("Countdown 0: {}", id);
                if (!this.available) {
//                    logger.info("Mana ready");
                    this.available = true;
                    ManaRegenerateEvent event = new ManaRegenerateEvent(1, this, placedBy);
                    MinecraftForge.EVENT_BUS.post(event);
                }
            }
        }
    }

    // TODO Source does not re-attach to player after respawn from death
    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ManaTotemBlockEntity entity) {
//        if (!level.isClientSide) logger.info("server tick {}", entity);
        if (entity.color == null) {
            Holder<Biome> biome = level.getBiome(entity.getBlockPos());
//            logger.info("Biome {}", getBiomeStr(biome));
            entity.color = getColorForBiome(getBiomeStr(biome));
//            logger.info("Color {}", entity.color);
        }
        if (entity.placedByUuid != null && entity.placedBy == null && entity.loadedTries > 0) {
//            logger.info("placedBy currently null {}", entity.placedByUuid);

            // TODO find better way to save and load entity over sessions
            Vec3 vec = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            List<Entity> entities =
                    level.getEntities(null, AABB.ofSize(vec, CHECK_DISTANCE, CHECK_DISTANCE, CHECK_DISTANCE));
            Entity entity1 = entities.stream()
                    .filter(e -> e.getStringUUID().equals(entity.placedByUuid))
                    .findFirst()
                    .orElse(null);
//            logger.info("entity1 {}", entity1);
            if (entity1 instanceof LivingEntity livingEntity) {
                entity.placedBy = livingEntity;
//                logger.info("serverTick Set placedBy to {}", entity.placedBy);
//                logger.info("serverTick So entity is {}", entity);
                entity.loadedTries = 0;
//                logger.info("serverTick setting changed");
                entity.setChanged();

//                logger.info("Sending AddManaSourceEvent from serverTick");
                MinecraftForge.EVENT_BUS.post(new AddManaSourceEvent(livingEntity, entity));
            }

            entity.loadedTries--;
        }
//        if (level.getGameTime() % 60 == 0) {
//            logger.info("Server tick: {}\n{}\n{}\n{}", level, blockPos, blockState, entity);
//        }
        entity.decrementCount();
    }

    public static String getBiomeStr(Holder<Biome> p_205375_) {
        return p_205375_.unwrap().map((p_205377_) -> {
            return p_205377_.location().toString();
        }, (p_205367_) -> {
            return "[unregistered " + p_205367_ + "]";
        });
    }

    public static ManaColor getColorForBiome(String biome) {
        if (biome.contains("savanna") || biome.contains("plain") || biome.contains("meadow")) {
            return ManaColor.WHITE;
        } else if (biome.contains("forest") || biome.contains("taiga")) {
            return ManaColor.GREEN;
        } else if (biome.contains("swamp")) {
            return ManaColor.BLACK;
        } else if (biome.contains("hills") || biome.contains("slopes") || biome.contains("peaks")) {
            return ManaColor.RED;
        } else if (biome.contains("river") || biome.contains("beach") || biome.contains("shore") || biome.contains("ocean")) {
            return ManaColor.BLUE;
        }
        // to decide: desert, jungle, badlands, meadow, grove, mushroom_fields, caves

        return ManaColor.COLORLESS;
    }

    @Override
    public String toString() {
        return "ManaTotemBlockEntity{" +
                "id=" + id +
                ", placedBy=" + (placedBy != null) +
                ", countdown=" + countdown +
                ", available=" + available +
                ", color=" + color +
                ", worldPosition=" + worldPosition +
                '}';
    }
}
