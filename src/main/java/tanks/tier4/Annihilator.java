package tanks.tier4;

import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

// The Annihilator class is a specialized type of Tank
// It inherits all properties and methods from the Tank superclass

public class Annihilator extends Tank {
    // Constructor sets much larger radius and barrel dimensions for the annihilator tank
    public Annihilator(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(radius, radius, 0, 0, 0, 1.2f, defaultRecoil * 2, barrelTexture);
        this.barrels = new Barrel[] {barrel1};
        updateStats();
    }
}
