package com.icoder.couldnewsclient.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.icoder.couldnewsclient.R;
import com.icoder.couldnewsclient.app.MyApplication;
import com.icoder.couldnewsclient.entity.NewsChannel;
import com.icoder.couldnewsclient.utils.ConstantParams;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.AppTheme_splash);
        setContentView(R.layout.activity_splash);
        SharedPreferences sp = getSharedPreferences("init", MODE_PRIVATE);
        boolean isFirstLoad = sp.getBoolean("isFisstLoad", true);
        //如果是第一加载
        if(isFirstLoad) {
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.translate_in_left,R.anim.translate_in_left);
            }
        },3000);
    }
}