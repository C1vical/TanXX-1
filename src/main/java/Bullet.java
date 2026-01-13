import static com.raylib.Raylib.*;

public class Bullet extends Sprite {
    // Lifetime in frames
    private float lifeTime;

    public Bullet(float worldX, float worldY, float size, float angle, float speed, Texture texture, Color color, Color stroke) {
        super(worldX, worldY, size, angle, speed, texture, color, stroke);
        lifeTime = 3f;
        alive = true;
    }

    public void update() {
        // Move bullet in the direction of its angle
        worldX += (float) (Math.cos(angle) * speed * GameScreen.dt);
        worldY += (float) (Math.sin(angle) * speed * GameScreen.dt);

        centerX = worldX + size / 2f;
        centerY = worldY + size / 2f;

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