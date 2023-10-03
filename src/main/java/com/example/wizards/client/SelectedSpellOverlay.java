package com.example.wizards.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.slf4j.Logger;

public class SelectedSpellOverlay {

    private static final Logger logger = LogUtils.getLogger();

    public static final IGuiOverlay GUI_OVERLAY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth / 2;
        int y = screenHeight;
        int startX = x + 100;
        int startY = y - 15;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        String str = "Spell: " + ClientSpellList.getSelectedName() + "  [" + getSelected() + "]";
        guiGraphics.drawString(Minecraft.getInstance().font, str, startX, startY, 14737632);
    });

    private static String getSelected() {
        Entity selected = ClientSideHelper.getSelectedEntity();
        if (selected == null) {
            return "";
        }
//        print();
        return selected.getName().getString() + " (" + selected.getId() + ")";
    }

    private static void print() {
        long gameTime = Minecraft.getInstance().player.level().getGameTime();
        if (gameTime % 40 == 0 && ClientSideHelper.getSelectedEntity() != null) {
            logger.info("Selected pos {}, bb {}", ClientSideHelper.getSelectedEntity().blockPosition(), ClientSideHelper.getSelectedEntity().getBoundingBox());
        }
    }
}
