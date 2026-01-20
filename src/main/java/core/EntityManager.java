package core;

import entities.*;
import physics.Collision;
import physics.SpatialGrid;

import java.util.ArrayList;
import java.util.List;

import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.Texture;

// EntityManager handles all entities in the game: tanks, bullets, shapes, and collisions
public class EntityManager {

    // World size and initial shape count
    public static final int worldW = 4000;
    public static final int worldH = 4000;
    public static final int startShapes = 100;

    // Game flags
    public static float dt;     // Delta time per frame
    public static boolean deathScreen = false;
    public static boolean hitbox = false;
    public static boolean autoFire = false;
    public static boolean autoSpin = false;

    // Entity lists
    public static List<Bullet> bullets = new ArrayList<>();
    public static List<Shape> shapes = new ArrayList<>();

    // Spatial grid for collision checks
    public static SpatialGrid spatialGrid = new SpatialGrid(worldW, worldH, 100);
    public static List<Shape> potentialShapes = new ArrayList<>();
    public static float MAX_SHAPE_RADIUS = 50f;

    // Player info
    public static Tank playerTank;
    public static TankType playerTankType;
    public static TankType requestedTank;
    public static float angle;
    public static Texture tank;
    public static Texture barrel;
    public static Texture bullet;

    // Spawn enough shapes to maintain startShapes
    public static void spawnShapes() {
        for (int i = 0; i < startShapes - shapes.size(); i++) {
            addShape();
        }
    }

    // Add a single shape at a random safe position
    public static void addShape() {
        // Determine random type (squares, triangles more common than the bigger shapes)
        double rand = Math.random();
        int type;
        if (rand < 0.3) type = 0;
        else if (rand < 0.7) type = 1;
        else if (rand < 0.90) type = 2;
        else if (rand < 0.96) type = 3;
        else if (rand < 0.99) type = 4;
        else if (rand < 0.998) type = 5;
        else type = 6;

        // Generate a random position far enough from player
        float orbitX, orbitY;
        float orbitRadius = 30 + (float) (Math.random() * 70);
        float safeDistance = playerTank.getWidth() / 2 + 150;

        do {
            orbitX = (float) (Math.random() * (worldW - 2 * orbitRadius)) + orbitRadius;
            orbitY = (float) (Math.random() * (worldH - 2 * orbitRadius)) + orbitRadius;
        } while (Math.hypot(orbitX - playerTank.getCenterX(), orbitY - playerTank.getCenterY()) < safeDistance);

        // Add shape based on type, each with their own radius, health, body damage, colour, and xp
        switch (type) {
            case 0 -> shapes.add(new Shape(orbitX, orbitY, 20, orbitRadius, 0, 3, 30, 8, newColor(214, 51, 30, 255), newColor(148, 30, 15, 255), 25));
            case 1 -> shapes.add(new Shape(orbitX, orbitY, 20, orbitRadius, 0, 4, 10, 8, newColor(214, 208, 30, 255), newColor(158, 152, 24, 255), 10));
            case 2 -> shapes.add(new Shape(orbitX, orbitY, 25, orbitRadius, 0, 5, 150, 12, newColor(82, 58, 222, 255), newColor(59, 36, 212, 255), 80));
            case 3 -> shapes.add(new Shape(orbitX, orbitY, 30, orbitRadius, 0, 6, 250, 18, newColor(75, 227, 217, 255), newColor(62, 184, 176, 255), 150));
            case 4 -> shapes.add(new Shape(orbitX, orbitY, 45, orbitRadius, 0, 7, 500, 30, newColor(53, 219, 105, 255), newColor(39, 168, 79, 255), 400));
            case 5 -> shapes.add(new Shape(orbitX, orbitY, 60, orbitRadius, 0, 8, 1000, 50, newColor(209, 144, 23, 255), newColor(181, 127, 27, 255), 1000));
            default -> shapes.add(new Shape(orbitX, orbitY, 80, orbitRadius, 0, 10, 2500, 100, newColor(27, 27, 27, 255), newColor(17, 17, 17, 255), 5000));
        }
    }

    // Fire bullets from the player tank
    public static void fireBullet() {
        for (Barrel b : playerTank.barrels) {
            if (!b.canShoot()) continue;

            float baseAngle = playerTank.angle;
            float turretAngle = b.getTurretAngle() * (float) Math.PI / 180f;
            float finalAngle = baseAngle + turretAngle;

            float offsetX = -(float) Math.sin(baseAngle) * b.getOffset();
            float offsetY = (float) Math.cos(baseAngle) * b.getOffset();

            float bulletRadius = b.getBarrelH();
            float forward = b.getBarrelW() + bulletRadius * 0.5f;

            float bulletX = playerTank.getCenterX() + offsetX + forward * (float) Math.cos(finalAngle);
            float bulletY = playerTank.getCenterY() + offsetY + forward * (float) Math.sin(finalAngle);

            bullets.add(new Bullet(bulletX, bulletY, finalAngle, bullet, bulletRadius, playerTank.getBulletDamage(), playerTank.getBulletSpeed(), playerTank.getBulletPenetration()));

            b.reloadTimer = b.reloadSpeed;
            b.delayTimer = b.delay;
        }

        playerTank.applyRecoil();
    }

    // Respawn the player tank at a random, safe location
    public static void respawnPlayer() {
        // Set death screen to false
        deathScreen = false;

        // Make sure player doesn't spawn on top of a shape
        float randX, randY;
        float safeDistance = 100;
        boolean safe;

        do {
            randX = worldW * (float) Math.random();
            randY = worldH * (float) Math.random();
            safe = true;

            for (Shape s : shapes) {
                float dx = randX - s.getCenterX();
                float dy = randY - s.getCenterY();
                if (Math.hypot(dx, dy) < safeDistance) {
                    safe = false;
                    break;
                }
            }
        } while (!safe);

        // Calculate level and score
        int newLevel = Math.max(playerTank.getLevel() / 2, 1);
        int newScore = playerTank.getTotalScore(newLevel);

        playerTank = TankFactory.create(TankType.BASIC, randX, randY, angle);
        playerTankType = TankType.BASIC;
        requestedTank = TankType.BASIC;
        playerTank.addScore(newScore);

        playerTank.pendingUpgradeLevels.clear();
        playerTank.checkUpgradeLevels();

        autoSpin = false;
        autoFire = false;
    }

    // Check all collisions between bullets, shapes, and the tank
    public static void checkCollisions() {
        checkBulletShapeCollisions();
        checkShapeShapeCollisions();
        if (!deathScreen) checkTankShapeCollisions();
    }

    // Bullet vs Shape collisions
    public static void checkBulletShapeCollisions() {
        for (Bullet b : bullets) {
            spatialGrid.getPotentialCollisions(b.getCenterX(), b.getCenterY(), b.getWidth() / 2f, MAX_SHAPE_RADIUS, potentialShapes);

            for (Shape s : potentialShapes) {
                if (s.isAlive() && Collision.circlePolygonCollision(b.getCenterX(), b.getCenterY(), b.getWidth() / 2f, s.polygon)) {
                    s.setDamage(true);
                    b.setDamage(true);
                    resolveCollision(b, s, b.getBodyDamage(), s.getBodyDamage());
                    applyKnockback(b, s, 10, 10);

                    if (!s.isAlive()) {
                        playerTank.addScore(s.getXp());
                        playerTank.updateNumShapesKilled();
                    }
                }
            }
        }
    }

    // Tank vs Shape collisions
    public static void checkTankShapeCollisions() {
        spatialGrid.getPotentialCollisions(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getWidth() / 2f, MAX_SHAPE_RADIUS, potentialShapes);

        for (Shape s : potentialShapes) {
            if (s.isAlive() && Collision.circlePolygonCollision(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getWidth() / 2f, s.polygon)) {
                s.setDamage(true);
                playerTank.setDamage(true);

                resolveCollision(playerTank, s, playerTank.getBodyDamage(), s.getBodyDamage());
                applyKnockback(s, playerTank, 50, 50);

                if (!s.isAlive()) {
                    playerTank.addScore(s.getXp());
                    playerTank.updateNumShapesKilled();
                }
            }
        }
    }

    // Shape vs Shape collisions
    public static void checkShapeShapeCollisions() {
        for (Shape a : shapes) {
            spatialGrid.getPotentialCollisions(a.getCenterX(), a.getCenterY(), a.getWidth() / 2f, MAX_SHAPE_RADIUS, potentialShapes);

            for (Shape b : potentialShapes) {
                if (a == b || !b.isAlive() || !shapes.contains(b)) continue;

                if (Collision.polygonPolygonCollision(a.polygon, b.polygon)) {
                    applyKnockback(a, b, 8, 8);
                }
            }
        }
    }

    // Apply proportional damage to two colliding entities
    public static void resolveCollision(Entity a, Entity b, float damageA, float damageB) {
        float ratioA = Math.min(1.0f, damageA * dt > b.getHealth() ? b.getHealth() / (damageA * dt) : 1.0f);
        float ratioB = Math.min(1.0f, damageB * dt > a.getHealth() ? a.getHealth() / (damageB * dt) : 1.0f);
        float ratio = Math.min(ratioA, ratioB);

        a.takeDamage(damageB * ratio);
        b.takeDamage(damageA * ratio);
    }

    // Apply knockback to two entities
    public static void applyKnockback(Entity a, Entity b, float knockbackStrengthA, float knockbackStrengthB) {
        float dx = b.getCenterX() - a.getCenterX();
        float dy = b.getCenterY() - a.getCenterY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist != 0) {
            dx /= dist;
            dy /= dist;

            a.addVelocity(-dx * knockbackStrengthA, -dy * knockbackStrengthA);
            b.addVelocity(dx * knockbackStrengthB, dy * knockbackStrengthB);
        }
    }

    // Update all entities
    public static void updateEntities() {
        spatialGrid.clear();

        for (Shape s : shapes) {
            s.update();
            spatialGrid.addShape(s);
        }

        for (Bullet b : bullets) b.update();

        if (!deathScreen) playerTank.update();

        if (!playerTank.isAlive()) deathScreen = true;

        removeEntities();
    }

    // Remove dead bullets and shapes
    public static void removeEntities() {
        bullets.removeIf(b -> !b.isAlive() && b.getTimeSinceDeath() > 0.08);
        shapes.removeIf(s -> !s.isAlive() && s.getTimeSinceDeath() > 0.08);
    }

    // Reset game state
    public static void resetGame() {
        shapes.clear();
        bullets.clear();

        if (deathScreen) {
            respawnPlayer();
            deathScreen = false;
        }
    }
}