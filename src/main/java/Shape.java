import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

// Shape class for all shapes in the game, extends off entity class

public class Shape extends Entity {
    // Rotation and orbit variables
    float rotationSpeed;
    float orbitAngle;
    float orbitAngleSpeed;
    float orbitRadius;
    float orbitX;
    float orbitY;

    int sides;  // Number of sides
    float step; // Angle between vertices
    Vector2[] vertices; // Vertices of the shape
    public Polygon polygon;

    public Color color;     // Fill color
    public Color stroke;    // Stroke color

    protected int xp;

    public Shape(float orbitX, float orbitY, float angle, int sides, float maxHealth, float bodyDamage) {
        super(0, 0, angle);
        this.size = 25;
        this.orbitX = orbitX;
        this.orbitY = orbitY;
        this.alive = true;

        this.sides = sides;
        this.maxHealth = maxHealth;
        this.bodyDamage = bodyDamage;
        this.health = maxHealth;

        orbitAngleSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        rotationSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        orbitRadius = 30 + (float) (Math.random() * 70);

        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        vertices = new Vector2[sides];
        step = (float) (Math.PI * 2.0 / sides);

        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i] = new Vector2().x(centerX + (float) Math.cos(a) * (size + strokeWidth)).y(centerY + (float) Math.sin(a) * (size + strokeWidth));
        }

        polygon = new Polygon(vertices);
    }

    public void update() {
        regenHealth(GameScreen.dt);

        orbitAngle += orbitAngleSpeed * GameScreen.dt;
        angle += rotationSpeed * GameScreen.dt;

        velocityX -= velocityX * decay * GameScreen.dt;
        velocityY -= velocityY * decay * GameScreen.dt;

        orbitX += velocityX * GameScreen.dt;
        orbitY += velocityY * GameScreen.dt;

        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        if (Math.abs(velocityX) < 0.5f) velocityX = 0f;
        if (Math.abs(velocityY) < 0.5f) velocityY = 0f;

        // Ensure orbit center stays within bounds
        float minX = orbitRadius;
        float maxX = GameScreen.worldW - orbitRadius;
        float minY = orbitRadius;
        float maxY = GameScreen.worldH - orbitRadius;

        if (orbitX < minX) {
            orbitX = minX;
            velocityX = 0; // stop further movement
        }
        if (orbitX > maxX) {
            orbitX = maxX;
            velocityX = 0;
        }

        if (orbitY < minY) {
            orbitY = minY;
            velocityY = 0;
        }
        if (orbitY > maxY) {
            orbitY = maxY;
            velocityY = 0;
        }

        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i].x(centerX + (float) Math.cos(a) * (size + strokeWidth));
            vertices[i].y(centerY + (float) Math.sin(a) * (size + strokeWidth));
        }

        polygon.update();
    }

    public void draw() {
        Color currentStroke = alive ? stroke : RED;
        Color currentColor = alive ? color : RED;
        DrawPoly(new Vector2().x(centerX).y(centerY), sides, size + strokeWidth, angle * (180f / (float) Math.PI), currentStroke);
        DrawPoly(new Vector2().x(centerX).y(centerY), sides, size, angle * (180f / (float) Math.PI), currentColor);
        if (GameScreen.hitbox) drawHitBox();
        if (health < maxHealth && alive) drawHealthBar();
    }

    public void drawHitBox() {
        DrawPolyLines(new Vector2().x(centerX).y(centerY), sides, size + strokeWidth, angle * (180f / (float) Math.PI), hitboxColor);
    }

    public int getXp() { return xp; }
}
