package ui;

import game.TextureMap;
import jangl.color.Color;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.graphics.shaders.premade.DefaultVertShader;
import jangl.graphics.textures.Image;
import jangl.graphics.textures.MutableTexture;
import jangl.graphics.textures.Texture;
import jangl.graphics.textures.TextureBuilder;
import jangl.shapes.Rect;

public class Bar implements AutoCloseable {
    private final Image icon;
    private final Rect bar;
    private final float maxWidth;
    private final ShaderProgram barShader;
    private final WorldCoords originalCenter;
    private final Rect barOutlineRect;
    private final Texture barOutlineTexture;

    public Bar(String iconID, WorldCoords topLeft, float width, float height, Color barColor) {
        this.icon = new Image(
                new Rect(
                        topLeft, height, height
                ),

                TextureMap.get(iconID)
        );

        WorldCoords barTopLeft = new WorldCoords(topLeft.x + height + 0.01f, topLeft.y);

        this.bar = new Rect(barTopLeft, width, height);
        this.maxWidth = this.bar.getWidth();
        this.barShader = new ShaderProgram(new DefaultVertShader(), new ColorShader(barColor));
        this.barShader.getVertexShader().setObeyCamera(false);
        this.originalCenter = this.bar.getTransform().getCenter();

        this.barOutlineTexture = this.getOutline(barColor);

        // This formula works since the bar outline is 0.005 worldCoords per WorldCoords unit of width/height
        WorldCoords barOutlineTopLeft = new WorldCoords(
                barTopLeft.x - 0.005f * this.bar.getWidth(), barTopLeft.y + 0.005f * this.bar.getHeight()
        );
        this.barOutlineRect = new Rect(
                barOutlineTopLeft,
                this.bar.getWidth() + 0.005f,
                this.bar.getHeight() + 0.005f
        );
    }

    private MutableTexture getOutline(Color barColor) {
        Color outlineColor = ColorFactory.fromNormalized(
                Math.max(0, barColor.getNormRed() - 0.2f),
                Math.max(0, barColor.getNormGreen() - 0.2f),
                Math.max(0, barColor.getNormBlue() - 0.2f),
                Math.max(0, barColor.getNormAlpha())
        );

        MutableTexture mutableTexture = new MutableTexture(
                new TextureBuilder().fill(
                        outlineColor,
                        // Multiply by 200 so the thickness is equal to 1 pixel per 0.005 WorldCoords
                        (int) (200 * this.bar.getWidth()),
                        (int) (200 * this.bar.getHeight())
                ).setObeyCamera(false)
        );

        for (int x = 1; x < mutableTexture.width - 1; x++) {
            for (int y = 1; y < mutableTexture.height - 1; y++) {
                mutableTexture.setPixelAt(x, y, ColorFactory.from255(0, 0, 0, 0));
            }
        }

        return mutableTexture;
    }

    public void setPercentage(float percentage) {
        this.bar.setWidth(this.maxWidth * percentage);
        this.bar.getTransform().setPos(this.originalCenter.x - this.maxWidth * (1 - percentage) / 2, this.originalCenter.y);
    }

    public void draw() {
        this.icon.draw();

        this.bar.draw(this.barShader);
        this.barOutlineRect.draw(this.barOutlineTexture);
    }

    @Override
    public void close() {
        this.icon.rect().close();
        this.barOutlineTexture.close();
        this.barOutlineRect.close();
        this.bar.close();
    }
}
