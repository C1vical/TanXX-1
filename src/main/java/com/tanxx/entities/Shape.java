package com.tanxx.entities;

import com.tanxx.physics.Polygon;
import com.tanxx.screens.GameScreen;

import static com.raylib.Colors.BLUE;
import static com.raylib.Helpers.newColor;
import static com.raylib.Helpers.newRectangle;
import static com.raylib.Raylib.*;

public class Shape extends Sprite {
    private static final Color SQUARE_COLOR = newColor(214, 208, 30, 255);
    private static final Color SQUARE_STROKE = newColor(158, 152, 24, 255);
    private static final Color TRIANGLE_COLOR = newColor(214, 51, 30, 255);
    private static final Color TRIANGLE_STROKE = newColor(148, 30, 15, 255);
    private static final Color PENTAGON_COLOR = newColor(82, 58, 222, 255);
    private static final Color PENTAGON_STROKE = newColor(59, 36, 212, 255);

    float rotationSpeed;
    double orbitAngle;
    float orbitAngleSpeed;
    float orbitRadius;
    float orbitX;
    float orbitY;
    int type;
    int sides;
    float step;
    Vector2[] vertices;
    public Polygon polygon;

    public Shape(float orbitX, float orbitY, float angle, Texture texture, int type) {
        super(0, 0, angle, texture);
        this.size = 25;
        this.type = type;
        this.orbitX = orbitX;
        this.orbitY = orbitY;
        this.alive = true;

        switch (type) {
            case 0 -> {
                color = SQUARE_COLOR;
                stroke = SQUARE_STROKE;
                sides = 4;
                maxHealth = 30;
                bodyDamage = 10;
            }
            case 1 -> {
                color = TRIANGLE_COLOR;
                stroke = TRIANGLE_STROKE;
                sides = 3;
                maxHealth = 20;
                bodyDamage = 15;
            }
            default -> {
                color = PENTAGON_COLOR;
                stroke = PENTAGON_STROKE;
                sides = 5;
                maxHealth = 100;
                bodyDamage = 30;
            }
        }
        health = maxHealth;

        orbitAngleSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        rotationSpeed = (float) (Math.random() * 0.08 + 0.02f) * (Math.random() < 0.5 ? 1 : -1);
        orbitRadius = 30 + (float) (Math.random() * 70);

        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        vertices = new Vector2[sides];
        step = (float) (Math.PI * 2.0 / sides);

        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i] = new Vector2().x(centerX + (float) Math.cos(a) * (size + strokeWidth)).y(centerY + (float) Math.sin(a) * (size + strokeWidth));
        }

        polygon = new Polygon(vertices);
    }

    public void update() {
        orbitAngle += orbitAngleSpeed * GameScreen.dt;
        angle += rotationSpeed * GameScreen.dt;

        centerX = (float) (orbitX + Math.cos(orbitAngle) * orbitRadius);
        centerY = (float) (orbitY + Math.sin(orbitAngle) * orbitRadius);

        for (int i = 0; i < sides; i++) {
            float a = angle + i * step;
            vertices[i].x(centerX + (float) Math.cos(a) * (size + strokeWidth));
            vertices[i].y(centerY + (float) Math.sin(a) * (size + strokeWidth));
        }

        polygon.update();
    }

    public void draw() {
        DrawPoly(new Vector2().x(centerX).y(centerY), sides, size + strokeWidth, angle * (180f / (float) Math.PI), stroke);
        DrawPoly(new Vector2().x(centerX).y(centerY), sides, size, angle * (180f / (float) Math.PI), color);
    }

    public void drawHitBox() {
        DrawPolyLines(new Vector2().x(centerX).y(centerY), sides, size + strokeWidth, angle * (180f / (float) Math.PI), hitboxColor);
    }
}
