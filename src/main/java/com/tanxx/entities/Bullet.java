package com.tanxx.entities;

import com.tanxx.screens.GameScreen;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

public class Bullet extends Sprite {
    private float lifeTime;
    float bulletDamage;



    public Bullet(float centerX, float centerY, float angle, Texture texture, float size) {
        super(centerX, centerY, angle, texture);
        this.size = size;
        this.speed = 100;
        this.color = newColor(24, 158, 140, 255);
        this.maxHealth = 8;
        this.health = maxHealth;
        this.bulletDamage = 7;
        this.lifeTime = 2f;
        this.alive = true;
    }

    public void update() {
        centerX += (float) (Math.cos(angle) * speed * GameScreen.dt);
        centerY += (float) (Math.sin(angle) * speed * GameScreen.dt);

        lifeTime -= GameScreen.dt;
        if (lifeTime <= 0f) alive = false;
    }

    public void draw() {
        Rectangle source = newRectangle(0, 0, texture.width(), texture.height());
        Rectangle dest = newRectangle(centerX, centerY, size, size);
        Vector2 origin = new Vector2().x(size / 2).y(size / 2);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);
        if (health < maxHealth) drawHealthBar();
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2, hitboxColor);
    }
    public float getBulletDamage() { return bulletDamage; }
}
