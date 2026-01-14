package com.tanxx.entities;

import static com.raylib.Raylib.*;

public class Annihilator extends Tank {
    public Annihilator(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture, barrelTexture);
        this.size = 75;
        this.speed = 150;
        this.barrelW = size - 10;
        this.barrelH = size - 10;
        this.recoil = barrelH * 1.8f;
        this.reloadSpeed = 1.2f;
    }
}
