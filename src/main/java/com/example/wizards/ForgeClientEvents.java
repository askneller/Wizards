package com.example.wizards;

import com.example.wizards.client.ClientSpellList;
import com.example.wizards.client.ClientSideHelper;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import static com.example.wizards.ManaPoolProvider.MANA_POOL;
import static com.example.wizards.Wizards.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {

    private static final Logger logger = LogUtils.getLogger();

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton.Pre event) {
        if (event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT && event.getAction() == InputConstants.PRESS) {
            if (ClientSideHelper.isLeftAltDown()) {
                EntityHitResult cursorEntityHit = ClientSideHelper.getCursorEntityHit();
                if (cursorEntityHit != null) {
                    Entity hit = cursorEntityHit.getEntity();
                    ClientSideHelper.setSelectedEntity(hit);
                } else {
                    ClientSideHelper.setSelectedEntity(null);
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onMouseWheel(InputEvent.MouseScrollingEvent event) {
        if (ClientSideHelper.isLeftAltDown()) {
            if (event.getScrollDelta() > 0) {
                ClientSpellList.inc();
            } else if (event.getScrollDelta() < 0) {
                ClientSpellList.dec();
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == InputConstants.KEY_LALT &&
                (event.getAction() == InputConstants.REPEAT || event.getAction() == InputConstants.PRESS)) {
            ClientSideHelper.setLeftAltKeyDown(true);
        }
        if (event.getKey() == InputConstants.KEY_LALT && event.getAction() == InputConstants.RELEASE) {
            ClientSideHelper.setLeftAltKeyDown(false);
        }

        // Some client setup code
        if (event.getKey() == InputConstants.KEY_R && event.getAction() == InputConstants.PRESS) {
            LocalPlayer player = Minecraft.getInstance().player;
            assert player != null;

            BlockPos finalPos = ClientSideHelper.getBlockHitLocation();
            player.getCapability(MANA_POOL).ifPresent(pool -> {

                int spellNumber = ClientSpellList.getSelected();
                if (finalPos == null && spellNumber > 2) {
                    player.sendSystemMessage(Component.literal("Cannot cast spell: No target position").withStyle(ChatFormatting.RED));
                } else {
                    if (finalPos == null) {
                        PacketHandler.sendToServer(player, spellNumber);
                    } else {
                        PacketHandler.sendToServer(player, spellNumber, finalPos);
                    }
                }
            });
        }
    }

    /*
    // Attempting to render a box outline of ClientUtil.getSelected()

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        long gameTime = Minecraft.getInstance().player.level().getGameTime();
        if (gameTime % 100 == 0 && ClientUtil.getSelected() != null) {
            logger.info("onRender {}", event.getStage());
            logger.info("Selected pos {}, bb {}", ClientUtil.getSelected().blockPosition(), ClientUtil.getSelected().getBoundingBox());
        }

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES && ClientUtil.getSelected() != null) {

            // LevelRenderer 1331 maybe
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource.BufferSource multibuffersource$buffersource =
                    MultiBufferSource.immediateWithBuffers(new HashMap<>(), new BufferBuilder(256)); // this.renderBuffers.bufferSource();
            VertexConsumer vertexconsumer2 = multibuffersource$buffersource.getBuffer(RenderType.lines());
            Camera camera = event.getCamera();
            Vec3 vec3 = camera.getPosition();
            double d0 = vec3.x();
            double d1 = vec3.y();
            double d2 = vec3.z();
//            logger.info("d0 {}, d1 {}, d2 {}", d0, d1, d2);
            BlockPos blockpos1 = ClientUtil.getSelected().blockPosition(); // ((BlockHitResult)hitresult).getBlockPos();
//            logger.info("Blockpos1 {}", blockpos1);
            BlockState blockstate = Minecraft.getInstance().player.level().getBlockState(blockpos1);

            renderHitOutline(poseStack, vertexconsumer2, camera.getEntity(), d0, d1, d2, blockpos1, blockstate);
        }
    }

    private static void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double p_109641_, double p_109642_, double p_109643_, BlockPos blockPos, BlockState blockState) {
        renderShape(poseStack, vertexConsumer, blockState.getShape(
                Minecraft.getInstance().player.level(), //this.level,
                blockPos,
                CollisionContext.of(entity)),
                (double)blockPos.getX() - p_109641_,
                (double)blockPos.getY() - p_109642_,
                (double)blockPos.getZ() - p_109643_,
                1.0F,
                1.0F,
                1.0F,
                1.0F);
    }

    private static void renderShape(PoseStack p_109783_,
                                    VertexConsumer p_109784_,
                                    VoxelShape p_109785_,
                                    double p_109786_,
                                    double p_109787_,
                                    double p_109788_,
                                    float p_109789_,
                                    float p_109790_,
                                    float p_109791_,
                                    float p_109792_) {
        PoseStack.Pose posestack$pose = p_109783_.last();
        p_109785_.forAllEdges((p_234280_, p_234281_, p_234282_, p_234283_, p_234284_, p_234285_) -> {
            float f = (float)(p_234283_ - p_234280_);
            float f1 = (float)(p_234284_ - p_234281_);
            float f2 = (float)(p_234285_ - p_234282_);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            p_109784_.vertex(posestack$pose.pose(), (float)(p_234280_ + p_109786_), (float)(p_234281_ + p_109787_), (float)(p_234282_ + p_109788_)).color(p_109789_, p_109790_, p_109791_, p_109792_).normal(posestack$pose.normal(), f, f1, f2).endVertex();
            p_109784_.vertex(posestack$pose.pose(), (float)(p_234283_ + p_109786_), (float)(p_234284_ + p_109787_), (float)(p_234285_ + p_109788_)).color(p_109789_, p_109790_, p_109791_, p_109792_).normal(posestack$pose.normal(), f, f1, f2).endVertex();
        });
    }
    */

}
