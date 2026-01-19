package tanks.tier3;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class QuadTank extends Tank {
    public QuadTank(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture){
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.94f;
        Barrel barrel1 = new Barrel(radius, radius / 2, 0, 0, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(radius, radius / 2, 0, 90, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(radius, radius / 2, 0, 180, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel4 = new Barrel(radius, radius / 2, 0, 270, 0, 0.6f, defaultRecoil * 0.25f, barrelTexture);

        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4};

        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
