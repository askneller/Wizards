//package com.example.examplemod.client;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.GameRenderer;
//import net.minecraftforge.client.gui.overlay.IGuiOverlay;
//
//public class ManaOverlay {
//
//    private static String message = "Hello world";
//    private static float percentage = -1.0f;
//    private static int timer = 0;
//    private static final int START_MAX = 200;
//    private static int fadeStartTimer = START_MAX;
//    private static final int COUNTDOWN_MAX = 200;
//    private static int fadeCountdown = COUNTDOWN_MAX;
//
//    public static final IGuiOverlay GUI_OVERLAY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
//        int x = screenWidth / 2;
//        int y = screenHeight;
//        int startX = x - 80;
//        int startY = y - 55;
//
//        if (fadeStartTimer > 0) {
//            fadeStartTimer--;
//        }
//        if (fadeStartTimer == 0 && fadeCountdown > 0) {
//            fadeCountdown--;
//        }
//
//        float alpha = (float) fadeCountdown / COUNTDOWN_MAX;
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        String str = "ClientThirstData / Mana " + ClientThirstData.getPlayerThirst(); //fadeCountdown > 0 ? message + " " + getPercentageString() : "";
//        guiGraphics.drawString(Minecraft.getInstance().font, str, startX, startY, 14737632);
////        guiGraphics.drawString(Minecraft.getInstance().font, getPercentageString(), startX + 150, startY, 14737632);
//    });
//
//    private static String getPercentageString() {
//        if (percentage < 0.0f) {
//            return "";
//        }
//        return String.format("%3.2f%%", percentage * 100);
//    }
//
//    public static void update(String message) {
//        fadeStartTimer = START_MAX;
//        fadeCountdown = COUNTDOWN_MAX;
//        ManaOverlay.message = message;
//        ManaOverlay.percentage = -1.0f;
//    }
//
//    public static void update(String message, float percentage) {
//        fadeStartTimer = START_MAX;
//        fadeCountdown = COUNTDOWN_MAX;
//        ManaOverlay.message = message;
//        ManaOverlay.percentage = percentage;
//    }
//
//    public static void clear() {
//        message = "";
//        percentage = -1.0f;
//        fadeStartTimer = 0;
//        fadeCountdown = 0;
//    }
//}
