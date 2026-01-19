package tanks.tier4;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class TripleTwin extends Tank {
    public TripleTwin(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(radius, radius / 2, -12, 0, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(radius, radius / 2, 12, 0, 0.3f, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(radius, radius / 2, -12, 120, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel4 = new Barrel(radius, radius / 2, 12, 120, 0.3f, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel5 = new Barrel(radius, radius / 2, -12, 240, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel6 = new Barrel(radius, radius / 2, 12, 240, 0.3f, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4, barrel5, barrel6};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
