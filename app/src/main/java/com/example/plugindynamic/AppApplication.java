package com.example.plugindynamic;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class AppApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {
            HookHelper.hookInstrumentation(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Resources getResources() {
        return PluginHelper.getPluginResources() == null ? super.getResources() : PluginHelper.getPluginResources();
    }
}
