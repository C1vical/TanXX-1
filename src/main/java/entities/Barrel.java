package entities;

import core.EntityManager;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

public class Barrel {
    private final float turretAngle;
    private final Texture barrelTexture;
    private final Color barrelColor = newColor(100, 99, 107, 255);
    private final Color barrelStrokeColor = newColor(57, 56, 59, 255);

    // Reload
    public float reloadSpeed;
    public float reloadTimer;
    // Reload
    public float delay;
    public float delayTimer;
    // Saved base stats
    float baseBarrelW;
    float baseBarrelH;
    float baseOffset;
    float baseRecoil;
    float baseReloadSpeed;
    float baseDelay;
    private float barrelW;
    private float barrelH;
    private float offset;
    private float recoil;
    // Booleans
    private boolean canShoot;
    private boolean isShoot;


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
        // Update the reloadaw timers
        if (reloadTimer > 0f) reloadTimer -= EntityManager.dt;

        // Check if player is holding the mouse button
        isShoot = IsMouseButtonDown(MOUSE_BUTTON_LEFT) || EntityManager.autoFire;

        if (isShoot) {
            if (delayTimer > 0f) delayTimer -= EntityManager.dt;
        } else {
            delayTimer = delay;
        }

        // entities.Barrel can shoot if both timers are done
        canShoot = delayTimer <= 0f && reloadTimer <= 0f;
    }

    public void draw() {
        float angle = EntityManager.playerTank.angle + turretAngle * (float) Math.PI / 180f;

        float offsetX = -(float) Math.sin(angle) * offset;
        float offsetY =  (float) Math.cos(angle) * offset;

        float pivotX = EntityManager.playerTank.centerX + offsetX;
        float pivotY = EntityManager.playerTank.centerY + offsetY;

        Rectangle source = newRectangle(0, 0, barrelTexture.width(), barrelTexture.height());

        float rotation = angle * (180f / (float) Math.PI);

        // Outer
        Rectangle outerDest = newRectangle(pivotX, pivotY, barrelW, barrelH);
        Vector2 outerOrigin = new Vector2().x(0).y(barrelH / 2f);

        DrawTexturePro(barrelTexture, source, outerDest, outerOrigin, rotation, barrelStrokeColor);

        // Inner barrel
        int strokeWidth = 3;
        float innerW = barrelW - strokeWidth * 2f;
        float innerH = barrelH - strokeWidth * 2f;

        // Offset
        float innerOffsetX = (float) Math.cos(angle) * strokeWidth;
        float innerOffsetY = (float) Math.sin(angle) * strokeWidth;

        Rectangle innerDest = newRectangle(pivotX + innerOffsetX, pivotY + innerOffsetY, innerW, innerH);
        Vector2 innerOrigin = new Vector2().x(0).y(innerH / 2f);

        DrawTexturePro(barrelTexture, source, innerDest, innerOrigin, rotation, barrelColor);
    }


    public float getBarrelW() {
        return barrelW;
    }

    public void setBarrelW(float barrelW) {
        this.barrelW = barrelW;
    }

    public float getBarrelH() {
        return barrelH;
    }

    public void setBarrelH(float barrelH) {
        this.barrelH = barrelH;
    }

    public float getTurretAngle() {
        return turretAngle;
    }

    public float getRecoil() {
        return recoil;
    }

    public void setRecoil(float recoil) {
        this.recoil = recoil;
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public boolean canShoot() {
        return canShoot;
    }

    public Texture getBarrelTexture() {
        return barrelTexture;
    }

}