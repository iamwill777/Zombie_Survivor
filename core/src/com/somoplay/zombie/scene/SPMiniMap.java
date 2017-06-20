package com.somoplay.zombie.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.somoplay.zombie.asset.SPAssetManager;
import com.somoplay.zombie.main.SPGameScene;
import com.somoplay.zombie.sprite.SPPlayer;
import com.somoplay.zombie.sprite.SPSpriteManager;
import com.somoplay.zombie.sprite.SPZombie;

import java.util.ArrayList;

/**
 * Created by yaolu on 2017-06-15.
 */

public class SPMiniMap implements Disposable {
    public Stage stage;
    //private SPGameScene screen;
    private Viewport viewport;
    private SpriteBatch spriteBatch;
    private SPPlayer player;

    //Mini Map
    private final float beginPosY = Gdx.graphics.getHeight()-272;
    private float worldSizeX;
    private float worldSizeY;
    private Sprite miniMap;
    private Sprite miniPlayer;
    private ArrayList<SPZombie> zombies;
    private Sprite miniZombie;



    public SPMiniMap (SpriteBatch spriteBatch, SPSpriteManager spSpriteManager){
        //this.screen = screen;
        player = spSpriteManager.getPlayer();
        zombies = spSpriteManager.getZombie();
        this.spriteBatch = spriteBatch;
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        //MAP needImprove
        worldSizeX = (float) SPMap.width*15-player.getWidth();
        worldSizeY = (float) SPMap.height*15-player.getHeight();
        Gdx.app.log("x",Float.toString(worldSizeX));
        Gdx.app.log("x",Float.toString(worldSizeY));

        miniMap = new Sprite(SPAssetManager.getInstance().getMap(),399,272);
        miniMap.setPosition(0,beginPosY);

        miniPlayer = new Sprite(SPAssetManager.getInstance().getMiniPlayer());
        miniPlayer.setScale(0.5f);
        miniPlayer.setPosition(0,beginPosY);

        miniZombie = new Sprite(SPAssetManager.getInstance().getZombie());
        miniZombie.setOrigin(0,0);
        miniZombie.setScale(0.06f);
        miniZombie.setPosition(0,beginPosY);

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void render() {
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);

        spriteBatch.begin();
        miniMap.draw(spriteBatch);
        miniPlayer.draw(spriteBatch);
        //drawPlayer(spriteBatch);
        drawZombie(spriteBatch);
        spriteBatch.end();
    }

//    private void drawPlayer(SpriteBatch spriteBatch) {
//        miniPlayer.draw(spriteBatch);
//    }

    private void drawZombie(SpriteBatch spriteBatch) {
        for (SPZombie zombie:zombies){
            miniZombie.draw(spriteBatch);

            float x = zombie.getHitBox().getX()/worldSizeX;
            float y = zombie.getHitBox().getY()/worldSizeY;

            miniZombie.setPosition(340*x+30,beginPosY-10+y*222);

        }
    }

    public void update(float delta) {
        updateMiniPlayer(delta);
    }

    private void updateMiniPlayer(float delta) {
        float x = player.getX()/worldSizeX;
        float y = player.getY()/worldSizeY;

        miniPlayer.setPosition(340*x, beginPosY-30+y*222);
    }
}