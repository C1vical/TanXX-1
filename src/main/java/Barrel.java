import static com.raylib.Raylib.*;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class Barrel {
    private float barrelW;
    private float barrelH;
    float originalBarrelW;
    float originalBarrelH;
    float originalRecoil;
    float offset;
    private final float turretAngle;
    private float recoil;
    float delay;
    float delayTimer = 0f;
    boolean canShoot = false;
    private final Texture barrelTexture;
    private final Color barrelColor = newColor(100, 99, 107, 255);

    float reloadTimer = 0f;

    public Barrel(float barrelW, float barrelH, float offset, float turretAngle, float delay, Texture barrelTexture) {
        this.barrelW = barrelW;
        this.barrelH = barrelH;
        this.offset = offset;
        this.turretAngle = turretAngle;
        this.delay = delay;
        this.delayTimer = delay;
        this.recoil = barrelH * 1.8f;
        this.barrelTexture = barrelTexture;

        originalBarrelW = barrelW;
        originalBarrelH = barrelH;
        originalRecoil = recoil;
    }

    public void update() {
        // Update the delay timer
        delayTimer -= EntityManager.dt;

        // Only allow shooting if both delay and reload timer are ready
        if (delayTimer <= 0 && reloadTimer <= 0f) {
            canShoot = true;       // Barrel can fire
            reloadTimer = EntityManager.playerTank.reloadSpeed; // reset reload timer
            delayTimer = 0f;
        } else {
            canShoot = false;
        }

        // Countdown reload timer
        if (reloadTimer > 0f) {
            reloadTimer -= EntityManager.dt;
        }
    }


    // Returns true if this barrel can shoot
    public boolean canShoot() {
        return canShoot;
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

    public void setBarrelW(float barrelW) { this.barrelW = barrelW; }
    public void setBarrelH(float barrelH) { this.barrelH = barrelH; }
    public void setRecoil(float recoil) { this.recoil = recoil; }

}
