package tanks.tier4;

import core.TankType;
import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Annihilator extends Tank {
    public Annihilator(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.type = TankType.ANNIHILATOR;
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.91f;
        Barrel barrel1 = new Barrel(50, 48, 0, 0, 0, defaultReload * 2f, defaultRecoil * 2.6f, barrelTexture);
        this.barrels = new Barrel[] {barrel1};

        updateStats();
    }
}
