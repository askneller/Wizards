package com.example.wizards;

public enum ManaColor {
    WHITE('W'),
    BLUE('U'),
    BLACK('B'),
    RED('R'),
    GREEN('G'),
    COLORLESS('C');

    private ManaColor(char c) {
        this.c = c;
    }

    private char c;

    public String getChar() {
        return c + "";
    }
}
