package entities;

import core.EntityManager;
import physics.Polygon;

import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.RED;
import static com.raylib.Raylib.*;

// Shape class represents polygons in the game world
// Extends Entity, so it has position, health, and velocity
public class Shape extends Entity {
    public Polygon polygon;        // Polygon for collision
    public Color color;            // Fill color
    public Color stroke;           // Stroke color
    protected int xp;              // XP given when destroyed

    // Rotation and orbit properties
    float rotationSpeed;
    float orbitAngle;
    float orbitAngleSpeed;
    float orbitRadius;
    float orbitX;
    float orbitY;

    int sides;                     // Number of sides of polygon
    float step;                    // Angle between vertices
    Vector2[] vertices;            // Vertices of the polygon

    // Constructor to create a shape at an orbit with given properties
    public Shape(float orbitX, float orbitY, float radius, float orbitRadius, float angle,
                 int sides, float maxHealth, float bodyDamage, Color color, Color stroke, int xp) {
        super(0, 0, angle);

        // Size and orbit setup
        this.radius = radius;
        this.width = radius * 2;
        this.height = radius * 2;
        this.orbitX = orbitX;
        this.orbitY = orbitY;
        this.orbitRadius = orbitRadius;

        // Health and damage
        this.alive = true;
        this.maxHealth = maxHealth;
        this.bodyDamage = bodyDamage;
        this.health = maxHealth;

        // Visual
        this.color = color;
        this.stroke = stroke;

        this.xp = xp;

        // Random initial orbit and rotation angles
        this.orbitAngle = (float) (Math.random() * Math.PI * 2);
        this.orbitAngleSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        this.rotationSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);

        // Initial position based on orbit
        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        // Polygon setup
        this.sides = sides;
        this.vertices = new Vector2[sides];
        this.step = (float) (Math.PI * 2.0 / sides);

        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i] = new Vector2().x(centerX + (float) Math.cos(a) * (radius + strokeWidth))
                    .y(centerY + (float) Math.sin(a) * (radius + strokeWidth));
        }

        polygon = new Polygon(vertices);
    }

    // Update shape each frame
    public void update() {
        // Reset damage indicator after short time
        if (timeSinceLastHit > 0.02) isDamage = false;

        // Update time since death if dead
        if (!alive) timeSinceDeath += EntityManager.dt;

        // Regenerate health slowly
        regenHealth(EntityManager.dt);

        // Health ratio for health bar
        healthRatio = health / maxHealth;

        // Update rotation and orbit
        orbitAngle += orbitAngleSpeed * EntityManager.dt;
        angle += rotationSpeed * EntityManager.dt;

        // Apply velocity decay
        velocityX -= velocityX * decay * EntityManager.dt;
        velocityY -= velocityY * decay * EntityManager.dt;

        // Update orbit center by velocity
        orbitX += velocityX * EntityManager.dt;
        orbitY += velocityY * EntityManager.dt;

        // Calculate actual position based on orbit
        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        // Stop tiny velocities
        if (Math.abs(velocityX) < 0.5f) velocityX = 0f;
        if (Math.abs(velocityY) < 0.5f) velocityY = 0f;

        // Keep orbit center within world bounds
        float minX = orbitRadius;
        float maxX = EntityManager.worldW - orbitRadius;
        float minY = orbitRadius;
        float maxY = EntityManager.worldH - orbitRadius;

        if (orbitX < minX) { orbitX = minX; velocityX = 0; }
        if (orbitX > maxX) { orbitX = maxX; velocityX = 0; }
        if (orbitY < minY) { orbitY = minY; velocityY = 0; }
        if (orbitY > maxY) { orbitY = maxY; velocityY = 0; }

        // Update vertices based on current angle and center
        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i].x(centerX + (float) Math.cos(a) * radius);
            vertices[i].y(centerY + (float) Math.sin(a) * radius);
        }

        // Update polygon for collisions
        polygon.update();
    }

    // Draw the shape
    public void draw() {
        Color currentColor = color;
        Color currentStroke = stroke;

        // Change color if damaged or dead
        if (isDamage) {
            currentColor = RAYWHITE;
            currentStroke = RAYWHITE;
        } else if (!alive) {
            currentColor = RED;
            currentStroke = RED;
        }

        // Draw stroke
        DrawPoly(new Vector2().x(centerX).y(centerY), sides, radius, angle * (180f / (float) Math.PI), currentStroke);

        // Draw inner fill
        DrawPoly(new Vector2().x(centerX).y(centerY), sides, radius - strokeWidth, angle * (180f / (float) Math.PI), currentColor);

        // Draw hitbox if debug mode
        if (EntityManager.hitbox) drawHitBox();

        // Draw health bar if damaged
        if (health < maxHealth && alive && timeSinceDeath < 100) drawHealthBar();
    }

    // Draw polygon hitbox for debugging
    public void drawHitBox() {
        DrawPolyLines(new Vector2().x(centerX).y(centerY), sides, radius, angle * (180f / (float) Math.PI), hitboxColor);
    }

    public int getXp() { return xp; }

    // Regenerate health over time
    public void regenHealth(float dt) {
        timeSinceLastHit += dt;

        if (alive && health < maxHealth) {
            if (timeSinceLastHit >= 30f) {
                health += 0.1f * maxHealth * dt;
                if (health > maxHealth) health = maxHealth;
            }
        }
    }
}