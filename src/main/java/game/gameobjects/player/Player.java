package game.gameobjects.player;

import game.gameobjects.Enemy;
import game.gameobjects.GameObject;
import game.gameobjects.Wall;
import jangl.coords.WorldCoords;
import jangl.io.keyboard.Keyboard;
import jangl.io.mouse.Mouse;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import jangl.shapes.Transform;
import jangl.time.Clock;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Player extends GameObject {
    private final float speed;
    private final LaserGun laserGun;
    private final List<Wall> walls;

    public Player(List<Wall> walls, List<Enemy> aliens, float speed) {
        super(new Rect(new WorldCoords(0, 0), 0.075f, 0.075f), "player");

        this.speed = speed;
        this.laserGun = new LaserGun(walls, aliens);
        this.walls = walls;
    }

    @Override
    public void draw() {
        this.laserGun.draw();
        super.draw();
    }

    @Override
    public void update() {
        // Set the rotation to 0 to not interfere with collision
        this.getRect().getTransform().setLocalRotation(0);

        this.move();
        this.setRotation();

        this.laserGun.update(this.getRect().getTransform());
    }

    /**
     * @param movement The WorldCoords movement. WARNING: only have one axis at a time set to a non-zero value.
     * @param iterations The number of iterations to have
     */
    private void stepUntilColliding(WorldCoords movement, int iterations) {
        Transform transform = this.getRect().getTransform();

        float step = Math.max(Math.abs(movement.x), Math.abs(movement.y)) / iterations;
        WorldCoords movePerStep = new WorldCoords(movement.x / iterations, movement.y / iterations);

        for (float i = 0; i < Math.max(Math.abs(movement.x), Math.abs(movement.y)); i += step) {
            transform.shift(movePerStep);

            boolean stopStep = false;
            for (Wall wall : this.walls) {
                if (Shape.collides(wall.getRect(), this.getRect())) {
                    transform.shift(-movePerStep.x, -movePerStep.y);
                    stopStep = true;
                    break;
                }
            }

            if (stopStep) {
                break;
            }
        }
    }

    private void move() {
        float amountToMove = (float) (this.speed * Clock.getTimeDelta());
        WorldCoords movement = new WorldCoords(0, 0);

        // Vertical axis
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_W)) {
            movement.y += amountToMove;
        } if (Keyboard.getKeyDown(GLFW.GLFW_KEY_S)) {
            movement.y -= amountToMove;
        }

        // Horizontal axis
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_A)) {
            movement.x -= amountToMove;
        } if (Keyboard.getKeyDown(GLFW.GLFW_KEY_D)) {
            movement.x += amountToMove;
        }

        // Prevent horizontal movement being sqrt(2) times as fast (due to Pythagoras)
        if (movement.x != 0 && movement.y != 0) {
            double radians = Math.toRadians(45);

            movement.x *= Math.cos(radians);
            movement.y *= Math.sin(radians);
        }

        // Move
        stepUntilColliding(new WorldCoords(movement.x, 0), 3);
        stepUntilColliding(new WorldCoords(0, movement.y), 3);
    }

    private void setRotation() {
        WorldCoords middle = WorldCoords.getMiddle();
        WorldCoords mouseCoords = Mouse.getMousePos();

        float angle = (float) (Math.atan2(mouseCoords.y - middle.y, mouseCoords.x - middle.x));
        this.getRect().getTransform().setLocalRotation(angle);
    }

    @Override
    public void close() {
        super.close();
        this.laserGun.close();
    }
}
