package ui;

import game.TextureMap;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.textures.Image;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;

public class UpgradeItem implements AutoCloseable {
    public static final float IMAGE_HEIGHT_WIDTH = 0.2f;
    public static final float TEXT_HEIGHT = 0.05f;
    private final Image image;
    private final Text text;
    private final String name;
    private float price;

    public UpgradeItem(WorldCoords topLeft, String imageID, Font font, String name, float price) {
        this.image = new Image(
                new Rect(topLeft, IMAGE_HEIGHT_WIDTH, IMAGE_HEIGHT_WIDTH),
                TextureMap.get(imageID)
        );

        WorldCoords textTopLeft = new WorldCoords(topLeft.x, topLeft.y - IMAGE_HEIGHT_WIDTH - 0.01f);
        this.text = new Text(
                textTopLeft,
                font,
                TEXT_HEIGHT,
                getTextToDisplay(name, price)
        );

        this.name = name;
        this.price = price;
    }

    private static String getTextToDisplay(String name, float price) {
        return name + "\n$" + Math.round(price);
    }

    public void setPrice(float newPrice) {
        this.price = newPrice;
        this.text.setText(getTextToDisplay(this.name, newPrice));
    }

    public float getPrice() {
        return this.price;
    }

    public boolean wasSelected(MouseEvent mouseEvent) {
        return mouseEvent.action == GLFW.GLFW_PRESS &&
                mouseEvent.button == GLFW.GLFW_MOUSE_BUTTON_1 &&
                Shape.collides(this.image.rect(), Mouse.getMousePos());
    }

    public void draw() {
        this.image.draw();
        this.text.draw();
    }

    @Override
    public void close() {
        this.image.rect().close();
        this.text.close();
    }
}
