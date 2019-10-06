package com.arpg.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Map {
    public enum BlockType {
        EMPTY, WALL;
    }

    public static final int CELL_SIZE = 80;
    public static final int MAP_SIZE_X = 24;
    public static final int MAP_SIZE_X_PX = MAP_SIZE_X * CELL_SIZE;
    public static final int MAP_SIZE_Y = 14;
    public static final int MAP_SIZE_Y_PX = MAP_SIZE_Y * CELL_SIZE;

    private BlockType[][] data;
    private TextureRegion[] textureWall;
    private TextureRegion textureGrass;

    public Map() {
        this.data = new BlockType[MAP_SIZE_X][MAP_SIZE_Y];

        this.textureGrass = Assets.getInstance().getAtlas().findRegion("Grass");
        this.textureWall = new TextureRegion(Assets.getInstance().getAtlas().findRegion("trees")).split(80, 120)[0];
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                data[i][j] = BlockType.EMPTY;
                if (MathUtils.random() < 0.05) {
                    data[i][j] = BlockType.WALL;
                }
            }
        }
    }

    public void setRefVectorToEmptyPoint(Vector2 refInput) {
        do {
            refInput.set(MathUtils.random(0, Map.MAP_SIZE_X_PX), MathUtils.random(0, Map.MAP_SIZE_Y_PX));
        } while (!isCellPassable(refInput));
    }

    public void renderGround(SpriteBatch batch, int x, int y) {
        batch.draw(textureGrass, x * 80, y * 80);
    }

    public void renderWalls(SpriteBatch batch, int x, int y) {
        if (data[x][y] == BlockType.WALL) {
            batch.draw(textureWall[0], x * 80, y * 80);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                batch.draw(textureGrass, i * 80, j * 80);
                if (data[i][j] == BlockType.WALL) {
                    batch.draw(textureWall[0], i * 80 , j * 80 );
                }
            }
        }
    }

    public boolean isCellPassable(Vector2 position) {
        if (position.x < 0.0f || position.y < 0.0f) {
            return false;
        }
        int cellX = (int) (position.x / 80);
        int cellY = (int) (position.y / 80);
        if (cellX < 0 || cellX >= MAP_SIZE_X || cellY < 0 || cellY >= MAP_SIZE_Y) {
            return false;
        }
        return data[cellX][cellY] == BlockType.EMPTY;
    }

    public int isCellPassable(int x, int y){
        if(data[x][y] == BlockType.EMPTY)
            return 1;
        return -1;
    }


}
