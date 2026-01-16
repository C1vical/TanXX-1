import static com.raylib.Raylib.*;

import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;

public class Barrel {
    private float barrelW;
    private float barrelH;
    private final float turretAngle;
    private float recoil;
    private final Texture barrelTexture;
    private final Color barrelColor = newColor(100, 99, 107, 255);

    public Barrel(float barrelW, float barrelH, float turretAngle, Texture barrelTexture) {
        this.barrelW = barrelW;
        this.barrelH = barrelH;
        this.turretAngle = turretAngle;
        this.recoil = barrelH * 1.8f;
        this.barrelTexture = barrelTexture;
    }

    public float getBarrelW() { return barrelW; }
    public float getBarrelH() { return barrelH; }
    public float getTurretAngle() { return turretAngle; }
    public float getRecoil() { return recoil; }

    public void draw() {
        Rectangle source = newRectangle(0, 0, barrelTexture.width(), barrelTexture.height());
        Rectangle dest = newRectangle(EntityManager.playerTank.centerX, EntityManager.playerTank.centerY, barrelW, barrelH);
        System.out.println(barrelW);
        Vector2 origin = new Vector2().x(0).y(barrelH / 2f);
        DrawTexturePro(barrelTexture, source, dest, origin, EntityManager.playerTank.angle * (180f / (float) Math.PI) + turretAngle, barrelColor);
    }

    public void setBarrelW(float barrelW) { this.barrelW = barrelW; }
    public void setBarrelH(float barrelH) { this.barrelH = barrelH; }
    public void setRecoil(float recoil) { this.recoil = recoil; }

}
