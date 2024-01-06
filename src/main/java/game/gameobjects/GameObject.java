package game.gameobjects;

import game.TextureMap;
import jangl.graphics.textures.Texture;
import jangl.shapes.Rect;


public class GameObject implements AutoCloseable {
    protected final Rect rect;
    protected final Texture texture;

    public GameObject(Rect rect, String textureID) {
        this.rect = rect;
        this.texture = TextureMap.get(textureID);
    }

    public Rect getRect() {
        return this.rect;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void draw() {
        this.rect.draw(this.texture);
    }

    public void update() {

    }

    @Override
    public void close() {
        this.rect.close();
    }
}
