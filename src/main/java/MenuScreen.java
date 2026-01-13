import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;


public class MenuScreen extends GameState {
    
    // Textures
    public Texture background, logo, playBtn, creditsBtn, exitBtn;

    public Rectangle backgroundRect, logoRect, playRect, creditsRect, exitRect;

    // Booleans
    public boolean showCredits = false, playHover = true, creditsHover = true, exitHover = true;

    private ScreenType requestedScreen = ScreenType.MENU;

    public MenuScreen() {
        // Load Textures
        background = LoadTexture("resources/menu/menubackgroundblur.png");
        logo = LoadTexture("resources/menu/logo.png");
        playBtn = LoadTexture("resources/menu/play.png");
        creditsBtn = LoadTexture("resources/menu/credits.png");
        exitBtn = LoadTexture("resources/menu/exit.png");

        updateLayout();
    }

    @Override
    public void update() {

        if (IsWindowResized()) updateLayout();

        Vector2 mouse = GetMousePosition();
        
        if (isHover(playRect, mouse)) {
            playHover = true;
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && !showCredits) requestedScreen = ScreenType.GAME;
        } else if (isHover(creditsRect, mouse)) {
            creditsHover = true;
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && !showCredits) {
                showCredits = true;
            }
        } else if (isHover(exitRect, mouse))  {
            exitHover= true;
            if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && !showCredits) requestedScreen = ScreenType.EXIT;
        } else {
            playHover = false;
            creditsHover = false;
            exitHover = false;
        }

        if (IsKeyPressed(KEY_SPACE)) {
            showCredits = false;
        }
    }

    @Override
    public void draw() {
        drawScaled(background, backgroundRect, WHITE);
        drawScaled(logo, logoRect, WHITE);
        drawButton(playBtn, playRect, playHover, showCredits);
        drawButton(creditsBtn, creditsRect, creditsHover, showCredits);
        drawButton(exitBtn, exitRect, exitHover, showCredits);

        if (showCredits) drawCredits();
    }

    @Override
    public void unload() {
        UnloadTexture(logo);
        UnloadTexture(background);
        UnloadTexture(playBtn);
        UnloadTexture(creditsBtn);
        UnloadTexture(exitBtn);
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

        backgroundRect = newRectangle(0, 0, screenW, screenH);

        float logoW = 625 * ratioW;
        float logoH = 250 * ratioH;
        logoRect = newRectangle(screenW / 2f - logoW / 2, 75 * ratioH, logoW, logoH);

        float playW = 600f * ratioW;
        float playH = 240f * ratioH;
        playRect = newRectangle(screenW / 2f - playW / 2, screenH / 2f, playW, playH);

        float credW = 200f * ratioW;
        float credH = 80f * ratioH;
        creditsRect = newRectangle(10 * ratioW, screenH - credH - 10 * ratioH, credW, credH);

        float exitW = credW;
        float exitH = credH;
        exitRect = newRectangle(screenW - exitW - 10 * ratioW, screenH - exitH - 10 * ratioH, exitW, exitH);
    }

    
    void drawCredits() {
        DrawRectangle(0, 0, screenW, screenH, newColor(0, 0, 0, 180));

        int boxW = 400;
        int boxH = 200;
        int boxX = (screenW - boxW) / 2;
        int boxY = (screenH - boxH) / 2;

        DrawRectangleRounded(newRectangle(boxX, boxY, boxW, boxH), 0.2f, 10, RAYWHITE);
        DrawRectangleRoundedLines(newRectangle(boxX, boxY, boxW, boxH), 0.2f, 10, DARKGRAY);

        DrawText("Credits", boxX + boxW / 2 - MeasureText("Credits", 30) / 2, boxY + 20, 30, BLACK);
        DrawText("Made by:", boxX + 40, boxY + 60, 20, BLACK);
        DrawText("Jonathan Yu", boxX + 40, boxY + 95, 20, BLACK);
        DrawText("Cheney Chen", boxX + 40, boxY + 130, 20, BLACK);
        DrawText("Press SPACE to close", boxX + boxW / 2 - MeasureText("Press SPACE to close", 14) / 2,  boxY + 165, 14, BLACK);
    }
}