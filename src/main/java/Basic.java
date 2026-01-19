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
        Barrel barrel1 = new Barrel(radius, radius / 2, 0, 0, 0, 0.6f, barrelTexture);
        this.barrels = new Barrel[] {barrel1};
        updateStats(); // Ensure initial radius and stats are calculated now that barrels exist
    }
}
