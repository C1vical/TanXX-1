package com.tanxx.entities;

import static com.raylib.Raylib.*;

public class Basic extends Tank {
    public Basic(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture, barrelTexture);
        this.size = 50;
        this.speed = 150;
        this.barrelW = size;
        this.barrelH = size / 2;
        this.recoil = barrelH * 0.5f;
        this.reloadSpeed = 0.4f;
    }
}
