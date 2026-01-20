package tanks.tier4;

import core.TankType;
import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

// The Basic class is a simple, standard type of Tank
// Inherits all behavior from the Tank superclass

public class Streamliner extends Tank {
    // Constructor sets default radius and barrel dimensions for the basic tank
    public Streamliner(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.type = TankType.STREAMLINER;
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 0.95f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(55, 25, 0, 0, 0, defaultReload, defaultRecoil * 0.05f, barrelTexture);
        Barrel barrel2 = new Barrel(50, 25, 0, 0, 0.1f, defaultReload, defaultRecoil * 0.05f, barrelTexture);
        Barrel barrel3 = new Barrel(45, 25, 0, 0, 0.2f, defaultReload, defaultRecoil* 0.05f, barrelTexture);
        Barrel barrel4 = new Barrel(40, 25, 0, 0, 0.3f, defaultReload, defaultRecoil* 0.05f, barrelTexture);
        Barrel barrel5 = new Barrel(35, 25, 0, 0, 0.4f, defaultReload, defaultRecoil* 0.05f, barrelTexture);
        Barrel barrel6 = new Barrel(30, 25, 0, 0, 0.5f, defaultReload, defaultRecoil* 0.05f, barrelTexture);
        this.barrels = new Barrel[]{barrel1,barrel2,barrel3,barrel4,barrel5, barrel6};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}