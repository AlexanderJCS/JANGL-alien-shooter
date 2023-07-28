package ui.upgrades;

import game.SoundPlayer;
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
                        new WorldCoords(0.2f, 0.8f),
                        "pierce",
                        Consts.FONT,
                        "Pierce",
                        Consts.SETTINGS.getFloat("upgrades/pierce_cost"),
                        Consts.SETTINGS.getInt("upgrades/max_pierce_upgrade")
                )
        );

        this.upgradeItems.put(
                "speed_up",
                new UpgradeItem(
                        new WorldCoords(0.5f, 0.8f),
                        "speed_up",
                        Consts.FONT,
                        "Speed",
                        Consts.SETTINGS.getFloat("upgrades/speed_cost"),
                        Consts.SETTINGS.getInt("upgrades/max_speed_upgrade")
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
                if (!item.wasSelected(event)) {
                    continue;
                }

                if (this.bank.getMoney() < item.getPrice() || item.atMaxUpgrade()) {
                    SoundPlayer.playSound("cant_buy_item");
                    continue;
                }

                this.bank.addMoney(-item.getPrice());
                item.setPrice(item.getPrice() * 2);
                item.incrementUpgradeLevel();
                SoundPlayer.playSound("buy_item");
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
