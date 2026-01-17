import static com.raylib.Raylib.*;

// The ArenaCloser class is a specialized type of Tank
// It inherits all properties and methods from the Tank superclass

public class ArenaCloser extends Tank {
    public ArenaCloser(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.baseRadius = 120;
        this.radiusMultiplier = 1.5f; // Initial radius 75 (50 * 1.5)
        this.barrels = new Barrel[] {
            new Barrel(radius, radius / 2, 0, 0, 0, barrelTexture)
        };
        updateStats();
    }
}
