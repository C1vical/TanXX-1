import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;


public class GameScreen extends GameState {

    // World dimensions
    public static final int worldW = 2000;
    public static final int worldH = 2000;
    public static final int borderSize = 2000;

    // Grid dimensions
    public final int tileSize = 20;

    // World colors
    public final Color worldGridColour = newColor(65, 65, 65, 255);
    public final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public final Color borderGridColour = newColor(34, 34, 34, 255);
    public final Color borderGridLineColour = newColor(45, 45, 45, 255);

    // Colors
    public final Color barrelColor = newColor(100, 99, 107, 255);
    public final Color barrelStrokeColor = newColor(55, 55, 55, 255);

    // Tank
    public Texture tank;
    public static Tank playerTank;
    public static final int tankSize = 75;
    public static float angle;
    public static final int tankSpeed = 200; // pixels per second

    // Barrel
    public Texture barrel;
    public static final int barrelW = 75;
    public static final int barrelH = tankSize + 10;

    // Bullets
    public Texture bullet;
    public static float reloadSpeed = 0.2f; // default 0.8f
    public static float reloadTimer = 0f;
    public static int bulletSize = barrelH; // default barrelH
    public static int bulletSpeed = 250; // pixels per second
    List<Bullet> bullets = new ArrayList<>();

    // Shapes
    public Texture square, triangle, pentagon;
    public static int shapeRadius = 25;
    List<Shape> shapes = new ArrayList<>();
    private static final int startShapes = 30;

    // Settings icon
    public Texture settings;
    public static int settingsSize = 50;
    public Rectangle settingsRect;
    public boolean settingsHover = false;
    public static boolean showSettings = false;
    
    // Camera
    public static Camera2D camera = new Camera2D();
    float camLeft;
    float camRight;
    float camTop;
    float camBottom;

    // Camera zoom level
    public static float zoomLevel = 1.0f;

    // Frame time
    public static float dt;

    // Booleans
    public static boolean hitbox = false, autoFire = false, autoSpin = false;

    // Lerp values
    public final float movementLerp = 0.1f;
    public final float zoomLerp = 0.1f;

    ScreenType requestedScreen = ScreenType.GAME;

    public GameScreen() {
        // Load Textures
        tank = resizeImage(LoadImage("resources/game/tank.png"), tankSize + 2 * 5, tankSize + 2 * 5);
        barrel = resizeImage(LoadImage("resources/game/barrel.png"), barrelW, barrelH);
        bullet = resizeImage(LoadImage("resources/game/bullet.png"), bulletSize, bulletSize);
        square = resizeImage(LoadImage("resources/game/square.png"), shapeRadius, shapeRadius);
        triangle = resizeImage(LoadImage("resources/game/triangle.png"), shapeRadius, shapeRadius);
        pentagon = resizeImage(LoadImage("resources/game/pentagon.png"), shapeRadius, shapeRadius);
        settings = resizeImage(LoadImage("resources/game/settings.png"), settingsSize, settingsSize);

        // Set player tank
        playerTank = new Tank(worldW / 2f, worldH / 2f, angle, tank);

        // Set camera
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

        // Get frame time
        dt = GetFrameTime();

        Vector2 mouseScreen = GetMousePosition();

        // Camera
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));
        Vector2 desiredTarget = new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY()); // Smooth lerp
        camera.target().x(camera.target().x() + (desiredTarget.x() - camera.target().x()) * movementLerp);
        camera.target().y(camera.target().y() + (desiredTarget.y() - camera.target().y()) * movementLerp);

        if (!showSettings) {
             // Zoom
            getZoomLevel();
            float desiredZoom = zoomLevel;
            camera.zoom(camera.zoom() + (desiredZoom - camera.zoom()) * zoomLerp);

            // Input
            Vector2 mouse = GetScreenToWorld2D(GetMousePosition(), camera);

            if (!autoSpin) {
                angle = (float) Math.atan2(mouse.y() - playerTank.getCenterY(), mouse.x() - playerTank.getCenterX());
            } else {
                angle += 1f * dt;
            }
            
            if (IsKeyPressed(KEY_B)) {
                hitbox = !hitbox;
            }
            
            if (reloadTimer > 0f) {
                reloadTimer -= dt;
            }
            
            if ((IsMouseButtonPressed(MOUSE_BUTTON_LEFT) || IsMouseButtonDown(MOUSE_BUTTON_LEFT)) && reloadTimer <= 0f && !isHover(settingsRect, mouseScreen)) {
                fireBullet();
                playerTank.applyRecoil();
            }

            if (IsKeyPressed(KEY_Q)) {
                addShape();
            }

            if (IsKeyPressed(KEY_E)) {
                autoFire = !autoFire;
            }

            if(autoFire && reloadTimer <= 0f) {
                fireBullet();
                playerTank.applyRecoil();
            }

            if (IsKeyPressed(KEY_C)) {
                autoSpin = !autoSpin;
            }

            if(shapes.size() < startShapes) {
                spawnShapes(startShapes - shapes.size());
            }

            for (Shape s : shapes) s.update();
            playerTank.update();

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
        // Shapes
        for (Shape s : shapes) {
            if (s.getCenterX() >= camLeft && s.getCenterX() <= camRight && s.getCenterY() >= camTop && s.getCenterY() <= camBottom) {
                s.draw();
                if (hitbox) {
                    s.drawHitBox();
                }
            }

        }

        // Bullets
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            if (b.getCenterX() >= camLeft && b.getCenterX() <= camRight && b.getCenterY() >= camTop && b.getCenterY() <= camBottom) {
                if (b.isAlive()) {
                    b.draw();
                    if (hitbox) {
                        b.drawHitBox();
                    }
                } else {
                    it.remove();
                }
            }
        }

        // Barrel
        Rectangle source = newRectangle(0, 0, barrelW, barrelH);
        Rectangle dest = newRectangle(playerTank.getCenterX(), playerTank.getCenterY(), barrelW, barrelH);
        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrel, source, dest, origin, angle * (180f / (float) Math.PI), barrelColor);

        // Tank
        playerTank.setAngle(angle);
        playerTank.draw();
        if (hitbox) {
            playerTank.drawHitBox();
        }

        EndMode2D();

        drawButton(settings, settingsRect, settingsHover, showSettings);
        
        if (showSettings) {
            DrawRectangle(0, 0, screenW, screenH, newColor(0, 0, 0, 180));
            int boxW = 1000, boxH = 600, boxX = (screenW - boxW) / 2, boxY = (screenH - boxH) / 2;

            Rectangle rect = newRectangle(boxX, boxY, boxW, boxH);

            DrawRectangleRounded(rect, 0.2f, 10, RAYWHITE);
            DrawRectangleRoundedLines(rect, 0.2f, 10, DARKGRAY);

            DrawText("Settings", boxX + boxW / 2 - MeasureText("Settings", 50) / 2, boxY + 20, 50, BLACK);
            DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 20) / 2,  boxY + 165, 20, BLACK);
        }

        DrawFPS(10, 10);
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

    public void updateLayout() {
        screenW = GetScreenWidth();
        screenH = GetScreenHeight();

        float ratioW = screenW / (float) defaultScreenW;
        float ratioH = screenH / (float) defaultScreenH;

        float settingsW = settingsSize * ratioW;
        float settingsH = settingsSize * ratioH;
        settingsRect = newRectangle(screenW - settingsW - 10 * ratioW, 10 * ratioH, settingsW, settingsH);
    }

    public void drawWorld() {
        for (int x = - borderSize; x <= worldW + borderSize; x += tileSize) {
            DrawLine(x, -borderSize, x, worldH + borderSize, borderGridLineColour);
        }

        for (int y = - borderSize; y <= worldH + borderSize; y += tileSize) {
            DrawLine(-borderSize, y, worldW + borderSize, y, borderGridLineColour);
        }

        DrawRectangle(0, 0, worldW, worldH, worldGridColour);

        for (int x = 0; x <= worldW; x += tileSize) {
            DrawLine(x, 0, x, worldH, worldGridLineColour);
        }

        for (int y = 0; y <= worldH; y += tileSize) {
            DrawLine(0, y, worldW, y, worldGridLineColour);
        }
    }

    public void getZoomLevel() {
        float scroll = GetMouseWheelMove();
        if (scroll > 0) {
            zoomLevel += 0.03f;
        } else if (scroll < 0) {
            zoomLevel -= 0.03f;
        }

        if (zoomLevel < 0.8f) zoomLevel = 0.8f;
        if (zoomLevel > 3.0f) zoomLevel = 3.0f;
    }

    public Texture resizeImage(Image img, int newWidth, int newHeight) {
        ImageResize(img, newWidth, newHeight);
        Texture tex = LoadTextureFromImage(img);
        UnloadImage(img);
        return tex;
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
            case 0 -> shapes.add(new Shape(orbitX, orbitY, 0, square, 0));
            case 1 -> shapes.add(new Shape(orbitX, orbitY, 0, triangle, 1));
            default -> shapes.add(new Shape(orbitX, orbitY, 0,   pentagon, 2));
        }
    }

    private void fireBullet() {
        float bulletX = playerTank.getCenterX() + (float) Math.cos(angle) * (barrelW + bulletSize / 2f);
        float bulletY = playerTank.getCenterY() + (float) Math.sin(angle) * (barrelW + bulletSize / 2f);
        bullets.add(new Bullet(bulletX, bulletY, angle, bullet));
        reloadTimer = reloadSpeed;
    }

    public void checkCollisions() {
        // Shape-shape collision
//        for (int i = 0; i < shapes.size(); i++) {
//            Shape s1 = shapes.get(i);
//
//            for (int j = i + 1; j < shapes.size(); j++) {
//                Shape s2 = shapes.get(j);
//
//                boolean hit = Collision.polygonPolygonCollision(s1.polygon, s2.polygon);
//
//                if (hit) {
//                    // Remove both shapes
//                    shapes.remove(j); // remove s2 first (higher index)
//                    shapes.remove(i); // then remove s1
//                    i--; // step back i because s1 was removed
//                    break; // exit inner loop since s1 is gone
//                }
//            }
//        }

        // Bullet and shape collision
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();

            Iterator<Shape> shapeIt = shapes.iterator();
            while (shapeIt.hasNext()) {
                Shape s = shapeIt.next();

                boolean hit;

                // Circle vs Polygon (bullet is circle)
                hit = Collision.circlePolygonCollision(new Vector2().x(b.getCenterX()).y(b.getCenterY()), b.size / 2f, s.polygon);

                if (hit) {
                    bulletIt.remove();
                    shapeIt.remove();
                    break; // stop checking other shapes for this bullet
                }
            }
        }

        // Shape and tank collision
        Iterator<Shape> shapeIt = shapes.iterator();
        while (shapeIt.hasNext()) {
            Shape s = shapeIt.next();

            boolean hit;

            // Polygon vs Polygon (tank can be polygon if you want) or circle vs polygon
            hit = Collision.circlePolygonCollision(new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY()), playerTank.size / 2f + 2 * 5, s.polygon);

            if (hit) {
                shapeIt.remove();
                // optionally, you can handle tank damage here
                break; // remove only 1 shape per frame
            }
        }
    }

}
