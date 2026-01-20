package tanks.tier2;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

// The Basic class is a simple, standard type of Tank
// Inherits all behavior from the Tank superclass

public class Pounder extends Tank {
    // Constructor sets default radius and barrel dimensions for the basic tank
    public Pounder(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 1f;
        Barrel barrel1 = new Barrel(50, 25, 0, 0, 0, defaultReload, defaultRecoil, barrelTexture);
        Barrel barrel2 = new Barrel(42, 28, 0, 0, 0.4f, defaultReload, defaultRecoil, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}