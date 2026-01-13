import static com.raylib.Raylib.*;

public class Polygon {
    public Vector2[] vertices;

    public Polygon(Vector2[] vertices) {
        this.vertices = vertices;
    }

    public void update (Vector2[]vertices) {
        this.vertices = vertices;
    }
}
