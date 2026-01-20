package entities;

import core.EntityManager;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

// Bullet class represents a projectile fired by tanks
// Inherits from Entity, so it has position, velocity, and basic rendering
public class Bullet extends Entity {
    private float lifeTime;     // How long the bullet exists before disappearing (seconds)

    // Constructor: initialize bullet with position, angle, texture, size, damage, speed, and penetration
    public Bullet(float centerX, float centerY, float angle, Texture texture, float radius, float bulletDamage, float bulletSpeed, float bulletPenetration) {
        super(centerX, centerY, angle);  // Call Entity constructor
        this.texture = texture;          // Texture used to draw bullet
        this.radius = radius;            // Radius for collision
        this.width = radius;             // Width for drawing
        this.height = radius;            // Height for drawing
        this.speed = bulletSpeed;        // Movement speed of the bullet
        this.color = newColor(24, 158, 140, 255);  // Visual color
        this.maxHealth = bulletPenetration;        // Bullet penetration = health
        this.health = maxHealth;                    // Start with full health
        this.bodyDamage = bulletDamage;             // Damage bullet deals on collision
        this.lifeTime = 2f;                         // Bullet exists for 2 seconds by default
        this.alive = true;                          // Bullet starts alive
    }

    // Update bullet position, velocity, and lifetime each frame
    public void update() {
        if (!alive) timeSinceDeath += EntityManager.dt;  // Track time since death if dead

        // Apply velocity decay (friction or resistance)
        velocityX -= velocityX * decay * EntityManager.dt;
        velocityY -= velocityY * decay * EntityManager.dt;

        // Move the bullet forward in its angle direction plus any additional velocity (e.g., from recoil)
        centerX += (float) (Math.cos(angle) * speed * EntityManager.dt) + velocityX * EntityManager.dt;
        centerY += (float) (Math.sin(angle) * speed * EntityManager.dt) + velocityY * EntityManager.dt;

        // Stop movement if velocity is very small to prevent jitter
        if (Math.abs(velocityX) < 0.5f) velocityX = 0f;
        if (Math.abs(velocityY) < 0.5f) velocityY = 0f;

        // Reduce lifetime
        lifeTime -= EntityManager.dt;
        if (lifeTime <= 0f) alive = false;  // Bullet dies if lifetime ends

        // Remove bullet if health is depleted (e.g., penetration used up)
        if (health <= 0f) alive = false;
    }

    // Draw the bullet
    public void draw() {
        // Define source and destination rectangles for texture drawing
        Rectangle source = newRectangle(0, 0, texture.width(), texture.height());
        Rectangle dest = newRectangle(centerX, centerY, width, height);

        // Set origin for rotation (center of the bullet)
        Vector2 origin = new Vector2().x(width / 2).y(height / 2);

        // Draw texture rotated according to bullet angle
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);

        // Optionally draw hitbox if enabled
        if (EntityManager.hitbox) drawHitBox();
    }

    // Draw the bullet's collision hitbox for debugging
    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), radius / 2, hitboxColor);
    }
}
