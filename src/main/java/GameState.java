import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

abstract class GameState {

    public static final int defaultScreenW = 1280;
    public static final int defaultScreenH = 720;

    public static int screenW;
    public static int screenH;

    public Color hovered = newColor(180, 180, 180, 255);
    
    public abstract void update();
    public abstract void draw();
    public abstract void unload();
    public abstract ScreenType getRequestedScreen();

    public boolean isHover(Rectangle rect, Vector2 mouse) {
        return CheckCollisionPointRec(mouse, rect);
    }

    public void drawScaled(Texture tex, Rectangle dest, Color color) {
        Rectangle source = newRectangle(0, 0, tex.width(), tex.height());
        DrawTexturePro(tex, source, dest, new Vector2().x(0).y(0), 0f, color);
    }

    public void drawButton(Texture tex, Rectangle rect, boolean hover, boolean show) {
        drawScaled(tex, rect, hover && !show ? hovered : WHITE);
    }
}