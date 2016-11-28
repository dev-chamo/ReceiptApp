package com.tobcross.gymmanagerreceipt;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;


/**
 * Created by Koo on 2016. 11. 24..
 */

public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}
