package ui;

import helper.Consts;
import helper.EventsManager;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.graphics.shaders.premade.DefaultVertShader;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Rect;

import java.util.ArrayList;
import java.util.List;

public class UpgradeShop implements AutoCloseable {
    private final List<UpgradeItem> upgradeItems;
    private final Font font;
    private final Rect background;
    private final ShaderProgram backgroundShader;

    public UpgradeShop() {
        this.font = new Font(
                "src/main/resources/font/arial.fnt", "src/main/resources/font/arial.png"
        );

        this.upgradeItems = new ArrayList<>();
        this.upgradeItems.add(
                new UpgradeItem(
                        new WorldCoords(0.1f, 0.9f),
                        "enemyUI",
                        Consts.FONT,
                        "test",
                        0
                )
        );

        this.background = new Rect(new WorldCoords(0, 1), WorldCoords.getMiddle().x * 2, 1);
        this.backgroundShader = new ShaderProgram(
                new ColorShader(ColorFactory.fromNormalized(0, 0, 0, 0.4f))
        );
        this.backgroundShader.getVertexShader().setObeyCamera(false);
    }

    public void draw() {
        this.background.draw(this.backgroundShader);

        for (UpgradeItem item : this.upgradeItems) {
            item.draw();
        }
    }

    public void update() {
        // Increment the price by 1 if the item was bought
        for (MouseEvent event : EventsManager.getMouseEvents()) {
            for (UpgradeItem item : this.upgradeItems) {
                if (item.wasSelected(event)) {
                    item.setPrice(item.getPrice() + 1);
                }
            }
        }
    }

    @Override
    public void close() {
        for (UpgradeItem item : this.upgradeItems) {
            item.close();
        }

        this.background.close();
        this.backgroundShader.close();
    }
}
