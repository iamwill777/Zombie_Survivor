package com.somoplay.zombie.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.somoplay.zombie.asset.SPAssetManager;
import com.somoplay.zombie.protocol.SPMainListener;
import com.somoplay.zombie.scene.SPMap;
import com.somoplay.zombie.scene.SPMiniMap;
import com.somoplay.zombie.sprite.SPPlayer;
import com.somoplay.zombie.sprite.SPSpriteManager;
import com.somoplay.zombie.ui.SPJoyStick;
import com.somoplay.zombie.web.CodeDefine;
import com.somoplay.zombie.web.SPIRecvMessageHandler;
import com.somoplay.zombie.web.SPSocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaolu on 2017-06-14.
 */

public class SPGameScene implements Screen, SPIRecvMessageHandler
{
    private OrthographicCamera camera; //2D camera

    private SPMap spMap;
    private SPSpriteManager spSpriteManager;
    private SPJoyStick spJoyStick;
    private SPMiniMap spMiniMap;
    private SpriteBatch spriteBatch;

    private SPSocketManager mSocketManager;
    private SPMainListener spMainListener;

    public SPSpriteManager getSPSpriteManager(){
        return spSpriteManager;
    }
    public SPSocketManager getSPMessageHandler() {
        return mSocketManager;
    }

    public void setSpMainListener(SPMainListener spMainListener) {
        this.spMainListener = spMainListener;
    }

    @Override
    public void show() {
        SPAssetManager.getInstance().Init();
        spriteBatch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.camera.update();
    }

    @Override
    public void render(float delta) {
        // this sequence is very important for using libGDX AssetManager
        if (SPAssetManager.getInstance().getAssetManager().update()) {        // waiting for loading resources

            if (!SPAssetManager.getInstance().isbIsLoaded()) {                // if done?
                SPAssetManager.getInstance().makeResource();                  // make a resources like animations that was needed
                createObjects();                                            // create objects like world, player, etc.
            }
            //clear screen
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            updateCamera();

            spriteBatch.setProjectionMatrix(camera.combined);
            spMap.render(camera);
            spJoyStick.render(camera);

            spriteBatch.begin();
            generalUpdate();
            spSpriteManager.render(camera, spriteBatch);
            spSpriteManager.updateBullets();
            spSpriteManager.updateEnemy(delta);
            spriteBatch.end();

            //spriteBatch.setProjectionMatrix(spMiniMap.stage.getCamera().combined);
            spMiniMap.render();
            spMiniMap.update(delta);
        } else {
            Gdx.app.log("Loading Resources", Float.toString(SPAssetManager.getInstance().getAssetManager().getProgress()));
            // later on display loading bar here
        }

    }

    private void createObjects() {
        spMap = new SPMap();
        spSpriteManager = new SPSpriteManager(this);
        spJoyStick = new SPJoyStick(spSpriteManager.getPlayer(), this.camera, this);
        spMiniMap = new SPMiniMap(spriteBatch, spSpriteManager);

        mSocketManager = new SPSocketManager(this);
        getSPMessageHandler().connectToGate();
    }

    private void updateCamera() {

        if(spSpriteManager.getPlayer().getX()+this.camera.viewportWidth/2<spMap.width*16&&spSpriteManager.getPlayer().getX()-this.camera.viewportWidth/2>0){
            this.camera.position.x = spSpriteManager.getPlayer().getX();
        }
        if(spSpriteManager.getPlayer().getY()+this.camera.viewportHeight/2<spMap.height*16&&spSpriteManager.getPlayer().getY()-this.camera.viewportHeight/2>0){
            this.camera.position.y = spSpriteManager.getPlayer().getY();
        }
        camera.update();
    }

    public void generalUpdate(){
        if(Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.LEFT)))
        {
            spSpriteManager.setPlayPositionLEFT(spJoyStick);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT)))
        {
            spSpriteManager.setPlayPositionRIGHT(spJoyStick);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S) || (Gdx.input.isKeyPressed(Input.Keys.DOWN)))
        {
            spSpriteManager.setPlayPositionDOWN(spJoyStick);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W) || (Gdx.input.isKeyPressed(Input.Keys.UP)))
        {
            spSpriteManager.setPlayPositionUP(spJoyStick);
        }
    }

    @Override public void resize(int width, int height) {
        if (spMainListener != null) {
            spMainListener.logPrint("resize is called.");
        }
    }

    @Override public void hide() {

    }

    @Override public void pause() {

    }

    @Override public void resume() {

    }

    @Override public void dispose() {
        spMap.dispose();
        spJoyStick.dispose();
        mSocketManager.disconnect();
    }

    @Override
    public void socketHandler(JSONObject message) {
        try {
            int code = message.getInt("code");

            switch(code) {
                case CodeDefine.CODE_MSG_ANSWER_LOGIN: {
                    JSONArray users = message.getJSONArray("users");
                    if (users != null && users.length() > 0) {
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject objectInArray = users.getJSONObject(i);
                            SPPlayer newPlayer = new SPPlayer(false, objectInArray.getString("user_name"), objectInArray.getDouble("posX"), objectInArray.getDouble("posY"));
                            this.getSPSpriteManager().addPlayers(newPlayer);
                        }
                    }

                    JSONArray monsters = message.getJSONArray("monsters");
                    if (monsters != null && monsters.length() > 0) {
                        for (int i = 0; i < monsters.length(); i++) {
                            JSONObject objectInArray = monsters.getJSONObject(i);
                            this.getSPSpriteManager().addZombie(objectInArray.getInt("mobIndex"), (float) objectInArray.getDouble("posX"), (float) objectInArray.getDouble("posY"), objectInArray.getInt("health"));
                        }
                    }
                    break;
                }
                case CodeDefine.CODE_MSG_ADD_PLAYER: {
                    SPPlayer newPlayer = new SPPlayer(false, message.getString("user_name"), message.getDouble("posX"), message.getDouble("posY"));
                    this.getSPSpriteManager().addPlayers(newPlayer);
                    break;
                }
                case CodeDefine.CODE_MSG_REMOVE_PLAYER: {
                    String username;
                    username = message.getString("user");
                    this.getSPSpriteManager().removePlayers(username);
                    break;
                }
                case CodeDefine.CODE_MSG: {
                    int msgNum = message.getInt("msgNum");
                    switch(msgNum) {
                        case CodeDefine.MSG_PLAYER_MOVING:
                            this.getSPSpriteManager().setPlayerPosition(message.getString("username"), (float) message.getDouble("X"), (float) message.getDouble("Y"), (float) message.getDouble("angle"));
                            break;

                        case CodeDefine.MSG_PLAYER_SHOOTING:
                            this.getSPSpriteManager().addBullets(message.getString("username"), (float) message.getDouble("X"), (float) message.getDouble("Y"), (float) message.getDouble("angle"));
                            break;

                        case CodeDefine.MSG_KILLED_MONSTER: {
                            this.getSPSpriteManager().removeMonster(message.getInt("idKilledMonster"));

//                            JSONArray monsters = message.getJSONArray("monsters");
//                            if (monsters != null && monsters.length() > 0) {
//                                for (int i = 0; i < monsters.length(); i++) {
//                                    JSONObject objectInArray = monsters.getJSONObject(i);
//                                    this.getSPSpriteManager().addZombie(objectInArray.getInt("mobIndex"), (float) objectInArray.getDouble("posX"), (float) objectInArray.getDouble("posY"), objectInArray.getInt("health"));
//                                }
//                            }
                            break;
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createStandaloneZombies(int num) {
        if (getSPSpriteManager().getZombie().size() <= 0) {
            getSPSpriteManager().createStandaloneZombies(num);
        }
    }

    @Override
    public void getRequestLoginInfo(JSONObject requestLogin) {
        try {
            requestLogin.put("username", getSPSpriteManager().getPlayer().getUserName());
            requestLogin.put("X", getSPSpriteManager().getPlayer().getX());
            requestLogin.put("Y", getSPSpriteManager().getPlayer().getY());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
