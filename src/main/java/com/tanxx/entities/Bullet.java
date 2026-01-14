package com.tanxx.entities;

import com.tanxx.screens.GameScreen;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

public class Bullet extends Sprite {
    private float lifeTime;

    private float bulletDamage;
    private float bulletSpeed;
    private float bulletPenetration;

    public Bullet(float centerX, float centerY, float angle, Texture texture, float size, float bulletDamage, float bulletSpeed, float bulletPenetration) {
        super(centerX, centerY, angle, texture);
        this.size = size;
        this.speed = bulletSpeed;
        this.color = newColor(24, 158, 140, 255);
        this.maxHealth = bulletPenetration;
        this.health = maxHealth;
        this.bulletDamage = bulletDamage;
        this.lifeTime = 2f;
        this.alive = true;
    }

//    public void updateStats() {
//        healthRegen = 0.1f + (0.4f * healthRegenPoints);
//        maxHealth = 50 + 2 * (level - 1) + 20 * maxHealthPoints;
//        bodyDamage = 20 + 4 * bodyDamagePoints;
//        bulletSpeed = (5 + (4 * bulletSpeedPoints));
//        bulletPenetration = (8 + (6 * bulletPenetrationPoints));
//        bulletDamage = (7 + (3 * bulletDamagePoints));
//    }

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
