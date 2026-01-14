package com.tanxx.entities;

import com.tanxx.screens.GameScreen;

import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newRectangle;

public class Tank extends Sprite {
    protected float bounceStrength = 0.8f;
    protected float bounceX = 0f;
    protected float bounceY = 0f;
    protected float velocityX = 0;
    protected float velocityY = 0;

    private static final float FRICTION = 0.9f;
    private static final float accel = 20f;

    protected float recoilX = 0f;
    protected float recoilY = 0f;
    protected float recoil;
    protected float decay = 4f;
    protected float barrelW;
    protected float barrelH;
    protected Color barrelColor = newColor(100, 99, 107, 255);
    protected Color barrelStrokeColor = newColor(55, 55, 55, 255);
    protected Texture barrelTexture;
    protected float reloadSpeed;
    protected float reloadTimer = 0f;

    public Tank(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.barrelTexture = barrelTexture;
        this.color = newColor(144, 252, 3, 255);
        this.stroke = newColor(55, 55, 55, 255);
        this.maxHealth = 100;
        this.health = maxHealth;
        this.bodyDamage = 20;
        this.healthRegen = 8;
        this.recoil = size * 0.8f;
        this.alive = true;
    }

    public void update() {
        if (reloadTimer > 0f) {
            reloadTimer -= GameScreen.dt;
        }

        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT)) moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;

        if (moveX != 0 && moveY != 0) {
            moveX /= (float) Math.sqrt(2);
            moveY /= (float) Math.sqrt(2);
        }

        velocityX *= FRICTION;
        velocityY *= FRICTION;

        velocityX += moveX * accel;
        velocityY += moveY * accel;

        velocityX += recoilX;
        velocityY += recoilY;

        recoilX *= 0.9f;
        recoilY *= 0.9f;
        bounceX *= 0.9f;
        bounceY *= 0.9f;

        float speedNow = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        float maxSpeed = 10f * accel;

        if (speedNow > maxSpeed) {
            velocityX = (velocityX / speedNow) * maxSpeed;
            velocityY = (velocityY / speedNow) * maxSpeed;
        }

//        velocityX = moveX * speed + recoilX;
//        velocityY = moveY * speed + recoilY;
//
//        recoilX -= recoilX * decay * GameScreen.dt;
//        recoilY -= recoilY * decay * GameScreen.dt;
//        bounceX -= bounceX * decay * GameScreen.dt;
//        bounceY -= bounceY * decay * GameScreen.dt;

        centerX += (velocityX + bounceX) * GameScreen.dt;
        centerY += (-velocityY + bounceY) * GameScreen.dt;

        if (centerX < 0 && velocityX < 0) {
            centerX = 0;
            bounceX = -velocityX * bounceStrength;
        }
        if (centerX > GameScreen.worldW && velocityX > 0) {
            centerX = GameScreen.worldW;
            bounceX = -velocityX * bounceStrength;
        }
        if (centerY < 0 && velocityY > 0) {
            centerY = 0;
            bounceY = velocityY * bounceStrength;
        }
        if (centerY > GameScreen.worldH && velocityY < 0) {
            centerY = GameScreen.worldH;
            bounceY = velocityY * bounceStrength;
        }

        if (Math.abs(bounceX) < 0.5f) bounceX = 0f;
        if (Math.abs(bounceY) < 0.5f) bounceY = 0f;
        if (Math.abs(recoilX) < 0.5f) recoilX = 0f;
        if (Math.abs(recoilY) < 0.5f) recoilY = 0f;
    }

    public void draw() {
        // Barrel
        Rectangle source = newRectangle(0, 0, barrelTexture.width(), barrelTexture.height());
        Rectangle dest = newRectangle(centerX, centerY, barrelW, barrelH);
        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrelTexture, source, dest, origin, angle * (180f / (float) Math.PI), barrelColor);

        // Tank
        source = newRectangle(0, 0, texture.width() , texture.height());
        dest = newRectangle(centerX, centerY, size, size);
        origin = new Vector2().x(size / 2).y(size / 2);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);

        if (health < maxHealth) drawHealthBar();
    }

    public void applyRecoil() {
        recoilX += (float) -Math.cos(angle) * recoil;
        recoilY += (float) Math.sin(angle) * recoil;
    }

    public boolean canFire() {
        return reloadTimer <= 0f;
    }

    public void resetReload() {
        reloadTimer = reloadSpeed;
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2, hitboxColor);
    }

    public float getBulletSize() {
        return barrelH;
    }
}
