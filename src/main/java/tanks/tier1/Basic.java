package tanks.tier1;

import core.TankType;
import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

// The Basic class is a simple, standard type of Tank
// Inherits all behavior from the Tank superclass

public class Basic extends Tank {
    // Constructor sets default radius and barrel dimensions for the basic tank
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
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}