package com.hoony.mediaplayer;

import android.app.Application;
import android.os.Build;


/**
 * App
 *
 * Created by minsus on 2018-11-26.
 */
public class App extends Application {

    private static App self;

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;

    }
}
