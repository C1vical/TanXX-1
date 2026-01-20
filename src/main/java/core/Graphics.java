package core;

import entities.Bullet;
import entities.Shape;
import entities.Tank;

import java.util.ArrayList;
import java.util.List;

import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

// Graphics class handles rendering the world, entities, and UI elements
public class Graphics {

    // Color for buttons when hovered
    public static final Color hovered = newColor(180, 180, 180, 255);

    // Colors for world grid and background
    public static final Color worldGridColour = newColor(65, 65, 65, 255);
    public static final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public static final Color borderGridColour = newColor(34, 34, 34, 255);
    public static final Color borderGridLineColour = newColor(45, 45, 45, 255);

    // Size of each grid tile
    public static final int tileSize = 20;

    // Minimap position and size
    public static float miniMapX;
    public static float miniMapY;
    public static final float miniMapW = 200;
    public static final float miniMapH = 200;

    // Colors for minimap
    public static final Color miniMapColour = newColor(148, 148, 148, 200);
    public static final Color miniMapBorderColour = newColor(20, 20, 20, 200);

    // Names and colors for player stats in the stats menu
    public static final String[] statNames = {
            "Health Regen", "Max Health", "Body Damage", "Bullet Speed",
            "Bullet Penetration", "Bullet Damage", "Reload Speed", "Movement Speed"
    };
    public static final Color[] statColors = {
            newColor(252, 173, 118, 255),
            newColor(249, 67, 255, 255),
            newColor(133, 67, 255, 255),
            newColor(67, 127, 255, 255),
            newColor(255, 222, 67, 255),
            newColor(255, 67, 67, 255),
            newColor(130, 255, 67, 255),
            newColor(67, 255, 249, 255)
    };

    // Stats menu dimensions and animation state
    public static final float statsMenuW = 200;
    public static final float statsItemH = 25;
    public static final float statsMenuH = statNames.length * (statsItemH + 5);
    public static float startY;
    public static float statsMenuAnim = 0f; // 0 = collapsed, 1 = expanded
    public static float statsMenuTimer = 0f; // time menu stays open after keypress
    public static final float hiddenX = -statsMenuW - 5; // offscreen X position
    public static final float skillTextX = 15; // X position for skill points text

    // Lerp constants for smooth camera movement and zoom
    public static final float movementLerp = 0.1f;
    public static final float zoomLerp = 0.1f;

    // General UI padding
    public static float padding = 15;

    // Level bar properties
    public static float levelBarW = 350;
    public static float levelBarH = 25;
    public static float levelBarX;
    public static float levelBarY;
    public static int levelTextFont = 20;

    // Score bar properties
    public static float scoreBarW = 250;
    public static float scoreBarH = 20;
    public static float scoreBarX;
    public static float scoreBarY;
    public static int scoreTextFont = 15;
    public static float margin = 10;

    // Player name display
    public static String nameText = "Player 1";
    public static int nameTextFont = 25;
    public static float nameTextX;
    public static float nameTextY;

    // Upgrade menu rectangles and options
    public static List<Rectangle> upgradeRects = new ArrayList<>();
    public static List<TankType> upgradeOptions = new ArrayList<>();

    // Game paused state
    public static boolean pauseGame = false;

    // Menu layout rectangles
    public static Rectangle backgroundRect;
    public static Rectangle logoRect;
    public static Rectangle playRect;
    public static Rectangle creditsRect;
    public static Rectangle exitRect;

    // Menu hover states
    public static boolean showCredits = false;
    public static boolean playHover = false;
    public static boolean creditsHover = false;
    public static boolean exitHover = false;

    // Camera and viewport boundaries
    public static Camera2D camera = new Camera2D();
    public static float camLeft, camRight, camTop, camBottom;
    public static float zoomLevel = 1.5f;
    public static final float defaultZoom = 1.5f;

    // Update camera to follow player with smooth movement
    public static void updateCamera(Tank playerTank) {
        // Center camera on screen
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));

        // Smoothly follow player
        Vector2 desiredTarget = new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY());
        camera.target().x(camera.target().x() + (desiredTarget.x() - camera.target().x()) * movementLerp);
        camera.target().y(camera.target().y() + (desiredTarget.y() - camera.target().y()) * movementLerp);

        // Smooth zoom adjustment
        float desiredZoom = zoomLevel * playerTank.getZoomFactor();
        camera.zoom(camera.zoom() + (desiredZoom - camera.zoom()) * zoomLerp);
    }

    // Update zoom based on mouse wheel movement
    public static void getZoomLevel() {
        float scroll = GetMouseWheelMove();
        if (scroll > 0) zoomLevel += 0.1f;
        else if (scroll < 0) zoomLevel -= 0.1f;

        // Clamp zoom values
        if (zoomLevel < 0.8f) zoomLevel = 0.8f;
        if (zoomLevel > 20.0f) zoomLevel = 20.0f;
    }

    // Update positions of UI elements based on screen size
    public static void updateGameLayout() {
        GameState.screenW = GetScreenWidth();
        GameState.screenH = GetScreenHeight();

        float ratioW = GameState.screenW / (float) GameState.DEFAULT_SCREEN_W;
        float ratioH = GameState.screenH / (float) GameState.DEFAULT_SCREEN_H;

        // Level bar position
        levelBarX = GameState.screenW / 2f - levelBarW / 2;
        levelBarY = GameState.screenH - padding - levelBarH;

        // Score bar position
        scoreBarX = GameState.screenW / 2f - scoreBarW / 2;
        scoreBarY = levelBarY - margin - scoreBarH;

        // Player name position
        nameTextX = GameState.screenW / 2f - (float) MeasureText(nameText, nameTextFont) / 2;
        nameTextY = scoreBarY - margin - nameTextFont;

        // Stats menu Y position
        startY = GameState.screenH - statsMenuH - 50;

        // Minimap position
        miniMapX = GameState.screenW - miniMapW - padding * ratioW;
        miniMapY = padding * ratioH;
    }

    // Update menu layout positions based on screen size
    public static void updateMenuLayout() {
        GameState.screenW = GetScreenWidth();
        GameState.screenH = GetScreenHeight();

        float ratioW = GameState.screenW / (float) GameState.DEFAULT_SCREEN_W;
        float ratioH = GameState.screenH / (float) GameState.DEFAULT_SCREEN_H;

        backgroundRect = newRectangle(0, 0, GameState.screenW, GameState.screenH);
        logoRect = newRectangle(GameState.screenW / 2f - 950 * ratioW / 2, 125 * ratioH, 950 * ratioW, 375 * ratioH);
        playRect = newRectangle(GameState.screenW / 2f - 900 * ratioW / 2, GameState.screenH / 2f, 900 * ratioW, 360 *  ratioH);
        creditsRect = newRectangle(padding * ratioW, GameState.screenH - 120f * ratioH - padding * ratioH, 300f * ratioW, 120f * ratioH);
        exitRect = newRectangle(GameState.screenW - 300f * ratioW - padding * ratioW, GameState.screenH - 120f * ratioH - padding * ratioH, 300f * ratioW, 120f * ratioH);
    }

    // Draw a texture scaled to fit a destination rectangle
    public static void drawScaled(Texture tex, Rectangle dest, Color color) {
        Rectangle source = newRectangle(0, 0, tex.width(), tex.height());
        DrawTexturePro(tex, source, dest, new Vector2().x(0).y(0), 0f, color);
    }

    // Draw a button with hover effect
    public static void drawButton(Texture tex, Rectangle rect, boolean hover, boolean show) {
        drawScaled(tex, rect, hover && !show ? hovered : WHITE);
    }

    // Draw world background and grid lines
    public static void drawWorld() {
        float drawLeft = Math.max(0, camLeft);
        float drawTop = Math.max(0, camTop);
        float drawRight = Math.min(EntityManager.worldW, camRight);
        float drawBottom = Math.min(EntityManager.worldH, camBottom);

        // Draw border area
        DrawRectangleV(new Vector2().x(camLeft).y(camTop), new Vector2().x(camRight - camLeft).y(camBottom - camTop), borderGridColour);

        // Draw main world area
        DrawRectangleV(new Vector2().x(drawLeft).y(drawTop), new Vector2().x(drawRight - drawLeft).y(drawBottom - drawTop), worldGridColour);

        // Draw vertical grid lines
        int startX = (int) Math.floor(camLeft / tileSize) * tileSize;
        int endX = (int) Math.ceil(camRight / tileSize) * tileSize;

        for (int x = startX; x <= endX; x += tileSize) {
            if (x < drawLeft || x > drawRight) {
                DrawLineV(new Vector2().x(x).y(camTop), new Vector2().x(x).y(camBottom), borderGridLineColour);
            } else {
                DrawLineV(new Vector2().x(x).y(camTop), new Vector2().x(x).y(drawTop), borderGridLineColour);
                DrawLineV(new Vector2().x(x).y(drawTop), new Vector2().x(x).y(drawBottom), worldGridLineColour);
                DrawLineV(new Vector2().x(x).y(drawBottom), new Vector2().x(x).y(camBottom), borderGridLineColour);
            }
        }

        // Draw horizontal grid lines
        int startY = (int) Math.floor(camTop / tileSize) * tileSize;
        int endY = (int) Math.ceil(camBottom / tileSize) * tileSize;

        for (int y = startY; y <= endY; y += tileSize) {
            if (y < drawTop || y > drawBottom) {
                DrawLineV(new Vector2().x(camLeft).y(y), new Vector2().x(camRight).y(y), borderGridLineColour);
            } else {
                DrawLineV(new Vector2().x(camLeft).y(y), new Vector2().x(drawLeft).y(y), borderGridLineColour);
                DrawLineV(new Vector2().x(drawLeft).y(y), new Vector2().x(drawRight).y(y), worldGridLineColour);
                DrawLineV(new Vector2().x(drawRight).y(y), new Vector2().x(camRight).y(y), borderGridLineColour);
            }
        }
    }

    // Draw all game entities (shapes, bullets, player tank)
    public static void drawEntities() {
        for (Shape s : EntityManager.shapes) {
            if (s.getCenterX() + s.getRadius() > camLeft && s.getCenterX() - s.getRadius() < camRight
                    && s.getCenterY() + s.getRadius() > camTop && s.getCenterY() - s.getRadius() < camBottom) {
                s.draw();
            }
        }

        for (Bullet b : EntityManager.bullets) {
            if (b.getCenterX() + b.getRadius() > camLeft && b.getCenterX() - b.getRadius() < camRight
                    && b.getCenterY() + b.getRadius() > camTop && b.getCenterY() - b.getRadius() < camBottom) {
                b.draw();
            }
        }

        if (!EntityManager.deathScreen) {
            EntityManager.playerTank.draw();
        }
    }

    // Draw the player's level bar, score bar, and name
    public static void drawLevelBar() {
        // Draw background of level bar
        DrawRectangleRounded(newRectangle(levelBarX, levelBarY, levelBarW, levelBarH), 0.8f, 20, newColor(0, 0, 0, 200));

        // Draw filled portion based on level progress
        float progress = EntityManager.playerTank.getLevelProgress();
        DrawRectangleRounded(newRectangle(levelBarX, levelBarY, levelBarW * progress, levelBarH), 0.8f, 20, newColor(255, 222, 67, 230));

        // Draw level text
        String levelText = "Level " + EntityManager.playerTank.getLevel();
        DrawText(levelText, (int) (levelBarX + levelBarW / 2) - MeasureText(levelText, levelTextFont) / 2,
                (int) (levelBarY + levelBarH / 2) - levelTextFont / 2, levelTextFont, WHITE);

        // Draw score bar background
        DrawRectangleRounded(newRectangle(scoreBarX, scoreBarY, scoreBarW, scoreBarH), 0.8f, 20, newColor(67, 255, 145, 255));

        // Draw score text
        String scoreText = "Score: " + EntityManager.playerTank.getScore();
        DrawText(scoreText, (int) (scoreBarX + scoreBarW / 2) - MeasureText(scoreText, scoreTextFont) / 2,
                (int) (scoreBarY + scoreBarH / 2) - scoreTextFont / 2, scoreTextFont, BLACK);

        // Draw player name above bars
        DrawText(nameText, (int) nameTextX, (int) nameTextY, nameTextFont, WHITE);
    }

    // Draw the stats menu with skill levels and hotkeys
    public static void drawStatsMenu() {
        if (EntityManager.deathScreen) return; // Skip if player is dead

        // Calculate animated X position for sliding effect
        float x = hiddenX + (padding - hiddenX) * statsMenuAnim;

        // Determine if stats are locked due to zero skill points
        boolean locked = false;
        if (EntityManager.playerTank.getSkillPoints() <= 0) {
            locked = true;
            EntityManager.playerTank.setUpgradeSkill(false);
        }

        // Colors for locked/unlocked states
        Color bgColor = locked ? newColor(0, 0, 0, 210) : newColor(0, 0, 0, 150);
        Color skillTextColor = locked ? LIGHTGRAY : WHITE;
        Color statTextColor = locked ? GRAY : WHITE;

        // Draw skill points count
        DrawText("x" + EntityManager.playerTank.getSkillPoints(), (int) skillTextX, (int) startY - 30, 25, skillTextColor);

        // Draw each stat
        for (int i = 0; i < statNames.length; i++) {
            float y = startY + i * (statsItemH + 5);
            Rectangle rect = newRectangle(x, y, statsMenuW, statsItemH);

            // Draw background of stat row
            DrawRectangleRounded(rect, 0.4f, 20, bgColor);

            // Draw stat level segments
            int statLevel = EntityManager.playerTank.getStats()[i];
            float segmentGap = 2;
            float segmentWidth = (statsMenuW - 10 - (7 * segmentGap)) / 7f;

            for (int j = 0; j < 7; j++) {
                float segX = x + 5 + j * (segmentWidth + segmentGap);
                Rectangle segRect = newRectangle(segX, y + 18, segmentWidth, 4);
                if (j < statLevel) {
                    Color barColor = statColors[i];
                    if (locked) barColor = newColor(barColor.r(), barColor.g(), barColor.b(), 100);
                    DrawRectangleRounded(segRect, 1f, 20, barColor);
                } else {
                    DrawRectangleRounded(segRect, 1f, 20, newColor(50, 50, 50, 255));
                }
            }

            // Draw stat name
            DrawText(statNames[i], (int) x + 10, (int) y + 3, 12, statTextColor);

            // Draw hotkey number
            DrawText(String.valueOf(i + 1), (int) (x + statsMenuW - padding), (int) y + 3, 12, locked ? DARKGRAY : GRAY);
        }
    }

    // Draw the minimap with player indicator
    public static void drawMiniMap() {
        Rectangle rect = newRectangle(miniMapX, miniMapY, miniMapW, miniMapH);
        DrawRectangleRounded(rect, 0.05f, 10, miniMapColour);
        DrawRectangleRoundedLinesEx(rect, 0.05f, 10, 4, miniMapBorderColour);

        // Calculate player position in minimap coordinates
        float x = EntityManager.playerTank.getCenterX() / EntityManager.worldW;
        float y = EntityManager.playerTank.getCenterY() / EntityManager.worldH;

        // Draw player indicator
        DrawCircleV(new Vector2().x(miniMapX + miniMapW * x).y(miniMapY + miniMapH * y), 5, RED);
    }

    // Draw upgrade menu boxes for tank upgrades
    public static void drawUpgradeMenu(TankType currentType) {
        upgradeRects.clear();
        upgradeOptions.clear();

        List<TankType> upgrades = UpgradeData.UPGRADES.get(currentType);
        if (upgrades == null || upgrades.isEmpty()) return;

        float boxW = 180;
        float boxH = 90;
        float gap = 20;

        float totalW = upgrades.size() * boxW + (upgrades.size() - 1) * gap;
        float startX = (GetScreenWidth() - totalW) / 2f;
        float y = 80;

        for (int i = 0; i < upgrades.size(); i++) {
            float x = startX + i * (boxW + gap);

            Rectangle rect = newRectangle(x, y, boxW, boxH);
            upgradeRects.add(rect);
            upgradeOptions.add(upgrades.get(i));

            drawUpgradeBox(rect, upgrades.get(i).name());
        }
    }

    // Draw individual upgrade box with hover effect
    private static void drawUpgradeBox(Rectangle r, String name) {
        Vector2 mouse = GetMousePosition();
        boolean hover = CheckCollisionPointRec(mouse, r);

        DrawRectangleRounded(r, 0.5f, 1, hover ? newColor(120, 120, 120, 220) : newColor(0, 0, 0, 150));
        DrawRectangleRoundedLinesEx(r, 0.5f, 1, 2, WHITE);

        int fontSize = 20;
        int textW = MeasureText(name, fontSize);

        DrawText(name, (int) (r.x() + (r.width() - textW) / 2), (int) (r.y() + r.height() / 2 - fontSize / 2f), fontSize, WHITE);
    }

    // Draw overlay when player dies
    public static void drawDeathScreen() {
        // Semi-transparent background
        DrawRectangle(0, 0, GameState.screenW, GameState.screenH, newColor(0, 0, 0, 75));

        // "You Died" text
        DrawText("YOU DIED!", GetScreenWidth() / 2 - MeasureText("YOU DIED!", 40) / 2, GetScreenHeight() / 2 - 40, 40, RED);

        // Instructions to respawn or return to menu
        DrawText("Press R to respawn!", GetScreenWidth() / 2 - MeasureText("Press R to respawn!", 40) / 2, GetScreenHeight() / 2 + 10, 40, WHITE);
        DrawText("Press SPACE to return to the main menu", GetScreenWidth() / 2 - MeasureText("Press SPACE to return to the main menu", 20) / 2, GetScreenHeight() / 2 + 60, 20, WHITE);

        // Player statistics display
        DrawText("Statistics", GetScreenWidth() / 2 - MeasureText("Statistics", 30) / 2, GetScreenHeight() - 300, 30, WHITE);
        DrawText("Level: " + EntityManager.playerTank.getLevel(), GetScreenWidth() / 2 - MeasureText("Level: " + EntityManager.playerTank.getLevel(), 20) / 2, GetScreenHeight() - 260, 20, WHITE);
        DrawText("Score: " + EntityManager.playerTank.getScore(), GetScreenWidth() / 2 - MeasureText("Score: " + EntityManager.playerTank.getScore(), 20) / 2, GetScreenHeight() - 220, 20, WHITE);
        DrawText("Shapes killed: " + EntityManager.playerTank.getNumShapesKilled(), GetScreenWidth() / 2 - MeasureText("Shapes killed: " + EntityManager.playerTank.getNumShapesKilled(), 20) / 2, GetScreenHeight() - 180, 20, WHITE);
        DrawText("Time Alive: " + Math.round(EntityManager.playerTank.getTimeAlive()) + " s", GetScreenWidth() / 2 - MeasureText("Time Alive: " + Math.round(EntityManager.playerTank.getTimeAlive()) + " s", 20) / 2, GetScreenHeight() - 140, 20, WHITE);
    }

    // Draw credits screen
    public static void drawCredits() {
        // Dark overlay background
        DrawRectangle(0, 0, GameState.screenW, GameState.screenH, newColor(0, 0, 0, 180));

        // Credits box
        int boxW = 400;
        int boxH = 200;
        int boxX = (GameState.screenW - boxW) / 2;
        int boxY = (GameState.screenH - boxH) / 2;
        Rectangle rect = newRectangle(boxX, boxY, boxW, boxH);

        DrawRectangleRounded(rect, 0.2f, 10, RAYWHITE);
        DrawRectangleRoundedLines(rect, 0.2f, 10, DARKGRAY);

        // Credits text
        DrawText("Credits", boxX + boxW / 2 - MeasureText("Credits", 30) / 2, boxY + 20, 30, BLACK);
        DrawText("Made by:", boxX + 40, boxY + 60, 20, BLACK);
        DrawText("Jonathan Yu", boxX + 40, boxY + 95, 20, BLACK);
        DrawText("Cheney Chen", boxX + 40, boxY + 130, 20, BLACK);
        DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 14) / 2, boxY + 165, 14, BLACK);
    }
}
