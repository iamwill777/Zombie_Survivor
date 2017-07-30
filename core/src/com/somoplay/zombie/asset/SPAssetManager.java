package com.somoplay.zombie.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by yaolu on 2017-06-14.
 */

public class SPAssetManager {
    private static SPAssetManager mInstance = null;

    protected SPAssetManager() {
        // Exists only to defeat instantiation.
    }

    public static SPAssetManager getInstance() {
        if(mInstance == null) {
            mInstance = new SPAssetManager();
        }
        return mInstance;
    }

    private AssetManager mAssetManager = new AssetManager();
    private Texture mTxtZombie;
    private Texture mTxtBullet;
    private TextureAtlas mAtlasWalking;
    private Animation mAniCharRight, mAniCharRightDown, mAniCharDown,
            mAniCharDownLeft, mAniCharLeft, mAniCharLeftUp, mAniCharUp, mAniCharUpRight;
    private boolean bIsLoaded = false;

    //Health bar
    private Texture mHealthBar;
    private Texture mHealthBarBorder;
    private String mHealthBarName = "uiAssets/green.jpg";
    private String mHealthBarBorderName = "uiAssets/healthbart.png";

    //Map
    private Texture mMap;
    private String mMapName = "uiAssets/smallmap.png";
    private Texture mMiniPlayer;
    private String mMiniPlayerName = "players/character10/18.png";

    //Animate Zombie
    Array<TextureRegion> frames;
    private Texture mZombieSheet;
    private String mZombieSheetName = "Zombie/enemyAttack.png";
    private Animation mZombieRight, mZombieAttack;

    // Male zombie
    private TextureAtlas maleWalkAtlas;
    private TextureAtlas maleAttackAtlas;
    private Animation<TextureRegion> mWalkAnimation;
    private Animation<TextureRegion> mAttackAnimation;
    private Texture slime;

    // Placeholders for dropped items
    private Texture mHealthPack;
    private Texture mPlayerDamage;

    // bitmap
    private BitmapFont mBitmapFont;

    public void Init() {
        mAssetManager.load("resources/zombie.png", Texture.class);
        mAssetManager.load("resources/bullet.png", Texture.class);
        mAssetManager.load("resources/healthpack.png", Texture.class);
        mAssetManager.load("resources/playerdamage.png", Texture.class);
        mAssetManager.load("Zombie/Slime.png", Texture.class);
        mAssetManager.load("players/character10/right/walkingRight.atlas", TextureAtlas.class);
        mAssetManager.load("Zombie/male/Attack/maleattacking.atlas", TextureAtlas.class);
        mAssetManager.load("Zombie/male/Walk/malewalking.atlas", TextureAtlas.class);
        for(int i=1; i<=22; i++) {
            String strName = "players/character10/" + String.format("%02d", i) + ".png";
            mAssetManager.load(strName, Texture.class);
        }

        mAssetManager.load(mHealthBarName, Texture.class);
        mAssetManager.load(mHealthBarBorderName,Texture.class);
        mAssetManager.load(mMapName,Texture.class);
        mAssetManager.load(mMiniPlayerName,Texture.class);
        mAssetManager.load(mZombieSheetName,Texture.class);

        frames = new Array<TextureRegion>();

        mBitmapFont = new BitmapFont();
    }

    public void makeResource() {
        mTxtZombie = mAssetManager.get("resources/zombie.png", Texture.class);
        mTxtBullet = mAssetManager.get("resources/bullet.png", Texture.class);
        mHealthPack = mAssetManager.get("resources/healthpack.png", Texture.class);;
        mPlayerDamage = mAssetManager.get("resources/playerdamage.png", Texture.class);
        slime = mAssetManager.get("Zombie/Slime.png");

        // make animation from atlas
        mAtlasWalking = mAssetManager.get("players/character10/right/walkingRight.atlas", TextureAtlas.class);
        mAniCharRight = new Animation(1/10f, mAtlasWalking.getRegions());

        // zombie animation
        maleAttackAtlas = mAssetManager.get("Zombie/male/Attack/maleattacking.atlas", TextureAtlas.class);
        maleWalkAtlas = mAssetManager.get("Zombie/male/Walk/malewalking.atlas", TextureAtlas.class);
        mWalkAnimation = new Animation(0.2f, maleWalkAtlas.getRegions());
        mAttackAnimation = new Animation(0.15f, maleAttackAtlas.getRegions());

        // make animation from texture
        ArrayList<String> lstTexture = new ArrayList<String>();
        lstTexture.add("players/character10/09.png");
        lstTexture.add("players/character10/10.png");
        lstTexture.add("players/character10/11.png");
        lstTexture.add("players/character10/12.png");
        mAniCharRightDown = makeCharacterAnimation(4, lstTexture);

        lstTexture.clear();
        lstTexture.add("players/character10/20.png");
        lstTexture.add("players/character10/21.png");
        lstTexture.add("players/character10/22.png");
        mAniCharDown = makeCharacterAnimation(3, lstTexture);

        lstTexture.clear();
        lstTexture.add("players/character10/13.png");
        lstTexture.add("players/character10/14.png");
        lstTexture.add("players/character10/15.png");
        lstTexture.add("players/character10/16.png");
        mAniCharDownLeft = makeCharacterAnimation(4, lstTexture);

        lstTexture.clear();
        lstTexture.add("players/character10/05.png");
        lstTexture.add("players/character10/06.png");
        lstTexture.add("players/character10/07.png");
        lstTexture.add("players/character10/08.png");
        mAniCharLeft = makeCharacterAnimation(4, lstTexture);
        mAniCharLeftUp = makeCharacterAnimation(4, lstTexture);

        lstTexture.clear();
        lstTexture.add("players/character10/17.png");
        lstTexture.add("players/character10/18.png");
        lstTexture.add("players/character10/19.png");
        mAniCharUp = makeCharacterAnimation(3, lstTexture);

        lstTexture.clear();
        lstTexture.add("players/character10/01.png");
        lstTexture.add("players/character10/02.png");
        lstTexture.add("players/character10/03.png");
        lstTexture.add("players/character10/04.png");
        mAniCharUpRight = makeCharacterAnimation(4, lstTexture);

        //Make Zombie animation
        mZombieSheet = mAssetManager.get(mZombieSheetName,Texture.class);

        //Bad sprite sheet, so I have to make animation in this way..lol..

        frames.add(new TextureRegion(mZombieSheet,0,152,25,43));
        frames.add(new TextureRegion(mZombieSheet,27,152,23,43));
        frames.add(new TextureRegion(mZombieSheet,52,152,25,43));
        frames.add(new TextureRegion(mZombieSheet,80,152,28,43));
        frames.add(new TextureRegion(mZombieSheet,111,152,22,43));
        frames.add(new TextureRegion(mZombieSheet,136,152,22,43));
        frames.add(new TextureRegion(mZombieSheet,161,152,24,43));
        mZombieRight = new Animation(0.1f,frames, Animation.PlayMode.LOOP);

        frames.clear();
        frames.add(new TextureRegion(mZombieSheet,25,20,30,40));
        frames.add(new TextureRegion(mZombieSheet,58,20,30,40));
        frames.add(new TextureRegion(mZombieSheet,91,20,29,40));
        frames.add(new TextureRegion(mZombieSheet,123,20,30,40));
        frames.add(new TextureRegion(mZombieSheet,156,20,34,40));
        frames.add(new TextureRegion(mZombieSheet,193,20,32,40));
        mZombieAttack = new Animation(0.3f,frames,Animation.PlayMode.NORMAL);




        //Make UI
        mHealthBar = mAssetManager.get(mHealthBarName,Texture.class);
        mHealthBarBorder = mAssetManager.get(mHealthBarBorderName,Texture.class);
        mMap = mAssetManager.get(mMapName,Texture.class);
        mMiniPlayer = mAssetManager.get(mMiniPlayerName,Texture.class);

        bIsLoaded = true;

    }

    public Animation makeCharacterAnimation(int regionCount, ArrayList<String> lstTexture) {
        int nCount = 0;
        TextureRegion[] frames = new TextureRegion[regionCount];
        for(String textureName: lstTexture) {
            if (mAssetManager.isLoaded(textureName)) {
                Texture texture = mAssetManager.get(textureName, Texture.class);
                frames[nCount++] = new TextureRegion(texture);
            }
        }
        return new Animation(1/10f, frames);
    }

    public com.badlogic.gdx.assets.AssetManager getAssetManager() { return mAssetManager; }
    public Texture getZombie() { return mTxtZombie; }
    public Texture getBullet() { return mTxtBullet; }
    public Animation getAniCharRight() { return mAniCharRight; }
    public Animation getAniCharRightDown() { return mAniCharRightDown; }
    public Animation getAniCharDown() { return mAniCharDown; }
    public Animation getAniCharDownLeft() { return mAniCharDownLeft; }
    public Animation getAniCharLeft() { return mAniCharLeft; }
    public Animation getAniCharLeftUp() { return mAniCharLeftUp; }
    public Animation getAniCharUp() { return mAniCharUp; }
    public Animation getAniCharUpRight() { return mAniCharUpRight; }
    public Animation getAniZombieRight() { return mZombieRight; }
    public Animation getAniZombieAttack() { return  mZombieAttack; }
    public Animation<TextureRegion> getWalkAnimation(){return mWalkAnimation; }
    public Animation<TextureRegion> getAttackAnimation(){return mAttackAnimation;}

    public Texture getHealthBar() { return mHealthBar; }
    public Texture getHealthBarBorder() { return mHealthBarBorder; }
    public Texture getMap() { return mMap; }
    public Texture getMiniPlayer(){ return mMiniPlayer; }
    public Texture getHealthPack() {return mHealthPack;}
    public Texture getPlayerDamage(){return mPlayerDamage;}
    public Texture getSlime(){return slime;}
    public BitmapFont getBitmapFont() { return mBitmapFont; }

    public boolean isbIsLoaded() {
        return bIsLoaded;
    }
}
