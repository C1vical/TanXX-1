package tanks.tier4;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;


public class PentaShot extends Tank {
    public PentaShot(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(42, 25, 0, 50, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(42, 25, 0, -50, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(48, 25, 0, 25, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel4 = new Barrel(48, 25, 0, -25, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel5 = new Barrel(55, 25, 0, 0, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);

        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4, barrel5};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}