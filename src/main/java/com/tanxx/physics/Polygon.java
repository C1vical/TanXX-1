package com.tanxx.physics;

import static com.raylib.Raylib.*;

public class Polygon {
    public final Vector2[] vertices;
    public float minX, maxX, minY, maxY;

    public Polygon(Vector2[] vertices) {
        this.vertices = vertices;
        calculateAABB();
    }

    public void update() {
        calculateAABB();
    }

    private void calculateAABB() {
        if (vertices == null || vertices.length == 0) return;

        minX = vertices[0].x();
        maxX = minX;
        minY = vertices[0].y();
        maxY = minY;

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