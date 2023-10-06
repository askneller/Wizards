package com.example.wizards.client;

import com.example.wizards.ManaColor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.alchemy.Potion;
//import net.minecraft.world.item.alchemy.PotionUtils;
//import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ClientSideHelper {

    public static final double MAX_ENTITY_DISTANCE = 20.0;
    public static final double MAX_BLOCK_DISTANCE = 10.0;

    private static final Logger logger = LogUtils.getLogger();

    private static Entity selectedEntity;
    private static boolean leftAltKeyDown = false;

    public static Entity getSelectedEntity() {
        return selectedEntity;
    }

    public static void setSelectedEntity(Entity selectedEntity) {
        ClientSideHelper.selectedEntity = selectedEntity;
    }

    public static boolean isLeftAltDown() {
        return leftAltKeyDown;
    }

    public static void setLeftAltKeyDown(boolean leftAltKeyDown) {
        ClientSideHelper.leftAltKeyDown = leftAltKeyDown;
    }

    public static EntityHitResult getCursorEntityHit() {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        if (entity != null) {
            float offset = 0.0f; // offset?
            double distance = MAX_ENTITY_DISTANCE; // distance
            boolean p_19910_ = false; // fluid
            Vec3 eyePosition = entity.getEyePosition(offset);
            Vec3 viewVector = entity.getViewVector(offset);
            Vec3 scaledView = eyePosition.add(viewVector.x * distance, viewVector.y * distance, viewVector.z * distance);
//            BlockHitResult clip = entity.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, p_19910_ ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
//            logger.info("hit res {} type {}", clip, clip.getType());

            double d0 = MAX_ENTITY_DISTANCE; //(double)Minecraft.getInstance().gameMode.getPickRange();
            double d1 = d0 * d0;
            AABB aabb = entity.getBoundingBox().expandTowards(viewVector.scale(d0)).inflate(MAX_ENTITY_DISTANCE, MAX_ENTITY_DISTANCE, MAX_ENTITY_DISTANCE);
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

    public static BlockPos getBlockHitLocation() {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        float offset = 0.0f;
        boolean pickFluid = false;
        HitResult hitResult = entity.pick(MAX_BLOCK_DISTANCE, offset, pickFluid);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return ((BlockHitResult) hitResult).getBlockPos();
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

        // For more particle stuff see net.minecraft.world.entity.AreaEffectCloud.tick
        for (int i = 0; i < 60; i++) {
            double rx = (randomsource.nextDouble() * 1.5 - 0.75);
            double ry = (randomsource.nextDouble() * 1.5 - 0.75);
            double rz = (randomsource.nextDouble() * 1.5 - 0.75);

            level.addAlwaysVisibleParticle(ParticleTypes.ENTITY_EFFECT,
                    true,
                    blockPos.getX() + 0.5 + rx,
                    blockPos.getY() + 1.5 + ry,
                    blockPos.getZ() + 0.5 + rz,
                    // color
                    (204 / 255.0),
                    (80 / 255.0),
                    (202 / 255.0));
        }
    }

    public static void setRenderColor(ManaColor color) {
        RenderSystem.setShaderColor(color.R(), color.G(), color.B(), 1.0f);
    }

    /*
    // Make cloud of particles (like a potion explosion) as the summon creature particles effect
    public static void makeAreaOfEffectCloud(Level level,
                                             Vec3 position,
                                             LivingEntity entity,
                                             ItemStack itemStack,
                                             Potion potion) {
        AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, position.x, position.y, position.z);
//        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity) {
            areaeffectcloud.setOwner((LivingEntity)entity);
        }

        areaeffectcloud.setRadius(3.0F);
        areaeffectcloud.setRadiusOnUse(-0.5F);
        areaeffectcloud.setWaitTime(10);
        areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());
        areaeffectcloud.setPotion(potion);

        for(MobEffectInstance mobeffectinstance : PotionUtils.getCustomEffects(itemStack)) {
            areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
        }

//        CompoundTag compoundtag = itemStack.getTag();
//        if (compoundtag != null && compoundtag.contains("CustomPotionColor", 99)) {
//            areaeffectcloud.setFixedColor(compoundtag.getInt("CustomPotionColor"));
            areaeffectcloud.setFixedColor(3694022);
//        }

        logger.info("Cloud at {}: {}", position, areaeffectcloud);
        level.addFreshEntity(areaeffectcloud);
    }
    */

}
