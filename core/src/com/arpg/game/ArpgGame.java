package com.arpg.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class ArpgGame extends Game {
	private SpriteBatch batch;

	// Домашнее задание:
	// - При ударе по тигру, есть 20% вероятность что он разозлится, и будет секунд 20
	// гоняться за персонажем
	// - * Добавление экрана меню
	// - * Поправить отрисовку персонажей возле деревьев

	// Идеи:
	// - Сделать волны
	// - Добавить вылетающие повер-апсы
	// - Добавить дальний тип атак
	// - Разнообразить монстров
	// - Добавить Score
	// - Таблица High Score
	// - Добавить выносливость (ограничение на постоянные удары и бег)

	// План игры:
	// - Каждую минуту появляется волна монстров, старые монстры никуда не деваются
	// - В каждой волне по 5 монстров
	// - Босс на каждой 5-й волне
	// - Выпадающее оружие, с возможностью его взять, но навсегда выбросить старое
	// - Монстры появляются на краю экрана
	// - Если монстры 2 минуты не сталкиваются с игроком, начинают драться между собой
	// - Если монстр набил 3 уровня, у него появляется жажда крови и он охотится на игрока
	// - Когда монстр убивает монстра, он полностью залечивается
	// - Вопрос: Как зависит сила монстров от номера волны? [monsterLevel = (int)(waveLevel / 3)]


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
