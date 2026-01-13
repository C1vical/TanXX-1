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

    public Tank(float centerX, float centerY, float size, float angle, float speed, Texture texture, Color color, Color stroke) {
        super(centerX, centerY, size, angle, speed, texture, color, stroke);
        recoil = size * 0.8f;
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

        recoilX -= recoilX * decay * GameScreen.dt;
        recoilY -= recoilY * decay * GameScreen.dt;
        bounceX -= bounceX * decay * GameScreen.dt;
        bounceY -= bounceY * decay * GameScreen.dt;

        centerX += (velocityX + bounceX) * GameScreen.dt;
        centerY += (-velocityY + bounceY) * GameScreen.dt;

        if (centerX < 0 && velocityX < 0) {
            centerX = 0;
            bounceX = -velocityX * bounceStrength;
        }

        if (centerX > GameScreen.worldW  && velocityX > 0) {
            centerX = GameScreen.worldW;
            bounceX = -velocityX * bounceStrength;
        }

        if (centerY < 0 && velocityY > 0) {
            centerY = 0;
            bounceY = velocityY * bounceStrength;
        }

        if (centerY > GameScreen.worldH && velocityY < 0) {
            centerY = GameScreen.worldH;
            bounceY = velocityY * bounceStrength;
        }

        if (Math.abs(bounceX) < 0.5f) bounceX = 0f;
        if (Math.abs(bounceY) < 0.5f) bounceY = 0f;
        if (Math.abs(recoilX) < 0.5f) recoilX = 0f;
        if (Math.abs(recoilY) < 0.5f) recoilY = 0f;
    }

    public void draw() {
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
