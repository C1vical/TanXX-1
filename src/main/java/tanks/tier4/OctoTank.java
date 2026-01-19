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
        this.zoomFactor = 1f;
        Barrel barrel1 = new Barrel(radius, radius / 2, 0, 45, 0, 0.6f, 50, barrelTexture);
        Barrel barrel2 = new Barrel(radius, radius / 2, 0, 135, 0, 0.6f, 50, barrelTexture);
        Barrel barrel3 = new Barrel(radius, radius / 2, 0, 225, 0, 0.6f, 50, barrelTexture);
        Barrel barrel4 = new Barrel(radius, radius / 2, 0, 315, 0, 0.6f, 50, barrelTexture);
        Barrel barrel5 = new Barrel(radius, radius / 2, 0, 0, 0.3f, 0.6f, 50, barrelTexture);
        Barrel barrel6 = new Barrel(radius, radius / 2, 0, 90, 0.3f, 0.6f, 50, barrelTexture);
        Barrel barrel7 = new Barrel(radius, radius / 2, 0, 180, 0.3f, 0.6f, 50, barrelTexture);
        Barrel barrel8 = new Barrel(radius, radius / 2, 0, 270, 0.3f, 0.6f, 50, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4, barrel5, barrel6, barrel7, barrel8};

        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
