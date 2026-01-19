import static com.raylib.Raylib.*;

public class Sniper extends Tank {
    public Sniper(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        this.bulletSpeedFactor = 1.4f;
        this.zoomFactor = 0.8f;
        Barrel barrel1 = new Barrel(radius * 1.2f, radius / 2, 0, 0, 0, 0.6f * 5 / 3, 50, barrelTexture);
        this.barrels = new Barrel[] {barrel1};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
