package ui;

import jangl.color.Color;
import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.graphics.shaders.premade.DefaultVertShader;
import jangl.graphics.textures.Image;
import jangl.graphics.textures.Texture;
import jangl.shapes.Rect;

public class Bar implements AutoCloseable {
    private final Image icon;
    private final Rect bar;
    private final float maxWidth;
    private final ShaderProgram barShader;
    private final WorldCoords originalCenter;

    public Bar(Texture icon, WorldCoords topLeft, float width, float height, Color barColor) {
        this.icon = new Image(
                new Rect(
                        topLeft, height, height
                ),

                icon
        );

        icon.getShaderProgram().getVertexShader().setObeyCamera(false);

        WorldCoords barTopLeft = new WorldCoords(topLeft.x + height + 0.01f, topLeft.y);

        this.bar = new Rect(barTopLeft, width, height);
        this.maxWidth = this.bar.getWidth();
        this.barShader = new ShaderProgram(new DefaultVertShader(), new ColorShader(barColor));
        this.barShader.getVertexShader().setObeyCamera(false);
        this.originalCenter = this.bar.getTransform().getCenter();
    }

    public void setPercentage(float percentage) {
        this.bar.setWidth(this.maxWidth * percentage);
        this.bar.getTransform().setPos(this.originalCenter.x - this.maxWidth * (1 - percentage) / 2, this.originalCenter.y);
    }

    public void draw() {
        this.icon.draw();

        this.bar.draw(this.barShader);
    }

    @Override
    public void close() {
        this.icon.rect().close();
        this.bar.close();
    }
}
