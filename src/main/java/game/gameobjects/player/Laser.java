package game.gameobjects.player;

import game.gameobjects.Enemy;
import helper.Destroyable;
import game.gameobjects.GameObject;
import game.gameobjects.Wall;
import jangl.coords.WorldCoords;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import jangl.time.Clock;

import java.util.ArrayList;
import java.util.List;

public class Laser extends GameObject implements Destroyable {
    private boolean shouldDestroy;
    private final List<Wall> walls;
    private final List<Enemy> aliens;
    private final float shiftX;
    private final float shiftY;
    private int pierce;

    public Laser(List<Wall> walls, List<Enemy> aliens, WorldCoords origin, float angle, float speed) {
        super(new Rect(new WorldCoords(0, 0), 0.04f, 0.0075f), "green");

        this.walls = walls;
        this.aliens = aliens;

        this.getRect().getTransform().setPos(origin);
        this.getRect().getTransform().setLocalRotation(angle);

        this.shiftX = (float) (speed * Math.cos(angle));
        this.shiftY = (float) (speed * Math.sin(angle));

        this.shouldDestroy = false;
    }

    @Override
    public boolean shouldDestroy() {
        if (this.shouldDestroy) {
            return true;
        }

        List<GameObject> collide = new ArrayList<>(this.walls);
        collide.addAll(this.aliens);

        for (GameObject object : collide) {
            if (object.getClass() == Enemy.class) {
                Enemy alien = (Enemy) object;
                if (alien.onCooldown()) {
                    continue;
                }
            }

            if (Shape.collides(object.getRect(), this.getRect())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void update() {
        this.getRect().getTransform().shift(
                (float) (this.shiftX * Clock.getTimeDelta()),
                (float) (this.shiftY * Clock.getTimeDelta())
        );

        for (Enemy alien : this.aliens) {
            if (!alien.onCooldown() && Shape.collides(this.getRect(), alien.getRect())) {
                alien.takeDamage(8);
                this.shouldDestroy = true;
            }
        }
    }
}
