package entities;

import core.EntityManager;
import core.Graphics;
import core.TankType;

import java.util.ArrayList;
import java.util.List;

import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

// Tank class represents the player-controlled tank
// Extends Entity, so it has position, health, velocity, and rendering logic
public class Tank extends Entity {

    // Movement and physics properties
    protected float bounceStrength = 0.8f; // Force applied when hitting world bounds
    protected float inputVelX = 0;         // Input velocity X
    protected float inputVelY = 0;         // Input velocity Y
    protected float baseRadius = 50f;      // Base tank radius

    // Array of barrels the tank can fire from
    public Barrel[] barrels;

    // Tank stats (0-7 correspond to different upgrades)
    private int[] stats = new int[8];
    private float healthRegen;      // Regeneration per second
    private float bulletSpeed;       // Current bullet speed
    private float bulletPenetration; // Current bullet penetration
    private float bulletDamage;      // Current bullet damage
    private float speed;             // Base movement speed

    // Progression stats
    private int score;               // Total score
    private int levelScore;          // Score for current level
    private int level;               // Current level
    private float levelProgress;     // Progress to next level
    private int skillPoints;         // Available skill points for upgrades
    private final int[] levelXP = {4, 9, 15, 22, 28, 35, 44, 54, 64, 75, 87, 101, 117, 132, 161, 161, 192, 215, 251, 259, 299, 322, 388, 398, 450, 496, 546, 600, 659, 723, 791, 839, 889, 942, 999, 1059, 1093, 1190, 1261, 1337, 1417, 1502, 1593, 1687, 0};
    private boolean upgradeSkill = false; // True if a skill can be upgraded
    public boolean upgradeTank;           // True if tank upgrade is available
    public List<Integer> pendingUpgradeLevels = new ArrayList<>(); // Queued tank upgrades

    // Game statistics
    private float timeAlive;          // Time tank has been alive
    private int numShapesKilled;      // Number of shapes destroyed

    // Tank scaling factors
    protected float sizeFactor;
    protected float zoomFactor;
    protected float bulletSpeedFactor;
    protected float damageFactor;

    // Default barrel stats
    protected static final float defaultReload = 0.6f;
    protected static final float defaultRecoil = 50f;

    // Tank type (basic, twin, sniper, etc.)
    protected TankType type;

    // Constructor
    public Tank(float centerX, float centerY, float angle, Texture texture) {
        super(centerX, centerY, angle);

        this.texture = texture;           // Tank texture
        this.color = newColor(24, 158, 140, 255); // Default color

        // Initialize stats array
        for (int i = 0; i < 8; i++) stats[i] = 0;

        // Default progression values
        this.alive = true;
        this.level = 1;
        this.score = 0;
        this.levelScore = 0;
        this.skillPoints = 0;
        this.levelProgress = 0f;

        // Default size
        this.health = maxHealth;
        this.baseRadius = radius;

        // Gameplay stats
        this.timeAlive = 0;
        this.numShapesKilled = 0;

        // Tank upgrade flag
        this.upgradeTank = false;

        // Default scaling
        this.sizeFactor = 1f;
    }

    // Recalculate stats based on stat levels and player level
    public void updateStats() {
        healthRegen = 0.001f + 0.005f * (float)Math.pow(1.67, stats[0]); // Health regen formula
        maxHealth = 50 + 2 * (level - 1) + 20 * stats[1];               // Max health formula
        bodyDamage = 20 + 4 * stats[2];                                 // Body collision damage
        bulletSpeed = (200 + 20 * stats[3]) * bulletSpeedFactor;        // Bullet speed
        bulletPenetration = 8 + 6 * stats[4];                           // Penetration
        bulletDamage = 7 + 3 * stats[5];                                // Bullet damage

        // Update barrels reload speed
        for (Barrel b : barrels) b.reloadSpeed = b.baseReloadSpeed - 0.04f * stats[6];

        // Movement speed
        speed = 150 + 10 * stats[7];

        // Camera zoom adjusts with level
        Graphics.zoomLevel = Graphics.defaultZoom * (float)Math.pow(0.995f, level - 1);

        // Maintain current health ratio
        health = maxHealth * healthRatio;

        // Update barrel delay
        updateDelay();
    }

    // Update barrel dimensions and recoil
    protected void updateDimensions() {
        sizeFactor = (float)Math.pow(1.01f, level - 1);
        radius = baseRadius * sizeFactor;
        width = radius;
        height = radius;

        for (Barrel b : barrels) {
            b.setBarrelW(b.baseBarrelW * sizeFactor);
            b.setBarrelH(b.baseBarrelH * sizeFactor);
            b.setOffset(b.baseOffset * sizeFactor);
            b.setRecoil(b.baseRecoil * sizeFactor);
        }
    }

    // Update barrel delay for firing
    protected void updateDelay() {
        for (Barrel b : barrels) {
            b.delay = b.reloadSpeed * b.baseDelay / b.baseReloadSpeed;
        }
    }

    // Update method called every frame
    public void update() {
        timeAlive += EntityManager.dt;

        // Update all barrels
        for (Barrel b : barrels) b.update();

        // Update movement
        handleInput();

        // Passive health regen
        regenHealth(EntityManager.dt);

        // Update health ratio for UI
        healthRatio = health / maxHealth;

        // Damage cooldown
        if (timeSinceLastHit > 0.02) isDamage = false;

        // Time since death if dead
        if (!alive) timeSinceDeath += EntityManager.dt;
    }

    // Handle tank movement input
    public void handleInput() {
        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT)) moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;

        // Normalize diagonal movement
        if (moveX != 0 && moveY != 0) {
            moveX /= Math.sqrt(2);
            moveY /= Math.sqrt(2);
        }

        inputVelX = moveX * speed;
        inputVelY = moveY * speed;

        // Apply velocity decay
        velocityX -= velocityX * decay * EntityManager.dt;
        velocityY -= velocityY * decay * EntityManager.dt;

        // Update position
        centerX += (inputVelX + velocityX) * EntityManager.dt;
        centerY += (-inputVelY + velocityY) * EntityManager.dt;

        // Keep tank inside world bounds
        checkBounds();

        // Stop tiny velocities
        if (Math.abs(velocityX) < 0.5f) velocityX = 0f;
        if (Math.abs(velocityY) < 0.5f) velocityY = 0f;
    }

    // World boundary collisions with bounce
    public void checkBounds() {
        if (centerX < 0 && (inputVelX + velocityX) < 0) {
            centerX = 0;
            velocityX = -(inputVelX + velocityX) * bounceStrength - inputVelX;
        }
        if (centerX > EntityManager.worldW && (inputVelX + velocityX) > 0) {
            centerX = EntityManager.worldW;
            velocityX = -(inputVelX + velocityX) * bounceStrength - inputVelX;
        }
        if (centerY < 0 && (-inputVelY + velocityY) < 0) {
            centerY = 0;
            velocityY = -(-inputVelY + velocityY) * bounceStrength + inputVelY;
        }
        if (centerY > EntityManager.worldH && (-inputVelY + velocityY) > 0) {
            centerY = EntityManager.worldH;
            velocityY = -(-inputVelY + velocityY) * bounceStrength + inputVelY;
        }
    }

    // Draw the tank and barrels
    public void draw() {
        for (Barrel barrel : barrels) barrel.draw();

        Color currentColor = color;
        if (isDamage) currentColor = RAYWHITE;
        else if (!alive) currentColor = RED;

        Rectangle source = newRectangle(0, 0, texture.width(), texture.height());
        Rectangle dest = newRectangle(centerX, centerY, width, height);
        Vector2 origin = new Vector2().x(width / 2).y(height / 2);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float)Math.PI), currentColor);

        if (EntityManager.hitbox) drawHitBox();
        if (health < maxHealth) drawHealthBar();
    }

    // Apply knockback when firing
    public void applyRecoil() {
        for (Barrel barrel : barrels) {
            float a = angle + barrel.getTurretAngle() * (float)Math.PI / 180f;
            addVelocity(-barrel.getRecoil() * (float)Math.cos(a), -barrel.getRecoil() * (float)Math.sin(a));
        }
    }

    // Draw circular hitbox
    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), radius / 2, hitboxColor);
    }

    // Passive health regeneration
    public void regenHealth(float dt) {
        timeSinceLastHit += dt;

        if (alive && health < maxHealth) {
            if (timeSinceLastHit >= 30f) health += (healthRegen + 0.1f) * maxHealth * dt;
            else health += healthRegen * maxHealth * dt;
            if (health > maxHealth) health = maxHealth;
        }
    }

    // Add score and handle level up
    public void addScore(int amount) {
        score += amount;
        levelScore += amount;

        // Handle multiple level ups
        while (level < 45 && levelScore >= levelXP[level - 1]) levelUp();

        levelProgress = level < 45 ? (float)levelScore / levelXP[level - 1] : 1f;
    }

    // Level up logic
    public void levelUp() {
        levelScore -= levelXP[level - 1];
        level++;
        // If max level, progress is instantly 100% to signify max level
        if (level >= 45) {
            levelProgress = 1f;
        } else {
            levelProgress = (float) levelScore / levelXP[level - 1];
        }
        // Award skill points
        if (level <= 28 || level % 3 == 0) {
            skillPoints++;
            upgradeSkill = true;
        }

        // Check tank upgrade milestones
        if (level == 15 || level == 30 || level == 45) upgradeTank = true;

        // Recalculate stats
        updateStats();
        updateDimensions();
    }

    // Upgrade a stat if points are available
    public void upgradeStat(int index) {
        if (skillPoints > 0 && stats[index] < 7) {
            stats[index]++;
            skillPoints--;
            updateStats();
        }
    }

    // Total score until a certain level
    public int getTotalScore(int level) {
        int total = 0;
        for (int i = 0; i < level - 1; i++) total += levelXP[i];
        return total;
    }

    // Queue upgrade levels based on current level
    public void checkUpgradeLevels() {
        pendingUpgradeLevels.clear();
        int[] upgradeTriggers = {15, 30, 45};
        for (int lvl : upgradeTriggers) if (level >= lvl) pendingUpgradeLevels.add(lvl);
        upgradeTank = !pendingUpgradeLevels.isEmpty();
    }

    // Complete an upgrade
    public void completeUpgrade() {
        if (!pendingUpgradeLevels.isEmpty()) pendingUpgradeLevels.remove(0);
        upgradeTank = !pendingUpgradeLevels.isEmpty();
    }

    // Copy stats from another tank (used during upgrades)
    public void copyStats(Tank tank) {
        this.level = tank.level;
        this.levelScore = tank.levelScore;
        this.levelProgress = tank.levelProgress;
        this.skillPoints = tank.skillPoints;
        this.stats = tank.stats.clone();

        this.score = tank.score;
        this.timeAlive = tank.timeAlive;
        this.numShapesKilled = tank.numShapesKilled;

        this.upgradeTank = tank.upgradeTank;
        this.alive = tank.alive;

        updateStats();
        updateDimensions();
    }

    // Check if any barrel can fire
    public boolean canFire() {
        for (Barrel b : barrels) if (b.canShoot()) return true;
        return false;
    }

    // Getter and setter methods
    public float getBulletSpeed() { return bulletSpeed; }
    public float getBulletDamage() { return bulletDamage; }
    public float getBulletPenetration() { return bulletPenetration; }
    public int getLevel() { return level; }
    public int getScore() { return score; }
    public float getLevelProgress() { return levelProgress; }
    public int getSkillPoints() { return skillPoints; }
    public int[] getStats() { return stats; }
    public float getZoomFactor() { return zoomFactor; }
    public TankType getType() { return type; }
    public boolean isUpgradeSkill() { return upgradeSkill; }
    public int getNumShapesKilled() { return numShapesKilled; }
    public float getTimeAlive() { return timeAlive; }

    public void setUpgradeSkill(boolean upgradeSkill) { this.upgradeSkill = upgradeSkill; }
    public void setLevelProgress(float levelProgress) { this.levelProgress = levelProgress; }
    public void updateNumShapesKilled() { numShapesKilled++; }
}

