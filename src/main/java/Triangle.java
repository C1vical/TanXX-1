import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;

// Triangle shape class, inherits from the Shape superclass
public class Triangle extends Shape {
    Color triangleColor = newColor(214, 51, 30, 255);
    Color triangleStrokeColor = newColor(148, 30, 15, 255);

    public Triangle(float orbitX, float orbitY, float angle) {
        super(orbitX, orbitY, angle, 3,30, 8);
        color = triangleColor;
        stroke = triangleStrokeColor;
        xp = 25;
    }
}
