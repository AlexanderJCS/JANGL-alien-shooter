package ui.upgrades;

import game.TextureMap;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.textures.Texture;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;

public class UpgradeItem implements AutoCloseable {
    public static final float IMAGE_HEIGHT_WIDTH = 0.2f;
    public static final float TEXT_HEIGHT = 0.035f;
    private final Rect rect;
    private final Texture texture;
    private final Text text;
    private final String name;
    private float price;
    private int upgradeLevel;
    private final int maxUpgrade;

    public UpgradeItem(WorldCoords topLeft, String imageID, Font font, String name, float price, int maxUpgradeLevel) {
        this.rect = new Rect(topLeft, IMAGE_HEIGHT_WIDTH, IMAGE_HEIGHT_WIDTH);
        this.texture = TextureMap.get(imageID);

        this.name = name;
        this.upgradeLevel = 1;
        this.maxUpgrade = maxUpgradeLevel;

        WorldCoords textTopLeft = new WorldCoords(topLeft.x, topLeft.y - IMAGE_HEIGHT_WIDTH - 0.01f);
        this.text = new TextBuilder(font, getTextToDisplay(name, price, this.upgradeLevel))
                .setCoords(textTopLeft)
                .setYHeight(TEXT_HEIGHT)
                .toText();

        this.setPrice(price);
    }

    private static String getTextToDisplay(String name, float price, int upgradeLevel) {
        return name + "\n$" + Math.round(price) + "\nLvl " + upgradeLevel;
    }

    public void setPrice(float newPrice) {
        this.price = newPrice;

        if (this.upgradeLevel < this.maxUpgrade - 1 || this.maxUpgrade < 0) {
            this.text.setText(getTextToDisplay(this.name, newPrice, this.upgradeLevel));
        } else {
            this.text.setText("MAX");
        }
    }

    public float getPrice() {
        return this.price;
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    public void incrementUpgradeLevel() {
        this.upgradeLevel++;
    }

    public boolean wasSelected(MouseEvent mouseEvent) {
        return mouseEvent.action == GLFW.GLFW_PRESS &&
                mouseEvent.button == GLFW.GLFW_MOUSE_BUTTON_1 &&
                Shape.collides(this.rect, Mouse.getMousePos());
    }

    public boolean atMaxUpgrade() {
        return this.upgradeLevel == this.maxUpgrade;
    }

    public void draw() {
        this.rect.draw(this.texture);
        this.text.draw();
    }

    @Override
    public void close() {
        this.rect.close();
        this.text.close();
    }
}
