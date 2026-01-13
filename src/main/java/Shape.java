import static com.raylib.Colors.WHITE;
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

    public Shape(float orbitX, float orbitY, float size, float angle, float speed, Texture texture, Color color, Color stroke, int type) {
        super(0, 0, size, angle, speed, texture, color, stroke);
        
        this.orbitX = orbitX;
        this.orbitY = orbitY;

        // Random speed between 0.08 and 0.1, random sign (for cw and ccw rotation)
        orbitAngleSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        rotationSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);

        // Random orbit radius between 30 and 100
        orbitRadius = 30 + (float)(Math.random() * 70);
        
        worldX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        worldY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        alive = false;

        this.type = type;
    }

    public void update() {
        // // Update orbit angle
        orbitAngle += orbitAngleSpeed * GameScreen.dt;
        // Update rotation angle
        angle += rotationSpeed * GameScreen.dt;;

        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        worldX = centerX - size / 2f;
        worldY = centerY - size / 2f;
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
