package entities;

import core.EntityManager;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

// entities.Bullet class represents a projectile fired by tanks
// Inherits from the entity superclass, so it has position, velocity, and basic rendering
public class Bullet extends Entity {
    private float lifeTime;     // How long the bullet exists before disappearing

    // Constructor
    public Bullet(float centerX, float centerY, float angle, Texture texture, float radius, float bulletDamage, float bulletSpeed, float bulletPenetration) {
        super(centerX, centerY, angle);
        this.texture = texture;
        this.radius = radius;
        this.width = radius;
        this.height = radius;
        this.speed = bulletSpeed;
        this.color = newColor(24, 158, 140, 255);
        this.maxHealth = bulletPenetration;     // entities.Bullet penetration is the same as bullet health
        this.health = maxHealth;
        this.bodyDamage = bulletDamage;
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
        Rectangle dest = newRectangle(centerX, centerY, width, height);
        Vector2 origin = new Vector2().x(width / 2).y(height / 2);
        DrawTexturePro(texture, source, dest, origin, angle * (180f / (float) Math.PI), color);

        // Draw hitbox if boolean is true
        if (EntityManager.hitbox) drawHitBox();
    }

    // Draw bullet hitbox
    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), radius / 2, hitboxColor);
    }

    // Getter for bullet damage
    public float getBodyDamage() {
        return bodyDamage;
    }
}
