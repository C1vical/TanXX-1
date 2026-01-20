package entities;

import core.EntityManager;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

// Barrel class represents a tank's barrel, handles shooting, drawing, and stats
public class Barrel {
    // Fixed turret angle relative to tank
    private final float turretAngle;

    // Texture and colors for drawing the barrel
    private final Texture barrelTexture;
    private final Color barrelColor = newColor(100, 99, 107, 255);        // Inner barrel color
    private final Color barrelStrokeColor = newColor(57, 56, 59, 255);    // Outer stroke color

    // Reload and delay timers
    public float reloadSpeed;  // Time needed to reload after shooting
    public float reloadTimer;  // Current reload timer
    public float delay;        // Delay between shots
    public float delayTimer;   // Current delay timer

    // Saved base stats for resetting/upgrades
    float baseBarrelW;
    float baseBarrelH;
    float baseOffset;
    float baseRecoil;
    float baseReloadSpeed;
    float baseDelay;

    // Current adjustable stats
    private float barrelW;
    private float barrelH;
    private float offset;
    private float recoil;

    // Shooting state
    private boolean canShoot;  // True if barrel can shoot
    private boolean isShoot;   // True if player is trying to shoot

    // Constructor: initialize barrel with size, offset, turret angle, delay, reload speed, recoil, and texture
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

        // Initialize shooting state
        this.isShoot = false;
        this.canShoot = !(delayTimer > 0f);

        // Save base stats for reference or upgrades
        saveBaseStats();
    }

    // Save base stats to allow resets or reference
    public void saveBaseStats() {
        baseBarrelW = barrelW;
        baseBarrelH = barrelH;
        baseOffset = offset;
        baseRecoil = recoil;
        baseReloadSpeed = reloadSpeed;
        baseDelay = delay;
    }

    // Update barrel state every frame
    public void update() {
        // Reduce reload timer based on delta time
        if (reloadTimer > 0f) reloadTimer -= EntityManager.dt;

        // Check if player is holding shoot button or auto-fire is enabled
        isShoot = IsMouseButtonDown(MOUSE_BUTTON_LEFT) || EntityManager.autoFire;

        // Reduce delay timer if shooting, reset if not
        if (isShoot) {
            if (delayTimer > 0f) delayTimer -= EntityManager.dt;
        } else {
            delayTimer = delay;
        }

        // Barrel can shoot only if both reload and delay timers are finished
        canShoot = delayTimer <= 0f && reloadTimer <= 0f;
    }

    // Draw the barrel on screen
    public void draw() {
        // Calculate the final angle of the barrel (tank angle + turret offset)
        float angle = EntityManager.playerTank.angle + turretAngle * (float) Math.PI / 180f;

        // Offset barrel position from tank center
        float offsetX = -(float) Math.sin(angle) * offset;
        float offsetY =  (float) Math.cos(angle) * offset;

        // Position of barrel pivot
        float pivotX = EntityManager.playerTank.centerX + offsetX;
        float pivotY = EntityManager.playerTank.centerY + offsetY;

        // Source rectangle for the texture
        Rectangle source = newRectangle(0, 0, barrelTexture.width(), barrelTexture.height());

        // Convert angle to degrees for DrawTexturePro
        float rotation = angle * (180f / (float) Math.PI);

        // Draw outer barrel (stroke)
        Rectangle outerDest = newRectangle(pivotX, pivotY, barrelW, barrelH);
        Vector2 outerOrigin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrelTexture, source, outerDest, outerOrigin, rotation, barrelStrokeColor);

        // Draw inner barrel
        int strokeWidth = 3;
        float innerW = barrelW - strokeWidth * 2f;
        float innerH = barrelH - strokeWidth * 2f;

        // Apply offset to inner barrel so stroke is visible
        float innerOffsetX = (float) Math.cos(angle) * strokeWidth;
        float innerOffsetY = (float) Math.sin(angle) * strokeWidth;

        Rectangle innerDest = newRectangle(pivotX + innerOffsetX, pivotY + innerOffsetY, innerW, innerH);
        Vector2 innerOrigin = new Vector2().x(0).y(innerH / 2f);

        DrawTexturePro(barrelTexture, source, innerDest, innerOrigin, rotation, barrelColor);
    }

    // Getters
    public float getBarrelW() { return barrelW; }
    public float getBarrelH() { return barrelH; }
    public float getRecoil() { return recoil; }
    public float getTurretAngle() { return turretAngle; }
    public float getOffset() { return offset; }
    public boolean canShoot() { return canShoot; }
    public Texture getBarrelTexture() { return barrelTexture; }

    // Setters
    public void setBarrelW(float barrelW) { this.barrelW = barrelW; }
    public void setBarrelH(float barrelH) { this.barrelH = barrelH; }
    public void setRecoil(float recoil) { this.recoil = recoil; }
    public void setOffset(float offset) { this.offset = offset; }
}