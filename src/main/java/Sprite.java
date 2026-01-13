import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newColor;

public class Sprite {
    // Coordinates in the world 
    protected float worldX;
    protected float worldY;

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

    // Sprite health
    protected float health;
    protected float maxHealth;

    // Stats
    protected float damage;

    // Alive status
    protected boolean alive;


    public Sprite(float worldX, float worldY, float size, float angle, float speed, Texture texture, Color color, Color stroke) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.size = size;
        this.angle = angle;
        this.speed = speed;
        this.texture = texture;
        this.color = color;
        this.stroke = stroke;
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
    
