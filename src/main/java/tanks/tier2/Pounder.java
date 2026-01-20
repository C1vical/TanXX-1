package tanks.tier2;

import core.TankType;
import entities.Barrel;
import entities.Tank;

import static com.raylib.Raylib.Texture;

public class Pounder extends Tank {
    public Pounder(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.type = TankType.POUNDER;
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 0.95f;
        Barrel barrel1 = new Barrel(50, 30, 0, 0, 0, defaultReload * 1.2f, defaultRecoil * 1.2f, barrelTexture);
        this.barrels = new Barrel[] {barrel1};

        updateStats();
    }
}