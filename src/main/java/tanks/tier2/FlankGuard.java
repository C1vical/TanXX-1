package tanks.tier2;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class FlankGuard extends Tank {
    public FlankGuard(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.97f;
        Barrel barrel1 = new Barrel(50, 25, 0, 0, 0, defaultReload, defaultRecoil, barrelTexture);
        Barrel barrel2 = new Barrel(40, 25, 0, 180, 0, defaultReload, defaultRecoil, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
