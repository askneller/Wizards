package com.example.wizards.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SelectedSpellOverlay {

    public static final IGuiOverlay GUI_OVERLAY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth / 2;
        int y = screenHeight;
        int startX = x + 100;
        int startY = y - 15;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        String str = "Spell: " + ClientSpellList.getSelectedName();
        guiGraphics.drawString(Minecraft.getInstance().font, str, startX, startY, 14737632);
    });

}
