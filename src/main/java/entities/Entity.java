package entities;

import static com.raylib.Colors.DARKGRAY;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

// Base class for all objects in the game world (Tanks, Shapes, Bullets)
public class Entity {
    // Position and movement
    protected float centerX;
    protected float centerY;
    public float angle;
    protected float speed;
    protected float radius;
    protected float width;
    protected float height;

    // Visual properties
    protected Texture texture;
    protected Color color;
    protected int strokeWidth = 5;
    protected Color hitboxColor = newColor(252, 3, 28, 255);

    // Health properties
    protected float maxHealth;
    protected float health;
    protected float bodyDamage;
    protected boolean alive;
    protected boolean isDamage;

    protected float timeSinceLastHit;
    protected float timeSinceDeath;

    protected float healthRatio;

    // Physics
    protected float velocityX;
    protected float velocityY;
    protected float decay = 6.0f;

    // Health bar dimensions
    float healthBarX, healthBarY, healthBarWidth, healthBarHeight;

    // Constructor
    public Entity(float centerX, float centerY, float angle) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.angle = angle;
        this.isDamage = false;
        this.timeSinceLastHit = 0f;
        this.timeSinceDeath = 0f;
        this.velocityX = 0f;
        this.velocityY = 0f;
        this.healthRatio = 1f;
    }

    // Getters and setters
    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public void setAngle(float angle) { this.angle = angle; }
    public float getRadius() { return radius; }
    public float getWidth() { return width; }
    public boolean isAlive() { return alive; }
    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getBodyDamage() { return bodyDamage; }

    // Take damage logic
    public void takeDamage(float amount) {
        if (!alive) return;
        health -= amount;
        timeSinceLastHit = 0f;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    // Apply knockback velocity
    public void addVelocity(float vx, float vy) {
        velocityX += vx;
        velocityY += vy;
    }

    // Draw health bar under the entity
    public void drawHealthBar() {
        healthBarX = centerX - width / 2f;
        healthBarY = centerY + height / 2f + 6;
        healthBarWidth = width;
        healthBarHeight = 4;

        healthRatio = getHealth() / getMaxHealth();

        Rectangle rect = newRectangle(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        DrawRectangleRounded(rect, 0.5f, 0, DARKGRAY);
        rect = newRectangle(healthBarX, healthBarY, healthBarWidth * healthRatio, healthBarHeight);
        DrawRectangleRounded(rect, 0.5f, 0, newColor(133, 227, 125, 255));
    }

    public void setDamage(boolean isDamage) { this.isDamage = isDamage; }
    public float getTimeSinceDeath() { return timeSinceDeath; }
}