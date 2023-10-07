package com.example.wizards;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.List;

import static com.example.wizards.ManaTotemBlockEntity.CHECK_DISTANCE;

public class SummonedCreatureUtil {

    private static final Logger logger = LogUtils.getLogger();

    public static void trySetController(LivingEntity living, Level level) {
        if (!level.isClientSide) {
            if (living instanceof ControlledEntity controlled && controlled.getController() == null) {
                if (controlled.getControllerUuid() != null) {
                    LivingEntity controller = findController(controlled.getControllerUuid(), living.blockPosition(), level);
                    if (controller != null) {
                        logger.info("Found! {}", controller);
                        logger.info("this {}", controlled);
                        controlled.setController(controller);
                        if (controller instanceof Player player) {
                            CastingSystem.addPlayerControlledEntity(player, living);
                        }
                        controlled.setControllerUuid(null);
                    }
                }
            }
        }
    }

    private static LivingEntity findController(String uuid, BlockPos blockPos, Level level) {
        logger.info("findController {}", uuid);
        Vec3 vec = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        List<Entity> entities =
                level.getEntities(null, AABB.ofSize(vec, CHECK_DISTANCE, CHECK_DISTANCE, CHECK_DISTANCE));
        Entity entity1 = entities.stream()
                .filter(e -> e.getStringUUID().equals(uuid))
                .findFirst()
                .orElse(null);
        logger.info("entity1 {}", entity1);
        if (entity1 instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

}
