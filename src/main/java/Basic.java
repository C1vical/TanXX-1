import static com.raylib.Raylib.*;

// The Basic class is a simple, standard type of Tank
// Inherits all behavior from the Tank superclass

public class Basic extends Tank {
    // Constructor sets default size and barrel dimensions for the basic tank
    public Basic(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.baseSize = 50;
        Barrel barrel1 = new Barrel(size, size / 2, 0, barrelTexture);
        Barrel barrel2 = new Barrel(size, size / 2, 45, barrelTexture);
        Barrel barrel3 = new Barrel(size, size / 2, 90, barrelTexture);
        Barrel barrel4 = new Barrel(size, size / 2, 135, barrelTexture);
        Barrel barrel5 = new Barrel(size, size / 2, 180, barrelTexture);
        Barrel barrel6 = new Barrel(size, size / 2, 225, barrelTexture);
        Barrel barrel7 = new Barrel(size, size / 2, 270, barrelTexture);
        Barrel barrel8 = new Barrel(size, size / 2, 315, barrelTexture);
        this.barrels = new Barrel[] {barrel1, barrel2, barrel3, barrel4, barrel5, barrel6, barrel7, barrel8};
        updateStats(); // Ensure initial size and stats are calculated now that barrels exist
    }
}
