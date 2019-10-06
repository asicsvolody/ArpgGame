package com.arpg.game.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PathFindingGame extends Game {
    private static class CellNode implements Comparable<CellNode> {
        private int x, y;
        private CellNode from;
        private int cost, priority;
        private boolean passable;
        private boolean processed;
        private List<CellNode> neighbors;

        public CellNode(int x, int y) {
            this.x = x;
            this.y = y;
            this.neighbors = new ArrayList<>();
            this.passable = true;
        }

        @Override
        public int compareTo(CellNode o) {
            return this.priority - o.priority;
        }

        @Override
        public String toString() {
            return "C{" + cost + '}';
        }
    }

    private static final int MAP_SIZE_X = 24;
    private static final int MAP_SIZE_Y = 14;
    private static final int CELL_SIZE = 80;

    private int[][] data;
    private CellNode[][] nodes;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font24;

    private int srcX = 1, srcY = 1;
    private int dstX = 8, dstY = 8;

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setAutoShapeType(true);
        this.data = new int[MAP_SIZE_X][MAP_SIZE_Y];
        this.nodes = new CellNode[MAP_SIZE_X][MAP_SIZE_Y];
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                this.nodes[i][j] = new CellNode(i, j);
                this.data[i][j] = 3;
                if (MathUtils.random(100) < 25) {
                    this.data[i][j] = 5;
                }
                if (j == 4) {
                    this.data[i][j] = 1;
                }
            }
        }
        for (int i = 0; i < 30; i++) {
            int rad = MathUtils.random(3, 12);
            int x = MathUtils.random(0, MAP_SIZE_X);
            int y = MathUtils.random(0, MAP_SIZE_Y);
            for (int j = -rad; j <= rad; j++) {
                for (int k = -rad; k < rad; k++) {
                    if (x + j >= 0 && x + j < MAP_SIZE_X && y + k >= 0 && y + k < MAP_SIZE_Y && (int) Math.sqrt(j * j + k * k) < rad) {
                        data[x + j][y + k] = -1;
                    }
                }
            }
        }

        AssetManager assetManager = new AssetManager();
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "/java/GeekBrains/LibGDX/Lesson 4/TowerDefenseGame/core/assets/fonts/atarian.ttf";
        fontParameter.fontParameters.size = 18;
        fontParameter.fontParameters.color = Color.WHITE;
        fontParameter.fontParameters.borderWidth = 1;
        fontParameter.fontParameters.borderColor = Color.BLACK;
        assetManager.load("/java/GeekBrains/LibGDX/Lesson 4/TowerDefenseGame/core/assets/fonts/zorque24.ttf", BitmapFont.class, fontParameter);
        assetManager.finishLoading();
        font24 = assetManager.get("/java/GeekBrains/LibGDX/Lesson 4/TowerDefenseGame/core/assets/fonts/zorque24.ttf");

        buildRoute();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin();
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                shapeRenderer.setColor(0, 1 - data[i][j] * 0.1f, 0, 1);
                if (data[i][j] == 1) {
                    shapeRenderer.setColor(0.8f, 0.7f, 0, 1);
                }
                if (data[i][j] == 15) {
                    shapeRenderer.setColor(1, 0, 0, 1);
                }
                if (data[i][j] == -1) {
                    shapeRenderer.setColor(0, 0, 0, 1);
                }
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.rect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // shapeRenderer.setColor(0, 0.3f, 0, 1);
                // shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                // shapeRenderer.rect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.circle(srcX * CELL_SIZE + CELL_SIZE / 2, srcY * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 3, CELL_SIZE / 3);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.circle(dstX * CELL_SIZE + CELL_SIZE / 2, dstY * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 3, CELL_SIZE / 3);
        shapeRenderer.setColor(1, 1, 1, 1);

        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        CellNode path = nodes[dstX][dstY];

        while (path.from != null) {
            shapeRenderer.line(path.x * CELL_SIZE + CELL_SIZE / 2, path.y * CELL_SIZE + CELL_SIZE / 2, path.from.x * CELL_SIZE + CELL_SIZE / 2, path.from.y * CELL_SIZE + CELL_SIZE / 2);
            path = path.from;
        }

        shapeRenderer.end();
        batch.begin();
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                // font24.draw(batch, "" + nodes[i][j].priority + "." + nodes[i][j].cost, i * CELL_SIZE, j * CELL_SIZE + 46, CELL_SIZE, 1, false);
            }
        }
        batch.end();
        update();
    }

    public void update() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int cx = (int) (Gdx.input.getX() / CELL_SIZE);
            int cy = (int) ((720 - Gdx.input.getY()) / CELL_SIZE);
            if (cx >= 0 && cx < MAP_SIZE_X && cy >= 0 && cy < MAP_SIZE_Y) {
                srcX = cx;
                srcY = cy;
            }
            buildRoute();
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            int cx = (int) (Gdx.input.getX() / CELL_SIZE);
            int cy = (int) ((720 - Gdx.input.getY()) / CELL_SIZE);
            if (cx >= 0 && cx < MAP_SIZE_X && cy >= 0 && cy < MAP_SIZE_Y) {
                dstX = cx;
                dstY = cy;
            }
            buildRoute();
        }
    }

    public void buildRoute() {
        for (int i = 0; i < MAP_SIZE_X; i++) {
            for (int j = 0; j < MAP_SIZE_Y; j++) {
                this.nodes[i][j].neighbors.clear();
                this.nodes[i][j].processed = false;
                this.nodes[i][j].from = null;
                this.nodes[i][j].cost = 0;
                this.nodes[i][j].priority = 0;

                if (i > 0 && data[i - 1][j] != -1) {
                    this.nodes[i][j].neighbors.add(this.nodes[i - 1][j]);
                }
                if (i < MAP_SIZE_X - 1 && data[i + 1][j] != -1) {
                    this.nodes[i][j].neighbors.add(this.nodes[i + 1][j]);
                }
                if (j > 0 && data[i][j - 1] != -1) {
                    this.nodes[i][j].neighbors.add(this.nodes[i][j - 1]);
                }
                if (j < MAP_SIZE_Y - 1 && data[i][j + 1] != -1) {
                    this.nodes[i][j].neighbors.add(this.nodes[i][j + 1]);
                }
            }
        }

        nodes[srcX][srcY].from = null;
        nodes[srcX][srcY].cost = 0;

        PriorityQueue<CellNode> frontier = new PriorityQueue<CellNode>(1000);
        frontier.add(nodes[srcX][srcY]);

        while (!frontier.isEmpty()) {
            CellNode current = frontier.poll();
            current.processed = true;

            if (current.x == dstX && current.y == dstY) {
                break;
            }

            for (int i = 0; i < current.neighbors.size(); i++) {
                CellNode next = current.neighbors.get(i);
                int newCost = current.cost + data[next.x][next.y];
                if (!next.processed && (next.from == null || newCost < next.cost)) {
                    next.cost = newCost;
                    next.priority = newCost + (Math.abs(next.x - dstX) + Math.abs(next.y - dstY)) * 1;
                    next.from = current;
                    if (!frontier.contains(next)) {
                        frontier.add(next);
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}