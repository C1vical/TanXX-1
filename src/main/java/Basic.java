import static com.raylib.Raylib.*;

// The Basic class is a simple, standard type of Tank
// Inherits all behavior from the Tank superclass

public class Basic extends Tank {
    // Constructor sets default radius and barrel dimensions for the basic tank
    public Basic(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.radius = 50;
        this.width = radius;
        this.height = radius;
        this.baseRadius = 50;
        Barrel barrel1 = new Barrel(radius, radius / 2, 0, 0, 0, barrelTexture);
        this.barrels = new Barrel[] {barrel1};
//        Barrel barrel1 = new Barrel(radius, radius / 2, -12, 0, 0, barrelTexture);
//        Barrel barrel2 = new Barrel(radius, radius / 2, 12,0, 0.3f, barrelTexture);
//        this.barrels = new Barrel[] {barrel1, barrel2};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
