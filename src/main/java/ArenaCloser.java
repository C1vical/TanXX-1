import static com.raylib.Raylib.*;

// The ArenaCloser class is a specialized type of Tank
// It inherits all properties and methods from the Tank superclass

public class ArenaCloser extends Tank {
    public ArenaCloser(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.baseSize = 120;
        Barrel barrel1 = new Barrel(size, size / 2, 0, barrelTexture);
        this.barrels = new Barrel[] {barrel1};
        this.sizeMultiplier = 1.5f; // Initial size 75 (50 * 1.5)
        updateStats();
    }
}
