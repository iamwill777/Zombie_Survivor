package com.somoplay.zombie.scene;

import com.badlogic.gdx.graphics.Color;


/**
 * Created by yaolu on 2017-06-14.
 */

public class SPMapTile {
    SPTileType type;
    Color color;
    int x, y;

    public SPMapTile(SPTileType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;

        if (type == SPTileType.TREE) {
            this.color = Color.GREEN;
        } //trees - solid
        else if (type == SPTileType.ROAD) {
            this.color = Color.DARK_GRAY;
        } //floor
        else if (type == SPTileType.BLACNK) {
            this.color = Color.RED;
        } //target area
        else {
            this.color = Color.WHITE;
        }
    }


    /*** Getters ***/
    public SPTileType getType() {
        return this.type;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Returns the position of this tile in the world
     *
     * @return
     */
    public float getX() {
        return this.x * SPMapConstant.TILESIZE;
    }

    public float getY() {
        return this.y * SPMapConstant.TILESIZE;
    }

    /**
     * Returns whether or not a Tile is passable
     *
     * @param type
     * @return
     */
    public boolean isPassable(SPTileType type) {
        if (type != SPTileType.TREE)
            return true;
        return false;
    }
}
