package com.tanxx.core;

import com.tanxx.screens.GameScreen;
import com.tanxx.screens.GameState;
import com.tanxx.screens.MenuScreen;
import com.tanxx.screens.ScreenType;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Main {
    public static final int DEFAULT_SCREEN_W = 1920;
    public static final int DEFAULT_SCREEN_H = 1080;
    public static ScreenType currentScreenType = ScreenType.MENU;
    public static GameState currentScreen;

    public static void main(String[] args) {
        SetConfigFlags(FLAG_WINDOW_RESIZABLE);
        InitWindow(DEFAULT_SCREEN_W, DEFAULT_SCREEN_H, "TanXX");
        MaximizeWindow();
        SetWindowMinSize(1280, 720);
        SetTargetFPS(60);

        Image icon = LoadImage("resources/menu/icon.png");
        SetWindowIcon(icon);
        UnloadImage(icon);

        currentScreen = new MenuScreen();

        while (!WindowShouldClose() && currentScreenType != ScreenType.EXIT) {
            currentScreen.update();

            ScreenType requested = currentScreen.getRequestedScreen();
            if (requested != currentScreenType) {
                currentScreen.unload();
                switch (requested) {
                    case MENU -> currentScreen = new MenuScreen();
                    case GAME -> currentScreen = new GameScreen();
                    case EXIT -> {}
                }
                currentScreenType = requested;
            }

            BeginDrawing();
            ClearBackground(RAYWHITE);
            currentScreen.draw();
            EndDrawing();
        }

        currentScreen.unload();
        CloseWindow();
    }
}
