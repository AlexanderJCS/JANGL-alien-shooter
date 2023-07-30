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

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    private final float speed;
    private final List<Wall> walls;
    private final ShaderProgram shaderProgram;
    private final HealthContainer healthContainer;
    private final UpgradeShop upgradeShop;
    private final PlayerBank bank;
    private final List<LaserGun> laserGuns;
    private final List<Enemy> aliens;
    private final OverheatShaderFrag overheatShader;

    public Player(List<Wall> walls, List<Enemy> aliens, float speed) {
        super(new Rect(new WorldCoords(0, 0), 0.075f, 0.075f), "player");


        this.bank = new PlayerBank();
        this.upgradeShop = new UpgradeShop(this.bank);
        this.laserGuns = new ArrayList<>();
        this.laserGuns.add(
                new LaserGun(this, walls, aliens, this.upgradeShop, 0)
        );
        this.aliens = aliens;

        this.speed = speed;
        this.walls = walls;

        this.getTexture().useDefaultShader(false);

        this.overheatShader = new OverheatShaderFrag(this.laserGuns.get(0).getOverheat());
        this.shaderProgram = new ShaderProgram(new TextureShaderVert(), this.overheatShader, TextureShaderVert.getAttribLocations());
        this.healthContainer = new HealthContainer(
                Consts.SETTINGS.getFloat("player/health"),
                Consts.SETTINGS.getFloat("player/invincibility"),
                Consts.SETTINGS.getFloat("player/regen"),
                "hurt"
        );

        this.getRect().getTransform().setPos(new WorldCoords(0, 0));
    }

    @Override
    public void draw() {
        for (LaserGun laserGun : this.laserGuns) {
            laserGun.drawLasers();
        }

        if (this.healthContainer.onCooldown() && Math.round(GLFW.glfwGetTime() * 20) % 2 == 0) {
            return;
        }

        for (LaserGun laserGun : this.laserGuns) {
            laserGun.draw();
        }

        this.shaderProgram.bind();
        this.image.draw();
        this.shaderProgram.unbind();
    }

    private void checkGunUpgrade() {
        int numGuns = this.upgradeShop.getUpgradeLevel("gun_up");
        if (numGuns == this.laserGuns.size()) {
            return;
        }

        for (LaserGun laserGun : this.laserGuns) {
            laserGun.close();
        }
        this.laserGuns.clear();

        float jump = (float) (2 * Math.PI / numGuns);
        for (int i = 0; i < numGuns; i++) {
            this.laserGuns.add(
                    new LaserGun(
                            this,
                            this.walls,
                            this.aliens,
                            this.upgradeShop,
                            jump * i
                    )
            );
        }

        this.overheatShader.setGunOverheat(this.laserGuns.get(0).getOverheat());
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

        for (LaserGun laserGun : this.laserGuns) {
            laserGun.update(this.getRect().getTransform(), this.getBank());
        }

        this.healthContainer.setRegen((float) (Consts.SETTINGS.getFloat("player/regen") + 0.025 * this.upgradeShop.getUpgradeLevel("regen")));
        this.healthContainer.update();
        this.checkGunUpgrade();
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
        float speedUp = (this.upgradeShop.getUpgradeLevel("speed_up") - 1) * 0.075f;
        float amountToMove = (float) ((this.speed + speedUp) * Clock.getTimeDelta());

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

    public float getRotation() {
        return this.getRect().getTransform().getLocalRotationAngle();
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
