package com.leo.lsn2;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

public class Factory2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 必须在 super 之前调用
        LayoutInflater.from(this).setFactory2(new LayoutInflater.Factory2() {
            @Nullable
            @Override
            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context,
                                     @NonNull AttributeSet attrs) {
//                if (TextUtils.equals(name, "TextView")) {
//                    Button btn = new Button(Factory2Activity.this);
//                    btn.setText("我是一个按钮");
//                    return btn;
//                }

                return null;
            }

            @Nullable
            @Override
            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                return null;
            }
        });
        super.onCreate(savedInstanceState);

        // 在super之后调用，反射  设置mFactorySet = false；

        setContentView(R.layout.activity_factory2);

        TextView tv = findViewById(R.id.tv);
        Log.e("leo", "tv: " + tv);

        TextView tv2 = new TextView(this);
        Log.e("leo", "tv2: " + tv2);
    }
}
