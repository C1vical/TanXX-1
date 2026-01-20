package tanks.tier4;

import core.TankType;
import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Fighter extends Tank {
    public Fighter(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.type = TankType.FIGHTER;
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(45, 25, 0, 0, 0, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel2 = new Barrel(40, 25, 0, 90, 0f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel3 = new Barrel(40, 25, 0, -90, 0f, defaultReload, defaultRecoil * 0.25f, barrelTexture);
        Barrel barrel4 = new Barrel(40, 25, 0, 148, 0.3f, defaultReload, defaultRecoil * 0.4f, barrelTexture);
        Barrel barrel5 = new Barrel(40, 25, 0, -148, 0.3f, defaultReload, defaultRecoil * 0.4f, barrelTexture);
        this.barrels = new Barrel[]{barrel1, barrel2, barrel3, barrel4, barrel5};

        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
