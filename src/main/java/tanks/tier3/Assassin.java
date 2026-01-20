package tanks.tier3;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Assassin extends Tank {
    public Assassin(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1.5f;
        this.zoomFactor = 0.72f;
        Barrel barrel1 = new Barrel(radius * 1.33f, radius / 2, 0, 0, 0, 0.6f * 1.8f, defaultRecoil, barrelTexture);
        this.barrels = new Barrel[]{barrel1};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
