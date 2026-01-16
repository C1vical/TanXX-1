import static com.raylib.Raylib.*;

// The Basic class is a simple, standard type of Tank
// Inherits all behavior from the Tank superclass

public class Basic extends Tank {
    public Basic(float centerX, float centerY, float angle, Texture bodyTexture, Texture barrelTexture) {
        super(centerX, centerY, angle, bodyTexture, barrelTexture);
        this.size = 50;
        this.barrelW = size;
        this.barrelH = size / 2;
        this.recoil = barrelH * 1.8f;
    }
}
