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
import com.somoplay.zombie.web.SPMessageHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaolu on 2017-06-14.
 */

public class SPGameScene implements Screen
{
    private OrthographicCamera camera; //2D camera

    private SPMap spMap;
    private SPSpriteManager spSpriteManager;
    private SPJoyStick spJoyStick;
    private SPMiniMap spMiniMap;
    private SpriteBatch spriteBatch;

    private SPMessageHandler spMessageHandler;

    private SPMainListener spMainListener;

    public static final String CHAT_SERVER_URL = "http://10.0.2.2:3010/";
    //public static final String CHAT_SERVER_URL = "http://10.51.205.75:3010/";
    //public static final String CHAT_SERVER_URL = "http://192.168.0.103:13337/";

    public static final String UserName = "Snow John";

    public SPSpriteManager getSPSpriteManager(){
        return spSpriteManager;
    }

    public SPMessageHandler getSPMessageHandler() {
        return spMessageHandler;
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
        spMessageHandler.disconnect();
    }

    public void socketHandler(String type, Object... args) {
        if (type == "notify spawn zombie") {
            JSONObject data = (JSONObject) args[0];
            try {
                JSONObject position = data.getJSONObject("zombiePosition");
                float fX = position.getInt("X");
                float fY = position.getInt("Y");
                Gdx.app.log("SOCKET.IO", "position: " + fX + ", " + fY);
                this.spSpriteManager.addZombie(fX, fY);
            } catch (JSONException e) {
                return;
            }
        }
        else if (type == "answer login") {      // answer to add userlist
            JSONObject data = (JSONObject) args[0];
            try {
                JSONArray users = data.getJSONArray("users");
                if(users != null && users.length() > 0) {
                    for(int i=0; i<users.length(); i++) {
                        JSONObject objectInArray = users.getJSONObject(i);
                        Gdx.app.log("SOCKET.IO", "username: " + objectInArray.getString("user_name"));
                        SPPlayer newPlayer = new SPPlayer(false, objectInArray.getString("user_name"), objectInArray.getDouble("posX"), objectInArray.getDouble("posY"));
                        this.spSpriteManager.addPlayers(newPlayer);
                    }
                }
            } catch (JSONException e) {
                return;
            }
        }
        else if (type == "notify login") {      // notify to add new_user
            JSONObject data = (JSONObject) args[0];
            try {
                JSONObject newUser = data.getJSONObject("body");
                SPPlayer newPlayer = new SPPlayer(false, newUser.getString("user_name"), newUser.getDouble("posX"), newUser.getDouble("posY"));
                this.spSpriteManager.addPlayers(newPlayer);
            } catch (JSONException e) {
                return;
            }
        }
        else if (type == "notify moving") {
            JSONObject packet = (JSONObject) args[0];
            String username;
            float fX, fY, fAngle;
            try {
                JSONObject data = packet.getJSONObject("body");
                username = data.getString("username");
                fX = Float.valueOf(String.valueOf(data.getString("X")));
                fY = Float.valueOf(String.valueOf(data.getString("Y")));
                fAngle = Float.valueOf(String.valueOf(data.getString("angle")));
                //Gdx.app.log("SOCKET.IO", "name: " + username + ", position X: " + fX + ", position Y: " + fY + ", angle: " + fAngle);
                this.spSpriteManager.setPlayerPosition(username, fX, fY, fAngle);
            } catch (JSONException e) {
                return;
            }
        }
    }

}
