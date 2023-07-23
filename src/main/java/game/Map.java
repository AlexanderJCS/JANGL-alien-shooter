package game;

import game.gameobjects.Floor;
import game.gameobjects.Wall;
import jangl.coords.WorldCoords;
import jangl.graphics.textures.TextureBuilder;
import jangl.shapes.Rect;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Map implements AutoCloseable {
    public static final float CUBE_DIMENSIONS = 0.4f;
    private final List<Wall> walls;
    private final List<WorldCoords> spawnLocations;
    private final Floor floor;

    public Map() {
        TextureBuilder map = new TextureBuilder().setImagePath("src/main/resources/map.png");
        WorldCoords topLeft = new WorldCoords(
                -1 * map.getWidth() / 2f * CUBE_DIMENSIONS,
                -1 * map.getHeight() / 2f * CUBE_DIMENSIONS
        );

        this.walls = parseMap(map, topLeft);
        this.spawnLocations = getSpawnLocations(map, topLeft);

        this.floor = new Floor(
                new Rect(
                        new WorldCoords(topLeft.x, topLeft.y * -1),
                        Math.abs(topLeft.x) * 2,
                        Math.abs(topLeft.y) * 2
                )
        );

        this.floor.getRect().setTexRepeatX(Math.abs(topLeft.x) * 2 / CUBE_DIMENSIONS);
        this.floor.getRect().setTexRepeatY(Math.abs(topLeft.y) * 2 / CUBE_DIMENSIONS);
    }

    public void drawFloor() {
        this.floor.draw();
    }

    public void drawWalls() {
        for (Wall wall : this.walls) {
            wall.draw();
        }
    }

    private static int numOfWallsInARow(byte[] data, int offset, int currentXValue, int maxXValue) {
        int inARow = 1;

        for (int i = offset; i < data.length && currentXValue < maxXValue; i += 4) {
            if (data[i] != 0) {
                break;
            }

            if (i != offset) {
                inARow++;
            }

            currentXValue++;
        }

        return inARow;
    }

    private static List<WorldCoords> getSpawnLocations(TextureBuilder map, WorldCoords topLeft) {
        ByteBuffer data = map.getImageData();
        data.rewind();

        byte[] dataArray = new byte[data.remaining()];
        data.get(dataArray);

        List<WorldCoords> spawnLocation = new ArrayList<>();

        // Add by 4 since we only want to look at the red element of the array
        // so skip blue, green, and alpha
        for (int i = 0; i < dataArray.length; i += 4) {
            byte redValue = dataArray[i];

            if (redValue == 0 || redValue == -1) {
                continue;
            }

            int xValue = (i / 4) % map.getWidth() + 1;
            int yValue = i / 4 / map.getWidth() + 1;

            WorldCoords middleOfGridSpace = new WorldCoords(
                    topLeft.x + CUBE_DIMENSIONS * xValue + CUBE_DIMENSIONS / 2,
                    topLeft.y + CUBE_DIMENSIONS * yValue - CUBE_DIMENSIONS / 2
            );

            spawnLocation.add(middleOfGridSpace);
        }

        return spawnLocation;
    }

    private static List<Wall> parseMap(TextureBuilder map, WorldCoords topLeft) {
        ByteBuffer data = map.getImageData();
        data.rewind();

        byte[] dataArray = new byte[data.remaining()];
        data.get(dataArray);

        List<Wall> walls = new ArrayList<>();

        // Add by 4 since we only want to look at the red element of the array
        // so skip blue, green, and alpha
        for (int i = 0; i < dataArray.length; i += 4) {
            byte redValue = dataArray[i];

            if (redValue != 0) {
                continue;
            }

            int xValue = (i / 4) % map.getWidth() + 1;
            int yValue = i / 4 / map.getWidth() + 1;

            int wallsInARow = numOfWallsInARow(dataArray, i, xValue, map.getWidth());

            Rect rect = new Rect(
                    new WorldCoords(
                            topLeft.x + CUBE_DIMENSIONS * xValue,
                            topLeft.y + CUBE_DIMENSIONS * yValue
                    ),
                    CUBE_DIMENSIONS * wallsInARow, CUBE_DIMENSIONS
            );

            rect.setTexRepeatX(wallsInARow);
            rect.setTexRepeatY(1);

            i += (wallsInARow - 1) * 4;  // skip the number of iterations wallsInARow skipped
            walls.add(new Wall(rect));
        }

        return walls;
    }

    public List<Wall> getWalls() {
        return this.walls;
    }

    public List<WorldCoords> getSpawnLocations() {
        return this.spawnLocations;
    }

    @Override
    public void close() {
        for (Wall wall : this.walls) {
            wall.close();
        }
    }
}
