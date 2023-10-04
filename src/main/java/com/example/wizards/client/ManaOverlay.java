package com.example.wizards.client;

import com.example.wizards.ManaColor;
import com.example.wizards.ManaPool;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Map;
import java.util.StringJoiner;

import static com.example.wizards.ManaColor.BLACK;
import static com.example.wizards.ManaColor.BLUE;
import static com.example.wizards.ManaColor.COLORLESS;
import static com.example.wizards.ManaColor.GREEN;
import static com.example.wizards.ManaColor.RED;
import static com.example.wizards.ManaColor.WHITE;
import static com.example.wizards.client.ClientSideHelper.setRenderColor;

public class ManaOverlay {

    private static String message = "Hello world";
    private static float percentage = -1.0f;
    private static int timer = 0;
    private static final int START_MAX = 200;
    private static int fadeStartTimer = START_MAX;
    private static final int COUNTDOWN_MAX = 200;
    private static int fadeCountdown = COUNTDOWN_MAX;

    public static final IGuiOverlay GUI_OVERLAY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth / 2;
        int y = screenHeight;
        int startX = x + 100;
        int startY = y - 37;

        if (fadeStartTimer > 0) {
            fadeStartTimer--;
        }
        if (fadeStartTimer == 0 && fadeCountdown > 0) {
            fadeCountdown--;
        }

        float alpha = (float) fadeCountdown / COUNTDOWN_MAX;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        ManaPool pool = ClientManaPool.getPlayerPool();

        // Mana colors
        setRenderColor(COLORLESS);
        String manaForColor = getManaForColor(pool, COLORLESS);
        guiGraphics.drawString(Minecraft.getInstance().font, manaForColor, startX, startY, 14737632);

        setRenderColor(WHITE);
        manaForColor = getManaForColor(pool, WHITE);
        guiGraphics.drawString(Minecraft.getInstance().font, manaForColor, startX + 15, startY, 14737632);

        setRenderColor(GREEN);
        manaForColor = getManaForColor(pool, GREEN);
        guiGraphics.drawString(Minecraft.getInstance().font, manaForColor, startX + 30, startY, 14737632);

        setRenderColor(RED);
        manaForColor = getManaForColor(pool, RED);
        guiGraphics.drawString(Minecraft.getInstance().font, manaForColor, startX + 45, startY, 14737632);

        setRenderColor(BLACK);
        manaForColor = getManaForColor(pool, BLACK);
        guiGraphics.drawString(Minecraft.getInstance().font, manaForColor, startX + 60, startY, 14737632);

        setRenderColor(BLUE);
        manaForColor = getManaForColor(pool, BLUE);
        guiGraphics.drawString(Minecraft.getInstance().font, manaForColor, startX + 75, startY, 14737632);

        // Left Alt
        setRenderColor(WHITE);
        guiGraphics.drawString(Minecraft.getInstance().font, getAltDown(), startX + 100, startY, 14737632);
    });

    private static String getManaString(ManaPool pool) {
        Map<ManaColor, Integer> colorIntegerMap = pool.getTotalMap();
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<ManaColor, Integer> entry : colorIntegerMap.entrySet()){
            String str = entry.getKey().getChar() + " " + entry.getValue();
            joiner.add(str);
        }
        return joiner.toString();
    }

    private static String getManaForColor(ManaPool pool, ManaColor color) {
        Map<ManaColor, Integer> colorIntegerMap = pool.getTotalMap();
        Integer integer = colorIntegerMap.get(color);
        return integer != null ? integer.toString() : "0";
    }

    private static String getAltDown() {
        if (ClientSideHelper.isLeftAltDown()) {
            return "LAlt";
        }
        return "";
    }

    private static String getPercentageString() {
        if (percentage < 0.0f) {
            return "";
        }
        return String.format("%3.2f%%", percentage * 100);
    }

    public static void update(String message) {
        fadeStartTimer = START_MAX;
        fadeCountdown = COUNTDOWN_MAX;
        ManaOverlay.message = message;
        ManaOverlay.percentage = -1.0f;
    }

    public static void update(String message, float percentage) {
        fadeStartTimer = START_MAX;
        fadeCountdown = COUNTDOWN_MAX;
        ManaOverlay.message = message;
        ManaOverlay.percentage = percentage;
    }

    public static void clear() {
        message = "";
        percentage = -1.0f;
        fadeStartTimer = 0;
        fadeCountdown = 0;
    }
}
