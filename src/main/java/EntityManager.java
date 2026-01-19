import java.util.ArrayList;
import java.util.List;
import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newColor;

// EntityManager class handles the spawning, updating, and collision detection for all entities in the game world
public class EntityManager {

    // World dimensions
    public static final int worldW = 4000;
    public static final int worldH = 4000;

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
    public static float MAX_SHAPE_RADIUS = 50f;
    public static final int startShapes = 100;

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
        double rand = Math.random();
        int type;
        if (rand < 0.3) {
            type = 0;
        } else if (rand < 0.7) {
            type = 1;
        } else if (rand < 0.85) {
            type = 2;
        } else if (rand < 0.95){
            type = 3;
        } else if (rand < 0.99){
            type = 4;
        } else {
            type = 5;
        }

        // Generate a random position that is NOT on top of the player and in world boundaries
        float orbitX;
        float orbitY;
        float orbitRadius = 30 + (float) (Math.random() * 70);
        float safeDistance = playerTank.getWidth() / 2 + 150;

        do {
            orbitX = (float) (Math.random() * (worldW - 2 * orbitRadius)) + orbitRadius;
            orbitY = (float) (Math.random() * (worldH - 2 * orbitRadius)) + orbitRadius;
        } while (Math.hypot(orbitX - playerTank.getCenterX(), orbitY - playerTank.getCenterY()) < safeDistance);

        switch (type) {
            case 0 -> shapes.add(new Shape(orbitX, orbitY, 20, orbitRadius, 0,3, 30, 8, newColor(214, 51, 30, 255), newColor(148, 30, 15, 255), 25));
            case 1 -> shapes.add(new Shape(orbitX, orbitY, 20, orbitRadius, 0,4, 10, 8, newColor(214, 208, 30, 255), newColor(158, 152, 24, 255), 10));
            case 2 -> shapes.add(new Shape(orbitX, orbitY, 25, orbitRadius, 0,5, 100, 12, newColor(82, 58, 222, 255), newColor(59, 36, 212, 255), 100));
            case 3 -> shapes.add(new Shape(orbitX, orbitY, 30, orbitRadius, 0,6, 180, 16, newColor(75, 227, 217, 255), newColor(62, 184, 176, 255), 200));
            case 4 -> shapes.add(new Shape(orbitX, orbitY, 40, orbitRadius, 0,7, 250, 20, newColor(53, 219, 105, 255), newColor(39, 168, 79, 255), 200));
            default -> shapes.add(new Shape(orbitX, orbitY, 45, orbitRadius, 0,8, 350, 30, newColor(209, 144, 23, 255), newColor(181, 127, 27, 255), 200));
        }
    }

    // Fire bullet
    public static void fireBullet() {
        for (Barrel b : playerTank.barrels) {

            // Only fire if the barrel can shoot
            if (!b.canShoot) continue;

            // Spawn bullet
            float baseAngle = playerTank.angle;
            float turretAngle = b.getTurretAngle() * (float) Math.PI / 180f;
            float finalAngle = baseAngle + turretAngle;

            // Offset
            float offsetX = -(float) Math.sin(baseAngle) * b.offset;
            float offsetY = (float) Math.cos(baseAngle) * b.offset;

            // Forward spawn distance
            float bulletRadius = b.getBarrelH();
            float forward = b.getBarrelW() + bulletRadius * 0.5f;

            float bulletX = playerTank.getCenterX() + offsetX + forward * (float) Math.cos(finalAngle);
            float bulletY = playerTank.getCenterY() + offsetY + forward * (float) Math.sin(finalAngle);

            bullets.add(new Bullet(bulletX, bulletY, finalAngle, bullet, bulletRadius, playerTank.getBulletDamage(), playerTank.getBulletSpeed(), playerTank.getBulletPenetration()));

            // Reset timers
            b.reloadTimer = b.reloadSpeed;
            b.delayTimer = b.delay;
        }
        playerTank.applyRecoil();
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

        playerTank = new Basic(randX, randY, angle, tank, barrel);
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
            spatialGrid.getPotentialCollisions(b.getCenterX(), b.getCenterY(), b.getWidth() / 2f, MAX_SHAPE_RADIUS, potentialShapes);

            // Then, loop through the list of potential shapes instead of all shapes (much more efficient)
            for (Shape s : potentialShapes) {
                // Only collide with living shapes
                if (s.isAlive() && Collision.circlePolygonCollision(b.getCenterX(), b.getCenterY(), b.getWidth() / 2f, s.polygon)) {
                    if (s.isAlive()) s.setDamage(true);
                    if (b.isAlive()) b.setDamage(true);
                    // Resolve damage exchange between bullet and shape
                    resolveCollision(b, s, b.getBodyDamage(), s.getBodyDamage());

                    // Apply knockback after damage
                    applyKnockback(b, s, 10, 10);

                    // Award XP
                    if (!s.isAlive()) {
                        playerTank.addScore(s.getXp());
                        playerTank.updateNumShapesKilled();
                    }
                }
            }
        }
    }

    // Check collision between tanks and shapes (same concept as above)
    public static void checkTankShapeCollisions() {
        spatialGrid.getPotentialCollisions(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getWidth() / 2f, MAX_SHAPE_RADIUS, potentialShapes);
        for (Shape s : potentialShapes) {
            if (s.isAlive() && Collision.circlePolygonCollision(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getWidth() / 2f, s.polygon)) {
                if (s.isAlive()) s.setDamage(true);
                if (playerTank.isAlive()) playerTank.setDamage(true);

                resolveCollision(playerTank, s, playerTank.getBodyDamage(), s.getBodyDamage());

                // Apply knockback after damage
                applyKnockback(s, playerTank, 50, 50);

                if (!s.isAlive()) {
                    playerTank.addScore(s.getXp());
                    playerTank.updateNumShapesKilled();
                }
            }
        }
    }

    // Check collision between shapes (again, same concept as above)
    public static void checkShapeShapeCollisions() {
        for (Shape a : shapes) {
            spatialGrid.getPotentialCollisions(a.getCenterX(), a.getCenterY(), a.getWidth() / 2f, MAX_SHAPE_RADIUS, potentialShapes);
            for (Shape b : potentialShapes) {
                if (a == b) continue;
                if (!b.isAlive()) continue;
                if (!shapes.contains(b)) continue;
                if (Collision.polygonPolygonCollision(a.polygon, b.polygon)) {
                    // Shapes don't deal damage to each other, but they do apply knockback
                    applyKnockback(a, b, 8, 8);
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

    // Applies a simple knockback force to separate two colliding entities
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
        // Update shapes
        spatialGrid.clear();
        for (Shape s : shapes) {
            s.update();
            spatialGrid.addShape(s);
        }

        // Update bullets
        for (Bullet b : bullets) {
            b.update();
        }

        if (playerTank.upgradeTank) {
            Twin newTank = new Twin(playerTank.getCenterX(), playerTank.getCenterY(), angle, tank, barrel);
            newTank.copyStats(playerTank);  // copy all previous stats
            playerTank = newTank;
        }

        // Update player tank
        if (!deathScreen) {
            playerTank.update(); // This now handles barrels and shooting
        }

        // Check if dead
        if (!playerTank.isAlive()) {
            deathScreen = true;
        }

        // Remove dead entities
        removeEntities();
    }

    public static void removeEntities() {
        bullets.removeIf(b -> !b.isAlive() && b.getTimeSinceDeath() > 0.08);
        shapes.removeIf(s -> !s.isAlive() && s.getTimeSinceDeath() > 0.08);
    }

    public static void resetGame() {
        shapes.clear();
        bullets.clear();
    }
}
