import static com.raylib.Raylib.*;

public class Collision {

    // Check if two convex polygons collide using the Separating Axis Theorem (SAT) for extremely accurate collisions
    public static boolean polygonPolygonCollision(Polygon a, Polygon b) {
        // First, perform a simple check using bounding boxes (basically just squares)
        if (a.maxX < b.minX || b.maxX < a.minX || a.maxY < b.minY || b.maxY < a.minY) return false;

        // Check all axes of polygon A against B
        if (checkPolygonAxes(a, b)) return false;

        // Check all axes of polygon B against A
        if (checkPolygonAxes(b, a)) return false;

        // If there is no separating axis found, polygons must overlap
        return true;
    }

    // Checks all of the edge normals of polygon a against polygon b
    private static boolean checkPolygonAxes(Polygon a, Polygon b) {
        Vector2[] vertices = a.vertices;

        // Loop over all edges of polygon a
        for (int i = 0; i < vertices.length; i++) {
            Vector2 v1 = vertices[i];
            Vector2 v2 = vertices[(i + 1) % vertices.length];

            // Edge vector (subtract the two vertices)
            float edgeX = v2.x() - v1.x();
            float edgeY = v2.y() - v1.y();

            // Edge normal (perpendicular to the edge vector)
            float axisX = -edgeY;
            float axisY = edgeX;

            // Normalize the normal vector
            float length = (float) Math.sqrt(axisX * axisX + axisY * axisY);
            if (length != 0) {
                axisX /= length;
                axisY /= length;
            }

            // Project all vertices of polygon a onto this axis
            float minA = Float.MAX_VALUE, maxA = -Float.MAX_VALUE;
            for (Vector2 v : a.vertices) {
                float proj = v.x() * axisX + v.y() * axisY;
                if (proj < minA) minA = proj;
                if (proj > maxA) maxA = proj;
            }

            // Project all vertices of polygon 'b' onto this axis
            float minB = Float.MAX_VALUE, maxB = -Float.MAX_VALUE;
            for (Vector2 v : b.vertices) {
                float proj = v.x() * axisX + v.y() * axisY;
                if (proj < minB) minB = proj;
                if (proj > maxB) maxB = proj;
            }

            // Check for a separating axis. If true, polygons do not collide
            if (maxA < minB || maxB < minA) return true;
        }

        // Otherwise, there is no separating axis found for this polygon
        return false;
    }

    // Check if a circle collides with a polygon using the Separating Axis Theorem as well
    public static boolean circlePolygonCollision(float circleX, float circleY, float radius, Polygon poly) {
        // First, perform a simple check using bounding boxes
        if (circleX + radius < poly.minX || circleX - radius > poly.maxX || circleY + radius < poly.minY || circleY - radius > poly.maxY) return false;

        // Same as the polygon-polygon collision check, but with a circle instead of a polygon
        Vector2[] vertices = poly.vertices;
        for (int i = 0; i < vertices.length; i++) {
            Vector2 v1 = vertices[i];
            Vector2 v2 = vertices[(i + 1) % vertices.length];

            float edgeX = v2.x() - v1.x();
            float edgeY = v2.y() - v1.y();

            float axisX = -edgeY;
            float axisY = edgeX;

            float length = (float) Math.sqrt(axisX * axisX + axisY * axisY);
            if (length != 0) {
                axisX /= length;
                axisY /= length;
            }

            float polyMin = Float.MAX_VALUE, polyMax = -Float.MAX_VALUE;
            for (Vector2 v : poly.vertices) {
                float proj = v.x() * axisX + v.y() * axisY;
                if (proj < polyMin) polyMin = proj;
                if (proj > polyMax) polyMax = proj;
            }

            // Project the circle onto the axis
            float cProjCenter = circleX * axisX + circleY * axisY;
            float cMin = cProjCenter - radius;
            float cMax = cProjCenter + radius;

            if (polyMax < cMin || cMax < polyMin) return false;
        }
        return true;
    }
}