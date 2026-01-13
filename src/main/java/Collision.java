import com.raylib.Raylib;

import java.util.Iterator;

import static com.raylib.Raylib.CheckCollisionCircles;

public class Collision {
//    public boolean collision(Shape s1, Shape s2) {
//
//    }
//
//    public boolean collision(Tank t, Shape s) {
//
//    }

    public void collision(Iterator<Bullet> bulletIt, Iterator<Shape> shapeIt) {
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();

            while (shapeIt.hasNext()) {
                Shape s = shapeIt.next();

                if (CheckCollisionCircles(new Raylib.Vector2().x(b.getCenterX()).y(b.getCenterY()),b.size / 2f, new Raylib.Vector2().x(s.getCenterX()).y(s.getCenterY()), s.size / 2f)) {
                    bulletIt.remove();
                    shapeIt.remove();
                    break;
                }
            }
        }
    }
}
