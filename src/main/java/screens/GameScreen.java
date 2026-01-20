package screens;

import core.*;
import entities.Tank;
import tanks.tier1.*;
import tanks.tier2.*;
import tanks.tier3.*;
import tanks.tier4.*;

import static com.raylib.Colors.WHITE;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

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
        EntityManager.playerTank = new Streamliner(randX, randY, EntityManager.angle, EntityManager.tank, EntityManager.barrel);
        EntityManager.playerTankType = TankType.BASIC;
        EntityManager.requestedTank = TankType.BASIC;

        Graphics.camera = new Camera2D();
        Graphics.camera.target(new Vector2().x(EntityManager.playerTank.getCenterX()).y(EntityManager.playerTank.getCenterY()));
        Graphics.camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        Graphics.zoomLevel = Graphics.defaultZoom;
        Graphics.camera.zoom(Graphics.zoomLevel);

        EntityManager.spawnShapes();

        Graphics.updateGameLayout();
    }

    public static void upgradeMenuAnimate(Vector2 mouseScreen) {
        if (!EntityManager.deathScreen) {
            if (Graphics.statsMenuTimer > 0) Graphics.statsMenuTimer -= EntityManager.dt;

            // The hover area expands as the menu pops out
            float hoverWidth = 50 + (Graphics.statsMenuW) * Graphics.statsMenuAnim;
            Rectangle hoverArea = newRectangle(0, Graphics.startY - 50, hoverWidth, Graphics.statsMenuH + 50);
            if (CheckCollisionPointRec(mouseScreen, hoverArea) || Graphics.statsMenuTimer > 0 || EntityManager.playerTank.isUpgradeSkill()) {
                Graphics.statsMenuAnim += 8f * EntityManager.dt;
            } else {
                Graphics.statsMenuAnim -= 8f * EntityManager.dt;
            }
        } else {
            Graphics.statsMenuAnim -= 8f * EntityManager.dt;
        }
        if (Graphics.statsMenuAnim > 1f) Graphics.statsMenuAnim = 1f;
        if (Graphics.statsMenuAnim < 0f) Graphics.statsMenuAnim = 0f;
    }

    // Update loop
    @Override
    public void update() {
        TankType requestedTank = EntityManager.requestedTank;
        if (EntityManager.playerTankType != requestedTank) {
            Tank newTank = new Tank(0, 0, 0, EntityManager.tank);
            if (requestedTank == TankType.TWIN) {
                newTank = new Twin(EntityManager.playerTank.getCenterX(), EntityManager.playerTank.getCenterY(), EntityManager.angle, EntityManager.tank, EntityManager.barrel);
                newTank.copyStats(EntityManager.playerTank);  // copy all previous stats
                EntityManager.playerTankType = TankType.TWIN;
            } else if (requestedTank == TankType.SNIPER) {
                newTank = new Sniper(EntityManager.playerTank.getCenterX(), EntityManager.playerTank.getCenterY(), EntityManager.angle, EntityManager.tank, EntityManager.barrel);
                newTank.copyStats(EntityManager.playerTank);  // copy all previous stats
                EntityManager.playerTankType = TankType.SNIPER;
            } else if (requestedTank == TankType.MACHINEGUN) {
//                 newTank = new Twin(EntityManager.playerTank.getCenterX(), EntityManager.playerTank.getCenterY(), EntityManager.angle, EntityManager.tank, EntityManager.barrel);
//                newTank.copyStats(EntityManager.playerTank);  // copy all previous stats
//                EntityManager.playerTankType = TankType.MACHINEGUN;
            } else if (requestedTank == TankType.FLANKGUARD) {
                newTank = new FlankGuard(EntityManager.playerTank.getCenterX(), EntityManager.playerTank.getCenterY(), EntityManager.angle, EntityManager.tank, EntityManager.barrel);
                newTank.copyStats(EntityManager.playerTank);  // copy all previous stats
                EntityManager.playerTankType = TankType.FLANKGUARD;
            }
            EntityManager.playerTank = newTank;
            EntityManager.playerTank.upgradeTank = false;
        }

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
        if (CheckCollisionPointRec(mouseScreen, Graphics.settingsRect)) {
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

    // Input handling
    private void handleInput(Vector2 mouseScreen) {
        // Mouse position in world space
        Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), Graphics.camera);

        if (EntityManager.deathScreen) {
            // Respawn
            if (IsKeyPressed(KEY_R)) {
                EntityManager.respawnPlayer();
            } else if (IsKeyPressed(KEY_SPACE)) { // Go back to the menu screen
                requestedScreen = ScreenType.MENU;
            }
            return;
        }

        // Upgrade handling
        // Animated X position for interaction
        float hiddenX = -Graphics.statsMenuW - 5;
        float targetX = Graphics.padding;
        float x = hiddenX + (targetX - hiddenX) * Graphics.statsMenuAnim;

        // Hotkeys 1-8 for upgrading stats
        if (IsKeyPressed(KEY_ONE)) {
            EntityManager.playerTank.upgradeStat(0);
            Graphics.statsMenuTimer = 2.0f;
        }
        if (IsKeyPressed(KEY_TWO)) {
            EntityManager.playerTank.upgradeStat(1);
            Graphics.statsMenuTimer = 2.0f;
        }
        if (IsKeyPressed(KEY_THREE)) {
            EntityManager.playerTank.upgradeStat(2);
            Graphics.statsMenuTimer = 2.0f;
        }
        if (IsKeyPressed(KEY_FOUR)) {
            EntityManager.playerTank.upgradeStat(3);
            Graphics.statsMenuTimer = 2.0f;
        }
        if (IsKeyPressed(KEY_FIVE)) {
            EntityManager.playerTank.upgradeStat(4);
            Graphics.statsMenuTimer = 2.0f;
        }
        if (IsKeyPressed(KEY_SIX)) {
            EntityManager.playerTank.upgradeStat(5);
            Graphics.statsMenuTimer = 2.0f;
        }
        if (IsKeyPressed(KEY_SEVEN)) {
            EntityManager.playerTank.upgradeStat(6);
            Graphics.statsMenuTimer = 2.0f;
        }
        if (IsKeyPressed(KEY_EIGHT)) {
            EntityManager.playerTank.upgradeStat(7);
            Graphics.statsMenuTimer = 2.0f;
        }

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            for (int i = 0; i < Graphics.statNames.length; i++) {
                float y = Graphics.startY + i * (Graphics.statsItemH + 5);
                Rectangle rect = newRectangle(x, y, Graphics.statsMenuW, Graphics.statsItemH);
                if (CheckCollisionPointRec(mouseScreen, rect)) {
                    EntityManager.playerTank.upgradeStat(i);
                    return; // Don't fire
                }
            }
        }

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && EntityManager.playerTank.upgradeTank) {
            checkUpgrade(mouseScreen);
            return;
        }


        // Manual firing
        if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && EntityManager.playerTank.canFire() && !CheckCollisionPointRec(mouseScreen, Graphics.settingsRect)) {
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

    public void checkUpgrade(Vector2 mouseScreen) {
        if (CheckCollisionPointRec(mouseScreen, Graphics.twinRect)) {
            EntityManager.requestedTank = TankType.TWIN;
        } else if (CheckCollisionPointRec(mouseScreen, Graphics.sniperRect)) {
            EntityManager.requestedTank = TankType.SNIPER;
        } else if (CheckCollisionPointRec(mouseScreen, Graphics.machineGunRect)) {
            EntityManager.requestedTank = TankType.MACHINEGUN;
        } else if (CheckCollisionPointRec(mouseScreen, Graphics.flankGuardRect)) {
            EntityManager.requestedTank = TankType.FLANKGUARD;
        }
    }


    // Drawing
    @Override
    public void draw() {
        // Clear screen
        ClearBackground(WHITE);

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
        Graphics.drawStatsMenu();

        if (EntityManager.playerTank.upgradeTank) {
            Graphics.drawUpgradeMenu();
        }

        if (!EntityManager.deathScreen) {
            Graphics.drawLevelBar();
            Graphics.drawMiniMap();
            if (Graphics.showSettings) Graphics.drawSettingsMenu();
        } else {
            // Draw death overlay if the player is dead
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
