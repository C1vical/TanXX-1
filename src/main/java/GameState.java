import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public abstract class GameState {
    // Default scren dimensions
    public static final int DEFAULT_SCREEN_W = 1920;
    public static final int DEFAULT_SCREEN_H = 1080;

    // current screen dimensions
    public static int screenW;
    public static int screenH;

    // Abstract methods that every GameState subclass must implement
    public abstract void update();                      // Update game logic
    public abstract void draw();                        // Draw game visuals
    public abstract void unload();                      // Unload textures or resources
    public abstract ScreenType getRequestedScreen();    // Return the screen type requested (MENU, GAME, EXIT, etc.)

    // Check if the mouse is hovering over a rectangle
    public boolean isHover(Rectangle rect, Vector2 mouse) {
        return CheckCollisionPointRec(mouse, rect);
    }
}
