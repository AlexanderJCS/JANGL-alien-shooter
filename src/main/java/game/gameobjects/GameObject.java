package game.gameobjects;

import game.TextureMap;
import jangl.coords.WorldCoords;
import jangl.graphics.Camera;
import jangl.graphics.textures.Image;
import jangl.graphics.textures.Texture;
import jangl.shapes.Rect;


public class GameObject implements AutoCloseable {
    protected final Image image;

    public GameObject(Rect rect, String textureID) {
        this.image = new Image(rect, TextureMap.get(textureID));
    }

    public Rect getRect() {
        return this.image.rect();
    }

    public Texture getTexture() {
        return this.image.texture();
    }

    public void draw() {
        this.image.draw();
    }

    public void update() {

    }

    @Override
    public void close() {
        this.image.rect().close();
    }
}
