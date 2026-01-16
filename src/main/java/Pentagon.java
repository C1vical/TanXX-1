import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;

// Pentagon shape class, inherits from the  Shape superclass
public class Pentagon extends Shape {
    Color pentagonColor = newColor(82, 58, 222, 255);
    Color pentagonStrokeColor = newColor(59, 36, 212, 255);

    public Pentagon(float orbitX, float orbitY, float angle) {
        super(orbitX, orbitY, angle,5,100, 12);
        color = pentagonColor;
        stroke = pentagonStrokeColor;
        xp = 100;
    }
}
