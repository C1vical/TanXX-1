package com.tanxx.entities;

import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;

public class Triangle extends Shape {

    Color triangleColor = newColor(214, 51, 30, 255);
    Color triangleStrokeColor = newColor(148, 30, 15, 255);

    public Triangle(float orbitX, float orbitY, float angle, Texture texture) {
        super(orbitX, orbitY, angle, texture, 3,15, 8);
        color = triangleColor;
        stroke = triangleStrokeColor;
    }
}
