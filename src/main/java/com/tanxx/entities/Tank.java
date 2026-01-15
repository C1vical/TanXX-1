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

    private int healthRegenPoints = 0;
    private int maxHealthPoints = 0;
    private int bodyDamagePoints = 0;
    private int bulletSpeedPoints = 0;
    private int bulletPenetrationPoints = 0;
    private int bulletDamagePoints = 0;
    private int reloadPoints = 0;
    private int movementSpeedPoints = 0;

    private float bulletSpeed;
    private float bulletPenetration;
    private float bulletDamage;
    private float bodyDamageTank;

    private int score;
    private int levelScore;
    private int level;
    private float levelProgress;

    public Tank(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.barrelTexture = barrelTexture;
        this.color = newColor(24, 158, 140, 255);
        updateStats();
        this.health = maxHealth;
        this.alive = true;
        this.score = 0;
        this.levelScore = 0;
        this.level = 1;
        this.levelProgress = 0f;
    }

    public void updateStats() {
        healthRegen = 0.1f + (0.4f * healthRegenPoints);
        maxHealth = 50 + 2 * (level - 1) + 20 * maxHealthPoints;
        bodyDamage = (20 + 4 * bodyDamagePoints);
        bodyDamageTank = 30 + 6 * bodyDamagePoints;
        bulletSpeed = (5 + 4 * bulletSpeedPoints) * 30;
        bulletPenetration = 8 + 6 * bulletPenetrationPoints;
        bulletDamage = 7 + 3 * bulletDamagePoints;
        reloadSpeed = 0.6f - (0.04f * reloadPoints);
        speed = 150 + (10 * movementSpeedPoints);
    }

    public void update() {
        if (reloadTimer > 0f) {
            reloadTimer -= GameScreen.dt;
        }

        regenHealth(GameScreen.dt);

        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT))  moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;

        if (moveX != 0 && moveY != 0) {
            moveX /= (float) Math.sqrt(2);
            moveY /= (float) Math.sqrt(2);
        }

        velocityX = moveX * speed + recoilX;
        velocityY = moveY * speed + recoilY;

        recoilX -= recoilX * decay * GameScreen.dt;
        recoilY -= recoilY * decay * GameScreen.dt;
        bounceX -= bounceX * decay * GameScreen.dt;
        bounceY -= bounceY * decay * GameScreen.dt;

        centerX += (velocityX + bounceX) * GameScreen.dt;
        centerY += (-velocityY + bounceY) * GameScreen.dt;

        if (centerX < 0 && velocityX < 0) {
            centerX = 0;
            bounceX = -velocityX * bounceStrength;
        }

        if (centerX > GameScreen.worldW  && velocityX > 0) {
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
        recoilX = -recoil * (float) Math.cos(angle);
        recoilY = recoil * (float) Math.sin(angle);
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

    public float getBulletSpeed() { return bulletSpeed; }

    public float getBulletDamage() { return bulletDamage; }

    public float getBulletPenetration() { return bulletPenetration; }

    public float getBodyDamageTank() { return bodyDamageTank; }

    public void setHealthRegenPoints(int healthRegenPoints) {
        this.healthRegenPoints = healthRegenPoints;
    }

    public void setMaxHealthPoints(int maxHealthPoints) {
        this.maxHealthPoints = maxHealthPoints;
    }

    public void setBodyDamagePoints(int bodyDamagePoints) {
        this.bodyDamagePoints = bodyDamagePoints;
    }

    public void setBulletSpeedPoints(int bulletSpeedPoints) {
        this.bulletSpeedPoints = bulletSpeedPoints;
    }

    public void setBulletPenetrationPoints(int bulletPenetrationPoints) {
        this.bulletPenetrationPoints = bulletPenetrationPoints;
    }

    public void setBulletDamagePoints(int bulletDamagePoints) {
        this.bulletDamagePoints = bulletDamagePoints;
    }

    public void setReloadPoints(int reloadPoints) {
        this.reloadPoints = reloadPoints;
    }

    public void setMovementSpeedPoints(int movementSpeedPoints) {
        this.movementSpeedPoints = movementSpeedPoints;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public float getLevelProgress() {
        return levelProgress;
    }

    public void addScore(int amount) {
        score += amount;
        levelScore += amount;

        while (levelScore >= xpToNextLevel()) {
            levelScore -= xpToNextLevel();
            levelUp();
        }

        levelProgress = (float) levelScore / xpToNextLevel();
    }


    private int xpToNextLevel() {
        return 100 + (level - 1) * 50;
    }

    private void levelUp() {
        level++;

        updateStats();
        health = maxHealth; // Diep-style full heal (optional)
    }

}
