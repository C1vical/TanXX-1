import static com.raylib.Colors.BLUE;
import static com.raylib.Raylib.*;

public class Shape extends Sprite {

    private float rotationSpeed;
    private double orbitAngle;
    private float orbitAngleSpeed;
    private float orbitRadius;
    private float orbitX;
    private float orbitY;

    private int type;

    private boolean alive = true;

    // Vertices
    public Polygon polygon;
    private int sides;
    private Vector2[] vertices;
    private float step;

    public Shape(float orbitX, float orbitY, float size, float angle, float speed, Texture texture, Color color, Color stroke, int type) {
        super(0, 0, size, angle, speed, texture, color, stroke);
        
        this.orbitX = orbitX;
        this.orbitY = orbitY;

        // Random speed between 0.08 and 0.1, random sign (for cw and ccw rotation)
        orbitAngleSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        rotationSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);

        // Random orbit radius between 30 and 100
        orbitRadius = 30 + (float)(Math.random() * 70);
        
        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        alive = false;

        this.type = type;

        switch (type) {
            case 0 -> sides = 4;
            case 1 -> sides = 3;
            default -> sides = 5;
        }

        vertices = new Vector2[sides];
        step = (float) (Math.PI * 2.0 / sides);

        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i] = new Vector2().x(centerX + (float) Math.cos(a) * (size + strokeWidth)).y(centerY + (float) Math.sin(a) * (size + strokeWidth));
        }

        polygon = new Polygon(vertices);
    }

    public void update() {
        // // Update orbit angle
        orbitAngle += orbitAngleSpeed * GameScreen.dt;
        // Update rotation angle
        angle += rotationSpeed * GameScreen.dt;;

        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i] = new Vector2().x(centerX + (float) Math.cos(a) * (size + strokeWidth)).y(centerY + (float) Math.sin(a) * (size + strokeWidth));
        }

        polygon.update(vertices);
    }

    public void draw() {
        switch (type) {
            case 0 -> {
                DrawPoly(new Vector2().x(centerX).y(centerY), 4, size + strokeWidth, angle * (180f / (float) Math.PI), stroke);
                DrawPoly(new Vector2().x(centerX).y(centerY), 4, size, angle * (180f / (float) Math.PI), color);

            }
            case 1 -> {
                DrawPoly(new Vector2().x(centerX).y(centerY), 3, size + strokeWidth, angle * 180f / (float) Math.PI, stroke);
                DrawPoly(new Vector2().x(centerX).y(centerY), 3, size, angle * 180f / (float) Math.PI, color);
            }
            default -> {
                DrawPoly(new Vector2().x(centerX).y(centerY), 5, size + strokeWidth, angle * 180f / (float) Math.PI, stroke);
                DrawPoly(new Vector2().x(centerX).y(centerY), 5, size, angle * 180f / (float) Math.PI, color);
            }
        }
    }

    public void drawHitBox()  {
        switch (type) {
            case 0 -> DrawPolyLines(new Vector2().x(centerX).y(centerY), 4, size + strokeWidth, angle * (180f / (float) Math.PI), hitboxColor);
            case 1 ->  DrawPolyLines(new Vector2().x(centerX).y(centerY), 3, size + strokeWidth, angle * 180f / (float) Math.PI, hitboxColor);
            default -> DrawPolyLines(new Vector2().x(centerX).y(centerY), 5, size + strokeWidth, angle * 180f / (float) Math.PI, hitboxColor);
        }
    }


    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
