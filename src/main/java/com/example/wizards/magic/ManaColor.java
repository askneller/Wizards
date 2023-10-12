package com.example.wizards.magic;

public enum ManaColor {
    COLORLESS('C', 165, 197, 211),
    WHITE('W', 255, 255, 255),
    GREEN('G', 66, 156, 70),
    RED('R', 237, 38, 7),
    BLACK('B', 35, 35, 35),
    BLUE('U', 12, 20, 245),
    ;

    ManaColor(char c, int r, int g, int b) {
        this.c = c;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private final char c;
    public final int r, g, b;

    public String getChar() {
        return c + "";
    }

    public float R() {
        return r / 255.0f;
    }

    public float G() {
        return g / 255.0f;
    }

    public float B() {
        return b / 255.0f;
    }

    public int toInt() {
        int color = 0;
        color |= (r << 16);
        color |= (g << 8);
        color |= b;
        return color;
    }
}
