import static com.raylib.Raylib.*;

// The Annihilator class is a specialized type of Tank
// It inherits all properties and methods from the Tank superclass

public class Annihilator extends Tank {
    // Constructor sets much larger size and barrel dimensions for the annihilator tank
    public Annihilator(float centerX, float centerY, float angle, Texture bodyTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.baseSize = 50;
        this.sizeMultiplier = 1.5f; // Initial size 75 (50 * 1.5)
        updateStats();
    }

    @Override
    protected void updateDimensions() {
        for (int i = 0; i < barrels.length; i++) {
            barrels[i].setBarrelW(size - 10);
            barrels[i].setBarrelH(size - 10);
            barrels[i].setRecoil(barrels[i].getBarrelH() * 1.8f);
        }
    }
}
