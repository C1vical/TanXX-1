package tanks.tier2;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Twin extends Tank {
    public Twin(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 1f;
        Barrel barrel1 = new Barrel(radius, radius / 2, -12, 0, 0, 0.6f, 50 * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(radius, radius / 2, 12, 0, 0.3f, 0.6f, 50 * 0.25f, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
