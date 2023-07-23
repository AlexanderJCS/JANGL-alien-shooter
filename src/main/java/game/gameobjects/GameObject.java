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
        // Implement a quick form of culling
        WorldCoords cameraCenter = Camera.getCenter();
        Rect rect = this.getRect();
        WorldCoords rectCenter = rect.getTransform().getCenter();

        double rectRadiusSquared = Math.pow(rect.getWidth() / 2, 2) + Math.pow(rect.getHeight() / 2, 2);
        double windowRadiusSquared = Math.pow(WorldCoords.getMiddle().x, 2) + Math.pow(WorldCoords.getMiddle().y, 2);
        double distanceSquared = Math.pow(cameraCenter.x - rectCenter.x, 2) + Math.pow(cameraCenter.y - rectCenter.y, 2);

        // Square roots are needed to prevent objects from being culled near the edges of the screen when they shouldn't
        if (Math.sqrt(rectRadiusSquared) + Math.sqrt(windowRadiusSquared) >= Math.sqrt(distanceSquared)) {
            this.image.draw();
        }
    }

    public void update() {

    }

    @Override
    public void close() {
        this.image.rect().close();
    }
}
