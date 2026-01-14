package com.tanxx.physics;

import static com.raylib.Raylib.*;

public class Collision {
    public static boolean polygonPolygonCollision(Polygon a, Polygon b) {
        if (a.maxX < b.minX || b.maxX < a.minX || a.maxY < b.minY || b.maxY < a.minY) return false;

        if (checkPolygonAxes(a, b)) return false;
        if (checkPolygonAxes(b, a)) return false;

        return true;
    }

    private static boolean checkPolygonAxes(Polygon a, Polygon b) {
        Vector2[] vertices = a.vertices;
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

            float minA = Float.MAX_VALUE, maxA = -Float.MAX_VALUE;
            for (Vector2 v : a.vertices) {
                float proj = v.x() * axisX + v.y() * axisY;
                if (proj < minA) minA = proj;
                if (proj > maxA) maxA = proj;
            }

            float minB = Float.MAX_VALUE, maxB = -Float.MAX_VALUE;
            for (Vector2 v : b.vertices) {
                float proj = v.x() * axisX + v.y() * axisY;
                if (proj < minB) minB = proj;
                if (proj > maxB) maxB = proj;
            }

            if (maxA < minB || maxB < minA) return true;
        }
        return false;
    }

    public static boolean circlePolygonCollision(float circleX, float circleY, float radius, Polygon poly) {
        if (circleX + radius < poly.minX || circleX - radius > poly.maxX || circleY + radius < poly.minY || circleY - radius > poly.maxY) return false;

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

            float cProjCenter = circleX * axisX + circleY * axisY;
            float cMin = cProjCenter - radius;
            float cMax = cProjCenter + radius;

            if (polyMax < cMin || cMax < polyMin) return false;
        }

        return true;
    }
}