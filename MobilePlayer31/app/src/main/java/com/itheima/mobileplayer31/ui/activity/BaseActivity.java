package com.itheima.mobileplayer31.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.util.Logutil;
import com.itheima.mobileplayer31.util.ToastUtil;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initListener();
        initData();
        regCommonBtn();
    }

    //处理公用按钮
    private void regCommonBtn() {
        View back = findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(this);
        }
    }

    //view值得初始化
    protected abstract void initData();

    //setListener  setAdapter
    protected abstract void initListener();

    //view初始化
    protected abstract void initView();

    //获取布局id
    protected abstract int getLayoutId();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else {
            processClick(v);
        }
    }

    //处理其他点击事件
    protected abstract void processClick(View v);

    //log打印
    protected void logD(String msg) {
        Logutil.logD(getClass().getSimpleName(), msg);
    }
    //toast显示
    protected void toast(final String msg) {
//        if(Looper.myLooper()==Looper.getMainLooper()){
//
//        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.toast(BaseActivity.this, msg);
            }
        });
    }

    protected void startNewActivity(Class clazz,boolean finish){
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
        if(finish) {
            finish();
        }
    }
}
