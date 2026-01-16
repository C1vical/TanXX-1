import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import java.util.Iterator;

public class Graphics {

    // Color when a button is hovered
    public static final Color hovered = newColor(180, 180, 180, 255);

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

    public static void drawWorld(GameScreen gs) {
        // Draw vertical border grid lines
        for (int x = - GameScreen.borderSize; x <= GameScreen.worldW + GameScreen.borderSize; x += gs.tileSize) {
            if (x >= gs.camLeft && x <= gs.camRight) DrawLine(x, -GameScreen.borderSize, x, GameScreen.worldH + GameScreen.borderSize, gs.borderGridLineColour);
        }

        // Draw horizontal border grid lines
        for (int y = - GameScreen.borderSize; y <= GameScreen.worldH + GameScreen.borderSize; y += gs.tileSize) {
            if (y >= gs.camTop && y <= gs.camBottom) DrawLine(-GameScreen.borderSize, y, GameScreen.worldW + GameScreen.borderSize, y, gs.borderGridLineColour);
        }

        // Draw world background
        DrawRectangle(0, 0, GameScreen.worldW, GameScreen.worldH, gs.worldGridColour);

        // Draw vertical grid lines inside the world
        for (int x = 0; x <= GameScreen.worldW; x += gs.tileSize) {
            if (x >= gs.camLeft && x <= gs.camRight) DrawLine(x, 0, x, GameScreen.worldH, gs.worldGridLineColour);
        }

        // Draw horizontal grid lines inside the world
        for (int y = 0; y <= GameScreen.worldH; y += gs.tileSize) {
            if (y >= gs.camTop && y <= gs.camBottom) DrawLine(0, y, GameScreen.worldW, y, gs.worldGridLineColour);
        }
    }

    public static void drawEntities(GameScreen gs) {
        // Shapes
        for (Shape s : gs.shapes) {
            // Camera culling
            if (s.getCenterX() + s.getSize() > gs.camLeft && s.getCenterX() - s.getSize() < gs.camRight && s.getCenterY() + s.getSize() > gs.camTop && s.getCenterY() - s.getSize() < gs.camBottom) {
                s.draw();
            }
        }

        // Bullets
        Iterator<Bullet> it = gs.bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();

            // Remove dead bullets
            if (!b.isAlive()) {
                it.remove();
                continue;
            }

            // Camera culling
            if (b.getCenterX() + b.getSize() > gs.camLeft && b.getCenterX() - b.getSize() < gs.camRight && b.getCenterY() + b.getSize() > gs.camTop && b.getCenterY() - b.getSize() < gs.camBottom) {
                b.draw();
            }
        }

        // Player tank
        if (!GameScreen.deathScreen) {
            GameScreen.playerTank.draw();
        }
    }

    public static void drawLevelBar(GameScreen gs) {
        // Level bar
        DrawRectangleRounded(newRectangle(gs.levelBarX, gs.levelBarY, gs.levelBarW, gs.levelBarH), 0.8f, 20, newColor(0, 0, 0, 200));
        float progress = GameScreen.playerTank.getLevelProgress();
        DrawRectangleRounded(newRectangle(gs.levelBarX, gs.levelBarY, gs.levelBarW * progress, gs.levelBarH), 0.8f, 20, newColor(255, 215, 0, 230));
        String levelText = "Level " + GameScreen.playerTank.getLevel();
        DrawText(levelText, (int) (gs.levelBarX + gs.levelBarW / 2) - MeasureText(levelText, gs.levelTextFont) / 2, (int) (gs.levelBarY + gs.levelBarH / 2) - gs.levelTextFont / 2, gs.levelTextFont, WHITE);

        // Score bar
        DrawRectangleRounded(newRectangle(gs.scoreBarX, gs.scoreBarY, gs.scoreBarW, gs.scoreBarH), 0.8f, 20, newColor(48, 240, 141, 255));
        String scoreText = "Score: " + GameScreen.playerTank.getScore();
        DrawText(scoreText, (int) (gs.scoreBarX + gs.scoreBarW / 2) - MeasureText(scoreText, gs.scoreTextFont) / 2, (int) (gs.scoreBarY + gs.scoreBarH / 2) - gs.scoreTextFont / 2, gs.scoreTextFont, BLUE);

        // Name
        DrawText(gs.nameText, (int) gs.nameTextX, (int) gs.nameTextY, gs.nameTextFont, WHITE);
    }

    public static void drawUI(GameScreen gs) {
        // Settings button
        drawButton(gs.settings, gs.settingsRect, gs.settingsHover, GameScreen.showSettings);

        // FPS counter
        DrawFPS(10, 15);

        // Upgrade menu
        drawUpgradeMenu(gs);

        // Settings menu overlay
        if (GameScreen.showSettings) drawSettings();
    }

    public static void drawUpgradeMenu(GameScreen gs) {
        if (GameScreen.deathScreen) return;

        float menuH = gs.statNames.length * (gs.upgradeItemHeight + 5);
        float startY = GameState.screenH - gs.padding - menuH - 50;

        // Animated X position
        float hiddenX = -gs.upgradeMenuWidth - 5;
        float targetX = gs.padding;
        float x = hiddenX + (targetX - hiddenX) * gs.upgradeMenuAnim;

        // Locked state visuals
        boolean locked = GameScreen.playerTank.getSkillPoints() <= 0;
        Color bgColor = locked ? newColor(0, 0, 0, 210) : newColor(0, 0, 0, 150);
        Color skillTextColor = locked ? LIGHTGRAY : WHITE;
        Color statTextColor = locked ? GRAY : WHITE;

        // Skill points indicator (stays partially visible to signal available upgrades)
        float skillTextX = Math.max(x, 15);
        DrawText("x" + GameScreen.playerTank.getSkillPoints(), (int)skillTextX, (int)startY - 30, 25, skillTextColor);

        for (int i = 0; i < gs.statNames.length; i++) {
            float y = startY + i * (gs.upgradeItemHeight + 5);
            Rectangle rect = newRectangle(x, y, gs.upgradeMenuWidth, gs.upgradeItemHeight);

            // Draw background
            DrawRectangleRounded(rect, 0.4f, 20, bgColor);

            // Draw stat level segments
            int statLevel = GameScreen.playerTank.getStats()[i];
            float segmentGap = 2;
            float segmentWidth = (gs.upgradeMenuWidth - 10 - (7 * segmentGap)) / 8f;

            for (int j = 0; j < 8; j++) {
                float segX = x + 5 + j * (segmentWidth + segmentGap);
                Rectangle segRect = newRectangle(segX, y + 18, segmentWidth, 4);
                if (j < statLevel) {
                    Color barColor = gs.statColors[i];
                    if (locked) {
                        barColor = newColor(barColor.r() & 0xFF, barColor.g() & 0xFF, barColor.b() & 0xFF, 100);
                    }
                    DrawRectangleRounded(segRect, 1f, 20, barColor);
                } else {
                    DrawRectangleRounded(segRect, 1f, 20, newColor(50, 50, 50, 255));
                }
            }

            // Draw stat name
            DrawText(gs.statNames[i], (int)x + 10, (int)y + 3, 12, statTextColor);

            // Draw hotkey number
            DrawText(String.valueOf(i + 1), (int)(x + gs.upgradeMenuWidth - 15), (int)y + 3, 12, locked ? DARKGRAY : GRAY);

            // Draw highlight if hovered
            if (CheckCollisionPointRec(GetMousePosition(), rect)) {
                DrawRectangleRoundedLines(rect, 0.4f, 20, locked ? GRAY : WHITE);
            }
        }
    }

    public static void drawSettings() {
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
