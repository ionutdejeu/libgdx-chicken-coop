package com.chickencoop.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.chickencoop.screens.GameplayScreen;

public class ChickenCoop extends Game {
	static public Skin gameSkin;

	@Override
	public void create() {

		gameSkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
		GameplayScreen main_screen = new GameplayScreen(this);
		//main_screen.create();
		this.setScreen(main_screen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}
}
