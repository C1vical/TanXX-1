import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

public class Bullet extends Sprite {
    // Lifetime in frames
    private float lifeTime;

    float bulletDamage;
    float bulletPenetration;

    Color defaultColor = newColor(144, 252, 3, 255);

    public Bullet(float centerX, float centerY, float angle, Texture texture) {
        super(centerX, centerY, angle, texture);
        size = 85;
        speed = 250;
        color = defaultColor;

        maxHealth = 30;
        health = maxHealth;
        bulletDamage = 25;
        bulletPenetration =

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
//        DrawCircleV(new Vector2().x(centerX).y(centerY), size / 2 + strokeWidth, stroke);
//        DrawCircleV(new Vector2().x(centerX).y(centerY), size / 2, color);
        Rectangle source = newRectangle(0, 0, texture.width(), texture.height());
        Rectangle dest = newRectangle(centerX, centerY, size + 2 * strokeWidth, size + 2 * strokeWidth);
        Vector2 origin = new Vector2().x(size / 2 + strokeWidth).y(size / 2 + strokeWidth);
        DrawTexturePro(texture, source, dest, origin, angle, color);
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2 + strokeWidth, hitboxColor);
    }
}