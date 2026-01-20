package tanks.tier4;

import core.TankType;
import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Booster extends Tank {
    public Booster(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.type = TankType.BOOSTER;
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(45, 25, 0, 0, 0, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(36, 25, 0, 135, 0.3f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(39, 25, 0, 149.5f, 0.1f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel4 = new Barrel(36, 25, 0, 224.5f, 0.3f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel5 = new Barrel(39, 25, 0, 209.5f, 0.1f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4, barrel5};

        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
