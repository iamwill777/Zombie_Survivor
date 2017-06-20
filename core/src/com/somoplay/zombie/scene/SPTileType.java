package com.somoplay.zombie.scene;

/**
 * Created by yaolu on 2017-06-15.
 */

enum SPTileType {
    BLACNK, ROAD, TREE, BIGTREE, STUMP, GLASS, FLOWER;

    public static SPTileType fromInteger(int id)
    {
        switch(id)
        {
            case 0: return BLACNK;
            case 1: return ROAD;
            case 2: return TREE;
            case 3: return BIGTREE;
            case 4: return STUMP;
            default: return BLACNK;
        }
    }
}
