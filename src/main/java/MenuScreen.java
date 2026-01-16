import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class MenuScreen extends GameState {

    // Textuers
    private final Texture background;
    private final Texture logo;
    private final Texture playBtn;
    private final Texture creditsBtn;
    private final Texture exitBtn;

    // Layout rectangles
    private Rectangle backgroundRect;
    private Rectangle logoRect;
    private Rectangle playRect;
    private Rectangle creditsRect;
    private Rectangle exitRect;

    // UI States
    private boolean showCredits = false;
    private boolean playHover = false;
    private boolean creditsHover = false;
    private boolean exitHover = false;

    // Set the requested screen type as MENU
    private ScreenType requestedScreen = ScreenType.MENU;

    // Constructor
    public MenuScreen() {
        // Load menu assets from the resources folder
        background = LoadTexture("resources/menu/menubackgroundblur.png");
        logo = LoadTexture("resources/menu/logo.png");
        playBtn = LoadTexture("resources/menu/play.png");
        creditsBtn = LoadTexture("resources/menu/credits.png");
        exitBtn = LoadTexture("resources/menu/exit.png");

        // Initial layout setup
        updateLayout();
    }

    // Update logic
    @Override
    public void update() {
        // Mouse handling
        Vector2 mouse = GetMousePosition();
        playHover = isHover(playRect, mouse);
        creditsHover = isHover(creditsRect, mouse);
        exitHover = isHover(exitRect, mouse);

        // Handle mouse clicks (only when credits are NOT open)
        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && !showCredits) {
            if (playHover) {
                requestedScreen = ScreenType.GAME;
            } else if (creditsHover) {
                showCredits = true;
            } else if (exitHover) {
                requestedScreen = ScreenType.EXIT;
            }
        }

        // Close credits with space
        if (IsKeyPressed(KEY_SPACE)) {
            showCredits = false;
        }
    }

    // Drawing
    @Override
    public void draw() {
        // Draw background and logo
        Graphics.drawScaled(background, backgroundRect, WHITE);
        Graphics.drawScaled(logo, logoRect, WHITE);

        // Draw menu buttons
        Graphics.drawButton(playBtn, playRect, playHover, showCredits);
        Graphics.drawButton(creditsBtn, creditsRect, creditsHover, showCredits);
        Graphics.drawButton(exitBtn, exitRect, exitHover, showCredits);

        // Draw credits if active
        if (showCredits) {
            Graphics.drawCredits();
        }
    }

    // Unload resources
    @Override
    public void unload() {
        UnloadTexture(logo);
        UnloadTexture(background);
        UnloadTexture(playBtn);
        UnloadTexture(creditsBtn);
        UnloadTexture(exitBtn);
    }

    // Screen switching
    @Override
    public ScreenType getRequestedScreen() {
        return requestedScreen;
    }

    // Layout scaling
    private void updateLayout() {
        // Current window size
        screenW = GetScreenWidth();
        screenH = GetScreenHeight();

        // Scale ratio relative to default resolution
        float ratioW = screenW / (float) DEFAULT_SCREEN_W;
        float ratioH = screenH / (float) DEFAULT_SCREEN_H;

        // Background fills the entire scren
        backgroundRect = newRectangle(0, 0, screenW, screenH);

        // Logo
        float logoW = 950 * ratioW;
        float logoH = 375 * ratioH;
        logoRect = newRectangle(screenW / 2f - logoW / 2, 125 * ratioH, logoW, logoH);

        // Play button (centered)
        float playW = 900 * ratioW;
        float playH = 360 * ratioH;
        playRect = newRectangle(screenW / 2f - playW / 2, screenH / 2f, playW, playH);

        // Credits button (bottom left)
        float credW = 300f * ratioW;
        float credH = 120f * ratioH;
        creditsRect = newRectangle(15 * ratioW, screenH - credH - 15 * ratioH, credW, credH);

        // Exit button (bottom right)
        exitRect = newRectangle(screenW - credW - 15 * ratioW, screenH - credH - 15 * ratioH, credW, credH);
    }

}
