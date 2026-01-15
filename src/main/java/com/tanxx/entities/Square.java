package com.tanxx.entities;

import static com.raylib.Helpers.newColor;
import static com.raylib.Raylib.*;

public class Square extends Shape {

    Color squareColor = newColor(214, 208, 30, 255);
    Color squareStrokeColor = newColor(158, 152, 24, 255);

    public Square(float orbitX, float orbitY, float angle, Texture texture) {
        super(orbitX, orbitY, angle, texture, 4,10, 8);
        color = squareColor;
        stroke = squareStrokeColor;
        xp = 10;
    }


}
