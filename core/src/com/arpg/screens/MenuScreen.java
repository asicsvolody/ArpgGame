package com.arpg.screens;

import com.arpg.utils.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen extends AbstractScreen {

    private Stage stage;


    private BitmapFont font34;


    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    private void init(){
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("smButton");
        textButtonStyle.font = font34;
        skin.add("smallButtonStyle", textButtonStyle);

        Button btnPlay = new TextButton("Play", skin, "smallButtonStyle");
        Button btnExit = new TextButton("Exit", skin, "smallButtonStyle");
        Group menuGroup = new Group();
        menuGroup.addActor(btnPlay);
        menuGroup.addActor(btnExit);
        btnPlay.setPosition(0, 0);
        btnExit.setPosition(140, 0);
        menuGroup.setPosition(480, 360);
        stage.addActor(menuGroup);
        skin.dispose();

        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

    }

    @Override
    public void show() {
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.font34 = Assets.getInstance().getAssetManager().get("fonts/font34.ttf");
        init();
        ScreenManager.getInstance().getCamera().position.set(ScreenManager.HALF_WORLD_WIDTH, ScreenManager.HALF_WORLD_HEIGHT, 0);
        ScreenManager.getInstance().getCamera().update();
        batch.setProjectionMatrix(ScreenManager.getInstance().getCamera().combined);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

}
