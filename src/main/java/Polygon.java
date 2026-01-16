import static com.raylib.Raylib.*;

// Polygon class for collision and rendering
public class Polygon {
    // Array of vertices of the shape
    public final Vector2[] vertices;

    // Minimum and maximum values of x and y (this is for the bounding box)
    public float minX, maxX, minY, maxY;

    // Constructor
    public Polygon(Vector2[] vertices) {
        this.vertices = vertices;
        // Calculate the bounding box
        calculateAABB();
    }

    // Call this each frame because the shape will move due to rotation and translations
    public void update() {
        calculateAABB();
    }
    // Calculate the axis-aligned bounding box (AABB) around the polygon
    private void calculateAABB() {

        // First, initialize the min and max values with the first vertex
        minX = vertices[0].x();
        maxX = minX;
        minY = vertices[0].y();
        maxY = minY;

        // Use a simple for loop to loop through all of the vertices to find the min and max values of x and y
        for (int i = 1; i < vertices.length; i++) {
            float x = vertices[i].x();
            float y = vertices[i].y();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
    }
}