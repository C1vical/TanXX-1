package tanks.tier4;

import com.raylib.Raylib;
import core.TankType;
import entities.Barrel;
import entities.Tank;

public class Triplet extends Tank {
    public Triplet(float centerX, float centerY, float angle, Raylib.Texture bodyTexture, Raylib.Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.type = TankType.TRIPLET;
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(40, 25, -12, 0, defaultReload / 2, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(40, 25, 12, 0, defaultReload / 2, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(50, 25, 0, 0, 0, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
