import static com.raylib.Raylib.*;

public class Tank extends Sprite {

    // Bounce
    float bounceStrength = 0.8f;
    float bounceX = 0f;
    float bounceY = 0f;

    // Velocity
    float velocityX = 0;
    float velocityY = 0;

    // Recoil
    float recoilX = 0f;
    float recoilY = 0f;
    float recoil;

    float decay = 4f;

    public Tank(float worldX, float worldY, float size, float angle, float speed, Texture texture, Color color, Color stroke) {
        super(worldX, worldY, size, angle, speed, texture, color, stroke);
        centerX = worldX + size / 2f;
        centerY = worldY + size / 2f;
        recoil = size * 1.8f;
    }

    public void update() {
        float moveX = 0;
        float moveY = 0;

        if (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP)) moveY += 1;
        if (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN)) moveY -= 1;
        if (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT))  moveX -= 1;
        if (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT)) moveX += 1;

        if (moveX != 0 && moveY != 0) {
            moveX /= (float) Math.sqrt(2);
            moveY /= (float) Math.sqrt(2);
        }

        velocityX = moveX * speed + recoilX;
        velocityY = moveY * speed + recoilY;

        worldX += (velocityX + bounceX) * GameScreen.dt;
        worldY += (-velocityY + bounceY) * GameScreen.dt;

        recoilX -= recoilX * decay * GameScreen.dt;
        recoilY -= recoilY * decay * GameScreen.dt;
        bounceX -= bounceX * decay * GameScreen.dt;
        bounceY -= bounceY * decay * GameScreen.dt;


        if (worldX < -size / 2f && velocityX < 0) {
            worldX = -size / 2f;
            bounceX = -velocityX * bounceStrength;
        }

        if (worldX > GameScreen.worldW - size / 2f && velocityX > 0) {
            worldX = GameScreen.worldW - size / 2f;
            bounceX = -velocityX * bounceStrength;
        }

        if (worldY < -size / 2f && velocityY > 0) {
            worldY = -size / 2f;
            bounceY = velocityY * bounceStrength;
        }

        if (worldY > GameScreen.worldH - size / 2f && velocityY < 0) {
            worldY = GameScreen.worldH - size / 2f;
            bounceY = velocityY * bounceStrength;
        }

        if (Math.abs(bounceX) < 0.5f) bounceX = 0f;
        if (Math.abs(bounceY) < 0.5f) bounceY = 0f;
        if (Math.abs(recoilX) < 0.5f) recoilX = 0f;
        if (Math.abs(recoilY) < 0.5f) recoilY = 0f;

        centerX = worldX + size / 2f;
        centerY = worldY + size / 2f;
    }

    public void draw() {
        centerX = worldX + size / 2f;
        centerY = worldY + size / 2f;
        DrawCircleV(new Vector2().x(centerX).y(centerY), size / 2 + strokeWidth, stroke);
        DrawCircleV(new Vector2().x(centerX).y(centerY), size / 2, color);
    }

    public void applyRecoil() {
        recoilX = -recoil * (float) Math.cos(angle);
        recoilY = recoil * (float) Math.sin(angle);
    }

    public void drawHitBox() {
        DrawCircleLinesV(new Vector2().x(centerX).y(centerY), size / 2 + strokeWidth, hitboxColor);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
