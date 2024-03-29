package com.leo.skinlib;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.collection.ArrayMap;
import androidx.core.view.LayoutInflaterCompat;

import com.leo.skinlib.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.Observable;

public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    /**
     * 被观察者
     */
    private Observable mObserable;
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new ArrayMap<>();

    /**
     * 通过这个构造方法完成 SkinManager 这个被观察者的传入。
     */
    public ApplicationActivityLifecycle(Observable observable) {
        mObserable = observable;
    }

    /**
     * 该方法会在每一个activity创建的时候执行，也就会保证，每一个页面创建之后都会将观察者添加进去，以便刷新。
     * @param activity
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        /**
         *  更新状态栏
         */
        SkinThemeUtils.updateStatusBarColor(activity);

        /*
         * 更新布局视图
         * 获得Activity的布局加载器
         */
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);
        try {
            // Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            // 如设置过抛出一次
            // 设置 mFactorySet 标签为false
            // Api 28或以上该方式失效。
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
                field.setAccessible(true);
                field.setBoolean(layoutInflater, false);
            } else {
                forceSetFactory2(layoutInflater, activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用factory2 设置布局加载工程
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);
        mObserable.addObserver(skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }

    // 通过反射替换掉系统的factory
    private SkinLayoutInflaterFactory forceSetFactory2(LayoutInflater inflater, Activity activity) {
        Class<LayoutInflater> inflaterClass = LayoutInflater.class;
        try {
            String mFactoryStr = "mFactory";
            Field mFactory = inflaterClass.getDeclaredField(mFactoryStr);
            mFactory.setAccessible(true);
            String mFactory2Str = "mFactory2";
            Field mFactory2 = inflaterClass.getDeclaredField(mFactory2Str);
            mFactory2.setAccessible(true);
            SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);
            // 改变factory
            mFactory2.set(inflater, skinLayoutInflaterFactory);
            mFactory.set(inflater, skinLayoutInflaterFactory);
            return skinLayoutInflaterFactory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
