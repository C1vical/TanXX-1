package tanks.tier3;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Destroyer extends Tank {
    public Destroyer(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.95f;
        Barrel barrel1 = new Barrel(42, 40, 0, 0, 0, defaultReload * 2f, defaultRecoil * 1.5f, barrelTexture);
        this.barrels = new Barrel[] {barrel1};

        updateStats();
    }
}
