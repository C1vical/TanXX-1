import static com.raylib.Raylib.*;

// The Basic class is a simple, standard type of Tank
// Inherits all behavior from the Tank superclass

public class Basic extends Tank {
    // Constructor sets default size and barrel dimensions for the basic tank
    public Basic(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture, barrelTexture);
        this.baseSize = 50;
        updateStats(); // Ensure initial size and stats are calculated
    }
}
