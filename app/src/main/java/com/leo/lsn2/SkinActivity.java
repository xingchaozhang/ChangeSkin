package com.leo.lsn2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.leo.skinlib.SkinManager;

public class SkinActivity extends Activity {

//    private String skinPackageName = "/skinpkg-debug.apk";
    private String skinPackageName = "/3116007.skin500";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
    }

    public void change(View view) {
        String skinPkg = getBaseContext().getCacheDir() + skinPackageName;

        // 换肤，皮肤包是独立的apk包，可以来自网络下载
        SkinManager.getInstance().loadSkin(skinPkg);
    }

    public void restore(View view) {
        SkinManager.getInstance().loadSkin(null);
    }
}
