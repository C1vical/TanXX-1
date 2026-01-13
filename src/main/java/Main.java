import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Main {

    public static final int defaultScreenW = 1280;
    public static final int defaultScreenH = 720;

    public static ScreenType currentScreen = ScreenType.MENU;
    public static GameState screen;

    public static void main(String[] args) {
        SetConfigFlags(FLAG_WINDOW_RESIZABLE);
        InitWindow(defaultScreenW, defaultScreenH, "TanXX");
        SetWindowMinSize(1280, 720);
        SetTargetFPS(60);

        Image icon = LoadImage("resources/menu/icon.png");
        SetWindowIcon(icon);
        UnloadImage(icon);

        screen = new MenuScreen();

        while (!WindowShouldClose() && currentScreen != ScreenType.EXIT) {
            
            screen.update();

            ScreenType requested = screen.getRequestedScreen();
            if (requested != currentScreen) {
                screen.unload();

                switch (requested) {
                    case MENU:
                        screen = new MenuScreen();
                        currentScreen = ScreenType.MENU;
                        break;
                    case GAME:
                        screen = new GameScreen();
                        currentScreen = ScreenType.GAME;
                        break;
                    case EXIT:
                        currentScreen = ScreenType.EXIT;
                        break; // loop will end
                }

                currentScreen = requested;
            }
            BeginDrawing();
            ClearBackground(RAYWHITE);
            screen.draw();
            EndDrawing();
        }

        screen.unload();
        CloseWindow();
    }
}