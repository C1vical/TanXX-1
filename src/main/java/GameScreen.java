import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class GameScreen extends GameState {

    // World dimensions
    public static final int worldW = 1000;
    public static final int worldH = 1000;
    public static final int borderSize = 1000;
    public final int tileSize = 20;

    // World colors
    public final Color worldGridColour = newColor(65, 65, 65, 255);
    public final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public final Color borderGridColour = newColor(34, 34, 34, 255);
    public final Color borderGridLineColour = newColor(45, 45, 45, 255);

    // Textures
    public Texture tank, barrel, bullet, settings;

    // Player and entities
    public static Tank playerTank;      // Main player tank
    public static float angle;          // Barrel angle (radians)

    // Lists storing all active entities
    List<Bullet> bullets = new ArrayList<>();
    List<Shape> shapes = new ArrayList<>();

    // Spacial partitioning (for collisions)
    SpatialGrid spatialGrid = new SpatialGrid(worldW, worldH, 100);
    List<Shape> potentialShapes = new ArrayList<>();
    private final float MAX_SHAPE_SIZE = 50f;
    private static final int startShapes = 30;

    // Camera
    public static Camera2D camera = new Camera2D();
    float camLeft, camRight, camTop, camBottom;
    private float zoomLevel = 1.0f;

    // Smooth camera and zoom movement
    final float movementLerp = 0.1f;
    final float zoomLerp = 0.1f;

    // Settings UI
    public static int settingsSize = 75;
    Rectangle settingsRect;
    boolean settingsHover = false;
    public static boolean showSettings = false;

    // Game state flags
    public static float dt;     // Delta time (seconds per frame)
    public static boolean deathScreen = false;
    public static boolean hitbox = false;
    public static boolean autoFire = false;
    public static boolean autoSpin = false;

    // Set the requested screen type as GAME
    ScreenType requestedScreen = ScreenType.GAME;

    // Level bar
    float levelBarW = 350;
    float levelBarH = 25;
    float padding = 20;
    float levelBarX = screenW / 2f - levelBarW / 2;
    float levelBarY = screenH  - padding - levelBarH;
    int levelTextFont = 20;

    // Score bar
    float scoreBarW = 250;
    float scoreBarH = 20;
    float margin = 10;
    float scoreBarX = screenW / 2f - scoreBarW / 2;
    float scoreBarY = levelBarY - margin - scoreBarH;
    int scoreTextFont = 15;

    // Name
    String nameText = "Player 1";
    int nameTextFont = 25;
    float nameTextX = screenW / 2f - (float) MeasureText(nameText, nameTextFont) / 2;
    float nameTextY = scoreBarY - margin - nameTextFont;

    // Upgrade Menu
    final String[] statNames = {
            "Health Regen", "Max Health", "Body Damage", "Bullet Speed",
            "Bullet Penetration", "Bullet Damage", "Reload Speed", "Movement Speed"
    };
    final Color[] statColors = {
            newColor(255, 128, 255, 255), // Pink
            newColor(128, 255, 128, 255), // Green
            newColor(128, 255, 255, 255), // Cyan
            newColor(255, 255, 128, 255), // Yellow
            newColor(128, 128, 255, 255), // Blue
            newColor(255, 128, 128, 255), // Red
            newColor(255, 179, 128, 255), // Orange
            newColor(230, 230, 230, 255)  // Gray
    };
    final float upgradeMenuWidth = 200;
    final float upgradeItemHeight = 25;
    private final float menuH = statNames.length * (upgradeItemHeight + 5);
    private final float startY = screenH - padding - menuH - 50;
    final float upgradeMenuPadding = 10;
    float upgradeMenuAnim = 0f; // 0 = collapsed, 1 = expanded
    private float upgradeMenuTimer = 0f; // Time to keep the menu open after keypress


    public GameScreen() {
        tank = LoadTexture("resources/game/tank.png");
        barrel = LoadTexture("resources/game/barrel.png");
        bullet = LoadTexture("resources/game/bullet.png");
        settings = LoadTexture("resources/game/settings.png");

        float randX = worldW * (float) Math.random();
        float randY = worldH * (float) Math.random();
        playerTank = new Basic(randX, randY, angle, tank, barrel);

        camera = new Camera2D();
        camera.target(new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY()));
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        camera.zoom(zoomLevel);

        spawnShapes(startShapes);
        updateLayout();
    }

    // Update loop
    @Override
    public void update() {

        // Time since last frame
        dt = GetFrameTime();

        // Mouse coordinates
        Vector2 mouseScreen = GetMousePosition();

        // Update the camera
        updateCamera();

        // Pause gameplay while settings are open
        if (!showSettings) {

            // Handle input
            handleInput(mouseScreen);

            // Upgrade menu animation
            if (!deathScreen) {
                if (upgradeMenuTimer > 0) upgradeMenuTimer -= dt;

                // Hover area expands as the menu pops out
                float hoverWidth = 40 + (upgradeMenuWidth) * upgradeMenuAnim;
                Rectangle hoverArea = newRectangle(0, startY - 50, hoverWidth, menuH + 100);
                DrawRectangleLinesEx(hoverArea, 2, GRAY);
                
                if (CheckCollisionPointRec(mouseScreen, hoverArea) || upgradeMenuTimer > 0) {
                    upgradeMenuAnim += 6f * dt;
                } else {
                    upgradeMenuAnim -= 6f * dt;
                }
            } else {
                upgradeMenuAnim -= 6f * dt;
            }
            if (upgradeMenuAnim > 1f) upgradeMenuAnim = 1f;
            if (upgradeMenuAnim < 0f) upgradeMenuAnim = 0f;

            // Spawn shapes if needed
            if (shapes.size() < startShapes) {
                spawnShapes(startShapes - shapes.size());
            }

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

            // Handle all collisions
            checkCollisions();
        }

        // Settings buttons hover and click
        if (isHover(settingsRect, mouseScreen)) {
            settingsHover = true;
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                showSettings = true;
            }
        } else {
            settingsHover = false;
        }

        // Close settings with space
        if (IsKeyPressed(KEY_SPACE)) {
            showSettings = false;
        }
    }

    // Camera logic
    private void updateCamera() {
        // Center camera
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));

        // Smoothly follow player (using lerp)
        Vector2 desiredTarget = new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY());
        camera.target().x(camera.target().x() + (desiredTarget.x() - camera.target().x()) * movementLerp);
        camera.target().y(camera.target().y() + (desiredTarget.y() - camera.target().y()) * movementLerp);

//        // Zoom only when settings are closed
//        if (!showSettings) {
//            getZoomLevel();
//            float desiredZoom = zoomLevel;
//            camera.zoom(camera.zoom() + (desiredZoom - camera.zoom()) * zoomLerp);
//        }
    }

    // Mouse-wheel zoom control
    private void getZoomLevel() {
        float scroll = GetMouseWheelMove();
        if (scroll > 0) zoomLevel += 0.1f;
        else if (scroll < 0) zoomLevel -= 0.1f;

        if (zoomLevel < 0.8f) zoomLevel = 0.8f;
        if (zoomLevel > 20.0f) zoomLevel = 20.0f;
    }

    // Input handling
    private void handleInput(Vector2 mouseScreen) {
        // Mouse position in world space
        Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), camera);

        // Respawn logic
        if (IsKeyPressed(KEY_R) && deathScreen) {
            deathScreen = false;
            float randX = worldW * (float) Math.random();
            float randY = worldH * (float) Math.random();
            int newLevel = Math.max(playerTank.getLevel() / 2, 1);
            int newScore = playerTank.getTotalScore(newLevel);

            playerTank = new Basic(randX, randY, angle, tank, barrel);
            playerTank.addScore(newScore);

            autoSpin = false;
            autoFire = false;
        }

        // If not in death-screen
        if (deathScreen) return;

        // Upgrade handling
        float menuH = statNames.length * (upgradeItemHeight + 5);
        float startY = screenH - padding - menuH - 50;

        // Animated X position for interaction
        float hiddenX = -upgradeMenuWidth - 5;
        float targetX = padding;
        float x = hiddenX + (targetX - hiddenX) * upgradeMenuAnim;

        // Hotkeys 1-8 for upgrading stats
        if (IsKeyPressed(KEY_ONE)) { playerTank.upgradeStat(0); upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_TWO)) { playerTank.upgradeStat(1); upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_THREE)) { playerTank.upgradeStat(2); upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_FOUR)) { playerTank.upgradeStat(3); upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_FIVE)) { playerTank.upgradeStat(4); upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_SIX)) { playerTank.upgradeStat(5); upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_SEVEN)) { playerTank.upgradeStat(6); upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_EIGHT)) { playerTank.upgradeStat(7); upgradeMenuTimer = 2.0f; }

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            for (int i = 0; i < statNames.length; i++) {
                float y = startY + i * (upgradeItemHeight + 5);
                Rectangle rect = newRectangle(x, y, upgradeMenuWidth, upgradeItemHeight);
                if (CheckCollisionPointRec(mouseScreen, rect)) {
                    playerTank.upgradeStat(i);
                    return; // Don't fire
                }
            }
        }

        // Manual firing
        if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && playerTank.canFire() && !isHover(settingsRect, mouseScreen)) {
            fireBullet();
            playerTank.applyRecoil();
            playerTank.resetReload();
        }

        // Toggle features
        if (IsKeyPressed(KEY_Q)) addShape();            // Add shape
        if (IsKeyPressed(KEY_B)) hitbox = !hitbox;      // Toggle hitbox
        if (IsKeyPressed(KEY_E)) autoFire = !autoFire;  // Autofire
        if (IsKeyPressed(KEY_C)) autoSpin = !autoSpin;  // Auto spin
        if (IsKeyPressed(KEY_K)) deathScreen = true;    // Force death

        // Autofire logic
        if (autoFire && playerTank.canFire()) {
            fireBullet();
            playerTank.applyRecoil();
            playerTank.resetReload();
        }

        // Auto spin logic
        if (autoSpin) {
            angle += (float) Math.PI / 2 * dt;
        } else {
            // Otherwise, set the angle as the mouse direction
            angle = (float) Math.atan2(mouse.y() - playerTank.getCenterY(), mouse.x() - playerTank.getCenterX());
        }

        // Set player tank angle
        playerTank.setAngle(angle);

    }

    // Drawing
    @Override
    public void draw() {
        // Clear screen with border grid color
        ClearBackground(borderGridColour);

        // Begin camera-based rendering
        BeginMode2D(camera);

        // Calculate camera bounds
        camLeft = camera.target().x() - camera.offset().x() / camera.zoom();
        camRight = camLeft + GetScreenWidth() / camera.zoom();
        camTop = camera.target().y() - camera.offset().y() / camera.zoom();
        camBottom = camTop + GetScreenHeight() / camera.zoom();

        // Draw world grid
        Graphics.drawWorld(this);

        // Draw all world entities (shapes, bullets, tank)
        Graphics.drawEntities(this);

        // End camera-based rendering
        EndMode2D();

        // Draw UI elements that are not affected by the camera
        Graphics.drawLevelBar(this);
        Graphics.drawUI(this);

        // Draw death overlay if the player is dead
        if (deathScreen) {
            Graphics.drawDeathScreen();
        }
    }

    // Unload resources
    @Override
    public void unload() {
        UnloadTexture(tank);
        UnloadTexture(barrel);
        UnloadTexture(bullet);
    }

    // Screen switching
    @Override
    public ScreenType getRequestedScreen() {
        return requestedScreen;
    }

    // Layout scaling
    private void updateLayout() {
        screenW = GetScreenWidth();
        screenH = GetScreenHeight();

        float ratioW = screenW / (float) DEFAULT_SCREEN_W;
        float ratioH = screenH / (float) DEFAULT_SCREEN_H;

        float settingsW = settingsSize * ratioW;
        float settingsH = settingsSize * ratioH;
        settingsRect = newRectangle(screenW - settingsW - 15 * ratioW, 15 * ratioH, settingsW, settingsH);

        levelBarX = screenW / 2f - levelBarW / 2;
        levelBarY = screenH  - padding - levelBarH;

        scoreBarX = screenW / 2f - scoreBarW / 2;
        scoreBarY = levelBarY - margin - scoreBarH;

        nameTextX = screenW / 2f - (float) MeasureText(nameText, nameTextFont) / 2;
        nameTextY = scoreBarY - margin - nameTextFont;
    }

    // Spawn shapes
    private void spawnShapes(int num) {
        for (int i = 0; i < num; i++) {
            addShape();
        }
    }

    // Add shapes
    private void addShape() {
        // Generate a random shape type
        int type = (int) (Math.random() * 3);

        // Generate a random orbit position
        float orbitX = (float) (Math.random() * GameScreen.worldW);
        float orbitY = (float) (Math.random() * GameScreen.worldH);

        switch (type) {
            case 0 -> shapes.add(new Square(orbitX, orbitY, 0));
            case 1 -> shapes.add(new Triangle(orbitX, orbitY, 0));
            default -> shapes.add(new Pentagon(orbitX, orbitY, 0));
        }
    }

    // Fire bullet
    private void fireBullet() {
        float bulletSize = playerTank.getBulletSize();
        float bulletX = playerTank.getCenterX() + (float) Math.cos(angle) * (playerTank.getBarrelW() + bulletSize / 2f);
        float bulletY = playerTank.getCenterY() + (float) Math.sin(angle) * (playerTank.getBarrelW() + bulletSize / 2f);
        bullets.add(new Bullet(bulletX, bulletY, angle, bullet, bulletSize, playerTank.getBulletDamage(), playerTank.getBulletSpeed(), playerTank.getBulletPenetration()));
        playerTank.resetReload();
    }

    // Check all collisions
    private void checkCollisions() {
        checkBulletShapeCollisions();
        checkShapeShapeCollisions();
        if (!deathScreen) checkTankShapeCollisions();
        removeEntities();
    }

    // Check collision between bullets and shapes
    private void checkBulletShapeCollisions() {
        // Loop through every bullet entity
        for (Bullet b : bullets) {
            // Find nearby shapes for collision testing usng the spatial grid
            spatialGrid.getPotentialCollisions(b.getCenterX(), b.getCenterY(), b.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);

            // Then, loop through the list of potential shapes instead of all shapes (much more efficient)
            for (Shape s : potentialShapes) {
                // Only collide with living shapes
                if (s.isAlive() && Collision.circlePolygonCollision(b.getCenterX(), b.getCenterY(), b.getSize() / 2f, s.polygon)) {
                    // Resolve damage exchange between bullet and shape
                    resolveCollision(b, s, b.getBulletDamage(), s.getBodyDamage());

                    // Remove dead shapes and award XP
                    if (!s.isAlive()) {
                        playerTank.addScore(s.getXp());
                    }
                }
            }
        }
    }

    // Check collision between tanks and shapes (same concept as above)
    private void checkTankShapeCollisions() {
        spatialGrid.getPotentialCollisions(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);
        for (Shape s : potentialShapes) {
            if (s.isAlive() && Collision.circlePolygonCollision(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getSize() / 2f, s.polygon)) {
                resolveCollision(playerTank, s, playerTank.getBodyDamage(), s.getBodyDamage());
                if (!s.isAlive()) {
                    playerTank.addScore(s.getXp());
                }
            }
        }
    }

    // Check collision between shapes (again, same concept as above)
    private void checkShapeShapeCollisions() {
        for (Shape a : shapes) {
            spatialGrid.getPotentialCollisions(a.getCenterX(), a.getCenterY(), a.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);
            for (Shape b : potentialShapes) {
                if (a == b) continue;
                if (!b.isAlive()) continue;
                if (!shapes.contains(b)) continue;
                if (Collision.polygonPolygonCollision(a.polygon, b.polygon)) {
                    // Shapes don't deal damage to each other, but they do apply knockback
                    applyKnockback(a, b);
                }
            }
        }
    }

    // Resolves a collision using proportional damage (partial damage if one dies early)
    private void resolveCollision(Entity a, Entity b, float damageA, float damageB) {
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

        // Apply physical knockback after damage resolution
        applyKnockback(a, b);
    }

    // Applies a simple push-back force to separate two colliding entities
    private void applyKnockback(Entity a, Entity b) {
        float dx = b.getCenterX() - a.getCenterX();
        float dy = b.getCenterY() - a.getCenterY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // Avoid division by zero
        if (dist != 0) {
            // Normalize direction vector
            dx /= dist;
            dy /= dist;

            float knockbackStrength = 5f;

            // Push entities away from each other
            a.addVelocity(-dx * knockbackStrength, -dy * knockbackStrength);
            b.addVelocity(dx * knockbackStrength, dy * knockbackStrength);
        }
    }

    private void removeEntities() {
        bullets.removeIf(b -> !b.isAlive());
        shapes.removeIf(s -> !s.isAlive());
    }
}
