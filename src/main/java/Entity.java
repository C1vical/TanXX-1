import static com.raylib.Colors.*;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newColor;

// Entity class is the base class for all objects in the game world (Tanks, Shapes, Bullets)
// It contains common properties like position, health, and basic movement logic
public abstract class Entity {
    // Position and movement fields
    protected float centerX;
    protected float centerY;
    protected float angle;
    protected float speed;
    protected float size;

    // Visual fields
    protected Texture texture;
    protected Color color;
    protected int strokeWidth = 5;
    protected Color hitboxColor = newColor(252, 3, 28, 255);

    // Health fields
    protected float maxHealth;
    protected float health;
    protected float healthRegen;
    protected float bodyDamage;
    protected boolean alive;
    protected boolean isDamage;

    protected float timeSinceLastHit = 0f;
    protected float timeSinceDeath = 0f;

    // Health bar dimensions
    float healthBarX, healthBarY, healthBarWidth, healthBarHeight;

    // Physics fields
    protected float velocityX = 0f;
    protected float velocityY = 0f;
    protected float decay = 6.0f;

    // Constructor
    public Entity(float centerX, float centerY, float angle) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.angle = angle;
        isDamage = false;
    }

    // Abstract methods to be implemented by subclasses
    abstract void update();
    abstract void draw();

    // Getters and Setters
    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public void setAngle(float angle) { this.angle = angle; }
    public float getSize() { return size; }
    public boolean isAlive() { return alive; }

    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getBodyDamage() { return bodyDamage; }

    // Logic for taking damage
    public void takeDamage(float amount) {
        if (!alive) return;
        health -= amount;
        timeSinceLastHit = 0f;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    // Add physical velocity (knockback)
    public void addVelocity(float vx, float vy) {
        velocityX += vx;
        velocityY += vy;
    }

    // Passive health regeneration
    public void regenHealth(float dt) {
        timeSinceLastHit += dt;
        if (alive && health < maxHealth && timeSinceLastHit >= 5f) {
            health += 0.1f * maxHealth * dt;
            if (health > maxHealth) health = maxHealth;
        }
    }

    // Drawing the health bar below the entity
    public void drawHealthBar() {
        healthBarX = centerX - size / 2f;
        healthBarY = centerY + size / 2f + 5;
        healthBarWidth = size;
        healthBarHeight = 8;

        Rectangle rect = newRectangle(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        DrawRectangleRounded(rect, 0.3f, 0, DARKGRAY);
        float healthRatio = getHealth() /getMaxHealth();
        rect = newRectangle(healthBarX, healthBarY, healthBarWidth * healthRatio, healthBarHeight);
        DrawRectangleRounded(rect, 0.3f, 0, GREEN);
    }

    public void setDamage(boolean isDamage) {
        this.isDamage = isDamage;
    }

    public float getTimeSinceDeath() {
        return timeSinceDeath;
    }
}
