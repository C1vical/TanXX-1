package tanks.tier3;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Triangle extends Tank {
    public Triangle(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.94f;
        Barrel barrel1 = new Barrel(45, 25, 0, 0, 0, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(40, 25, 0, 148, 0.2f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(40, 25, 0, -148, 0.2f, defaultReload, defaultRecoil * 0.25f, barrelTexture);

        this.barrels = new Barrel[]{barrel1, barrel2, barrel3};

        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
