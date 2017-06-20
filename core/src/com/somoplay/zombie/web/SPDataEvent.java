package com.somoplay.zombie.web;

import org.json.JSONObject;

import java.util.EventObject;

/**
 * Created by shoong on 2017-06-06.
 */

public class SPDataEvent extends EventObject {
    private JSONObject message;

    public JSONObject getMessage() {
        return message;
    }

    public void setMessage(JSONObject message) {
        this.message = message;
    }

    public SPDataEvent(Object source, JSONObject message) {
        super(source);
        this.message = message;
    }
}
