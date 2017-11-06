package com.itheima.mobileplayer31.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobileplayer31.util.Logutil;
import com.itheima.mobileplayer31.util.ToastUtil;

/**
 * Created by ThinkPad on 2016/11/24.
 */

public abstract class BaseFragment extends Fragment {
    protected Context context;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    protected abstract int getLayoutId();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    protected View findViewById(int id){
        return getView().findViewById(id);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    protected abstract void initData();

    protected abstract void initListener();

    protected abstract void initView();

    //log打印
    protected void logD(String msg) {
        Logutil.logD(getClass().getSimpleName(), msg);
    }

    //toast显示
    protected void toast(final String msg) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastUtil.toast(context, msg);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.toast(context, msg);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除消息避免内存泄漏
        handler.removeCallbacksAndMessages(null);
    }
}
