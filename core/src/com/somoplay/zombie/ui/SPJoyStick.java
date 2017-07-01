package com.somoplay.zombie.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Timer;
import com.somoplay.zombie.main.SPGameScene;
import com.somoplay.zombie.scene.SPMap;
import com.somoplay.zombie.scene.SPMapConstant;
import com.somoplay.zombie.sprite.SPPlayer;
import com.somoplay.zombie.web.CodeDefine;

/**
 * Created by yaolu on 2017-06-15.
 */

public class SPJoyStick {
    // for Direction
    private Stage stageDirection;
    private Touchpad tpDirection;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchDirectionBackground;
    private Drawable touchDirectionKnob;
    // for Shooting
    private Stage stageShooting;
    private Touchpad tpShooting;
    private Drawable touchShootingBackground;
    private Drawable touchShootingKnob;

    public static float blockSpeed = 5.0f;
    private SPPlayer mPlayer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera; //2D camera
    InputMultiplexer multiplexer = new InputMultiplexer();

    private Timer shootingTimer = null;
    private float timeForPlayerLocation = 0.0f;
    SPGameScene mGameScreen;


    public SPJoyStick(SPPlayer heroSprite, OrthographicCamera camera, SPGameScene gameScreen) {
        this.mPlayer = heroSprite;
        create(camera);
        mGameScreen = gameScreen;
    }

    public void create (OrthographicCamera camera) {
        spriteBatch = new SpriteBatch();

        // set joystick for Direction
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("resources/touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("resources/touchKnob.png"));
        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchDirectionBackground = touchpadSkin.getDrawable("touchBackground");
        touchDirectionKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchDirectionBackground;
        touchpadStyle.knob = touchDirectionKnob;
        //Create new TouchPad with the created style
        tpDirection = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)s
        tpDirection.setBounds(15, 15, 250, 250);

        //Create a Stage and add TouchPad
        stageDirection = new Stage();
        stageDirection.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageDirection.addActor(tpDirection);
        multiplexer.addProcessor(stageDirection);
        //Gdx.input.setInputProcessor(stageDirection);

        // set joystick for Shooting
        //Create Drawable's from TouchPad skin
        touchShootingBackground = touchpadSkin.getDrawable("touchBackground");
        touchShootingKnob = touchpadSkin.getDrawable("touchKnob");
        //Create new TouchPad with the created style
        tpShooting = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)s
        tpShooting.setBounds(Gdx.graphics.getWidth()-265, 15, 250, 250);

        //Create a Stage and add TouchPad
        stageShooting = new Stage();
        stageShooting.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageShooting.addActor(tpShooting);
        multiplexer.addProcessor(stageShooting);
        Gdx.input.setInputProcessor(multiplexer);

        //connect to hero sprite
        //Set position to centre of the screen
        //player.setPosition(Gdx.graphics.getWidth()/2-player.getWidth()/2, Gdx.graphics.getHeight()/2-player.getHeight()/2);
        mPlayer.setPosition((SPMap.width*15-mPlayer.getWidth())/2.0f, (SPMap.height*15-mPlayer.getHeight())/2.0f);
        this.camera = camera;
        camera.update();
    }

    public void render (OrthographicCamera camera) {

        //Move blockSprite with TouchPad
        mPlayer.checkBounds(tpDirection.getKnobPercentX()*blockSpeed, tpDirection.getKnobPercentY()*blockSpeed);
        if (tpDirection.isTouched()) {
            mGameScreen.getSPMessageHandler().notifyData(CodeDefine.MSG_PLAYER_MOVING, mPlayer.getUserName(), tpDirection.getKnobPercentX(), tpDirection.getKnobPercentY(), mPlayer.getX(), mPlayer.getY(), 0);
        }

        // for character moving
        if (tpDirection.isTouched()) {
            if (!tpShooting.isTouched()) {
                Vector2 v = new Vector2(tpDirection.getKnobPercentX(), tpDirection.getKnobPercentY());
                mPlayer.changeDirection(v.angle());
            }
        }
        else
            mPlayer.changeDirection(-1);     // means stop animation

        // for shooting
        if (tpShooting.isTouched()) {
            if (shootingTimer == null) {
                shootingTimer = new Timer();
                shootingTimer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Vector2 v = new Vector2(tpShooting.getKnobPercentX(), tpShooting.getKnobPercentY());
                        mPlayer.AddBullet(mPlayer.getX()+tpShooting.getKnobPercentX()+mPlayer.getTextureSize()/2, mPlayer.getY()+tpShooting.getKnobPercentY()+mPlayer.getTextureSize()/5, v.angle());
                        mPlayer.changeDirection(v.angle());

                        // send to message
                        mGameScreen.getSPMessageHandler().notifyData(CodeDefine.MSG_PLAYER_SHOOTING, mPlayer.getUserName(), mPlayer.getX()+tpShooting.getKnobPercentX()+mPlayer.getTextureSize()/2, mPlayer.getY()+tpShooting.getKnobPercentY()+mPlayer.getTextureSize()/5, 0, 0, v.angle());
                    }
                }, 0.0f, 0.2f);
            }
        }
        else {
            shootingTimer = null;
            Timer.instance().clear();
        }

        stageDirection.act(Gdx.graphics.getDeltaTime());
        stageDirection.draw();

        stageShooting.act(Gdx.graphics.getDeltaTime());
        stageShooting.draw();
    }

    public Boolean bIsShooting() {
        if (tpShooting.isTouched())
            return true;
        return false;
    }

    public void dispose () {
        spriteBatch.dispose();
    }
}