package com.itheima.mobileplayer31.ui.activity;

import android.os.Handler;
import android.view.View;

import com.itheima.mobileplayer31.R;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public class SplashActivity extends BaseActivity {
    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void processClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //定时2秒进入主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNewActivity(MainActivity.class,true);
            }
        },2000);
    }
}
