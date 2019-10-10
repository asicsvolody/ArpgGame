package com.arpg.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class ArpgGame extends Game {
	private SpriteBatch batch;

	// Домашнее задание:
	// - Добавить выносливость, запас 100 ед., при ударе отнимается 20.
	// Если выносливости не хватает, то бить не получится. Выносливость должна
	// восполняться с какой-то скоростью. Когда герой стоит, выносливость
	// восстанавливается быстрее, чем когда он идет, а когда бежит, она еще
	// и тратится;
	// - Добавить получение Score героем за уничтожение монстров;
	// - ** Как вы видите реализации драки монстров между собой?
	// - Заметка: приветствуются любые улучшения

	// Идеи:
	// - Добавить дальний тип атак
	// - Разнообразить монстров
	// - Таблица Hi

	@Override
	public void create () {
		this.batch = new SpriteBatch();
		ScreenManager.getInstance().init(this, batch);
		ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float dt = Gdx.graphics.getDeltaTime();
		getScreen().render(dt);

	}



	@Override
	public void dispose () {
		batch.dispose();

	}

}
