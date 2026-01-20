package tanks.tier1;

import core.TankType;
import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

// Subclass for different tanks. This code is also used for the rest of the tanks, just with specific numbers for each
public class Basic extends Tank {
    // Constructor sets dimensions, barrels, and stats for the basic tank
    public Basic(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.type = TankType.BASIC;
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 1f;
        Barrel barrel1 = new Barrel(50, 25, 0, 0, 0, defaultReload, defaultRecoil, barrelTexture);
        this.barrels = new Barrel[]{barrel1};
        updateStats();
    }
}