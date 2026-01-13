import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newColor;

public class Sprite {

    // Coordinates of center
    protected float centerX;
    protected float centerY;

    // Angle the sprite is facing
    protected float angle;

    // Sprite speed
    protected float speed;

    // Sprite dimensions
    protected float size;

    // Sprite texture
    protected Texture texture;

    // Sprite colour
    protected Color color;
    protected Color stroke;
    protected int strokeWidth = 5;

    // Hitbox color
    protected Color hitboxColor = newColor(17, 184, 83, 255);

    // Stats
    protected float maxHealth;
    protected float health;
    protected float healthRegen;
    protected float bodyDamage;

    // Alive status
    protected boolean alive;

    public Sprite(float centerX, float centerY, float angle, Texture texture) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.angle = angle;
        this.texture = texture;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public boolean isAlive() {
        return alive;
    }
}
    
