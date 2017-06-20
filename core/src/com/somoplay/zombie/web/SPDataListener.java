package com.somoplay.zombie.web;

import java.util.EventListener;

/**
 * Created by shoong on 2017-06-06.
 */

public interface SPDataListener extends EventListener {
    void receiveData(SPDataEvent event);
}
