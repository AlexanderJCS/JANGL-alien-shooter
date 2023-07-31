package game.gameobjects.player;

import game.gameobjects.Enemy;
import game.gameobjects.GameObject;
import game.gameobjects.Wall;
import helper.Consts;
import helper.HealthContainer;
import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.io.keyboard.Keyboard;
import jangl.io.mouse.Mouse;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import jangl.shapes.Transform;
import jangl.time.Clock;
import org.lwjgl.glfw.GLFW;
import shaders.OverheatShaderFrag;
import ui.upgrades.UpgradeShop;

import java.util.List;

public class Player extends GameObject {
    private final float speed;
    private final LaserGun laserGun;
    private final List<Wall> walls;
    private final ShaderProgram shaderProgram;
    private final HealthContainer healthContainer;
    private final UpgradeShop upgradeShop;
    private final PlayerBank bank;

    public Player(List<Wall> walls, List<Enemy> aliens, float speed) {
        super(new Rect(new WorldCoords(0, 0), 0.075f, 0.075f), "player");


        this.bank = new PlayerBank();
        this.upgradeShop = new UpgradeShop(this.bank);
        this.laserGun = new LaserGun(walls, aliens, this.upgradeShop);
        this.speed = speed;
        this.walls = walls;

        this.getTexture().useDefaultShader(false);

        this.shaderProgram = new ShaderProgram(new TextureShaderVert(), new OverheatShaderFrag(this.laserGun.getOverheat()), TextureShaderVert.getAttribLocations());
        this.healthContainer = new HealthContainer(
                Consts.SETTINGS.getFloat("player/health"),
                Consts.SETTINGS.getFloat("player/invincibility"),
                Consts.SETTINGS.getFloat("player/regen"),
                "hurt"
        );

        this.getRect().getTransform().setPos(new WorldCoords(0, 0));
        this.getRect().getTransform().setScale(1.25f);
    }

    @Override
    public void draw() {
        this.laserGun.draw();

        if (this.healthContainer.onCooldown() && Math.round(GLFW.glfwGetTime() * 20) % 2 == 0) {
            return;
        }

        this.shaderProgram.bind();
        this.image.draw();
        this.shaderProgram.unbind();
    }

    public void drawItemShop() {
        this.upgradeShop.draw();
    }

    public void updateItemShop() {
        this.upgradeShop.update();
    }

    @Override
    public void update() {
        // Set the rotation to 0 to not interfere with collision
        this.getRect().getTransform().setLocalRotation(0);

        this.move();
        this.setRotation();

        this.laserGun.update(this);
        this.healthContainer.setRegen((float) (Consts.SETTINGS.getFloat("player/regen") + 0.025 * this.upgradeShop.getUpgradeLevel("regen")));
        this.healthContainer.update();
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

    public WorldCoords getVelocity() {
        float speedUp = (this.upgradeShop.getUpgradeLevel("speed_up") - 1) * 0.075f;
        float speedWithUpgrade = (this.speed + speedUp);

        WorldCoords movement = new WorldCoords(0, 0);

        // Vertical axis
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_W)) {
            movement.y += speedWithUpgrade;
        } if (Keyboard.getKeyDown(GLFW.GLFW_KEY_S)) {
            movement.y -= speedWithUpgrade;
        }

        // Horizontal axis
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_A)) {
            movement.x -= speedWithUpgrade;
        } if (Keyboard.getKeyDown(GLFW.GLFW_KEY_D)) {
            movement.x += speedWithUpgrade;
        }

        // Prevent horizontal movement being sqrt(2) times as fast (due to Pythagoras)
        if (movement.x != 0 && movement.y != 0) {
            double radians = Math.toRadians(45);

            movement.x *= Math.cos(radians);
            movement.y *= Math.sin(radians);
        }

        return movement;
    }

    private void move() {
        WorldCoords movement = this.getVelocity();

        // Move
        stepUntilColliding(new WorldCoords(movement.x  * (float) Clock.getTimeDelta(), 0), 3);
        stepUntilColliding(new WorldCoords(0, movement.y * (float) Clock.getTimeDelta()), 3);
    }

    private void setRotation() {
        WorldCoords middle = WorldCoords.getMiddle();
        WorldCoords mouseCoords = Mouse.getMousePos();

        float angle = (float) (Math.atan2(mouseCoords.y - middle.y, mouseCoords.x - middle.x));
        this.getRect().getTransform().setLocalRotation(angle);
    }

    public void dealDamage(float damage) {
        this.healthContainer.takeDamage(damage);
    }

    public float getHealth() {
        return this.healthContainer.getHealth();
    }

    public float getMaxHealth() {
        return this.healthContainer.getMaxHealth();
    }

    public PlayerBank getBank() {
        return this.bank;
    }

    @Override
    public void close() {
        this.shaderProgram.close();
        super.close();
    }
}
