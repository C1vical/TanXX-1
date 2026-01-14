package com.tanxx.entities;

import static com.raylib.Colors.*;
import static com.raylib.Colors.BLACK;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newColor;
import static com.tanxx.screens.GameState.screenH;
import static com.tanxx.screens.GameState.screenW;

public class Sprite {
    protected float centerX;
    protected float centerY;
    protected float angle;
    protected float speed;
    protected float size;
    protected Texture texture;
    protected Color color;
    protected Color stroke;
    protected int strokeWidth = 5;
    protected Color hitboxColor = newColor(17, 184, 83, 255);

    protected float maxHealth;
    protected float health;
    protected float healthRegen;
    protected float bodyDamage;
    protected boolean alive;

    float healthBarX, healthBarY, healthBarWidth, healthBarHeight;

    public Sprite(float centerX, float centerY, float angle, Texture texture) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.angle = angle;
        this.texture = texture;
    }

    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public void setAngle(float angle) { this.angle = angle; }
    public float getSize() { return size; }
    public boolean isAlive() { return alive; }

    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getBodyDamage() { return bodyDamage; }

    public void takeDamage(float amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    public void regenHealth(float dt) {
        if (alive && health < maxHealth) {
            health += healthRegen * dt;
            if (health > maxHealth) health = maxHealth;
        }
    }

    public void drawHealthBar() {
        healthBarX = centerX - size / 2f;
        healthBarY = centerY + size / 2f + 5;
        healthBarWidth = size;
        healthBarHeight = 10;

        Rectangle rect = newRectangle(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        DrawRectangleRounded(rect, 0.3f, 0, DARKGRAY);
        float healthRatio = getHealth() /getMaxHealth();
        rect = newRectangle(healthBarX, healthBarY, healthBarWidth * healthRatio, healthBarHeight);
        DrawRectangleRounded(rect, 0.3f, 0, GREEN);

    }
}
