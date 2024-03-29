package com.leo.skinlib;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.leo.skinlib.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * 我们自定义一个Factory,用来接管系统的view的生产过程，这就是为了让我们记录每一个view的name与ID.
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    /**
     * create view的时候需要加上前缀，我们需要再这里进行添加。
     */
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };

    /**
     * 记录对应VIEW的构造函数，这是View有两个属性的构造函数。如果是xml中使用，至少需要两个参数的构造函数。
     */
    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    /**
     * 将源码拷贝下来，防止每次都通过反射来调用，解约性能。
     */
    private static final HashMap<String, Constructor<? extends View>> mConstructorMap = new HashMap<String, Constructor<? extends View>>();

    /**
     * 当选择新皮肤后需要替换View与之对应的属性
     * 页面属性管理器
     */
    private SkinAttribute skinAttribute;
    // 用于获取窗口的状态框的信息
    private Activity activity;

    public SkinLayoutInflaterFactory(Activity activity) {
        this.activity = activity;
        skinAttribute = new SkinAttribute();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        // 换肤就是在需要时候替换 View的属性(src、background等)
        // 所以这里创建 View,从而修改View属性,我们不需要去修改系统创建View的流程。
        View view = createSDKView(name, context, attrs);
        if (null == view) {
            view = createView(name, context, attrs);
        }
        // 这就是我们加入的逻辑
        if (null != view) {
            // 加载属性,对于每一个View，我们都需要保存需要替换的属性以及对应的ID，方便查找。
            skinAttribute.look(view, attrs);
        }
        return view;
    }

    private View createSDKView(String name, Context context, AttributeSet attrs) {
        // 如果包含 . 则不是SDK中的view 可能是自定义view包括support库中的View
        if (-1 != name.indexOf('.')) {
            return null;
        }
        //不包含就要在解析的 节点 name前，拼上： android.widget. 等尝试去反射
        for (int i = 0; i < mClassPrefixList.length; i++) {
            View view = createView(mClassPrefixList[i] + name, context, attrs);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    private View createView(String name, Context context, AttributeSet
            attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
        }
        return null;
    }

    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
            }
        }
        return constructor;
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    /**
     * 如果有人发送通知，这里就会执行
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        SkinThemeUtils.updateStatusBarColor(activity);
        skinAttribute.applySkin();
    }
}
