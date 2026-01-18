import static com.raylib.Raylib.*;

// The Annihilator class is a specialized type of Tank
// It inherits all properties and methods from the Tank superclass

public class Annihilator extends Tank {
    // Constructor sets much larger radius and barrel dimensions for the annihilator tank
    public Annihilator(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture);
        this.baseRadius = 50;
        this.barrels = new Barrel[] {
            new Barrel(radius, radius, 0, 0, 1.0f, barrelTexture)
        };
        updateStats();
    }

    @Override
    protected void updateDimensions() {
        for (int i = 0; i < barrels.length; i++) {
            barrels[i].setBarrelW(radius - 10);
            barrels[i].setBarrelH(radius - 10);
            barrels[i].setRecoil(barrels[i].getBarrelH() * 1.8f);
        }
    }
}
