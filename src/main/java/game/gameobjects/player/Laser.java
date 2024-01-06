package game.gameobjects.player;

import game.gameobjects.Enemy;
import game.gameobjects.Destroyable;
import game.gameobjects.GameObject;
import game.gameobjects.Wall;
import jangl.coords.WorldCoords;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import jangl.time.Clock;

import java.util.List;

public class Laser extends GameObject implements Destroyable {
    private final List<Wall> walls;
    private final List<Enemy> aliens;
    private final float shiftX;
    private final float shiftY;
    private final PlayerBank bank;
    private final float damage;
    private int pierce;

    public Laser(List<Wall> walls, List<Enemy> aliens, PlayerBank bank, WorldCoords origin, float angle, float speed, int pierce, float damage) {
        super(new Rect(new WorldCoords(0, 0), 0.04f, 0.0075f), "green");

        this.walls = walls;
        this.aliens = aliens;

        this.getRect().getTransform().setPos(origin);
        this.getRect().getTransform().setRotation(angle);

        this.shiftX = (float) (speed * Math.cos(angle));
        this.shiftY = (float) (speed * Math.sin(angle));

        this.pierce = pierce;
        this.bank = bank;
        this.damage = damage;
    }

    @Override
    public boolean shouldDestroy() {
        if (this.pierce <= 0) {
            return true;
        }

        for (Wall wall : this.walls) {
            if (Shape.collides(wall.getRect(), this.getRect())) {
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
            if (!alien.onCooldown() && Shape.collides(this.getRect(), alien.getRect()) && this.pierce > 0) {
                alien.takeDamage(this.damage);
                this.pierce--;

                // Award the player with money for killing aliens
                if (alien.shouldDestroy()) {
                    this.bank.addMoney(1);
                }
            }
        }
    }
}
