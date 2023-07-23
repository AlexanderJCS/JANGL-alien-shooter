package game.gameobjects.player;

import game.gameobjects.Enemy;
import game.gameobjects.helper.Cooldown;
import game.gameobjects.Wall;
import jangl.coords.WorldCoords;
import jangl.io.keyboard.Keyboard;
import jangl.shapes.Transform;
import jangl.sound.Sound;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class LaserGun implements AutoCloseable {
    private static final float LASER_DELETION_RANGE = Math.max(WorldCoords.getMiddle().x, WorldCoords.getMiddle().y) * 2 + 1;
    private final Cooldown cooldown;
    private final Sound sound;
    private final float speed;
    private final List<Laser> lasers;
    private final List<Wall> walls;
    private final List<Enemy> aliens;

    public LaserGun(List<Wall> walls, List<Enemy> aliens) {
        this.walls = walls;
        this.aliens = aliens;

        this.lasers = new ArrayList<>();
        this.speed = 1.6f;

        // Set the cooldown to 0.05 for a machine gun. Set the cooldown to 0 (or near 0)
        // for the laser equivalent of the A-10 Warthog
        this.cooldown = new Cooldown(0.2f);
        this.sound = new Sound("src/main/resources/sounds/shoot.ogg");
        this.sound.setVolume(0.3f);
    }

    public void draw() {
        for (Laser laser : this.lasers) {
            laser.draw();
        }
    }

    public void update(Transform playerTransform) {
        this.cooldown.update();

        this.spawnLaser(playerTransform);
        this.cleanUpLasers(playerTransform);

        for (int i = this.lasers.size() - 1; i >= 0; i--) {
            Laser laser = this.lasers.get(i);

            laser.update();
            if (laser.shouldDestroy()) {
                laser.close();
                this.lasers.remove(i);
            }
        }
    }

    private void spawnLaser(Transform playerTransform) {
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_SPACE) && !this.cooldown.onCooldown()) {
            this.lasers.add(
                    new Laser(this.walls, this.aliens, playerTransform.getCenter(), playerTransform.getLocalRotationAngle(), this.speed)
            );

            this.cooldown.activate();
            this.sound.play();
        }
    }

    private void cleanUpLasers(Transform playerTransform) {
        for (int i = this.lasers.size() - 1; i >= 0; i--) {
            Laser laser = this.lasers.get(i);

            WorldCoords laserCenter = laser.getRect().getTransform().getCenter();
            WorldCoords playerCenter = playerTransform.getCenter();

            double distSquared = Math.pow(laserCenter.x - playerCenter.x, 2)
                    + Math.pow(laserCenter.y - playerCenter.y, 2);

            if (distSquared > Math.pow(LASER_DELETION_RANGE, 2)) {
                laser.close();
                this.lasers.remove(i);
            }
        }
    }

    @Override
    public void close() {
        this.sound.close();

        for (Laser laser : this.lasers) {
            laser.close();
        }
    }
}
