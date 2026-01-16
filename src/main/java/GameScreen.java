import java.util.ArrayList;
import java.util.List;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class GameScreen extends GameState {

    // Textures
    public static Texture settings;

    // Set the requested screen type as GAME
    ScreenType requestedScreen = ScreenType.GAME;

    public GameScreen() {
        EntityManager.tank = LoadTexture("resources/game/tank.png");
        EntityManager.barrel = LoadTexture("resources/game/barrel.png");
        EntityManager.bullet = LoadTexture("resources/game/bullet.png");
        settings = LoadTexture("resources/game/settings.png");

        float randX = EntityManager.worldW * (float) Math.random();
        float randY = EntityManager.worldH * (float) Math.random();
        EntityManager.playerTank = new Basic(randX, randY, EntityManager.angle, EntityManager.tank, EntityManager.barrel);

        Graphics.camera = new Camera2D();
        Graphics.camera.target(new Vector2().x(EntityManager.playerTank.getCenterX()).y(EntityManager.playerTank.getCenterY()));
        Graphics.camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        Graphics.camera.zoom(Graphics.zoomLevel);

        EntityManager.spawnShapes();

        Graphics.updateGameLayout();
    }

    // Update loop
    @Override
    public void update() {

        // Time since last frame
        EntityManager.dt = GetFrameTime();

        // Mouse coordinates
        Vector2 mouseScreen = GetMousePosition();

        // Update the camera
        Graphics.updateCamera(EntityManager.playerTank);

        // Pause gameplay while settings are open
        if (!Graphics.showSettings) {

            // Handle input
            handleInput(mouseScreen);

            // Upgrade menu animation
            upgradeMenuAnimate(mouseScreen);

            // Spawn shapes if needed
            EntityManager.spawnShapes();

            // Update all entities
            EntityManager.updateEntities();

            // Handle all collisions
            EntityManager.checkCollisions();
        }

        // Settings buttons hover and click
        if (isHover(Graphics.settingsRect, mouseScreen)) {
            Graphics.settingsHover = true;
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                Graphics.showSettings = true;
            }
        } else {
            Graphics.settingsHover = false;
        }

        // Close settings with space
        if (IsKeyPressed(KEY_SPACE)) {
            Graphics.showSettings = false;
        }
    }

    public static void upgradeMenuAnimate(Vector2 mouseScreen) {
        if (!EntityManager.deathScreen) {
            if (Graphics.upgradeMenuTimer > 0) Graphics.upgradeMenuTimer -= EntityManager.dt;

            // Hover area expands as the menu pops out
            float hoverWidth = 40 + (Graphics.upgradeMenuWidth) * Graphics.upgradeMenuAnim;
            Rectangle hoverArea = newRectangle(0, Graphics.startY - 50, hoverWidth, Graphics.menuH + 100);

            if (CheckCollisionPointRec(mouseScreen, hoverArea) || Graphics.upgradeMenuTimer > 0) {
                Graphics.upgradeMenuAnim += 6f * EntityManager.dt;
            } else {
                Graphics.upgradeMenuAnim -= 6f * EntityManager.dt;
            }
        } else {
            Graphics.upgradeMenuAnim -= 6f * EntityManager.dt;
        }
        if (Graphics.upgradeMenuAnim > 1f) Graphics.upgradeMenuAnim = 1f;
        if (Graphics.upgradeMenuAnim < 0f) Graphics.upgradeMenuAnim = 0f;
    }


    // Input handling
    private void handleInput(Vector2 mouseScreen) {
        // Mouse position in world space
        Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), Graphics.camera);

        // Respawn logic
        if (IsKeyPressed(KEY_R) && EntityManager.deathScreen) {
            EntityManager.respawnPlayer();
        }

        // If not in death-screen
        if (EntityManager.deathScreen) return;

        // Upgrade handling
        // Animated X position for interaction
        float hiddenX = -Graphics.upgradeMenuWidth - 5;
        float targetX = Graphics.padding;
        float x = hiddenX + (targetX - hiddenX) * Graphics.upgradeMenuAnim;

        // Hotkeys 1-8 for upgrading stats
        if (IsKeyPressed(KEY_ONE)) { EntityManager.playerTank.upgradeStat(0); Graphics.upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_TWO)) { EntityManager.playerTank.upgradeStat(1); Graphics.upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_THREE)) { EntityManager.playerTank.upgradeStat(2); Graphics.upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_FOUR)) { EntityManager.playerTank.upgradeStat(3); Graphics.upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_FIVE)) { EntityManager.playerTank.upgradeStat(4); Graphics.upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_SIX)) { EntityManager.playerTank.upgradeStat(5); Graphics.upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_SEVEN)) { EntityManager.playerTank.upgradeStat(6); Graphics.upgradeMenuTimer = 2.0f; }
        if (IsKeyPressed(KEY_EIGHT)) { EntityManager.playerTank.upgradeStat(7); Graphics.upgradeMenuTimer = 2.0f; }

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            for (int i = 0; i < Graphics.statNames.length; i++) {
                float y = Graphics.startY + i * (Graphics.upgradeItemHeight + 5);
                Rectangle rect = newRectangle(x, y, Graphics.upgradeMenuWidth, Graphics.upgradeItemHeight);
                if (CheckCollisionPointRec(mouseScreen, rect)) {
                    EntityManager.playerTank.upgradeStat(i);
                    return; // Don't fire
                }
            }
        }

        // Manual firing
        if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && EntityManager.playerTank.canFire() && !isHover(Graphics.settingsRect, mouseScreen)) {
            EntityManager.fireBullet();
        }

        // Toggle features
        if (IsKeyPressed(KEY_Q)) EntityManager.addShape();            // Add shape
        if (IsKeyPressed(KEY_B)) EntityManager.hitbox = !EntityManager.hitbox;      // Toggle hitbox
        if (IsKeyPressed(KEY_E)) EntityManager.autoFire = !EntityManager.autoFire;  // Autofire
        if (IsKeyPressed(KEY_C)) EntityManager.autoSpin = !EntityManager.autoSpin;  // Auto spin
        if (IsKeyPressed(KEY_K)) EntityManager.deathScreen = true;    // Force death

        // Autofire logic
        if (EntityManager.autoFire && EntityManager.playerTank.canFire()) {
            EntityManager.fireBullet();
            EntityManager.playerTank.applyRecoil();
            EntityManager.playerTank.resetReload();
        }

        // Auto spin logic
        if (EntityManager.autoSpin) {
            EntityManager.angle += (float) Math.PI / 2 * EntityManager.dt;
        } else {
            // Otherwise, set the angle as the mouse direction
            EntityManager.angle = (float) Math.atan2(mouse.y() - EntityManager.playerTank.getCenterY(), mouse.x() - EntityManager.playerTank.getCenterX());
        }

        // Set player tank angle
        EntityManager.playerTank.setAngle(EntityManager.angle);

    }

    // Drawing
    @Override
    public void draw() {
        // Clear screen with border grid color
        ClearBackground(Graphics.borderGridColour);

        // Begin camera-based rendering
        BeginMode2D(Graphics.camera);

        // Calculate camera bounds
        Graphics.camLeft = Graphics.camera.target().x() - Graphics.camera.offset().x() / Graphics.camera.zoom();
        Graphics.camRight = Graphics.camLeft + GetScreenWidth() / Graphics.camera.zoom();
        Graphics.camTop = Graphics.camera.target().y() - Graphics.camera.offset().y() / Graphics.camera.zoom();
        Graphics.camBottom = Graphics.camTop + GetScreenHeight() / Graphics.camera.zoom();

        // Draw world grid
        Graphics.drawWorld();

        // Draw all world entities (shapes, bullets, tank)
        Graphics.drawEntities();

        // End camera-based rendering
        EndMode2D();

        // Draw UI elements that are not affected by the camera
        Graphics.drawSettings();
        Graphics.drawUpgradeMenu();
        if (Graphics.showSettings) Graphics.drawSettingsMenu();
        Graphics.drawLevelBar();

        // Draw death overlay if the player is dead
        if (EntityManager.deathScreen) {
            Graphics.drawDeathScreen();
        }
    }

    // Unload resources
    @Override
    public void unload() {
        UnloadTexture(EntityManager.tank);
        UnloadTexture(EntityManager.barrel);
        UnloadTexture(EntityManager.bullet);
        UnloadTexture(settings);
    }

    // Screen switching
    @Override
    public ScreenType getRequestedScreen() {
        return requestedScreen;
    }
}
