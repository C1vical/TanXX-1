import static com.raylib.Raylib.*;

public class Bullet extends Sprite {
    // Lifetime in frames
    private float lifeTime;

    public Bullet(float centerX, float centerY, float size, float angle, float speed, Texture texture, Color color, Color stroke) {
        super(centerX, centerY, size, angle, speed, texture, color, stroke);
        lifeTime = 2f;
        alive = true;
    }

    public void update() {
        // Move bullet in the direction of its angle
        centerX += (float) (Math.cos(angle) * speed * GameScreen.dt);
        centerY += (float) (Math.sin(angle) * speed * GameScreen.dt);

        // Reduce lifetime
        lifeTime -= GameScreen.dt;
        if (lifeTime <= 0f) alive = false;
    }

    public void draw() {
        DrawCircleV(new Vector2().x(centerX).y(centerY), size / 2 + strokeWidth, stroke);
        DrawCircleV(new Vector2().x(centerX).y(centerY), size / 2, color);
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2 + strokeWidth, hitboxColor);
    }
}