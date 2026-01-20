package tanks.tier4;

import com.raylib.Raylib;
import entities.Barrel;
import entities.Tank;

public class Triplet extends Tank {
    public Triplet(float centerX, float centerY, float angle, Raylib.Texture bodyTexture, Raylib.Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.97f;
        Barrel barrel1 = new Barrel(radius, radius / 2, -14, 0, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(radius, radius / 2, 14, 0, 0.3f, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(60, radius / 2, 0, 0, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
