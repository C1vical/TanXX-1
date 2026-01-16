import java.util.ArrayList;
import java.util.List;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newColor;

// EntityManager class handles the spawning, updating, and collision detection for all entities in the game world
public class EntityManager {

    // World dimensions
    public static final int worldW = 800;
    public static final int worldH = 800;
    public static final int borderSize = 5000;

    // Game state flags
    public static float dt;     // Delta time (seconds per frame)
    public static boolean deathScreen = false;
    public static boolean hitbox = false;
    public static boolean autoFire = false;
    public static boolean autoSpin = false;

    // Lists storing all active entities
    public static List<Bullet> bullets = new ArrayList<>();
    public static List<Shape> shapes = new ArrayList<>();

    // Spacial partitioning (for collisions)
    public static SpatialGrid spatialGrid = new SpatialGrid(worldW, worldH, 100);
    public static List<Shape> potentialShapes = new ArrayList<>();
    public static float MAX_SHAPE_SIZE = 50f;
    public static final int startShapes = 30;

    // Player and textures
    public static Tank playerTank;
    public static float angle;
    public static Texture tank;
    public static Texture barrel;
    public static Texture bullet;

    // Spawn shapes
    public static void spawnShapes() {
        for (int i = 0; i < EntityManager.startShapes - EntityManager.shapes.size(); i++) {
            addShape();
        }
    }

    // Add shapes
    public static void addShape() {
        // Generate a random shape type
        int type = (int) (Math.random() * 3);

        // Generate a random position that is NOT on top of the player and in world boundaries
        float orbitX;
        float orbitY;
        float orbitRadius = 30 + (float) (Math.random() * 70);
        float safeDistance = 75;

        do {
            orbitX = (float) (Math.random() * (worldW - 2 * orbitRadius)) + orbitRadius;
            orbitY = (float) (Math.random() * (worldH - 2 * orbitRadius)) + orbitRadius;
        } while (Math.hypot(orbitX - playerTank.getCenterX(), orbitY - playerTank.getCenterY()) < safeDistance);

        switch (type) {
            case 0 -> shapes.add(new Shape(orbitX, orbitY, orbitRadius, 0,4, 10, 8, newColor(214, 208, 30, 255), newColor(158, 152, 24, 255), 10));
            case 1 -> shapes.add(new Shape(orbitX, orbitY, orbitRadius, 0,3, 30, 8, newColor(214, 51, 30, 255), newColor(148, 30, 15, 255), 25));
            default -> shapes.add(new Shape(orbitX, orbitY, orbitRadius, 0,5, 100, 12, newColor(82, 58, 222, 255), newColor(59, 36, 212, 255), 100));
        }
    }

    // Fire bullet
    public static void fireBullet() {
        for (int i = 0; i < playerTank.barrels.length; i++) {
            float bulletSize = playerTank.barrels[i].getBarrelH();
            float turretAngle = playerTank.barrels[i].getTurretAngle() * (float) Math.PI / 180;
            float bulletX = playerTank.getCenterX() + (float) Math.cos(angle + turretAngle) * (playerTank.barrels[i].getBarrelW() + bulletSize / 2f);
            float bulletY = playerTank.getCenterY() + (float) Math.sin(angle + turretAngle) * (playerTank.barrels[i].getBarrelW() + bulletSize / 2f);
            bullets.add(new Bullet(bulletX, bulletY, angle + turretAngle, bullet, bulletSize, playerTank.getBulletDamage(), playerTank.getBulletSpeed(), playerTank.getBulletPenetration()));
            playerTank.applyRecoil();
            playerTank.resetReload();
        }

    }

    // Respawn player
    public static void respawnPlayer() {
        EntityManager.deathScreen = false;

        float randX, randY;
        float safeDistance = 100;

        // Keep picking a position until it's far enough from all shapes
        boolean safe;
        do {
            randX = EntityManager.worldW * (float) Math.random();
            randY = EntityManager.worldH * (float) Math.random();
            safe = true;

            // Check distance to all shapes
            for (Shape s : EntityManager.shapes) {
                float dx = randX - s.getCenterX();
                float dy = randY - s.getCenterY();
                if (Math.hypot(dx, dy) < safeDistance) {
                    safe = false;
                    break; // Too close to a shape, pick a new position
                }
            }
        } while (!safe);

        int newLevel = Math.max(playerTank.getLevel() / 2, 1);
        int newScore = playerTank.getTotalScore(newLevel);

        playerTank = new ArenaCloser(randX, randY, angle, tank, barrel);
        playerTank.addScore(newScore);

        EntityManager.autoSpin = false;
        EntityManager.autoFire = false;
    }
    // Check all collisions
    public static void checkCollisions() {
        checkBulletShapeCollisions();
        checkShapeShapeCollisions();
        if (!deathScreen) checkTankShapeCollisions();
    }

    // Check collision between bullets and shapes
    public static void checkBulletShapeCollisions() {
        // Loop through every bullet entity
        for (Bullet b : bullets) {
            // Find nearby shapes for collision testing usng the spatial grid
            spatialGrid.getPotentialCollisions(b.getCenterX(), b.getCenterY(), b.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);

            // Then, loop through the list of potential shapes instead of all shapes (much more efficient)
            for (Shape s : potentialShapes) {
                // Only collide with living shapes
                if (s.isAlive() && Collision.circlePolygonCollision(b.getCenterX(), b.getCenterY(), b.getSize() / 2f, s.polygon)) {
                    s.setDamage(true);
                    // Resolve damage exchange between bullet and shape
                    resolveCollision(b, s, b.getBulletDamage(), s.getBodyDamage());

                    // Apply knockback after damage
                    applyKnockback(b, s, 30, 30);

                    // Award XP
                    if (!s.isAlive()) {
                        playerTank.addScore(s.getXp());
                    }
                }
            }
        }
    }

    // Check collision between tanks and shapes (same concept as above)
    public static void checkTankShapeCollisions() {
        spatialGrid.getPotentialCollisions(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);
        for (Shape s : potentialShapes) {
            if (s.isAlive() && Collision.circlePolygonCollision(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getSize() / 2f, s.polygon)) {
                s.setDamage(true);
                playerTank.setDamage(true);

                resolveCollision(playerTank, s, playerTank.getBodyDamage(), s.getBodyDamage());

                // Apply knockback after damage
                applyKnockback(s, playerTank, 100, 100);

                if (!s.isAlive()) {
                    playerTank.addScore(s.getXp());
                }
            }
        }
    }

    // Check collision between shapes (again, same concept as above)
    public static void checkShapeShapeCollisions() {
        for (Shape a : shapes) {
            spatialGrid.getPotentialCollisions(a.getCenterX(), a.getCenterY(), a.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);
            for (Shape b : potentialShapes) {
                if (a == b) continue;
                if (!b.isAlive()) continue;
                if (!shapes.contains(b)) continue;
                if (Collision.polygonPolygonCollision(a.polygon, b.polygon)) {
                    // Shapes don't deal damage to each other, but they do apply knockback
                    applyKnockback(a, b, 5, 5);
                }
            }
        }
    }

    // Resolves a collision using proportional damage (partial damage if one dies early)
    public static void resolveCollision(Entity a, Entity b, float damageA, float damageB) {
        // Maximum possible damage this frame
        float potentialA = damageA * dt;
        float potentialB = damageB * dt;

        // Clamp damage so entities cannot deal more damage than remaining health allows
        float ratioA = (potentialA > b.getHealth()) ? (b.getHealth() / potentialA) : 1.0f;
        float ratioB = (potentialB > a.getHealth()) ? (a.getHealth() / potentialB) : 1.0f;

        // Use the smaller ratio so both sides scale consistently
        float ratio = Math.min(ratioA, ratioB);

        // Apply scaled damage to both entities
        a.takeDamage(damageB * ratio);
        b.takeDamage(damageA * ratio);
    }

    // Applies a simple push-back force to separate two colliding entities
    public static void applyKnockback(Entity a, Entity b, float knockbackStrengthA, float knockbackStrengthB) {
        float dx = b.getCenterX() - a.getCenterX();
        float dy = b.getCenterY() - a.getCenterY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // Avoid division by zero
        if (dist != 0) {
            // Normalize direction vector
            dx /= dist;
            dy /= dist;

            // Push entities away from each other
            a.addVelocity(-dx * knockbackStrengthA, -dy * knockbackStrengthA);
            b.addVelocity(dx * knockbackStrengthB, dy * knockbackStrengthB);
        }
    }

    // Update all entities
    public static void updateEntities() {
        // Update shapes and rebuild the spatial grid
        spatialGrid.clear();
        for (Shape s : shapes) {
            s.update();
            spatialGrid.addShape(s);
        }

        // Update player
        if (!deathScreen) {
            playerTank.update();
        }

        if (!playerTank.isAlive()) {
            deathScreen = true;
        }

        // Update bullets
        for (Bullet b : bullets) {
            b.update();
        }

        // Remove dead entities AFTER they have been updated (so they can move one last time)
        removeEntities();
    }

    public static void removeEntities() {
        bullets.removeIf(b -> !b.isAlive() && b.getTimeSinceDeath() > 0.08);
        shapes.removeIf(s -> !s.isAlive() && s.getTimeSinceDeath() > 0.08);
    }
}
