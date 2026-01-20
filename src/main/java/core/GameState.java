package core;

// GameState is an abstract base class for different screens (Menu, Game, etc.)
public abstract class GameState {
    // Default screen dimensions (used for scaling)
    public static final int DEFAULT_SCREEN_W = 1920;
    public static final int DEFAULT_SCREEN_H = 1080;

    // Current screen dimensions after window resizing
    public static int screenW;
    public static int screenH;

    // Abstract methods that every GameState subclass must implement
    public abstract void update();                      // Update game logic for the current screen
    public abstract void draw();                        // Draw visuals for the current screen
    public abstract void unload();                      // Free memory (textures, etc.) when switching screens
    public abstract ScreenType getRequestedScreen();    // Return the screen type requested (MENU, GAME, EXIT, etc.)
}
