import static com.raylib.Raylib.*;

public class Booster extends Tank {
    public Booster(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1f;
        this.zoomFactor = 1f;
        Barrel barrel1 = new Barrel(radius * 0.95f, radius / 2, 0, 0, 0, 0.6f, 50, barrelTexture);
        Barrel barrel2 = new Barrel(radius * 0.72f, radius / 2 , 0, 135, 0.3f, 0.6f, 50, barrelTexture);
        Barrel barrel3 = new Barrel(radius * 0.78f, radius / 2, 0, 149.5f, 0.1f, 0.6f, 50, barrelTexture);
        Barrel barrel4 = new Barrel(radius * 0.72f, radius / 2, 0, 224.5f, 0.3f, 0.6f, 50, barrelTexture);
        Barrel barrel5 = new Barrel(radius * 0.78f, radius / 2, 0, 209.5f, 0.1f, 0.6f, 50, barrelTexture);
        this.barrels = new Barrel[] {barrel1, barrel2, barrel3, barrel4, barrel5};

        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
