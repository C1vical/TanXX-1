import static com.raylib.Raylib.*;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class Barrel {
    private float barrelW;
    private float barrelH;
    private float offset;
    private final float turretAngle;
    private float recoil;

    // Reload
    float reloadSpeed;
    float reloadTimer;

    // Reload
    float delay;
    float delayTimer;

    // Saved base stats
    float baseBarrelW;
    float baseBarrelH;
    float baseOffset;
    float baseRecoil;
    float baseReloadSpeed;
    float baseDelay;

    // Booleans
    private boolean canShoot;
    private boolean isShoot;

    private final Texture barrelTexture;
    private final Color barrelColor = newColor(100, 99, 107, 255);

    public Barrel(float barrelW, float barrelH, float offset, float turretAngle, float delay, float reloadSpeed, float recoil, Texture barrelTexture) {
        this.barrelW = barrelW;
        this.barrelH = barrelH;
        this.offset = offset;
        this.turretAngle = turretAngle;
        this.delay = delay;
        this.delayTimer = delay;
        this.reloadSpeed = reloadSpeed;
        this.reloadTimer = 0f;
        this.recoil = recoil;
        this.barrelTexture = barrelTexture;

        // Set booleans to false
        this.isShoot = false;
        this.canShoot = !(delayTimer > 0f);

        saveBaseStats();
    }

    public void saveBaseStats() {
        baseBarrelW = barrelW;
        baseBarrelH = barrelH;
        baseOffset = offset;
        baseRecoil = recoil;
        baseReloadSpeed = reloadSpeed;
        baseDelay = delay;
    }

    public void update() {
        // Update the reload timers
        if (reloadTimer > 0f) reloadTimer -= EntityManager.dt;

        // Check if player is holding the mouse button
        isShoot = IsMouseButtonDown(MOUSE_BUTTON_LEFT) || EntityManager.autoFire;

        if (isShoot) {
            if (delayTimer > 0f) delayTimer -= EntityManager.dt;
        } else {
            delayTimer = delay;
        }

        // Barrel can shoot if both timers are done
        canShoot = delayTimer <= 0f && reloadTimer <= 0f;
    }

    public void draw() {
        float angle = EntityManager.playerTank.angle + turretAngle * (float) Math.PI / 180f;

        float offsetX = -(float)Math.sin(angle) * offset;
        float offsetY = (float)Math.cos(angle) * offset;

        Rectangle source = newRectangle(0, 0, barrelTexture.width(), barrelTexture.height());
        Rectangle dest = newRectangle(EntityManager.playerTank.centerX + offsetX, EntityManager.playerTank.centerY + offsetY, barrelW, barrelH);

        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);

        DrawTexturePro( barrelTexture, source, dest, origin, angle * (180f / (float)Math.PI), barrelColor);
    }

    public float getBarrelW() { return barrelW; }
    public float getBarrelH() { return barrelH; }
    public float getTurretAngle() { return turretAngle; }
    public float getRecoil() { return recoil; }

    public float getOffset() {
        return offset;
    }

    public boolean canShoot() {
        return canShoot;
    }



    public void setBarrelW(float barrelW) { this.barrelW = barrelW; }
    public void setBarrelH(float barrelH) { this.barrelH = barrelH; }
    public void setOffset(float offset) { this.offset = offset; }
    public void setRecoil(float recoil) { this.recoil = recoil; }

}
