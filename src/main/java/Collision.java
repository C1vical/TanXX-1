import static com.raylib.Raylib.*;

public class Collision {

    public static boolean polygonPolygonCollision(Polygon a, Polygon b) {
        for (int i = 0; i < 2; i++) {
            Polygon p = (i == 0) ? a : b; // First check a's normals and then check b's normals

            for (int j = 0; j < p.vertices.length; j++) {
                Vector2 v1 = p.vertices[j]; // First vertice
                Vector2 v2 = p.vertices[(j + 1) % p.vertices.length]; // Second vertice

                Vector2 edge = Vector2Subtract(v2, v1); // Wdge
                Vector2 axis = Vector2Normalize(Vector2Rotate(edge, (float) (Math.PI / 2))); // Normal to one of the sides of the polygon

                // Project both polygon vertices onto the axis
                float[] projA = project(a, axis);
                float[] projB = project(b, axis);

                // Check if minimum of a is > maximum of b or maximum of b is < minimum of a
                if (projA[1] < projB[0] || projB[1] < projA[0]) { return false; // Separating axis found if true
                }
            }
        }
        return true; // Overlaps on all axes
    }

    public static boolean circlePolygonCollision(Vector2 circleCenter, float radius, Polygon poly) {
        Vector2[] vertices = poly.vertices;

        for (int i = 0; i < vertices.length; i++) {
            Vector2 v1 = vertices[i]; // First vertice
            Vector2 v2 = vertices[(i + 1) % vertices.length]; // Second vertice

            Vector2 edge = Vector2Subtract(v2, v1); // Edge
            Vector2 axis = Vector2Normalize(Vector2Rotate(edge, (float) (Math.PI / 2))); // Normal to one of the sides of the polygon

            // Project polygon's vertices onto the axis
            float[] polyProj = project(poly, axis);

            // Different from the polygon-polygon collision since we are using a circle

            // Project center onto the axis
            float cProjCenter = Vector2DotProduct(circleCenter, axis);
            // The min and max is found by subtracting and adding the radii respectively
            float cMin = cProjCenter - radius;
            float cMax = cProjCenter + radius;

            if (polyProj[1] < cMin || cMax < polyProj[0]) return false;
        }

        return true; // Overlaps on all axes
    }

    private static float[] project(Polygon poly, Vector2 axis) {
        // Set minimum and maximum as the first vertex as a placeholder
        float min = Vector2DotProduct(poly.vertices[0], axis); // Dot product used to project point onto the vertice
        float max = min;

        // Loop through all of the vertices of the polygon and change min and max accordingly
        for (Vector2 v : poly.vertices) {
            float d = Vector2DotProduct(v, axis);
            min = Math.min(min, d);
            max = Math.max(max, d);
        }
        return new float[]{min, max}; // Returns the minimum and maximum vertices
    }
}