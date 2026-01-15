package com.tanxx.entities;

import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;

public class Pentagon extends Shape {
    Color pentagonColor = newColor(82, 58, 222, 255);
    Color pentagonStrokeColor = newColor(59, 36, 212, 255);

    public Pentagon(float orbitX, float orbitY, float angle, Texture texture) {
        super(orbitX, orbitY, angle, texture, 5,25, 100);
        color = pentagonColor;
        stroke = pentagonStrokeColor;
        xp = 130;
    }
}
