package com.somoplay.zombie.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.somoplay.zombie.protocol.SPMainListener;

/**
 * Created by yaolu on 2017-06-14.
 */

public class SPGameCtrl extends Game
{
    private SPMainListener mListener;
    public SpriteBatch batch;

    public SPGameCtrl(SPMainListener listener) {
        mListener = listener;
    }

    @Override
    public void create() {
        //set our GameScreen as our active screen
        SPGameScene spGameScene = new SPGameScene();
        spGameScene.setSpMainListener(mListener);
        setScreen(new SPGameScene());
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
    }

}
