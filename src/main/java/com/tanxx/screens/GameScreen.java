package com.tanxx.screens;

import com.tanxx.entities.*;
import com.tanxx.physics.Collision;
import com.tanxx.physics.SpatialGrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class GameScreen extends GameState {

    // World settings
    public static final int worldW = 2000;
    public static final int worldH = 2000;
    public static final int borderSize = 2000;
    public final int tileSize = 20;

    // Rendering colors
    public final Color worldGridColour = newColor(65, 65, 65, 255);
    public final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public final Color borderGridColour = newColor(34, 34, 34, 255);
    public final Color borderGridLineColour = newColor(45, 45, 45, 255);

    // Textures
    public Texture tank, barrel, bullet, settings;
    public Texture square, triangle, pentagon;

    // Entity settings
    public static Tank playerTank;
    public static final int tankSize = 75;
    public static final int barrelW = 75;
    public static final int barrelH = tankSize + 10;
    public static float angle;

    List<Bullet> bullets = new ArrayList<>();
    List<Shape> shapes = new ArrayList<>();
    SpatialGrid spatialGrid = new SpatialGrid(worldW, worldH, 100);
    List<Shape> potentialShapes = new ArrayList<>();
    private final float MAX_SHAPE_SIZE = 30f;
    private static final int startShapes = 30;

    // Camera settings
    public static Camera2D camera = new Camera2D();
    private float camLeft, camRight, camTop, camBottom;
    private float zoomLevel = 2f;
    final float movementLerp = 0.1f;
    final float zoomLerp = 0.1f;

    // Settings UI
    public static int settingsSize = 75;
    private Rectangle settingsRect;
    private boolean settingsHover = false;
    public static boolean showSettings = false;

    // Game state
    public static float dt;
    public static boolean deathScreen = false;
    public static boolean hitbox = false, autoFire = false, autoSpin = false;

    ScreenType requestedScreen = ScreenType.GAME;

    public GameScreen() {
        tank = LoadTexture("resources/game/tank.png");
        barrel = LoadTexture("resources/game/barrel.png");
        bullet = LoadTexture("resources/game/bullet.png");
        square = LoadTexture("resources/game/square.png");
        triangle = LoadTexture("resources/game/triangle.png");
        pentagon = LoadTexture("resources/game/pentagon.png");
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

    @Override
    public void update() {
        if (IsWindowResized()) updateLayout();

        dt = GetFrameTime();
        Vector2 mouseScreen = GetMousePosition();

        updateCamera();

        if (!showSettings) {
            handleInput(mouseScreen);

            if (shapes.size() < startShapes) spawnShapes(startShapes - shapes.size());

            spatialGrid.clear();
            for (Shape s : shapes) {
                s.update();
                spatialGrid.addShape(s);
            }

            if (!deathScreen) {
                playerTank.update();
                playerTank.regenHealth(dt);
            }

            if (!playerTank.isAlive()) {
                deathScreen = true;
            }

            for (Bullet b : bullets) b.update();

            checkCollisions();
        }

        if (isHover(settingsRect, mouseScreen)) {
            settingsHover = true;
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
                showSettings = true;
            }
        }

        if (IsKeyPressed(KEY_SPACE)) {
            showSettings = false;
        }
    }

    private void updateCamera() {
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        Vector2 desiredTarget = new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY());
        camera.target().x(camera.target().x() + (desiredTarget.x() - camera.target().x()) * movementLerp);
        camera.target().y(camera.target().y() + (desiredTarget.y() - camera.target().y()) * movementLerp);

//        if (!showSettings) {
//            getZoomLevel();
//            float desiredZoom = zoomLevel;
//            camera.zoom(camera.zoom() + (desiredZoom - camera.zoom()) * zoomLerp);
//        }
    }

    private void getZoomLevel() {
        float scroll = GetMouseWheelMove();
        if (scroll > 0) zoomLevel += 0.1f;
        else if (scroll < 0) zoomLevel -= 0.1f;

        if (zoomLevel < 0.8f) zoomLevel = 0.8f;
        if (zoomLevel > 20.0f) zoomLevel = 20.0f;
    }

    private void handleInput(Vector2 mouseScreen) {
        Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), camera);

        if (IsKeyPressed(KEY_R) && deathScreen) {
            deathScreen = false;
            float randX = worldW * (float) Math.random();
            float randY = worldH * (float) Math.random();
            playerTank = new Basic(randX, randY, angle, tank, barrel);
            autoSpin = false;
            autoFire = false;
        }

        if (!deathScreen) {
            if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && playerTank.canFire() && !isHover(settingsRect, mouseScreen)) {
                fireBullet();
                playerTank.applyRecoil();
                playerTank.resetReload();
            }

            if (IsKeyPressed(KEY_Q)) {
                addShape();
            }

            if (IsKeyPressed(KEY_B)) {
                hitbox = !hitbox;
            }

            if (IsKeyPressed(KEY_E)) {
                autoFire = !autoFire;
            }

            if (autoFire && playerTank.canFire()) {
                fireBullet();
                playerTank.applyRecoil();
            }

            if (IsKeyPressed(KEY_C)) {
                autoSpin = !autoSpin;
            }

            if (!autoSpin) {
                angle = (float) Math.atan2(mouse.y() - playerTank.getCenterY(), mouse.x() - playerTank.getCenterX());
            } else {
                angle += 1 * dt;
            }

            if (IsKeyPressed(KEY_K)) {
                deathScreen = true;
            }
        }
    }

    @Override
    public void draw() {
        ClearBackground(borderGridColour);

        BeginMode2D(camera);

        // Camera bounds
        camLeft = camera.target().x() - camera.offset().x() / camera.zoom();
        camRight = camLeft + GetScreenWidth() / camera.zoom();
        camTop = camera.target().y() - camera.offset().y() / camera.zoom();
        camBottom = camTop + GetScreenHeight() / camera.zoom();

        drawWorld();
        drawEntities();

        EndMode2D();

        drawUI();

        if (deathScreen) {
            drawDeathScreen();
        }
    }

    private void drawWorld() {
        for (int x = - borderSize; x <= worldW + borderSize; x += tileSize) {
            if (x >= camLeft && x <= camRight) DrawLine(x, -borderSize, x, worldH + borderSize, borderGridLineColour);
        }

        for (int y = - borderSize; y <= worldH + borderSize; y += tileSize) {
            if (y >= camTop && y <= camBottom) DrawLine(-borderSize, y, worldW + borderSize, y, borderGridLineColour);
        }

        DrawRectangle(0, 0, worldW, worldH, worldGridColour);

        for (int x = 0; x <= worldW; x += tileSize) {
            if (x >= camLeft && x <= camRight) DrawLine(x, 0, x, worldH, worldGridLineColour);
        }

        for (int y = 0; y <= worldH; y += tileSize) {
            if (y >= camTop && y <= camBottom) DrawLine(0, y, worldW, y, worldGridLineColour);
        }
    }

    private void drawEntities() {
        // Shapes
        for (Shape s : shapes) {
            if (s.getCenterX() + s.getSize() > camLeft && s.getCenterX() - s.getSize() < camRight && s.getCenterY() + s.getSize() > camTop && s.getCenterY() - s.getSize() < camBottom) {
                s.draw();
                if (hitbox) s.drawHitBox();
            }
        }

        // Bullets
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            if (!b.isAlive()) {
                it.remove();
                continue;
            }
            if (b.getCenterX() + b.getSize() > camLeft && b.getCenterX() - b.getSize() < camRight && b.getCenterY() + b.getSize() > camTop && b.getCenterY() - b.getSize() < camBottom) {
                b.draw();
                if (hitbox) b.drawHitBox();
            }
        }

        // Tank
        if (!deathScreen) {
            playerTank.setAngle(angle);
            playerTank.draw();
            if (hitbox) playerTank.drawHitBox();
        }
    }

    private void drawUI() {
        drawButton(settings, settingsRect, settingsHover, showSettings);
        DrawFPS(10, 10);

        if (showSettings) drawSettings();

    }

    private void drawSettings() {
        DrawRectangle(0, 0, screenW, screenH, newColor(0, 0, 0, 180));
        int boxW = 1000, boxH = 600, boxX = (screenW - boxW) / 2, boxY = (screenH - boxH) / 2;

        Rectangle rect = newRectangle(boxX, boxY, boxW, boxH);

        DrawRectangleRounded(rect, 0.2f, 10, RAYWHITE);
        DrawRectangleRoundedLines(rect, 0.2f, 10, DARKGRAY);

        DrawText("Settings", boxX + boxW / 2 - MeasureText("Settings", 50) / 2, boxY + 20, 50, BLACK);
        DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 20) / 2,  boxY + 165, 20, BLACK);
    }

    private void drawDeathScreen() {
        DrawRectangle(0, 0, screenW, screenH, newColor(0, 0, 0, 75));
        DrawText("You DIED!", GetScreenWidth() / 2 - MeasureText("You DIED!", 40) / 2, GetScreenHeight() / 2 - 40, 40, RED);
        DrawText("Press R to respawn!", GetScreenWidth() / 2 - MeasureText("Press R to respawn!", 40) / 2, GetScreenHeight() / 2 + 10, 40, WHITE);
    }


    @Override
    public void unload() {
        UnloadTexture(tank);
        UnloadTexture(barrel);
        UnloadTexture(bullet);
        UnloadTexture(square);
        UnloadTexture(triangle);
        UnloadTexture(pentagon);
    }

    @Override
    public ScreenType getRequestedScreen() {
        return requestedScreen;
    }

    private void updateLayout() {
        screenW = GetScreenWidth();
        screenH = GetScreenHeight();

        float ratioW = screenW / (float) DEFAULT_SCREEN_W;
        float ratioH = screenH / (float) DEFAULT_SCREEN_H;

        float settingsW = settingsSize * ratioW;
        float settingsH = settingsSize * ratioH;
        settingsRect = newRectangle(screenW - settingsW - 15 * ratioW, 15 * ratioH, settingsW, settingsH);
    }

    private void spawnShapes(int num) {
        for (int i = 0; i < num; i++) {
            addShape();
        }
    }

    private void addShape() {
        int type = (int) (Math.random() * 3);

        float orbitX = (float) (Math.random() * GameScreen.worldW);
        float orbitY = (float) (Math.random() * GameScreen.worldH);

        switch (type) {
            case 0 -> shapes.add(new Square(orbitX, orbitY, 0, square));
            case 1 -> shapes.add(new Triangle(orbitX, orbitY, 0, triangle));
            default -> shapes.add(new
                    Pentagon(orbitX, orbitY, 0, pentagon));
        }
    }

    private void fireBullet() {
        float bulletSize = playerTank.getBulletSize();
        float bulletX = playerTank.getCenterX() + (float) Math.cos(angle) * (barrelW + bulletSize / 2f);
        float bulletY = playerTank.getCenterY() + (float) Math.sin(angle) * (barrelW + bulletSize / 2f);
        bullets.add(new Bullet(bulletX, bulletY, angle, bullet, bulletSize, playerTank.getBulletDamage(), playerTank.getBulletSpeed(), playerTank.getBulletPenetration()));
        playerTank.resetReload();
    }

    private void checkCollisions() {
        checkBulletShapeCollisions();

        if (!deathScreen) checkTankShapeCollisions();
    }

    private void checkBulletShapeCollisions() {
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();

            spatialGrid.getPotentialCollisions(b.getCenterX(), b.getCenterY(), b.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);
            for (Shape s : potentialShapes) {
                if (Collision.circlePolygonCollision(b.getCenterX(), b.getCenterY(), b.getSize() / 2f, s.polygon)) {
                    s.takeDamage(b.getBulletDamage());
                    b.takeDamage(s.getBodyDamage());

                    if (!s.isAlive()) {
                        shapes.remove(s);
                    }
                    if (!b.isAlive()) {
                        bulletIt.remove();
                        break;
                    }
                }
            }
        }
    }

    private void checkTankShapeCollisions() {
        spatialGrid.getPotentialCollisions(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getSize() / 2f, MAX_SHAPE_SIZE, potentialShapes);
        for (Shape s : potentialShapes) {
            if (Collision.circlePolygonCollision(playerTank.getCenterX(), playerTank.getCenterY(), playerTank.getSize() / 2f, s.polygon)) {
                playerTank.takeDamage(s.getBodyDamage());
                s.takeDamage(playerTank.getBodyDamage());

                if (!s.isAlive()) {
                    shapes.remove(s);
                }
            }
        }
    }

}
