package ui.upgrades;

import game.gameobjects.player.PlayerBank;
import helper.Consts;
import helper.EventsManager;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Rect;

import java.util.HashMap;
import java.util.Map;

public class UpgradeShop implements AutoCloseable {
    private final Map<String, UpgradeItem> upgradeItems;
    private final Rect background;
    private final ShaderProgram backgroundShader;
    private final PlayerBank bank;

    public UpgradeShop(PlayerBank bank) {
        this.bank = bank;

        this.upgradeItems = new HashMap<>();
        this.upgradeItems.put(
                "pierce",
                new UpgradeItem(
                        new WorldCoords(0.1f, 0.9f),
                        "pierce",
                        Consts.FONT,
                        "Pierce",
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

        for (UpgradeItem item : this.upgradeItems.values()) {
            item.draw();
        }
    }

    public void update() {
        // Increment the price by 1 if the item was bought
        for (MouseEvent event : EventsManager.getMouseEvents()) {
            for (UpgradeItem item : this.upgradeItems.values()) {
                if (!item.wasSelected(event) || this.bank.getMoney() < item.getPrice()) {
                    continue;
                }

                this.bank.addMoney(-item.getPrice());
                item.setPrice(item.getPrice() + 1);
                item.incrementUpgradeLevel();
            }
        }
    }

    public int getUpgradeLevel(String id) {
        return this.upgradeItems.get(id).getUpgradeLevel();
    }

    @Override
    public void close() {
        for (UpgradeItem item : this.upgradeItems.values()) {
            item.close();
        }

        this.background.close();
        this.backgroundShader.close();
    }
}
