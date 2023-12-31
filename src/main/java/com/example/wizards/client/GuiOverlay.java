package com.example.wizards.client;

import com.example.wizards.entity.SummonedCreature;
import com.example.wizards.magic.CastingSystem;
import com.example.wizards.magic.ManaColor;
import com.example.wizards.magic.ManaPool;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.wizards.magic.ManaColor.BLACK;
import static com.example.wizards.magic.ManaColor.BLUE;
import static com.example.wizards.magic.ManaColor.COLORLESS;
import static com.example.wizards.magic.ManaColor.GREEN;
import static com.example.wizards.magic.ManaColor.RED;
import static com.example.wizards.magic.ManaColor.WHITE;
import static com.example.wizards.client.ClientSideHelper.setRenderColor;

public class GuiOverlay {

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

        int startX2 = x + 100;
        int startY2 = y - 24;

        // Selected spell
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MutableComponent component = Component.literal(ClientSpellList.getSelectedName());
        Component selectedCostString = ClientSpellList.getSelectedCostString();
        component.append(" ");
        component.append(selectedCostString);
        guiGraphics.drawString(Minecraft.getInstance().font, component, startX2, startY2, 14737632);

        // Selected entity
        String str = "[" + getSelected() + "]";
        guiGraphics.drawString(Minecraft.getInstance().font, str, startX2, startY2 + 13, 14737632);

        // Controlled entities
        List<Component> controlled = getControlled();
        int startX3 = 8;
        int startY3 = 8;
        for (Component comp: controlled) {
            // TODO do height check to make sure list doesn't go off screen
            guiGraphics.drawString(Minecraft.getInstance().font, comp, startX3, startY3, 14737632);
            startY3 += 13;
        }
    });

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

    private static String getSelected() {
        Entity selected = ClientSideHelper.getSelectedEntity();
        if (selected == null) {
            return "";
        }
//        print();
        return selected.getName().getString() + " (" + selected.getId() + ")";
    }

    private static List<Component> getControlled() {
        LocalPlayer player = Minecraft.getInstance().player;
        List<LivingEntity> controlled = CastingSystem.getControlled(player.getStringUUID());
        return controlled.stream()
                .map(entity -> {
                    Component name = entity.getName();
                    if (entity instanceof SummonedCreature sc) {
                        MutableComponent copy = name.copy();
                        copy.append(" " + sc.getCurrentPower() + "/" + sc.getCurrentToughness());
                        return copy;
                    }
                    return name;
                })
                .collect(Collectors.toList());
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
        GuiOverlay.message = message;
        GuiOverlay.percentage = -1.0f;
    }

    public static void update(String message, float percentage) {
        fadeStartTimer = START_MAX;
        fadeCountdown = COUNTDOWN_MAX;
        GuiOverlay.message = message;
        GuiOverlay.percentage = percentage;
    }

    public static void clear() {
        message = "";
        percentage = -1.0f;
        fadeStartTimer = 0;
        fadeCountdown = 0;
    }
}
