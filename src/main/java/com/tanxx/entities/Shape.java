package com.tanxx.entities;

import com.tanxx.physics.Polygon;
import com.tanxx.screens.GameScreen;

import static com.raylib.Raylib.*;

public abstract class Shape extends Sprite {
    float rotationSpeed;
    double orbitAngle;
    float orbitAngleSpeed;
    float orbitRadius;
    float orbitX;
    float orbitY;
    int sides;
    float step;
    Vector2[] vertices;
    public Polygon polygon;

    public Color color;
    public Color stroke;

    public Shape(float orbitX, float orbitY, float angle, Texture texture, int sides, float maxHealth, float bodyDamage) {
        super(0, 0, angle, texture);
        this.size = 25;
        this.orbitX = orbitX;
        this.orbitY = orbitY;
        this.alive = true;

        this.sides = sides;
        this.maxHealth = maxHealth;
        this.bodyDamage = bodyDamage;
        this.health = maxHealth;

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
        if (health < maxHealth) drawHealthBar();
    }

    public void drawHitBox() {
        DrawPolyLines(new Vector2().x(centerX).y(centerY), sides, size + strokeWidth, angle * (180f / (float) Math.PI), hitboxColor);
    }
}
