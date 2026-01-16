import static com.raylib.Raylib.*;

// The Annihilator class is a specialized type of Tank
// It inherits all properties and methods from the Tank superclass

public class Annihilator extends Tank {
    // Constructor sets much larger size and barrel dimensions for the annihilator tank
    public Annihilator(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture, barrelTexture);
        this.baseSize = 50;
        this.sizeMultiplier = 1.5f; // Initial size 75 (50 * 1.5)
        updateStats();
    }

    @Override
    protected void updateDimensions() {
        barrelW = size - 10;
        barrelH = size - 10;
        recoil = barrelH * 1.8f;
    }
}
