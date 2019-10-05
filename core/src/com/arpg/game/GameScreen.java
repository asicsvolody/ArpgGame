package com.arpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends AbstractScreen {
    private Map map;
    private Hero hero;
    private Bestiary bestiary;
    private MonsterController monsterController;
    private InfoController infoController;
    private EffectController effectController;
    private BitmapFont font24;
    private Vector2 mouse;
    private Vector2 tmp;
    private float spawnTimer;
    private List<MapElement>[] drawables;
    private int gameLevel;
    private float gameLevelTimer;
    private Stage stage;
    private boolean paused;

    public MonsterController getMonsterController() {
        return monsterController;
    }

    public EffectController getEffectController() {
        return effectController;
    }

    public Bestiary getBestiary() {
        return bestiary;
    }

    public InfoController getInfoController() {
        return infoController;
    }

    public Map getMap() {
        return map;
    }

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    public Hero getHero() {
        return hero;
    }

    @Override
    public void show() {
        this.map = new Map();
        drawables = new List[map.MAP_SIZE_Y];
        for (int i = 0; i < map.MAP_SIZE_Y; i++) {
            drawables[i] = new ArrayList<>();
        }
        this.hero = new Hero(this);
        this.bestiary = new Bestiary();
        this.monsterController = new MonsterController(this);
        this.gameLevel = 1;
        for (int i = 0; i < 5; i++) {
            this.monsterController.setup(MathUtils.random(gameLevel, gameLevel + 2));
        }
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        this.infoController = new InfoController();
        this.effectController = new EffectController();
        this.mouse = new Vector2(0.0f, 0.0f);
        this.tmp = new Vector2(0.0f, 0.0f);

        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("smButton");
        textButtonStyle.font = font24;
        skin.add("smallButtonStyle", textButtonStyle);

        Button btnPauseGame = new TextButton("Pause", skin, "smallButtonStyle");
        Button btnToMenu = new TextButton("Menu", skin, "smallButtonStyle");
        Group menuGroup = new Group();
        menuGroup.addActor(btnPauseGame);
        menuGroup.addActor(btnToMenu);
        btnPauseGame.setPosition(0, 0);
        btnToMenu.setPosition(140, 0);

        btnPauseGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                paused = !paused;
            }
        });

        btnToMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        menuGroup.setPosition(980, 660);
        stage.addActor(menuGroup);

        skin.dispose();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tmp.set(hero.getPosition());
        if (tmp.x < ScreenManager.HALF_WORLD_WIDTH) {
            tmp.x = ScreenManager.HALF_WORLD_WIDTH;
        }
        if (tmp.y < ScreenManager.HALF_WORLD_HEIGHT) {
            tmp.y = ScreenManager.HALF_WORLD_HEIGHT;
        }
        if (tmp.x > Map.MAP_SIZE_X_PX - ScreenManager.HALF_WORLD_WIDTH) {
            tmp.x = Map.MAP_SIZE_X_PX - ScreenManager.HALF_WORLD_WIDTH;
        }
        if (tmp.y > Map.MAP_SIZE_Y_PX - ScreenManager.HALF_WORLD_HEIGHT) {
            tmp.y = Map.MAP_SIZE_Y_PX - ScreenManager.HALF_WORLD_HEIGHT;
        }

        ScreenManager.getInstance().getCamera().position.set(tmp, 0.0f);
        ScreenManager.getInstance().getCamera().update();
        batch.setProjectionMatrix(ScreenManager.getInstance().getCamera().combined);

        for (int i = 0; i < drawables.length; i++) {
            drawables[i].clear();
        }
        for (int i = 0; i < monsterController.getActiveList().size(); i++) {
            Monster m = monsterController.getActiveList().get(i);
            drawables[m.getCellY()].add(m);
        }
        if (hero.isActive()) {
            drawables[hero.getCellY()].add(hero);
        }
        batch.begin();

        for (int i = Map.MAP_SIZE_Y - 1; i >= 0; i--) {
            for (int j = 0; j < Map.MAP_SIZE_X; j++) {
                map.renderGround(batch, j, i);
            }
        }

        for (int i = Map.MAP_SIZE_Y - 1; i >= 0; i--) {
            for (int j = 0; j < drawables[i].size(); j++) {
                drawables[i].get(j).render(batch, font24);
            }
            for (int j = 0; j < Map.MAP_SIZE_X; j++) {
                map.renderWalls(batch, j, i);
            }
        }

        effectController.render(batch);
        infoController.render(batch, font24);
        batch.end();

        ScreenManager.getInstance().getCamera().position.set(ScreenManager.HALF_WORLD_WIDTH, ScreenManager.HALF_WORLD_HEIGHT, 0);
        ScreenManager.getInstance().getCamera().update();
        batch.setProjectionMatrix(ScreenManager.getInstance().getCamera().combined);
        stage.draw();
    }

    public void gameUpdate(float dt) {
        spawnTimer += dt;
        gameLevelTimer += dt;
        if (gameLevelTimer > 60.0f) {
            gameLevelTimer = 0.0f;
            gameLevel++;
        }
        if (spawnTimer > 10.0f) {
            spawnTimer = 0.0f;
            this.monsterController.setup(MathUtils.random(gameLevel, gameLevel + 5));
        }
    }

    public void update(float dt) {
        if (!paused) {
            gameUpdate(dt);
            mouse.set(Gdx.input.getX(), Gdx.input.getY());
            ScreenManager.getInstance().getViewport().unproject(mouse);
            if (hero.isActive()) {
                hero.update(dt);
            }
            monsterController.update(dt);

            for (int i = 0; i < monsterController.getActiveList().size(); i++) {
                Unit m = monsterController.getActiveList().get(i);
                collideUnits(hero, m);
            }
            for (int i = 0; i < monsterController.getActiveList().size() - 1; i++) {
                Unit u1 = monsterController.getActiveList().get(i);
                for (int j = i + 1; j < monsterController.getActiveList().size(); j++) {
                    Unit u2 = monsterController.getActiveList().get(j);
                    collideUnits(u1, u2);
                }
            }

            effectController.update(dt);
            infoController.update(dt);
        }
        stage.act(dt);
    }

    public void collideUnits(Unit u1, Unit u2) {
        if (u1.getArea().overlaps(u2.getArea())) {
            tmp.set(u1.getArea().x, u1.getArea().y);
            tmp.sub(u2.getArea().x, u2.getArea().y);
            float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
            tmp.nor();

            u1.getPosition().mulAdd(tmp, halfInterLen);
            u2.getPosition().mulAdd(tmp, -halfInterLen);

            if (!map.isCellPassable(u1.getPosition())) {
                u1.getPosition().mulAdd(tmp, -halfInterLen);
            }
            if (!map.isCellPassable(u2.getPosition())) {
                u2.getPosition().mulAdd(tmp, halfInterLen);
            }

            u1.getArea().setPosition(u1.getPosition());
            u2.getArea().setPosition(u2.getPosition());
        }
    }
}
