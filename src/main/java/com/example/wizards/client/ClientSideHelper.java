package com.example.wizards.client;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ClientSideHelper {

    public static final double MAX_HIT_DISTANCE = 20.0;

    private static final Logger logger = LogUtils.getLogger();

    private static Entity selectedEntity;
    public static boolean leftAltKeyDown = false;

    public static Entity getSelectedEntity() {
        return selectedEntity;
    }

    public static void setSelectedEntity(Entity selectedEntity) {
        ClientSideHelper.selectedEntity = selectedEntity;
    }

    public static EntityHitResult getCursorEntityHit() {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        if (entity != null) {
            float offset = 0.0f; // offset?
            double distance = MAX_HIT_DISTANCE; // distance
            boolean p_19910_ = false; // fluid
            Vec3 eyePosition = entity.getEyePosition(offset);
            Vec3 viewVector = entity.getViewVector(offset);
            Vec3 scaledView = eyePosition.add(viewVector.x * distance, viewVector.y * distance, viewVector.z * distance);
//            BlockHitResult clip = entity.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, p_19910_ ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
//            logger.info("hit res {} type {}", clip, clip.getType());

            double d0 = MAX_HIT_DISTANCE; //(double)Minecraft.getInstance().gameMode.getPickRange();
            double d1 = d0 * d0;
            AABB aabb = entity.getBoundingBox().expandTowards(viewVector.scale(d0)).inflate(MAX_HIT_DISTANCE, MAX_HIT_DISTANCE, MAX_HIT_DISTANCE);
            EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(entity, eyePosition, scaledView, aabb, (p_234237_) -> {
                return true; // !p_234237_.isSpectator() && p_234237_.isPickable();
            }, d1);
            logger.info("hit res {}", entityhitresult);
            if (entityhitresult != null) {
                Entity hit = entityhitresult.getEntity();
                logger.info("type {}", entityhitresult.getType());
                logger.info("{}", hit);
                logger.info("dist {}", entity.distanceTo(hit));
            }
            return entityhitresult;
        }
        return null;
    }

    public static void particles(BlockPos blockPos) {
        // TODO move this somewhere else
        ClientLevel level = Minecraft.getInstance().level;
        ClientSideHelper.createSummonParticles(level, blockPos);
    }

    public static void createSummonParticles(Level level, BlockPos blockPos) {
        RandomSource randomsource = level.getRandom();

        // TODO change color or particles (something purple-y)
        // TODO try to find Zombie death particle spawn code
        // TODO find Witch potion explode code, has different colored particles
        for (int i = 0; i < 20; i++) {
            double rx = (randomsource.nextDouble() * 1.5 - 0.75);
            double rz = (randomsource.nextDouble() * 1.5 - 0.75);
            level.addParticle(ParticleTypes.EFFECT, false,
                    // position
                    blockPos.getX() + 0.5 + rx,
                    blockPos.getY() + 1.0,
                    blockPos.getZ() + 0.5 + rz,
                    // impulse
                    0.0,
                    0.5,
                    0.0);
        }
    }

}
