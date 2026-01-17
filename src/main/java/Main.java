import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Main {
    // Default screen dimensions
    public static final int DEFAULT_SCREEN_W = 1920;
    public static final int DEFAULT_SCREEN_H = 1080;

    // Keeps track of which screen should currently be shown (Menu, Game, or Exit)
    public static ScreenType currentScreenType = ScreenType.MENU;

    // The active screen object (MenuScreen or GameScreen)
    public static GameState currentScreen;

    public static void main(String[] args) {
        // Allow window resizing
        SetConfigFlags(FLAG_WINDOW_RESIZABLE);

        // Initialize a window with dimensions 1920 x 1080 (default) and our game name TanXX,
        InitWindow(DEFAULT_SCREEN_W, DEFAULT_SCREEN_H, "TanXX");

        // Load and set the window icon
        Image icon = LoadImage("resources/menu/icon.png");
        SetWindowIcon(icon);
        UnloadImage(icon);

        // This makes sure the window is maximized
        MaximizeWindow();

        // Disable window resizing after creating the window
        ClearWindowState(FLAG_WINDOW_RESIZABLE);

        // Set the minimum window radius as 1280 x 720
        SetWindowMinSize(1280, 720);

        // Set the target fps of our game to 60
        SetTargetFPS(60);

        // Begin on the menu screen
        currentScreen = new MenuScreen();

        // Main game loop
        while (!WindowShouldClose() && currentScreenType != ScreenType.EXIT) {

            // Update current screen
            currentScreen.update();

            // Check if the screen wants to change
            ScreenType requested = currentScreen.getRequestedScreen();
            if (requested != currentScreenType) {

                // Unload the old screen before switching
                currentScreen.unload();

                // Switch to the new screen
                switch (requested) {
                    case MENU -> currentScreen = new MenuScreen();
                    case GAME -> currentScreen = new GameScreen();
                    case EXIT -> {}
                }

                currentScreenType = requested;
            }

            // Begin drawing for this frame
            BeginDrawing();
            ClearBackground(RAYWHITE);

            // Draw the current screen
            currentScreen.draw();
            EndDrawing();
        }

        // Unload the current screen before closing the window and exiting the program
        currentScreen.unload();
        CloseWindow();
    }
}
