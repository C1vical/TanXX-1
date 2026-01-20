package screens;

import core.*;
import entities.Tank;

import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Colors.WHITE;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

public class GameScreen extends GameState {

    // Textures
    public static Texture settings; // Settings icon texture (not used here yet)

    // Constructor: initializes game screen
    public GameScreen() {
        // Set the requested screen type as GAME
        requestedScreen = ScreenType.GAME;

        // Load textures
        EntityManager.tank = LoadTexture("resources/game/tank.png");
        EntityManager.barrel = LoadTexture("resources/game/barrel.png");
        EntityManager.bullet = LoadTexture("resources/game/bullet.png");

        // Spawn player tank at a random position
        float randX = EntityManager.worldW * (float) Math.random();
        float randY = EntityManager.worldH * (float) Math.random();
        EntityManager.playerTank = TankFactory.create(TankType.BASIC, randX, randY, 0f);
        EntityManager.playerTankType = TankType.BASIC;  // Current tank type
        EntityManager.requestedTank = TankType.BASIC;   // Initial requested tank type

        // Initialize camera to follow the player
        Graphics.camera = new Camera2D();
        Graphics.camera.target(new Vector2().x(EntityManager.playerTank.getCenterX()).y(EntityManager.playerTank.getCenterY()));
        Graphics.camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        Graphics.zoomLevel = Graphics.defaultZoom;
        Graphics.camera.zoom(Graphics.zoomLevel);

        // Spawn initial world shapes
        EntityManager.spawnShapes();

        // Set up the UI/game layout
        Graphics.updateGameLayout();
    }

    // Upgrade menu animation logic
    public static void upgradeMenuAnimate(Vector2 mouseScreen) {
        if (!EntityManager.deathScreen) {
            if (Graphics.statsMenuTimer > 0) Graphics.statsMenuTimer -= EntityManager.dt;

            // Expand hover area as menu animates
            float hoverWidth = 50 + (Graphics.statsMenuW) * Graphics.statsMenuAnim;
            Rectangle hoverArea = newRectangle(0, Graphics.startY - 50, hoverWidth, Graphics.statsMenuH + 50);

            // Animate menu in/out based on mouse hover or timer
            if (CheckCollisionPointRec(mouseScreen, hoverArea) || Graphics.statsMenuTimer > 0 || EntityManager.playerTank.isUpgradeSkill()) {
                Graphics.statsMenuAnim += 8f * EntityManager.dt;
            } else {
                Graphics.statsMenuAnim -= 8f * EntityManager.dt;
            }
        } else {
            // Shrink menu if dead
            Graphics.statsMenuAnim -= 8f * EntityManager.dt;
        }

        // Clamp animation progress between 0 and 1
        if (Graphics.statsMenuAnim > 1f) Graphics.statsMenuAnim = 1f;
        if (Graphics.statsMenuAnim < 0f) Graphics.statsMenuAnim = 0f;
    }

    // Main game update loop
    @Override
    public void update() {
        EntityManager.dt = GetFrameTime(); // Delta time for smooth movement

        // Get current mouse position
        Vector2 mouseScreen = GetMousePosition();

        // Pause toggle
        if (IsKeyPressed(KEY_P)) {
            Graphics.pauseGame = !Graphics.pauseGame;
        }
        if (Graphics.pauseGame) return; // Skip updates while paused

        // Camera follows player
        Graphics.updateCamera(EntityManager.playerTank);

        // Handle player input
        handleInput(mouseScreen);

        // Animate upgrade menu
        upgradeMenuAnimate(mouseScreen);

        // Spawn shapes if needed
        EntityManager.spawnShapes();

        // Update all game entities (tanks, bullets, shapes)
        EntityManager.updateEntities();

        // Check for collisions between entities
        EntityManager.checkCollisions();
    }

    // Handle player input
    private void handleInput(Vector2 mouseScreen) {
        // Convert mouse position to world coordinates
        Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), Graphics.camera);

        // Death screen logic
        if (EntityManager.deathScreen) {
            if (IsKeyPressed(KEY_R)) {
                EntityManager.respawnPlayer(); // Respawn player
            } else if (IsKeyPressed(KEY_SPACE)) {
                requestedScreen = ScreenType.MENU; // Return to menu
            }
            return;
        }

        // Upgrade menu x position animation
        float hiddenX = -Graphics.statsMenuW - 5;
        float targetX = Graphics.padding;
        float x = hiddenX + (targetX - hiddenX) * Graphics.statsMenuAnim;

        // Upgrade hotkeys (1-8)
        if (IsKeyPressed(KEY_ONE)) { EntityManager.playerTank.upgradeStat(0); Graphics.statsMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_TWO)) { EntityManager.playerTank.upgradeStat(1); Graphics.statsMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_THREE)) { EntityManager.playerTank.upgradeStat(2); Graphics.statsMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_FOUR)) { EntityManager.playerTank.upgradeStat(3); Graphics.statsMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_FIVE)) { EntityManager.playerTank.upgradeStat(4); Graphics.statsMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_SIX)) { EntityManager.playerTank.upgradeStat(5); Graphics.statsMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_SEVEN)) { EntityManager.playerTank.upgradeStat(6); Graphics.statsMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_EIGHT)) { EntityManager.playerTank.upgradeStat(7); Graphics.statsMenuTimer = 2.0f; }

        // Upgrade menu mouse clicks
        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            for (int i = 0; i < Graphics.statNames.length; i++) {
                float y = Graphics.startY + i * (Graphics.statsItemH + 5);
                Rectangle rect = newRectangle(x, y, Graphics.statsMenuW, Graphics.statsItemH);
                if (CheckCollisionPointRec(mouseScreen, rect)) {
                    EntityManager.playerTank.upgradeStat(i);
                    return; // Prevent firing when upgrading
                }
            }
        }

        // Tank upgrade click logic
        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && EntityManager.playerTank.upgradeTank) {
            handleUpgradeClick(GetMousePosition());
            return;
        }

        // Manual firing
        if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && EntityManager.playerTank.canFire()) {
            EntityManager.fireBullet();
        }

        // Toggle debug features
//        if (IsKeyPressed(KEY_Q)) EntityManager.addShape(); // Add shape
        if (IsKeyPressed(KEY_B)) EntityManager.hitbox = !EntityManager.hitbox; // Toggle hitboxes
        if (IsKeyPressed(KEY_E)) EntityManager.autoFire = !EntityManager.autoFire; // Toggle auto-fire
        if (IsKeyPressed(KEY_C)) EntityManager.autoSpin = !EntityManager.autoSpin; // Toggle auto-spin

        // Autofire
        if (EntityManager.autoFire && EntityManager.playerTank.canFire()) {
            EntityManager.fireBullet();
        }

        // Auto-spin tank rotation
        if (EntityManager.autoSpin) {
            EntityManager.angle += (float) Math.PI / 2 * EntityManager.dt; // Rotate automatically
        } else {
            // Rotate tank to face mouse
            EntityManager.angle = (float) Math.atan2(mouse.y() - EntityManager.playerTank.getCenterY(), mouse.x() - EntityManager.playerTank.getCenterX());
        }

        // Set player tank rotation
        EntityManager.playerTank.setAngle(EntityManager.angle);
    }

    // Handle tank upgrade menu clicks
    private void handleUpgradeClick(Vector2 mouse) {
        for (int i = 0; i < Graphics.upgradeRects.size(); i++) {
            if (CheckCollisionPointRec(mouse, Graphics.upgradeRects.get(i))) {

                TankType next = Graphics.upgradeOptions.get(i);

                // Create new tank of the upgraded type
                Tank newTank = TankFactory.create(next, EntityManager.playerTank.getCenterX(), EntityManager.playerTank.getCenterY(), EntityManager.angle);
                newTank.copyStats(EntityManager.playerTank); // Preserve current stats

                EntityManager.playerTank = newTank; // Replace old tank
                EntityManager.playerTankType = next;

                // Mark upgrade as complete
                EntityManager.playerTank.completeUpgrade();
                return;
            }
        }
    }

    // Draw everything
    @Override
    public void draw() {
        ClearBackground(WHITE); // Clear screen

        // Begin camera-based rendering
        BeginMode2D(Graphics.camera);

        // Update camera bounds
        Graphics.camLeft = Graphics.camera.target().x() - Graphics.camera.offset().x() / Graphics.camera.zoom();
        Graphics.camRight = Graphics.camLeft + GetScreenWidth() / Graphics.camera.zoom();
        Graphics.camTop = Graphics.camera.target().y() - Graphics.camera.offset().y() / Graphics.camera.zoom();
        Graphics.camBottom = Graphics.camTop + GetScreenHeight() / Graphics.camera.zoom();

        // Draw world grid
        Graphics.drawWorld();

        // Draw all entities (tanks, bullets, shapes)
        Graphics.drawEntities();

        EndMode2D(); // End camera rendering

        // Draw death overlay if player is dead
        if (EntityManager.deathScreen) {
            Graphics.drawDeathScreen();
            return;
        }

        // Draw upgrade menu if active
        if (EntityManager.playerTank.upgradeTank) {
            Graphics.drawUpgradeMenu(EntityManager.playerTankType);
        }

        // Draw UI elements not affected by camera
        Graphics.drawStatsMenu();
        Graphics.drawLevelBar();
        Graphics.drawMiniMap();

        // Draw pause overlay
        if (Graphics.pauseGame) {
            DrawRectangle(0, 0, GameState.screenW, GameState.screenH, newColor(0, 0, 0, 180));
            DrawText("GAME PAUSED", screenW / 2 - MeasureText("GAME PAUSED", 40) / 2, screenH / 2 - 20, 40, RAYWHITE);
        }
    }

    // Unload textures
    @Override
    public void unload() {
        UnloadTexture(EntityManager.tank);
        UnloadTexture(EntityManager.barrel);
        UnloadTexture(EntityManager.bullet);
    }

    // Get the screen the player wants to switch to
    @Override
    public ScreenType getRequestedScreen() {
        return requestedScreen;
    }
}