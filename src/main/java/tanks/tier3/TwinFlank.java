package tanks.tier3;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;


public class TwinFlank extends Tank{
    public TwinFlank(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.94f;
        Barrel barrel1 = new Barrel(50, 23.5f, -12, 0, 0, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(50, 23.5f, 12, 0, 0.3f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(50, 23.5f, 12, 180, 0, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel4 = new Barrel(50, 23.5f, -12, 180, 0.3f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
