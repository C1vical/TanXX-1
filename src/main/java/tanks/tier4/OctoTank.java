package tanks.tier4;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class OctoTank extends Tank {
    public OctoTank(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(50, 25, 0, 45, 0, defaultReload, 50, barrelTexture);
        Barrel barrel2 = new Barrel(50, 25, 0, 135, 0, defaultReload, 50, barrelTexture);
        Barrel barrel3 = new Barrel(50, 25, 0, 225, 0, defaultReload, 50, barrelTexture);
        Barrel barrel4 = new Barrel(50, 25, 0, 315, 0, defaultReload, 50, barrelTexture);
        Barrel barrel5 = new Barrel(50, 25, 0, 0, 0.3f, defaultReload, 50, barrelTexture);
        Barrel barrel6 = new Barrel(50, 25, 0, 90, 0.3f, defaultReload, 50, barrelTexture);
        Barrel barrel7 = new Barrel(50, 25, 0, 180, 0.3f, defaultReload, 50, barrelTexture);
        Barrel barrel8 = new Barrel(50, 25, 0, 270, 0.3f, defaultReload, 50, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4, barrel5, barrel6, barrel7, barrel8};

        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
