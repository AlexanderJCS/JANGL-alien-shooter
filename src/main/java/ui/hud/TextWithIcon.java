package ui.hud;

import game.TextureMap;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.textures.Image;
import jangl.shapes.Rect;

public class TextWithIcon implements AutoCloseable {
    private final Text text;
    private final Image icon;

    public TextWithIcon(WorldCoords topLeft, Font font, String text, float height, String iconID) {
        this.icon = new Image(
                new Rect(
                        new WorldCoords(topLeft.x, topLeft.y),
                        height, height
                ),
                TextureMap.get(iconID)
        );

        WorldCoords textTopLeft = new WorldCoords(
                topLeft.x + height + 0.01f, topLeft.y - 0.005f
        );

        this.text = new TextBuilder(font, text).setCoords(textTopLeft).setYHeight(height).toText();
    }

    public Text getText() {
        return this.text;
    }

    public void draw() {
        this.icon.draw();
        this.text.draw();
    }

    @Override
    public void close() {
        try {
            this.icon.shape().close();
        } catch (Exception ignored) {}

        this.text.close();
    }
}
