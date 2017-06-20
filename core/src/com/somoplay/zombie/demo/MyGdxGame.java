package com.somoplay.zombie.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private BitmapFont font;

	private Texture texture;
	private Sprite sprite;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		font = new BitmapFont();
		//font = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"),false);
		font.setColor(Color.RED);

		texture = new Texture(Gdx.files.internal("data/01.png"));
		sprite = new Sprite(texture);
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		font.dispose();
		texture.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//batch.draw(img, 0, 0);

		//font.draw(batch, "Hello World", 500, 500);
		sprite.draw(batch);

		batch.end();
	}

}
