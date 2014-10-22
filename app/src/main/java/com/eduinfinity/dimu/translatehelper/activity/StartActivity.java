package com.eduinfinity.dimu.translatehelper.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;

public class StartActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        TextView tv = (TextView)findViewById(R.id.textView_motto);
        //从assert中获取有资源，获得app的assert，采用getAserts()，通过给出在assert/下面的相对路径。在实际使用中，字体库可能存在于SD卡上，可以采用createFromFile()来替代createFromAsset。
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/wawasc.otf");
        tv.setTypeface(face);

        new Handler().postDelayed(r, 100);// 1秒后关闭，并跳转到主页面
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
        // TODO Auto-generated method stub
            Intent intent = new Intent();
//            intent.setClass(StartActivity.this, ClassMenuActivity.class);
            intent.setClass(StartActivity.this, ClassMenuActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
