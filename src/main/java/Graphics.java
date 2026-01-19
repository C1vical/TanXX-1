import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

// Graphics class centralizes all drawing and UI layout logic
public class Graphics {

    // Color when a button is hovered
    public static final Color hovered = newColor(180, 180, 180, 255);

    // World and grid colors
    public static final Color worldGridColour = newColor(65, 65, 65, 255);
    public static final Color worldGridLineColour = newColor(78, 78, 78, 255);
    public static final Color borderGridColour = newColor(34, 34, 34, 255);
    public static final Color borderGridLineColour = newColor(45, 45, 45, 255);
    public static final int tileSize = 20;

    // Display properties
    public static float padding = 15;

    // Settings UI properties
    public static int settingsSize = 75;
    public static Rectangle settingsRect;
    public static boolean settingsHover = false;
    public static boolean showSettings = false;

    // Level bar properties
    public static float levelBarW = 350;
    public static float levelBarH = 25;
    public static float levelBarX;
    public static float levelBarY;
    public static int levelTextFont = 20;

    // Score bar properties
    public static float scoreBarW = 250;
    public static float scoreBarH = 20;
    public static float margin = 10;
    public static float scoreBarX;
    public static float scoreBarY;
    public static int scoreTextFont = 15;

    // Player name display properties
    public static String nameText = "Player 1";
    public static int nameTextFont = 25;
    public static float nameTextX;
    public static float nameTextY;

    // Minimap properties
    public static final float miniMapW = 200;
    public static final float miniMapH = 200;
    public static float miniMapX;
    public static float miniMapY;
    public static final Color miniMapColour = newColor(148, 148, 148, 200);
    public static final Color miniMapBorderColour = newColor(71, 71, 71, 200);

    // Upgrade Menu configuration
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
    public static final float upgradeMenuWidth = 200;
    public static final float upgradeItemHeight = 25;
    public static final float menuH = statNames.length * (upgradeItemHeight + 5);
    public static float startY;
    public static final float hiddenX = -upgradeMenuWidth - 5;
    public static float upgradeMenuAnim = 0f; // 0 = collapsed, 1 = expanded
    public static float upgradeMenuTimer = 0f; // Time to keep the menu open after keypress
    public static final float skillTextX = 15;

    // MenuScreen UI layout rectangles
    public static Rectangle backgroundRect;
    public static Rectangle logoRect;
    public static Rectangle playRect;
    public static Rectangle creditsRect;
    public static Rectangle exitRect;

    // MenuScreen UI states
    public static boolean showCredits = false;
    public static boolean playHover = false;
    public static boolean creditsHover = false;
    public static boolean exitHover = false;

    // Camera and viewport bounds
    public static Camera2D camera = new Camera2D();
    public static float camLeft, camRight, camTop, camBottom;
    public static float zoomLevel = 1f;

    // Lerp constants for smooth transitions
    public static final float movementLerp = 0.1f;
    public static final float zoomLerp = 0.1f;

    // Updates camera position and zoom based on player position and mouse scroll
    public static void updateCamera(Tank playerTank) {
        // Center camera
        camera.offset(new Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f));

        // Smoothly follow player (using lerp)
        Vector2 desiredTarget = new Vector2().x(playerTank.getCenterX()).y(playerTank.getCenterY());
        camera.target().x(camera.target().x() + (desiredTarget.x() - camera.target().x()) * movementLerp);
        camera.target().y(camera.target().y() + (desiredTarget.y() - camera.target().y()) * movementLerp);

//        System.out.println((camRight - camLeft) + " " + (camBottom - camTop));

        // Zoom only when settings are closed
        if (!showSettings) {
            getZoomLevel();
            float desiredZoom = zoomLevel;
            camera.zoom(camera.zoom() + (desiredZoom - camera.zoom()) * zoomLerp);
        }
    }

    public static void getZoomLevel() {
        float scroll = GetMouseWheelMove();
        if (scroll > 0) zoomLevel += 0.1f;
        else if (scroll < 0) zoomLevel -= 0.1f;

        if (zoomLevel < 0.8f) zoomLevel = 0.8f;
        if (zoomLevel > 20.0f) zoomLevel = 20.0f;
    }

    // Update game layout based on screen radius
    public static void updateGameLayout() {
        GameState.screenW = GetScreenWidth();
        GameState.screenH = GetScreenHeight();

        float ratioW = GameState.screenW / (float) GameState.DEFAULT_SCREEN_W;
        float ratioH = GameState.screenH / (float) GameState.DEFAULT_SCREEN_H;

        float settingsW = settingsSize * ratioW;
        float settingsH = settingsSize * ratioH;
        settingsRect = newRectangle(GameState.screenW - settingsW - padding * ratioW, GameState.screenH - settingsH - padding * ratioH, settingsW, settingsH);

        levelBarX = GameState.screenW / 2f - levelBarW / 2;
        levelBarY = GameState.screenH - padding - levelBarH;

        scoreBarX = GameState.screenW / 2f - scoreBarW / 2;
        scoreBarY = levelBarY - margin - scoreBarH;

        nameTextX = GameState.screenW / 2f - (float) MeasureText(nameText, nameTextFont) / 2;
        nameTextY = scoreBarY - margin - nameTextFont;

        startY = GameState.screenH - menuH - 50;

        miniMapX = GameState.screenW - miniMapW - padding * ratioW;
        miniMapY = padding * ratioH;
    }

    // Update menu layout based on screen radius
    public static void updateMenuLayout() {
        GameState.screenW = GetScreenWidth();
        GameState.screenH = GetScreenHeight();

        float ratioW = GameState.screenW / (float) GameState.DEFAULT_SCREEN_W;
        float ratioH = GameState.screenH / (float) GameState.DEFAULT_SCREEN_H;

        backgroundRect = newRectangle(0, 0, GameState.screenW, GameState.screenH);

        float logoW = 950 * ratioW;
        float logoH = 375 * ratioH;
        logoRect = newRectangle(GameState.screenW / 2f - logoW / 2, 125 * ratioH, logoW, logoH);

        float playW = 900 * ratioW;
        float playH = 360 * ratioH;
        playRect = newRectangle(GameState.screenW / 2f - playW / 2, GameState.screenH / 2f, playW, playH);

        float credW = 300f * ratioW;
        float credH = 120f * ratioH;
        creditsRect = newRectangle(padding * ratioW, GameState.screenH - credH - padding * ratioH, credW, credH);

        exitRect = newRectangle(GameState.screenW - credW - padding * ratioW, GameState.screenH - credH - padding * ratioH, credW, credH);
    }

    // Draw a texture with scaling applied to a destination rectangle
    public static void drawScaled(Texture tex, Rectangle dest, Color color) {
        Rectangle source = newRectangle(0, 0, tex.width(), tex.height());
        DrawTexturePro(tex, source, dest, new Vector2().x(0).y(0), 0f, color);
    }

    // Draw a button with a hover effect
    public static void drawButton(Texture tex, Rectangle rect, boolean hover, boolean show) {
        drawScaled(tex, rect, hover && !show ? hovered : WHITE);
    }

    // Gamescreen drawing methods

    public static void drawWorld() {

        float drawLeft = Math.max(0, camLeft);
        float drawTop = Math.max(0, camTop);
        float drawRight = Math.min(EntityManager.worldW, camRight);
        float drawBottom = Math.min(EntityManager.worldH, camBottom);

        // Draw border
        DrawRectangleV(new Vector2().x(camLeft).y(camTop), new Vector2().x(camRight - camLeft).y(camBottom - camTop), borderGridColour);

        // Draw world background
        DrawRectangleV(new Vector2().x(drawLeft).y(drawTop), new Vector2().x(drawRight - drawLeft).y(drawBottom - drawTop), worldGridColour);

        // Vertical grid lines
        int startX = (int) Math.floor(camLeft / tileSize) * tileSize;
        int endX = (int)Math.ceil(camRight / tileSize) * tileSize;

        for (int x = startX; x <= endX; x += tileSize) {
            if (x < drawLeft || x > drawRight) {
                DrawLineV(new Vector2().x(x).y(camTop), new Vector2().x(x).y(camBottom), borderGridLineColour);
            } else {
                DrawLineV(new Vector2().x(x).y(camTop), new Vector2().x(x).y(drawTop), borderGridLineColour);
                DrawLineV(new Vector2().x(x).y(drawTop), new Vector2().x(x).y(drawBottom), worldGridLineColour);
                DrawLineV(new Vector2().x(x).y(drawBottom), new Vector2().x(x).y(camBottom), borderGridLineColour);
            }
        }

        // Horizontal grid liens
        int startY = (int) Math.floor(camTop / tileSize) * tileSize;
        int endY = (int)Math.ceil(camBottom / tileSize) * tileSize;

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

    public static void drawEntities() {
        // Shapes
        for (Shape s : EntityManager.shapes) {
            // Camera culling
            if (s.getCenterX() + s.getRadius() > camLeft && s.getCenterX() - s.getRadius() < camRight && s.getCenterY() + s.getRadius() > camTop && s.getCenterY() - s.getRadius() < camBottom) {
                s.draw();
            }
        }

        // Bullets
        for (Bullet b : EntityManager.bullets) {
            // Camera culling
            if (b.getCenterX() + b.getRadius() > camLeft && b.getCenterX() - b.getRadius() < camRight && b.getCenterY() + b.getRadius() > camTop && b.getCenterY() - b.getRadius() < camBottom) {
                b.draw();
            }
        }

        // Player tank
        if (!EntityManager.deathScreen) {
            EntityManager.playerTank.draw();
        }
    }

    public static void drawLevelBar() {
        // Level bar
        DrawRectangleRounded(newRectangle(levelBarX, levelBarY, levelBarW, levelBarH), 0.8f, 20, newColor(0, 0, 0, 200));
        float progress = EntityManager.playerTank.getLevelProgress();
        DrawRectangleRounded(newRectangle(levelBarX, levelBarY, levelBarW * progress, levelBarH), 0.8f, 20, newColor(255, 222, 67, 230));
        String levelText = "Level " + EntityManager.playerTank.getLevel();
        DrawText(levelText, (int) (levelBarX + levelBarW / 2) - MeasureText(levelText, levelTextFont) / 2, (int) (levelBarY + levelBarH / 2) - levelTextFont / 2, levelTextFont, WHITE);

        // Score bar
        DrawRectangleRounded(newRectangle(scoreBarX, scoreBarY, scoreBarW, scoreBarH), 0.8f, 20, newColor(67, 255, 145, 255));
        String scoreText = "Score: " + EntityManager.playerTank.getScore();
        DrawText(scoreText, (int) (scoreBarX + scoreBarW / 2) - MeasureText(scoreText, scoreTextFont) / 2, (int) (scoreBarY + scoreBarH / 2) - scoreTextFont / 2, scoreTextFont, BLACK);

        // Name
        DrawText(nameText, (int) nameTextX, (int) nameTextY, nameTextFont, WHITE);
    }

    public static void drawUpgradeMenu() {
        if (EntityManager.deathScreen) return;

        // Animated X position
        float x = hiddenX + (padding - hiddenX) * upgradeMenuAnim;

        // Locked state visuals
        boolean locked = false;
        if (EntityManager.playerTank.getSkillPoints() <= 0) {
            locked = true;
            EntityManager.playerTank.setUpgradeSkill(false);

        }
        Color bgColor = locked ? newColor(0, 0, 0, 210) : newColor(0, 0, 0, 150);
        Color skillTextColor = locked ? LIGHTGRAY : WHITE;
        Color statTextColor = locked ? GRAY : WHITE;

        // Skill points indicator
        DrawText("x" + EntityManager.playerTank.getSkillPoints(), (int) skillTextX, (int) startY - 30, 25, skillTextColor);

        for (int i = 0; i < statNames.length; i++) {
            float y = startY + i * (upgradeItemHeight + 5);
            Rectangle rect = newRectangle(x, y, upgradeMenuWidth, upgradeItemHeight);

            // Draw background
            DrawRectangleRounded(rect, 0.4f, 20, bgColor);

            // Stat level
            int statLevel = EntityManager.playerTank.getStats()[i];
            // Gap between segments
            float segmentGap = 2;
            // Width of each segment
            float segmentWidth = (upgradeMenuWidth - 10 - (7 * segmentGap)) / 7f;

            // Draw stat level segments
            for (int j = 0; j < 7; j++) {
                float segX = x + 5 + j * (segmentWidth + segmentGap);
                Rectangle segRect = newRectangle(segX, y + 18, segmentWidth, 4);
                if (j < statLevel) {
                    Color barColor = statColors[i];
                    if (locked) {
                        barColor = newColor(barColor.r(), barColor.g(), barColor.b(), 100);
                    }
                    DrawRectangleRounded(segRect, 1f, 20, barColor);
                } else {
                    DrawRectangleRounded(segRect, 1f, 20, newColor(50, 50, 50, 255));
                }
            }

            // Draw the stat name
            DrawText(statNames[i], (int) x + 10, (int) y  + 3, 12, statTextColor);

            // Draw the hotkey number
            DrawText(String.valueOf(i + 1), (int) (x + upgradeMenuWidth - padding), (int) y + 3, 12, locked ? DARKGRAY : GRAY);

            // Draw highlight if hovered
            if (showSettings) continue;

            if (CheckCollisionPointRec(GetMousePosition(), rect)) {
                DrawRectangleRoundedLines(rect, 0.4f, 20, locked ? GRAY : WHITE);
            }
        }
    }

    public static void drawSettings() {
        drawButton(GameScreen.settings, settingsRect, settingsHover, showSettings);
    }

    public static void drawSettingsMenu() {
        DrawRectangle(0, 0, GameState.screenW, GameState.screenH, newColor(0, 0, 0, 180));
        int boxW = 1000, boxH = 600, boxX = (GameState.screenW - boxW) / 2, boxY = (GameState.screenH - boxH) / 2;

        Rectangle rect = newRectangle(boxX, boxY, boxW, boxH);

        DrawRectangleRounded(rect, 0.2f, 10, RAYWHITE);
        DrawRectangleRoundedLines(rect, 0.2f, 10, DARKGRAY);

        DrawText("Settings", boxX + boxW / 2 - MeasureText("Settings", 50) / 2, boxY + 20, 50, BLACK);
        DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 20) / 2,  boxY + 165, 20, BLACK);
    }

    public static void drawMiniMap() {
        Rectangle rect = newRectangle(miniMapX, miniMapY, miniMapW, miniMapH);
        DrawRectangleRounded(rect, 0.05f, 10, miniMapColour);
        DrawRectangleRoundedLines(rect, 0.05f, 10, miniMapBorderColour);

        float x = EntityManager.playerTank.getCenterX() / EntityManager.worldW;
        float y = EntityManager.playerTank.getCenterY() / EntityManager.worldH;

        DrawCircleV(new Vector2().x(miniMapX + miniMapW * x).y(miniMapY + miniMapH * y), 5, RED);
    }

    public static void drawDeathScreen() {
        DrawRectangle(0, 0, GameState.screenW, GameState.screenH, newColor(0, 0, 0, 75));
        DrawText("You DIED!", GetScreenWidth() / 2 - MeasureText("You DIED!", 40) / 2, GetScreenHeight() / 2 - 40, 40, RED);
        DrawText("Press R to respawn!", GetScreenWidth() / 2 - MeasureText("Press R to respawn!", 40) / 2, GetScreenHeight() / 2 + 10, 40, WHITE);
        DrawText("Press ESC to return to the main menu", GetScreenWidth() / 2 - MeasureText("Press ESC to return to the main menu", 20) / 2, GetScreenHeight() / 2 + 60, 20, WHITE);
        DrawText("Statistics", GetScreenWidth() / 2 - MeasureText("Statistics", 30) / 2, GetScreenHeight() - 300, 30, WHITE);
    }

    // Menu-screen drawing methods

    public static void drawCredits() {
        // Dark background (not fully opaque)
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
