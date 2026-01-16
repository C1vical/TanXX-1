import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class MenuScreen extends GameState {

    // Textures
    private final Texture background;
    private final Texture logo;
    private final Texture playBtn;
    private final Texture creditsBtn;
    private final Texture exitBtn;

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
        Graphics.updateMenuLayout();
    }

    // Update logic
    @Override
    public void update() {
        // Mouse handling
        Vector2 mouse = GetMousePosition();
        Graphics.playHover = isHover(Graphics.playRect, mouse);
        Graphics.creditsHover = isHover(Graphics.creditsRect, mouse);
        Graphics.exitHover = isHover(Graphics.exitRect, mouse);

        // Handle mouse clicks (only when credits are NOT open)
        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) && !Graphics.showCredits) {
            if (Graphics.playHover) {
                requestedScreen = ScreenType.GAME;
            } else if (Graphics.creditsHover) {
                Graphics.showCredits = true;
            } else if (Graphics.exitHover) {
                requestedScreen = ScreenType.EXIT;
            }
        }

        // Close credits with space
        if (IsKeyPressed(KEY_SPACE)) {
            Graphics.showCredits = false;
        }
    }

    // Drawing
    @Override
    public void draw() {
        // Draw background and logo
        Graphics.drawScaled(background, Graphics.backgroundRect, WHITE);
        Graphics.drawScaled(logo, Graphics.logoRect, WHITE);

        // Draw menu buttons
        Graphics.drawButton(playBtn, Graphics.playRect, Graphics.playHover, Graphics.showCredits);
        Graphics.drawButton(creditsBtn, Graphics.creditsRect, Graphics.creditsHover, Graphics.showCredits);
        Graphics.drawButton(exitBtn, Graphics.exitRect, Graphics.exitHover, Graphics.showCredits);

        // Draw credits if active
        if (Graphics.showCredits) {
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
}
