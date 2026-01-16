import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

// Bullet class represents a projectile fired by tanks
// Inherits from the entity superclass, so it has position, velocity, and basic rendering
public class Bullet extends Entity {
    private float lifeTime;     // How long the bullet exists before disappearing
    private float bulletDamage; // Damage dealt on impact

    // Constructor
    public Bullet(float centerX, float centerY, float angle, Texture texture, float size, float bulletDamage, float bulletSpeed, float bulletPenetration) {
        super(centerX, centerY, angle);
        this.texture = texture;
        this.size = size;
        this.speed = bulletSpeed;
        this.color = newColor(24, 158, 140, 255);
        this.maxHealth = bulletPenetration;     // Bullet penetration is the same as bullet health
        this.health = maxHealth;
        this.bulletDamage = bulletDamage;
        this.lifeTime = 2f; // Lifetime in seconds
        this.alive = true;
    }

    // Update the bullet's position, velocity, and lifetime each frame
    public void update() {
        if (!alive) timeSinceDeath += EntityManager.dt;
        // Apply decay to velocity
        velocityX -= velocityX * decay * EntityManager.dt;
        velocityY -= velocityY * decay * EntityManager.dt;

        // Move the bullet in the direction it's facing plus any velocity (from recoil, knockback, etc.)
        centerX += (float) (Math.cos(angle) * speed * EntityManager.dt) + velocityX * EntityManager.dt;
        centerY += (float) (Math.sin(angle) * speed * EntityManager.dt) + velocityY * EntityManager.dt;

        // Stop the bullet from moving if velocity is very small
        if (Math.abs(velocityX) < 0.5f) velocityX = 0f;
        if (Math.abs(velocityY) < 0.5f) velocityY = 0f;

        // Reduce lifetime
        lifeTime -= EntityManager.dt;
        if (lifeTime <= 0f) alive = false;

        // Remove the bullet if health is less than 0
        if (health <= 0f) alive = false;
    }

    // Draw the bullet
    public void draw() {
        Rectangle source = newRectangle(0, 0, texture.width(), texture.height());
        Rectangle dest = newRectangle(centerX, centerY, size, size);
        Vector2 origin = new Vector2().x(size / 2).y(size / 2);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);

        // Draw hitbox if boolean is true
        if (EntityManager.hitbox) drawHitBox();
    }

    // Draw bullet hitbox
    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2, hitboxColor);
    }

    // Getter for bullet damage
    public float getBulletDamage() {
        return bulletDamage;
    }
}
