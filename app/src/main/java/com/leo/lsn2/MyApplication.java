package com.leo.lsn2;

import android.app.Application;

import com.leo.skinlib.SkinManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
