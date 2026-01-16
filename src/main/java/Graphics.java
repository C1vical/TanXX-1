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

    // Settings UI properties
    public static int settingsSize = 75;
    public static Rectangle settingsRect;
    public static boolean settingsHover = false;
    public static boolean showSettings = false;

    // Level bar properties
    public static float levelBarW = 350;
    public static float levelBarH = 25;
    public static float padding = 20;
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

    // Upgrade Menu configuration
    public static final String[] statNames = {
            "Health Regen", "Max Health", "Body Damage", "Bullet Speed",
            "Bullet Penetration", "Bullet Damage", "Reload Speed", "Movement Speed"
    };
    public static final Color[] statColors = {
            newColor(255, 128, 255, 255), // Pink
            newColor(128, 255, 128, 255), // Green
            newColor(128, 255, 255, 255), // Cyan
            newColor(255, 255, 128, 255), // Yellow
            newColor(128, 128, 255, 255), // Blue
            newColor(255, 128, 128, 255), // Red
            newColor(255, 179, 128, 255), // Orange
            newColor(230, 230, 230, 255)  // Gray
    };
    public static final float upgradeMenuWidth = 200;
    public static final float upgradeItemHeight = 25;
    public static final float menuH = statNames.length * (upgradeItemHeight + 5);
    public static float startY;
    public static final float upgradeMenuPadding = 10;
    public static float upgradeMenuAnim = 0f; // 0 = collapsed, 1 = expanded
    public static float upgradeMenuTimer = 0f; // Time to keep the menu open after keypress

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
    public static float zoomLevel = 1.0f;

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

    public static void updateGameLayout() {
        GameState.screenW = GetScreenWidth();
        GameState.screenH = GetScreenHeight();

        float ratioW = GameState.screenW / (float) GameState.DEFAULT_SCREEN_W;
        float ratioH = GameState.screenH / (float) GameState.DEFAULT_SCREEN_H;

        float settingsW = settingsSize * ratioW;
        float settingsH = settingsSize * ratioH;
        settingsRect = newRectangle(GameState.screenW - settingsW - 15 * ratioW, 15 * ratioH, settingsW, settingsH);

        levelBarX = GameState.screenW / 2f - levelBarW / 2;
        levelBarY = GameState.screenH - padding - levelBarH;

        scoreBarX = GameState.screenW / 2f - scoreBarW / 2;
        scoreBarY = levelBarY - margin - scoreBarH;

        nameTextX = GameState.screenW / 2f - (float) MeasureText(nameText, nameTextFont) / 2;
        nameTextY = scoreBarY - margin - nameTextFont;

        startY = GameState.screenH - padding - menuH - 50;
    }

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
        creditsRect = newRectangle(15 * ratioW, GameState.screenH - credH - 15 * ratioH, credW, credH);

        exitRect = newRectangle(GameState.screenW - credW - 15 * ratioW, GameState.screenH - credH - 15 * ratioH, credW, credH);
    }

    // Draw a texture with scaling applied to a destination rectangle
    public static void drawScaled(Texture tex, Rectangle dest, Color color) {
        Rectangle source = newRectangle(0, 0, tex.width(), tex.height());
        DrawTexturePro(tex, source, dest, new Vector2().x(0).y(0), 0f, color);
    }

    // Draw a button with hover effect
    public static void drawButton(Texture tex, Rectangle rect, boolean hover, boolean show) {
        drawScaled(tex, rect, hover && !show ? hovered : WHITE);
    }

    // --- GameScreen Drawing Methods ---

    public static void drawWorld() {
        // Draw vertical border grid lines
        for (int x = - EntityManager.borderSize; x <= EntityManager.worldW + EntityManager.borderSize; x += tileSize) {
            if (x >= camLeft && x <= camRight) DrawLine(x, -EntityManager.borderSize, x, EntityManager.worldH + EntityManager.borderSize, borderGridLineColour);
        }

        // Draw horizontal border grid lines
        for (int y = - EntityManager.borderSize; y <= EntityManager.worldH + EntityManager.borderSize; y += tileSize) {
            if (y >= camTop && y <= camBottom) DrawLine(-EntityManager.borderSize, y, EntityManager.worldW + EntityManager.borderSize, y, borderGridLineColour);
        }

        // Draw world background
        DrawRectangle(0, 0, EntityManager.worldW, EntityManager.worldH, worldGridColour);

        // Draw vertical grid lines inside the world
        for (int x = 0; x <= EntityManager.worldW; x += tileSize) {
            if (x >= camLeft && x <= camRight) DrawLine(x, 0, x, EntityManager.worldH, worldGridLineColour);
        }

        // Draw horizontal grid lines inside the world
        for (int y = 0; y <= EntityManager.worldH; y += tileSize) {
            if (y >= camTop && y <= camBottom) DrawLine(0, y, EntityManager.worldW, y, worldGridLineColour);
        }
    }

    public static void drawEntities() {
        // Shapes
        for (Shape s : EntityManager.shapes) {
            // Camera culling
            if (s.getCenterX() + s.getSize() > camLeft && s.getCenterX() - s.getSize() < camRight && s.getCenterY() + s.getSize() > camTop && s.getCenterY() - s.getSize() < camBottom) {
                s.draw();
            }
        }

        // Bullets
        for (Bullet b : EntityManager.bullets) {
            // Camera culling
            if (b.getCenterX() + b.getSize() > camLeft && b.getCenterX() - b.getSize() < camRight && b.getCenterY() + b.getSize() > camTop && b.getCenterY() - b.getSize() < camBottom) {
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
        DrawRectangleRounded(newRectangle(levelBarX, levelBarY, levelBarW * progress, levelBarH), 0.8f, 20, newColor(255, 215, 0, 230));
        String levelText = "Level " + EntityManager.playerTank.getLevel();
        DrawText(levelText, (int) (levelBarX + levelBarW / 2) - MeasureText(levelText, levelTextFont) / 2, (int) (levelBarY + levelBarH / 2) - levelTextFont / 2, levelTextFont, WHITE);

        // Score bar
        DrawRectangleRounded(newRectangle(scoreBarX, scoreBarY, scoreBarW, scoreBarH), 0.8f, 20, newColor(48, 240, 141, 255));
        String scoreText = "Score: " + EntityManager.playerTank.getScore();
        DrawText(scoreText, (int) (scoreBarX + scoreBarW / 2) - MeasureText(scoreText, scoreTextFont) / 2, (int) (scoreBarY + scoreBarH / 2) - scoreTextFont / 2, scoreTextFont, BLUE);

        // Name
        DrawText(nameText, (int) nameTextX, (int) nameTextY, nameTextFont, WHITE);
    }

    public static void drawUpgradeMenu() {
        if (EntityManager.deathScreen) return;

        // Animated X position
        float hiddenX = -upgradeMenuWidth - 5;
        float targetX = padding;
        float x = hiddenX + (targetX - hiddenX) * upgradeMenuAnim;

        // Locked state visuals
        boolean locked = EntityManager.playerTank.getSkillPoints() <= 0;
        Color bgColor = locked ? newColor(0, 0, 0, 210) : newColor(0, 0, 0, 150);
        Color skillTextColor = locked ? LIGHTGRAY : WHITE;
        Color statTextColor = locked ? GRAY : WHITE;

        // Skill points indicator (stays partially visible to signal available upgrades)
        float skillTextX = Math.max(x, 15);
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
            float segmentWidth = (upgradeMenuWidth - 10 - (7 * segmentGap)) / 8f;

            // Draw stat level segments
            for (int j = 0; j < 8; j++) {
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
            DrawText(String.valueOf(i + 1), (int) (x + upgradeMenuWidth - 15), (int) y + 3, 12, locked ? DARKGRAY : GRAY);

            // Draw highlight if hovered
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

    public static void drawDeathScreen() {
        DrawRectangle(0, 0, GameState.screenW, GameState.screenH, newColor(0, 0, 0, 75));
        DrawText("You DIED!", GetScreenWidth() / 2 - MeasureText("You DIED!", 40) / 2, GetScreenHeight() / 2 - 40, 40, RED);
        DrawText("Press R to respawn!", GetScreenWidth() / 2 - MeasureText("Press R to respawn!", 40) / 2, GetScreenHeight() / 2 + 10, 40, WHITE);
    }

    // --- MenuScreen Drawing Methods ---

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
